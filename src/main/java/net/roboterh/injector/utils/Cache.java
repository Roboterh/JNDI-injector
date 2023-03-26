package net.roboterh.injector.utils;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

// 这个类直接使用JNDI-Exploit项目的Cache类
public class Cache {
    private Logger logger = LogManager.getLogger(Cache.class);

    private static ExpiringMap<String, byte[]> map = ExpiringMap.builder()
            .maxSize(1000)
            .expiration(30, TimeUnit.SECONDS)
            .variableExpiration()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    static{
        try {
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] get(String key){
        return map.get(key);
    }

    public static void set(String key, byte[] bytes){
        map.put(key, bytes);
    }

    public static boolean contains(String key){
        return map.containsKey(key);
    }

    public static void remove(String key){
        map.remove(key);
    }
}
