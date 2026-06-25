package com.checkaboy.comparison.runtime;

/**
 * Strategy callback for invoking strategies from behaviors.
 *
 * @author Taras Shaptala
 */
public interface StrategyCallback {

    /**
     * Invoke a comparison strategy.
     */
    ExecutionOutcome invokeStrategy(
            Object strategy,
            Object sourceValue,
            Object targetValue,
            ExecutionContext context
    );

}