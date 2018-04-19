package com.nl.bigdata.redis;

import java.io.Closeable;

import redis.clients.jedis.BinaryJedisClusterCommands;
import redis.clients.jedis.JedisCommands;

public interface IRedisCluster extends BinaryJedisClusterCommands,JedisCommands,Closeable{
	
	IRedisClusterPipeline pipeline();
}
