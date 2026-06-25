package com.checkaboy.comparison.runtime;

/**
 * Edge callback for invoking edges from behaviors.
 *
 * @author Taras Shaptala
 */
public interface EdgeCallback {

    /**
     * Invoke an edge execution.
     */
    ExecutionOutcome invokeEdge(
            CompiledEdgePlan edgePlan,
            Object sourceValue,
            Object targetValue,
            ExecutionContext context
    );

}