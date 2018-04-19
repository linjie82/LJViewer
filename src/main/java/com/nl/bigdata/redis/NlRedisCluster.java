package com.nl.bigdata.redis;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class NlRedisCluster extends JedisCluster implements IRedisCluster {
	
	
	public static IRedisCluster getCluster(String...nodelist ){
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		for (String n : nodelist) {
			String[] ns =n.split(":");
			nodes.add( new HostAndPort(ns[0], Integer.valueOf(ns[1])));
		}
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		return new NlRedisCluster(nodes,config);
	}
	
	private NlRedisCluster(Set<HostAndPort> nodes,GenericObjectPoolConfig config) {
		 super(nodes,config);
		
	}
	
	
	@Override
	public IRedisClusterPipeline pipeline() {
		return NlRedisClusterPipeline.fromJedisCluster(this);
	}
	
	
	
}
