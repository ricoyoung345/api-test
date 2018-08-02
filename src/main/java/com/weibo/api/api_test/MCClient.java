package com.weibo.api.api_test;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;

public class MCClient {
	public static String HOST = "10.75.3.28";
	public static int PORT = 2021;
	public static int ExpireTime = 3600;
	public static MemcachedClient memcachedClient;

	static {
		try {
			memcachedClient = new MemcachedClient(new InetSocketAddress(HOST, PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 用spymemcached将对象存入缓存
	public static void setValue(String key, byte[] data) {
		try {
			memcachedClient.set(key, ExpireTime, data);
		} catch (Exception ex) {
			System.out.println("MCClient set failed, " +  ex);
		}
	}

	// 用spymemcached从缓存中取得对象
	public static <T> CASValue<T> getValue(String key) {
		try {
			return (CASValue<T>) memcachedClient.gets(key);
		} catch (Exception ex) {
			System.out.println("MCClient get failed, " +  ex);
		}

		return null;
	}
}