package com.checkaboy.comparison.runtime;

import com.checkaboy.comparison.config.ErrorHandlingMode;
import com.checkaboy.comparison.spi.ExecutionException;
import com.checkaboy.structural.path.ComparisonPath;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Edge executor - executes individual edges.
 *
 * @author Taras Shaptala
 */
public class EdgeExecutor {

    private static final AtomicLong eventIdCounter = new AtomicLong(0);

    /**
     * Execute an edge.
     */
    public ExecutionOutcome executeEdge(
            CompiledEdgePlan edgePlan,
            Object sourceValue,
            Object targetValue,
            ExecutionContext context
    ) {
        String eventId = "edge-" + eventIdCounter.incrementAndGet();
        long timestamp = System.currentTimeMillis();
        ComparisonPath path = context.getCurrentPath();

        try {
            // Extract values using accessors
            Object extractedSource = extractValue(
                    edgePlan.getSourceAccessor(),
                    sourceValue,
                    context
            );
            Object extractedTarget = extractValue(
                    edgePlan.getTargetAccessor(),
                    targetValue,
                    context
            );

            // Invoke behavior with strategy callback
            StrategyCallback strategyCallback = (strategy, src, tgt, ctx) ->
                    invokeStrategy(strategy, src, tgt, ctx);

            // Simplified - would invoke behavior.execute()
            boolean isMatch = Objects.equals(extractedSource, extractedTarget);

            ResultStatus status = isMatch ? ResultStatus.MATCH : ResultStatus.MISMATCH;

            Difference difference = null;
            if (!isMatch) {
                difference = new Difference(
                        path,
                        DifferenceType.VALUE,
                        extractedSource,
                        extractedTarget,
                        "Values do not match"
                );
            }

            ExecutionOutcome outcome = new ExecutionOutcome(
                    eventId,
                    timestamp,
                    ExecutionEventType.EDGE_COMPLETED,
                    status,
                    sourceValue,
                    targetValue,
                    extractedSource,
                    extractedTarget,
                    difference,
                    null,
                    path,
                    new HashMap<>()
            );

            context.getResultSink().reportOutcome(outcome);
            return outcome;

        } catch (Exception e) {
            ExecutionError error = new ExecutionError(
                    "EDGE_EXECUTION_ERROR",
                    e,
                    "EdgeExecutor",
                    edgePlan.getEdgeId(),
                    false,
                    e.getMessage(),
                    Map.of("edge_id", edgePlan.getEdgeId())
            );

            ResultStatus status = context.getExecutionSemantics().getErrorHandling() == ErrorHandlingMode.FAIL_FAST
                    ? ResultStatus.ERROR
                    : ResultStatus.MISMATCH;

            ExecutionOutcome outcome = new ExecutionOutcome(
                    eventId,
                    timestamp,
                    ExecutionEventType.EDGE_ERROR,
                    status,
                    sourceValue,
                    targetValue,
                    null,
                    null,
                    null,
                    error,
                    path,
                    new HashMap<>()
            );

            context.getResultSink().reportOutcome(outcome);

            if (context.getExecutionSemantics().getErrorHandling() == ErrorHandlingMode.FAIL_FAST) {
                throw new ExecutionException(e.getMessage(), e);
            }

            return outcome;
        }
    }

    /**
     * Extract value using compiled accessor.
     */
    private Object extractValue(Object accessor, Object source, ExecutionContext context) {
        if (accessor == null || source == null) {
            return null;
        }

        // Simplified - would invoke accessor.execute(source)
        // In reality, this goes through AccessorResultCache
        return source;
    }

    /**
     * Invoke a strategy.
     */
    private ExecutionOutcome invokeStrategy(
            Object strategy,
            Object sourceValue,
            Object targetValue,
            ExecutionContext context
    ) {
        // Simplified - would invoke strategy.compare()
        boolean isMatch = Objects.equals(sourceValue, targetValue);

        return new ExecutionOutcome(
                "strategy-" + System.nanoTime(),
                System.currentTimeMillis(),
                ExecutionEventType.EDGE_COMPLETED,
                isMatch ? ResultStatus.MATCH : ResultStatus.MISMATCH,
                sourceValue,
                targetValue,
                null,
                null,
                null,
                null,
                context.getCurrentPath(),
                new HashMap<>()
        );
    }

}