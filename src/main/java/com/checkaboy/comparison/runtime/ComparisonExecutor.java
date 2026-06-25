package com.checkaboy.comparison.runtime;

import com.checkaboy.structural.path.ComparisonPath;

import java.util.Collections;
import java.util.HashMap;

/**
 * Comparison executor - main entry point for execution.
 *
 * @author Taras Shaptala
 */
public class ComparisonExecutor {

    private final PlanCache planCache;

    public ComparisonExecutor() {
        this(new LocalPlanCache(1000));
    }

    public ComparisonExecutor(PlanCache planCache) {
        this.planCache = planCache;
    }

    /**
     * Execute a comparison.
     */
    public ComparisonResultTree execute(
            CompiledComparisonPlan plan,
            Object sourceObject,
            Object targetObject,
            ResultSink resultSink
    ) {
        // Create immutable execution context
        ExecutionSemantics semantics = plan.getExecutionSemantics();

        ExecutionLimits limits = new ExecutionLimits(
                semantics.getMaxDepth() > 0 ? semantics.getMaxDepth() : Integer.MAX_VALUE,
                semantics.getMaxBreadth() > 0 ? semantics.getMaxBreadth() : Integer.MAX_VALUE,
                semantics.getTimeout() > 0 ? semantics.getTimeout() : Long.MAX_VALUE,
                Integer.MAX_VALUE
        );

        ExecutionContext context = new ExecutionContext(
                ComparisonPath.root(),
                limits,
                semantics,
                resultSink,
                new AccessorResultCache(),
                null,
                new HashMap<>()
        );

        // Create state manager (hidden)
        ExecutionStateManager stateManager = new ExecutionStateManager(semantics);

        try {
            // Execute root node
            CompiledNodePlan rootNodePlan = plan.getRootNodePlan();
            NodeExecutor nodeExecutor = new NodeExecutor();

            nodeExecutor.executeNode(
                    rootNodePlan,
                    sourceObject,
                    targetObject,
                    context,
                    stateManager
            );

            // Get result from sink
            Object result = resultSink.getResult();

            if (result instanceof ComparisonResultTree) {
                return (ComparisonResultTree) result;
            } else {
                // Fallback for streaming sink
                return new ComparisonResultTree(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        ResultStatus.PARTIAL
                );
            }

        } finally {
            // Cleanup
            context.getAccessorCache().clear();
        }
    }

    /**
     * Execute with custom result sink.
     */
    public ComparisonResultTree executeWithMemorySink(
            CompiledComparisonPlan plan,
            Object sourceObject,
            Object targetObject
    ) {
        MemoryResultSink sink = new MemoryResultSink();
        return execute(plan, sourceObject, targetObject, sink);
    }

    /**
     * Get plan from cache or compute.
     */
    public CompiledComparisonPlan getPlan(
            String planFingerprint,
            CompiledComparisonPlan defaultPlan
    ) {
        CompiledComparisonPlan cached = planCache.get(planFingerprint);
        if (cached != null) {
            return cached;
        }

        planCache.put(planFingerprint, defaultPlan);
        return defaultPlan;
    }

}