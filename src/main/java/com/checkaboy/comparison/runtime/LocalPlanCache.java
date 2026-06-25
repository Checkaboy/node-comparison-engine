package com.checkaboy.comparison.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Local plan cache implementation (in-memory, LRU eviction).
 *
 * @author Taras Shaptala
 */
public class LocalPlanCache
        implements PlanCache {

    private final Map<String, CompiledComparisonPlan> cache;
    private final int maxSize;

    public LocalPlanCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public CompiledComparisonPlan get(String fingerprint) {
        return cache.get(fingerprint);
    }

    @Override
    public void put(String fingerprint, CompiledComparisonPlan plan) {
        cache.put(fingerprint, plan);
    }

    @Override
    public void invalidate(String fingerprint) {
        cache.remove(fingerprint);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public int getSize() {
        return cache.size();
    }

}