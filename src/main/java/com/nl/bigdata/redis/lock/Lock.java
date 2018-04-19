package com.nl.bigdata.redis.lock;

import redis.clients.jedis.Jedis;

public class Lock {
	
	public static void main(String[] args) throws InterruptedException {
//		IRedisCluster 
//			cluster = NlRedisCluster.getCluster("10.1.8.1:6380");
		Jedis cluster = new Jedis("10.1.2.225");
		String lua = "local key=KEYS[1];local time=ARGV[1]; if redis.call('setnx',key,'a')==1 then  redis.call('expire',key,time) return 1 end  return 0";
		System.out.println(cluster.eval(lua, 1, "ssss","5"));
		System.out.println(cluster.setnx("ssss", "a"));
		System.out.println(cluster.eval(lua, 1, "ssss","5"));
		Thread.sleep(5500);
		System.out.println(cluster.eval(lua, 1, "ssss","5"));
	}

}
