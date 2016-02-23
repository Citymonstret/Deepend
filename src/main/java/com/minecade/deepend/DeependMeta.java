package com.minecade.deepend;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Citymonstret
 */
public class DeependMeta {

    private static final Map<String, String> map = new ConcurrentHashMap<>();

    public static boolean hasMeta(String key) {
        return map.containsKey(key);
    }

    public static String getMeta(String key) {
        return map.get(key);
    }

    public static void setMeta(String key, String val) {
        map.put(key, val);
    }

    public static void removeMeta(String key) {
        map.remove(key);
    }

}
