package com.checkaboy.comparison.compile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolution cache for compilation.
 *
 * @author Taras Shaptala
 */
public class ResolutionCache {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public Object get(String key) {
        return cache.get(key);
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

}
