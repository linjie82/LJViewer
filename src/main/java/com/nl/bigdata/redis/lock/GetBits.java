package com.nl.bigdata.redis.lock;

import java.util.Arrays;
import java.util.List;

import redis.clients.jedis.Jedis;

public class GetBits {
	
	public static void main(String[] args) {
		Jedis cluster = new Jedis("10.1.2.225");
		String lua = "local key=KEYS[1];local offsets=ARGV;local re={};local len=#offsets;for i=1,len do re[i]=redis.call('getbit',key,offsets[i]) end return re" ;
		String key="aaa";
		cluster.del(key);
		cluster.setbit(key, 3, true);
		cluster.setbit(key, 5, true);
		cluster.setbit(key, 15, true);
		cluster.setbit(key, 7, true);
		List<String> keys = Arrays.asList(key);
		List<String> vv = Arrays.asList("1","2","3","4","5","6","7","15","1212");
		long b = System.nanoTime();
		System.out.println(cluster.eval(lua, keys, vv)); ;
		System.out.println((System.nanoTime()-b)+"ns");
	}

}
