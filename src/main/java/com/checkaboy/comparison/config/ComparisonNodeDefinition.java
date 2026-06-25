package com.checkaboy.comparison.config;

import com.checkaboy.structural.shape.ShapeReference;
import com.checkaboy.structural.type.TypeReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Node definition - specifies a node in comparison graph.
 *
 * @author Taras Shaptala
 */
public final class ComparisonNodeDefinition {

    private final String nodeId;
    private final TypeReference sourceType;
    private final TypeReference targetType;
    private final ShapeReference shape;
    private final BehaviorReference nodeBehavior;
    private final CollectionHandlingMode collectionHandling;
    private final Map<String, Object> metadata;

    public ComparisonNodeDefinition(
            String nodeId,
            TypeReference sourceType,
            TypeReference targetType,
            ShapeReference shape,
            BehaviorReference nodeBehavior,
            CollectionHandlingMode collectionHandling,
            Map<String, Object> metadata
    ) {
        if (nodeId == null || nodeId.isEmpty()) {
            throw new IllegalArgumentException("nodeId must be non-empty");
        }
        if (sourceType == null) {
            throw new IllegalArgumentException("sourceType must not be null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }
        if (shape == null) {
            throw new IllegalArgumentException("shape must not be null");
        }

        this.nodeId = nodeId;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.shape = shape;
        this.nodeBehavior = nodeBehavior;
        this.collectionHandling = collectionHandling;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getNodeId() {
        return nodeId;
    }

    public TypeReference getSourceType() {
        return sourceType;
    }

    public TypeReference getTargetType() {
        return targetType;
    }

    public ShapeReference getShape() {
        return shape;
    }

    public BehaviorReference getNodeBehavior() {
        return nodeBehavior;
    }

    public CollectionHandlingMode getCollectionHandling() {
        return collectionHandling;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "ComparisonNodeDefinition{" + nodeId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparisonNodeDefinition)) return false;
        ComparisonNodeDefinition node = (ComparisonNodeDefinition) o;
        return nodeId.equals(node.nodeId);
    }

    @Override
    public int hashCode() {
        return nodeId.hashCode();
    }

}