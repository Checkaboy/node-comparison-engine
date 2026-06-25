package com.checkaboy.comparison.runtime;

/**
 * Execution limits tracking.
 *
 * @author Taras Shaptala
 */
public final class ExecutionLimits {

    private final int depthRemaining;
    private final int breadthRemaining;
    private final long timeRemaining;
    private final int differencesRemaining;

    public ExecutionLimits(
            int depthRemaining,
            int breadthRemaining,
            long timeRemaining,
            int differencesRemaining
    ) {
        this.depthRemaining = depthRemaining;
        this.breadthRemaining = breadthRemaining;
        this.timeRemaining = timeRemaining;
        this.differencesRemaining = differencesRemaining;
    }

    public int getDepthRemaining() {
        return depthRemaining;
    }

    public int getBreadthRemaining() {
        return breadthRemaining;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public int getDifferencesRemaining() {
        return differencesRemaining;
    }

    public ExecutionLimits decrementDepth() {
        return new ExecutionLimits(
                Math.max(0, depthRemaining - 1),
                breadthRemaining,
                timeRemaining,
                differencesRemaining
        );
    }

    public ExecutionLimits decrementDifferences() {
        return new ExecutionLimits(
                depthRemaining,
                breadthRemaining,
                timeRemaining,
                Math.max(0, differencesRemaining - 1)
        );
    }

}