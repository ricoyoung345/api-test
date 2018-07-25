package com.weibo.api.api_test;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.spy.memcached.MemcachedClient;

public class MCClient {
	public static String HOST = "127.0.0.1";
	public static int PORT = 11211;
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
	public static byte[] getValue(String key) {
		try {
			return (byte[])memcachedClient.get(key);
		} catch (Exception ex) {
			System.out.println("MCClient get failed, " +  ex);
		}

		return null;
	}
}