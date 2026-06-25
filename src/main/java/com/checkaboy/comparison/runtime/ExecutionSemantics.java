package com.checkaboy.comparison.runtime;

import com.checkaboy.comparison.config.CycleHandlingMode;
import com.checkaboy.comparison.config.ErrorHandlingMode;
import com.checkaboy.comparison.config.ExecutionMode;
import com.checkaboy.comparison.config.ResultPolicy;

/**
 * Execution semantics - configuration controlling execution behavior.
 * Immutable at runtime.
 *
 * @author Taras Shaptala
 */
public final class ExecutionSemantics {

    private final ExecutionMode executionMode;
    private final long timeout;
    private final int maxDepth;
    private final int maxBreadth;
    private final ErrorHandlingMode errorHandling;
    private final CycleHandlingMode cycleHandling;
    private final ResultPolicy resultPolicy;

    public ExecutionSemantics(
            ExecutionMode executionMode,
            long timeout,
            int maxDepth,
            int maxBreadth,
            ErrorHandlingMode errorHandling,
            CycleHandlingMode cycleHandling,
            ResultPolicy resultPolicy
    ) {
        this.executionMode = executionMode;
        this.timeout = timeout;
        this.maxDepth = maxDepth;
        this.maxBreadth = maxBreadth;
        this.errorHandling = errorHandling;
        this.cycleHandling = cycleHandling;
        this.resultPolicy = resultPolicy;
    }

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public long getTimeout() {
        return timeout;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxBreadth() {
        return maxBreadth;
    }

    public ErrorHandlingMode getErrorHandling() {
        return errorHandling;
    }

    public CycleHandlingMode getCycleHandling() {
        return cycleHandling;
    }

    public ResultPolicy getResultPolicy() {
        return resultPolicy;
    }

}