package com.nl.bigdata.redis;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import redis.clients.jedis.Builder;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.PipelineBase;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

public class NlRedisClusterPipeline extends PipelineBase implements IRedisClusterPipeline, Closeable {

	private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

	// 主节点信息在 clusterSlots 中的位置 （beginSolt,endSolt,master,slave0,...)
	private static int MASTER_NODE_INDEX = 2;

	// 槽点和主机名的映射
	private Map<Integer, String> slotMap = new HashMap<Integer, String>();
	// 主机名和redis连接的映射
	private Map<String, Jedis> nodeJedisMap = new HashMap<String, Jedis>();
	// 主机名和返回结构的映射
	private Map<String, Queue<Response<?>>> pipelinedResponsesMap = new HashMap<String, Queue<Response<?>>>();

	private boolean need;
	private JedisCluster cluster;
	
	public static  IRedisClusterPipeline fromJedisCluster(JedisCluster cluster){
		return new NlRedisClusterPipeline(cluster);
	}
	
	
	public NlRedisClusterPipeline(JedisCluster cluster){
		Map<String, JedisPool> map = cluster.getClusterNodes();
		Iterator<JedisPool> ites = map.values().iterator();
		while(ites.hasNext()){
			Jedis jedis = ites.next().getResource();
			try{
				System.out.println(jedis.getClient().getHost()+":"+jedis.getClient().getPort());
				if("PONG".equalsIgnoreCase(jedis.ping())){
					discoverClusterSlots(jedis);
					return;
				}
			}finally{
				jedis.close();
			}
		}
		throw new RuntimeException("没有可用的redis 连接");
	}
	

	@SuppressWarnings("unchecked")
	private void discoverClusterSlots(Jedis configJedis) {
		// 通过redis连接 ， 获取 redis集群的槽点信息
		List<Object> slots = configJedis.clusterSlots();
		// 解析槽点信息，建立槽点和redis master节点的关联，并为每个master节点创建一个redis连接
		for (Object slotInfoObj : slots) {
			List<Object> slotInfo = (List<Object>) slotInfoObj;
			if (slotInfo.size() <= MASTER_NODE_INDEX) {
				continue;
			}
			
			List<Object> hostInfos = (List<Object>) slotInfo
					.get(MASTER_NODE_INDEX);

			if (hostInfos.size() <= 0) {
				continue;
			}
			String host = SafeEncoder.encode((byte[]) hostInfos.get(0));
			long port = (Long) hostInfos.get(1);
			// 建立槽点和redis master节点的关联
			String nodekey = host + ":" + port;
			Map<Integer, String> map = buildSoltAndNodeMap(
					((Long) slotInfo.get(0)).intValue(),
					((Long) slotInfo.get(1)).intValue(), nodekey);
			slotMap.putAll(map);
			// 为每个master节点创建一个redis连接
			Jedis jedis = getJedis(host, (int) port);
			nodeJedisMap.put(nodekey, jedis);
			pipelinedResponsesMap.put(nodekey, new LinkedList<Response<?>>());
		}
	}
	
	private Jedis getJedis(String host,int port){
		String nodekey = host + ":" + port;
		if(cluster!=null 
				&& cluster.getClusterNodes()!=null 
				&& cluster.getClusterNodes().get(nodekey)!=null){
			
			return cluster.getClusterNodes().get(host).getResource();
		}else{
			return new Jedis(host,  port);
		}
	}

	/**
	 * 通过槽点id 获取具体的master 节点的client
	 * 
	 * @param slot
	 * @return
	 */
	private Client getClientBySlot(int slot) {
		String nodeKey = slotMap.get(slot);
		Client client = nodeJedisMap.get(nodeKey).getClient();
		threadLocal.set(nodeKey);
		return client;
	}

	@Override
	public Client getClient(String key) {
		int slot = JedisClusterCRC16.getSlot(key);
		return getClientBySlot(slot);
	}

	@Override
	protected Client getClient(byte[] key) {
		int slot = JedisClusterCRC16.getSlot(key);
		return getClientBySlot(slot);
	}

	// 将clean 不处理返回值
	public void flush() {
		sync(false);
	}

	// 处理返回值
	public void sync() {
		sync(true);
	}

	private void sync(boolean get) {
		for (Entry<String, Jedis> entry : nodeJedisMap.entrySet()) {
			Jedis jedis = entry.getValue();
			String nodekey = entry.getKey();
			Queue<Response<?>> queue = pipelinedResponsesMap.get(nodekey);
			try {
				if (get) {
					List<Object> unformatted = jedis.getClient().getAll();
					if (queue != null && queue.size() > 0) {
						for (Object data : unformatted) {
							Response<?> response = queue.poll();
							if (response != null) {
								response.set(data);
							}
						}
					}
				} else {
					jedis.getClient().getAll();
					queue.clear();
				}
			} catch (Exception ex) {
			} finally {
				queue.clear();
			}
		}
	}

	/**
	 * 使用完后必须调用close()，以释放redis连接
	 */
	public void close() {
		flush();
		Collection<Jedis> jediss = nodeJedisMap.values();
		for (Jedis jedis : jediss) {
			try {
				jedis.close();
			} catch (Exception ex) {
			}
		}
		nodeJedisMap.clear();
	}

	/**
	 * 创建槽点和node的关联
	 * 
	 * @param beginSlot
	 * @param endSlot
	 * @param nodekey
	 * @return
	 */
	private Map<Integer, String> buildSoltAndNodeMap(int beginSlot,
			int endSlot, String nodekey) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (int i = beginSlot; i <= endSlot; i++) {
			map.put(i, nodekey);
		}
		return map;
	}

	@Override
	protected <T> Response<T> getResponse(Builder<T> builder) {
		if(need){
			String nodekey = threadLocal.get();
			Queue<Response<?>> responses = pipelinedResponsesMap.get(nodekey);
			Response<T> lr = new Response<T>(builder);
			if (responses != null) {
				responses.add(lr);
			}
			return lr;
		}
		else{
			return null;
		}
		
	}


	@Override
	public void scanAndDel(String match) {
		for(Jedis jedis: nodeJedisMap.values()){
			{
				String curr = "0";
				try(Pipeline pipeline= jedis.pipelined()){
					do{
						ScanParams params = new ScanParams().match(match).count(5000);
						ScanResult<String> result = jedis.scan(curr, params);
						curr=result.getStringCursor();
						for( String key : result.getResult()){
							System.out.println(" del "+key);
							pipeline.del(key);
						}
						pipeline.syncAndReturnAll();
					}while(!"0".equals(curr));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public void needRetVal(boolean need) {
		this.need =need;
	}


}
