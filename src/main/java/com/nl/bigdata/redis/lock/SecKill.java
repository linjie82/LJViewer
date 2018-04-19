package com.nl.bigdata.redis.lock;

import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import com.nl.bigdata.redis.IRedisCluster;
import com.nl.bigdata.redis.NlRedisCluster;

public class SecKill {
	
	static String secKill = "local key=KEYS[1];if redis.call('get',key)>0  then  redis.call('decr',key) return 1 end  return 0";
	
	
	public static void main(String[] args) throws InterruptedException {
		
		final JedisCluster cluster = new JedisCluster(new HostAndPort("10.1.8.1", 6380));
		cluster.set("a","5");
		 cluster.eval(secKill, 1, "a");
//		final CountDownLatch countDownLatch = new CountDownLatch(50);
//		for(int i=0;i<50;i++){
//			Thread thread = new Thread(){
//				
//				public void run(){
//					try {
//						countDownLatch.await();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					Integer r = (Integer) cluster.eval(secKill, 1, "a");
//					if(r==1){
//						System.out.println("get");
//					}else{
//						System.err.println("unget");
//					}
//				}
//			};
//			thread.start();
//			countDownLatch.countDown();
//		}

		
		System.out.println(cluster.get("a"));
	}

}
