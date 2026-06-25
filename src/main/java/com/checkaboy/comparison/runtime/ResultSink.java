package com.checkaboy.comparison.runtime;

import java.util.Map;

/**
 * Result sink - collects or streams results.
 *
 * @author Taras Shaptala
 */
public interface ResultSink {

    /**
     * Report an execution outcome.
     */
    void reportOutcome(ExecutionOutcome outcome);

    /**
     * Get final result (for memory-based sink).
     * Streaming sink may not support this.
     */
    Object getResult();

    /**
     * Get execution statistics.
     */
    Map<String, Object> getStatistics();

}