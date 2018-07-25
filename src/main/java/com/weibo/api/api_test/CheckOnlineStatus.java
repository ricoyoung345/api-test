package com.weibo.api.api_test;

import static com.sun.btrace.BTraceUtils.println;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.OnTimer;
import com.sun.btrace.annotations.Return;
import com.sun.btrace.annotations.Self;

@BTrace
public class CheckOnlineStatus {
	@OnMethod(clazz = "cn.vika.memcached.SockIOPool", method = "getConnection", location = @Location(Kind.RETURN))
	@OnTimer(10000)
	public static void traceGetConnection(@Self Object self, String host, @Return Object sockIO) {
		if (BTraceUtils.endsWith(host, "2004")) {
			Field servers = BTraceUtils.field("cn.vika.memcached.SockIOPool", "servers");
			if (((String[]) BTraceUtils.get(servers, self)).length >= 8) {
				println("************************************************\n\n\n\n\n\n");

				println("host:" + host);
				BTraceUtils.printArray((String[]) BTraceUtils.get(servers, self));

				Field usedPool = BTraceUtils.field("cn.vika.memcached.SockIOPool", "usedPool");
				Field socketPool = BTraceUtils.field("cn.vika.memcached.SockIOPool", "socketPool");
				BTraceUtils.printMap((ConcurrentHashMap<String, Object>) BTraceUtils.get(usedPool, self));
				BTraceUtils.printMap((ConcurrentHashMap<String, Object>) BTraceUtils.get(socketPool, self));

				println("\n\n\n\n\n\n************************************************");
			}
		}
	}

	@OnMethod(clazz = "cn.vika.memcached.SockIOPool", method = "createSocket", location = @Location(Kind.RETURN))
	@OnTimer(10000)
	public static void traceCreateSocket(@Self Object self, String host, boolean forceCreate, @Return Object sockIO) {
		if (BTraceUtils.endsWith(host, "2004")) {
			Field servers = BTraceUtils.field("cn.vika.memcached.SockIOPool", "servers");
			if (((String[]) BTraceUtils.get(servers, self)).length >= 8) {
				println("################################################\n\n\n\n\n\n");
				println("createSocket host:" + host + ", forceCreate:" + forceCreate);
				println("\n\n\n\n\n\n################################################");
			}
		}
	}
}