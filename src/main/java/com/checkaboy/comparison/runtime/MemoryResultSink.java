package com.checkaboy.comparison.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Memory-based result sink - buffers all results.
 *
 * @author Taras Shaptala
 */
public class MemoryResultSink
        implements ResultSink {

    private final List<ExecutionOutcome> outcomes = new ArrayList<>();
    private final List<Difference> differences = new ArrayList<>();
    private long executionStartTime = System.currentTimeMillis();

    @Override
    public void reportOutcome(ExecutionOutcome outcome) {
        outcomes.add(outcome);
        if (outcome.getDifference() != null) {
            differences.add(outcome.getDifference());
        }
    }

    @Override
    public Object getResult() {
        return new ComparisonResultTree(
                outcomes,
                differences,
                determineStatus()
        );
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_outcomes", outcomes.size());
        stats.put("total_differences", differences.size());
        stats.put("execution_time_ms", System.currentTimeMillis() - executionStartTime);
        return stats;
    }

    private ResultStatus determineStatus() {
        if (differences.isEmpty()) {
            return ResultStatus.MATCH;
        } else {
            return ResultStatus.MISMATCH;
        }
    }

    public List<ExecutionOutcome> getOutcomes() {
        return outcomes;
    }

    public List<Difference> getDifferences() {
        return differences;
    }

}