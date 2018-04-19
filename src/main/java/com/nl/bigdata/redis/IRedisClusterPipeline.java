package com.nl.bigdata.redis;

import java.io.Closeable;

import redis.clients.jedis.BinaryRedisPipeline;
import redis.clients.jedis.RedisPipeline;

public interface IRedisClusterPipeline extends BinaryRedisPipeline, RedisPipeline ,Closeable{
	
	
	public void scanAndDel(String match);
	
	// 将clean 不处理返回值
	public void flush();

	// 处理返回值
	public void sync() ;
	
	public void needRetVal(boolean need);
}
