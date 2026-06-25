package com.checkaboy.comparison.runtime;

import com.checkaboy.structural.path.ComparisonPath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable execution context.
 * Passed to all executors.
 *
 * @author Taras Shaptala
 */
public final class ExecutionContext {

    private final ComparisonPath currentPath;
    private final ExecutionLimits remainingLimits;
    private final ExecutionSemantics executionSemantics;
    private final ResultSink resultSink;
    private final AccessorResultCache accessorCache;
    private final Object shapeResolver;
    private final Map<String, Object> metadata;
    private final long startTime;

    public ExecutionContext(
            ComparisonPath currentPath,
            ExecutionLimits remainingLimits,
            ExecutionSemantics executionSemantics,
            ResultSink resultSink,
            AccessorResultCache accessorCache,
            Object shapeResolver,
            Map<String, Object> metadata
    ) {
        this.currentPath = currentPath;
        this.remainingLimits = remainingLimits;
        this.executionSemantics = executionSemantics;
        this.resultSink = resultSink;
        this.accessorCache = accessorCache;
        this.shapeResolver = shapeResolver;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
        this.startTime = System.currentTimeMillis();
    }

    public ComparisonPath getCurrentPath() {
        return currentPath;
    }

    public ExecutionLimits getRemainingLimits() {
        return remainingLimits;
    }

    public ExecutionSemantics getExecutionSemantics() {
        return executionSemantics;
    }

    public ResultSink getResultSink() {
        return resultSink;
    }

    public AccessorResultCache getAccessorCache() {
        return accessorCache;
    }

    public Object getShapeResolver() {
        return shapeResolver;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * Create new context with updated path.
     */
    public ExecutionContext withPath(ComparisonPath newPath) {
        return new ExecutionContext(
                newPath,
                remainingLimits,
                executionSemantics,
                resultSink,
                accessorCache,
                shapeResolver,
                new HashMap<>(metadata)
        );
    }

    /**
     * Create new context with updated limits.
     */
    public ExecutionContext withLimits(ExecutionLimits newLimits) {
        return new ExecutionContext(
                currentPath,
                newLimits,
                executionSemantics,
                resultSink,
                accessorCache,
                shapeResolver,
                new HashMap<>(metadata)
        );
    }

}