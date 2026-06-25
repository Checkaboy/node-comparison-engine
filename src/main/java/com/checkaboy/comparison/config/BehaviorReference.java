package com.checkaboy.comparison.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference to a behavior implementation.
 *
 * @author Taras Shaptala
 */
public final class BehaviorReference {

    private final String behaviorId;
    private final Map<String, Object> parameters;
    private final Map<String, Object> metadata;

    public BehaviorReference(
            String behaviorId,
            Map<String, Object> parameters,
            Map<String, Object> metadata
    ) {
        if (behaviorId == null || behaviorId.isEmpty()) {
            throw new IllegalArgumentException("behaviorId must be non-empty");
        }
        this.behaviorId = behaviorId;
        this.parameters = Collections.unmodifiableMap(
                parameters != null ? new HashMap<>(parameters) : new HashMap<>()
        );
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getBehaviorId() {
        return behaviorId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "BehaviorReference{" + behaviorId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BehaviorReference)) return false;
        BehaviorReference ref = (BehaviorReference) o;
        return behaviorId.equals(ref.behaviorId);
    }

    @Override
    public int hashCode() {
        return behaviorId.hashCode();
    }

}