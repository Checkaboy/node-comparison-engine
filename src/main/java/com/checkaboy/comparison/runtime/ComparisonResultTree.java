package com.checkaboy.comparison.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result tree - final result of comparison.
 *
 * @author Taras Shaptala
 */
public final class ComparisonResultTree {

    private final List<ExecutionOutcome> outcomes;
    private final List<Difference> differences;
    private final ResultStatus status;

    public ComparisonResultTree(
            List<ExecutionOutcome> outcomes,
            List<Difference> differences,
            ResultStatus status
    ) {
        this.outcomes = Collections.unmodifiableList(
                outcomes != null ? new ArrayList<>(outcomes) : new ArrayList<>()
        );
        this.differences = Collections.unmodifiableList(
                differences != null ? new ArrayList<>(differences) : new ArrayList<>()
        );
        this.status = status;
    }

    public List<ExecutionOutcome> getOutcomes() {
        return outcomes;
    }

    public List<Difference> getDifferences() {
        return differences;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public boolean isMatch() {
        return status == ResultStatus.MATCH;
    }

    public boolean isMismatch() {
        return status == ResultStatus.MISMATCH;
    }

}