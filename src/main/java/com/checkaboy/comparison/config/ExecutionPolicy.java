package com.checkaboy.comparison.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Execution policy - configures execution behavior.
 *
 * @author Taras Shaptala
 */
public final class ExecutionPolicy {

    private final ExecutionMode executionMode;
    private final Long timeout;
    private final Integer maxDepth;
    private final Integer maxBreadth;
    private final ErrorHandlingMode errorHandling;
    private final CycleHandlingMode cycleHandling;
    private final Boolean streamingEnabled;
    private final Integer maxDifferences;
    private final Map<String, Object> metadata;

    public ExecutionPolicy(
            ExecutionMode executionMode,
            Long timeout,
            Integer maxDepth,
            Integer maxBreadth,
            ErrorHandlingMode errorHandling,
            CycleHandlingMode cycleHandling,
            Boolean streamingEnabled,
            Integer maxDifferences,
            Map<String, Object> metadata
    ) {
        this.executionMode = executionMode != null ? executionMode : ExecutionMode.COLLECT_ALL;
        this.timeout = timeout;
        this.maxDepth = maxDepth;
        this.maxBreadth = maxBreadth;
        this.errorHandling = errorHandling != null ? errorHandling : ErrorHandlingMode.FAIL_FAST;
        this.cycleHandling = cycleHandling != null ? cycleHandling : CycleHandlingMode.SKIP;
        this.streamingEnabled = streamingEnabled != null ? streamingEnabled : false;
        this.maxDifferences = maxDifferences;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public Integer getMaxBreadth() {
        return maxBreadth;
    }

    public ErrorHandlingMode getErrorHandling() {
        return errorHandling;
    }

    public CycleHandlingMode getCycleHandling() {
        return cycleHandling;
    }

    public Boolean isStreamingEnabled() {
        return streamingEnabled;
    }

    public Integer getMaxDifferences() {
        return maxDifferences;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Builder for ExecutionPolicy.
     */
    public static class Builder {

        private ExecutionMode executionMode = ExecutionMode.COLLECT_ALL;
        private Long timeout;
        private Integer maxDepth;
        private Integer maxBreadth;
        private ErrorHandlingMode errorHandling = ErrorHandlingMode.FAIL_FAST;
        private CycleHandlingMode cycleHandling = CycleHandlingMode.SKIP;
        private Boolean streamingEnabled = false;
        private Integer maxDifferences;
        private Map<String, Object> metadata = new HashMap<>();

        public Builder withExecutionMode(ExecutionMode mode) {
            this.executionMode = mode;
            return this;
        }

        public Builder withTimeout(long ms) {
            this.timeout = ms;
            return this;
        }

        public Builder withMaxDepth(int depth) {
            this.maxDepth = depth;
            return this;
        }

        public Builder withMaxBreadth(int breadth) {
            this.maxBreadth = breadth;
            return this;
        }

        public Builder withErrorHandling(ErrorHandlingMode mode) {
            this.errorHandling = mode;
            return this;
        }

        public Builder withCycleHandling(CycleHandlingMode mode) {
            this.cycleHandling = mode;
            return this;
        }

        public Builder withStreamingEnabled(boolean enabled) {
            this.streamingEnabled = enabled;
            return this;
        }

        public Builder withMaxDifferences(int max) {
            this.maxDifferences = max;
            return this;
        }

        public ExecutionPolicy build() {
            return new ExecutionPolicy(
                    executionMode,
                    timeout,
                    maxDepth,
                    maxBreadth,
                    errorHandling,
                    cycleHandling,
                    streamingEnabled,
                    maxDifferences,
                    metadata
            );
        }

    }

}
