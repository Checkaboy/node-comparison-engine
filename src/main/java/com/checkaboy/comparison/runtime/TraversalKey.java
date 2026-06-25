package com.checkaboy.comparison.runtime;

import java.util.Objects;

/**
 * Traversal key for cycle detection.
 * Identifies a unique (nodeId, sourceObject, targetObject) tuple.
 *
 * @author Taras Shaptala
 */
public final class TraversalKey {

    private final String nodeId;
    private final int sourceHashCode;
    private final int targetHashCode;

    public TraversalKey(String nodeId, Object sourceObject, Object targetObject) {
        this.nodeId = nodeId;
        this.sourceHashCode = System.identityHashCode(sourceObject);
        this.targetHashCode = System.identityHashCode(targetObject);
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getSourceHashCode() {
        return sourceHashCode;
    }

    public int getTargetHashCode() {
        return targetHashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TraversalKey)) return false;
        TraversalKey key = (TraversalKey) o;
        return nodeId.equals(key.nodeId) &&
                sourceHashCode == key.sourceHashCode &&
                targetHashCode == key.targetHashCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, sourceHashCode, targetHashCode);
    }

    @Override
    public String toString() {
        return "TraversalKey{" + nodeId + ":" + sourceHashCode + ":" + targetHashCode + "}";
    }

}