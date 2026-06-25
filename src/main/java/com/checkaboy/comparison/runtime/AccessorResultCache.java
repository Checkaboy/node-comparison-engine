package com.checkaboy.comparison.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Accessor result cache - caches extracted values during execution.
 *
 * @author Taras Shaptala
 */
public final class AccessorResultCache {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public Object getCachedOrCompute(Object accessor, Object source) {
        String key = System.identityHashCode(accessor) + ":" +
                System.identityHashCode(source);
        return cache.computeIfAbsent(key, k -> {
            try {
                // Simplified - would invoke accessor.execute(source)
                return source;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.clear();
    }

}