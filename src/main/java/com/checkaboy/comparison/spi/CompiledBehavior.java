package com.checkaboy.comparison.spi;

/**
 * Compiled behavior - result of BehaviorProvider.compile().
 * Can be node behavior (orchestrates edges) or edge behavior.
 *
 * @author Taras Shaptala
 */
public interface CompiledBehavior {

    /**
     * For node behavior: orchestrate edges within a node.
     * For edge behavior: execute edge comparison.
     */
    Object execute(
            Object sourceValue,
            Object targetValue,
            Object executionContext,
            Object callback
    ) throws ExecutionException;

}
