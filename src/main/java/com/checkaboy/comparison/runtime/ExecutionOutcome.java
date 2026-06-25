package com.checkaboy.comparison.runtime;

import com.checkaboy.structural.path.ComparisonPath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Execution outcome - result of executing a node or edge.
 *
 * @author Taras Shaptala
 */
public final class ExecutionOutcome {

    private final String eventId;
    private final long timestamp;
    private final ExecutionEventType eventType;
    private final ResultStatus status;
    private final Object sourceValue;
    private final Object targetValue;
    private final Object extractedSource;
    private final Object extractedTarget;
    private final Difference difference;
    private final ExecutionError error;
    private final ComparisonPath path;
    private final Map<String, Object> metadata;

    public ExecutionOutcome(
            String eventId,
            long timestamp,
            ExecutionEventType eventType,
            ResultStatus status,
            Object sourceValue,
            Object targetValue,
            Object extractedSource,
            Object extractedTarget,
            Difference difference,
            ExecutionError error,
            ComparisonPath path,
            Map<String, Object> metadata
    ) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.status = status;
        this.sourceValue = sourceValue;
        this.targetValue = targetValue;
        this.extractedSource = extractedSource;
        this.extractedTarget = extractedTarget;
        this.difference = difference;
        this.error = error;
        this.path = path;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getEventId() {
        return eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ExecutionEventType getEventType() {
        return eventType;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public Object getSourceValue() {
        return sourceValue;
    }

    public Object getTargetValue() {
        return targetValue;
    }

    public Object getExtractedSource() {
        return extractedSource;
    }

    public Object getExtractedTarget() {
        return extractedTarget;
    }

    public Difference getDifference() {
        return difference;
    }

    public ExecutionError getError() {
        return error;
    }

    public ComparisonPath getPath() {
        return path;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public boolean isMatch() {
        return status == ResultStatus.MATCH;
    }

    public boolean isMismatch() {
        return status == ResultStatus.MISMATCH;
    }

    public boolean isError() {
        return status == ResultStatus.ERROR;
    }

}