package com.checkaboy.comparison.effective;

import com.checkaboy.comparison.config.CycleHandlingMode;
import com.checkaboy.comparison.config.ErrorHandlingMode;

/**
 * Execution profile - carries execution configuration through compilation.
 *
 * @author Taras Shaptal
 */
public final class ExecutionProfile {

    private final Object executionSemantics;
    private final Object executionLimits;
    private final ErrorHandlingMode errorHandling;
    private final CycleHandlingMode cycleHandling;
    private final boolean streamingEnabled;

    public ExecutionProfile(
            Object executionSemantics,
            Object executionLimits,
            ErrorHandlingMode errorHandling,
            CycleHandlingMode cycleHandling,
            boolean streamingEnabled
    ) {
        this.executionSemantics = executionSemantics;
        this.executionLimits = executionLimits;
        this.errorHandling = errorHandling;
        this.cycleHandling = cycleHandling;
        this.streamingEnabled = streamingEnabled;
    }

    public Object getExecutionSemantics() {
        return executionSemantics;
    }

    public Object getExecutionLimits() {
        return executionLimits;
    }

    public ErrorHandlingMode getErrorHandling() {
        return errorHandling;
    }

    public CycleHandlingMode getCycleHandling() {
        return cycleHandling;
    }

    public boolean isStreamingEnabled() {
        return streamingEnabled;
    }

}