package demo;

import java.net.URI;
import java.net.URISyntaxException;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import com.nl.bigdata.redis.IRedisCluster;
import com.nl.bigdata.redis.IRedisClusterPipeline;
import com.nl.bigdata.redis.NlRedisCluster;


public class Demo {
	
 	private static String getFileFromUri(URI uri){
 		String path = uri.getPath();
 		if(path==null || "".equals(path.trim()) || "/".equals(path.trim())){
 			return null;
 		}
 		String[] sps = path.split("/");
 		return sps[sps.length-1];
 	}
	//批量加载redis的demo
	public static void main(String[] args) {
		
		String url ="http://11223:333/asd.ad";
		try {
			URI uri = new URI(url);
			
			String f = getFileFromUri(uri);
			if(f!=null){
				System.out.println(f.split("\\.")[1]); ;
			}
		} catch (URISyntaxException e) {
		}
		
		//集群的配置    host1:port1,host2:port2...
		//不用写全所有的集群中的主机，只要有一个通就可以
//		String redishosts="10.1.8.1:6380";
//		IRedisCluster cluster = NlRedisCluster.getCluster(redishosts); 
//		
//		//加载数据到redis 
//		//创建 一个pipeline  ★★★★★ 一次加载任务 ，只要创建一次
//		IRedisClusterPipeline clusterPipeline= cluster.pipeline();
//		long b=System.currentTimeMillis();
//		clusterPipeline.scanAndDel("130*");
//		System.err.println(System.currentTimeMillis()-b+"ms");
//		try{
//			//批量加载
//			for(int i=0;i<30000000;i++){
//				//这里是set key-value ， 
//				clusterPipeline.set("key"+i, ""+i);
//				//设置单个field设置 hash
//				//clusterPipeline.hset(key, field, value);
//				//一次性设置hash
//				//clusterPipeline.hmset(key, Map)
//				
//				if(i%100000==0){
//					System.err.println("完成  "+i);
//					//加载一定的数据后 执行一次， 以防止oom
//					clusterPipeline.flush();
//				}
//			}
//		} finally {
//			// ★★★★★使用完 pipeline 必须关闭，否则会造成一直占用 redis 连接
//			try {
//				clusterPipeline.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		try {
//			cluster.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	public static void getValueDemo(){
		//IRedisCluster cluster = NlRedisCluster.getCluster(redishosts);
		//可以用原生的 
		JedisCluster cluster = new JedisCluster(new HostAndPort("10.1.8.1", 6380));
		//k-v  获取
		cluster.get("key1");
		//获取单个field
	//	cluster.hget(key, field)
		//获取多个field
	//	cluster.hmget(key, field1,field2,...)
		//获取整个hash
	//	cluster.hgetAll(key)
	}
}
