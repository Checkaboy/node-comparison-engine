package com.checkaboy.comparison.spi;

import java.util.Map;

/**
 * Terminal comparison strategy - determines if values match.
 *
 * @author Taras Shaptala
 */
public interface ComparisonStrategy {

    /**
     * Compare two values.
     *
     * @return ExecutionOutcome with MATCH or MISMATCH status
     * @throws StrategyException for fatal errors (framework catches and wraps)
     */
    Object compare(
            Object sourceValue,
            Object targetValue,
            Object executionContext
    ) throws StrategyException;

    /**
     * Get metadata about this strategy.
     */
    Map<String, Object> getMetadata();

}