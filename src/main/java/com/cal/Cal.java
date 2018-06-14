package com.cal;

import com.nl.bigdata.redis.IRedisCluster;
import com.nl.bigdata.redis.IRedisClusterPipeline;
import com.nl.bigdata.redis.NlRedisCluster;

public class Cal {
	
	public static void main(String[] args) {
//		//100 5 77 92 22 22 29 87 50 46 99 90
//		int v=100;
//		int leftv = 100;
//		int maxm = 0;
//		int[] ivs = new int[]{77,22,29,50,99};
//		int[] ims = new int[]{92,22,87,46,90};
		
		IRedisCluster cluster= NlRedisCluster.getCluster("10.1.8.1:6380");
//		try (IRedisClusterPipeline pipeline =cluster.pipeline()){
//			for (int i = 0; i < 1000000; i++) {
//				pipeline.sadd("data", (i+1000000)+"");
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		System.out.println(cluster.scard("data"));
		for (int i = 0; i < 1000000; i++) {
			long b = System.currentTimeMillis();
			if(cluster.scard("data")<1400000){
				cluster.sadd("data", (i+1000000)+"");
			}
			long dd = System.currentTimeMillis()-b;
			if(dd>10){
				System.err.println(dd+"ms");
			}
		}
		
		System.out.println(cluster.sismember("data", "1000020"));
		;
		
	}

}
