package com.checkaboy.comparison.runtime;

/**
 * Plan cache - caches compiled plans by fingerprint.
 *
 * @author Taras Shaptala
 */
public interface PlanCache {

    /**
     * Get cached plan by fingerprint.
     */
    CompiledComparisonPlan get(String fingerprint);

    /**
     * Put plan in cache.
     */
    void put(String fingerprint, CompiledComparisonPlan plan);

    /**
     * Invalidate cached plan.
     */
    void invalidate(String fingerprint);

    /**
     * Clear entire cache.
     */
    void clear();

}
