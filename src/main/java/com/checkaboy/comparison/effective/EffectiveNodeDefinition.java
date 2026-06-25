package com.checkaboy.comparison.effective;

import com.checkaboy.comparison.config.BehaviorReference;
import com.checkaboy.comparison.config.CollectionHandlingMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Effective node definition (after policies applied).
 *
 * @author Taras Shaptala
 */
public final class EffectiveNodeDefinition {

    private final String nodeId;
    private final ResolvedTypeInfo sourceType;
    private final ResolvedTypeInfo targetType;
    private final Object resolvedShape;
    private final BehaviorReference nodeBehavior;
    private final CollectionHandlingMode collectionHandling;
    private final Map<String, Object> metadata;

    public EffectiveNodeDefinition(
            String nodeId,
            ResolvedTypeInfo sourceType,
            ResolvedTypeInfo targetType,
            Object resolvedShape,
            BehaviorReference nodeBehavior,
            CollectionHandlingMode collectionHandling,
            Map<String, Object> metadata
    ) {
        this.nodeId = nodeId;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.resolvedShape = resolvedShape;
        this.nodeBehavior = nodeBehavior;
        this.collectionHandling = collectionHandling;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getNodeId() {
        return nodeId;
    }

    public ResolvedTypeInfo getSourceType() {
        return sourceType;
    }

    public ResolvedTypeInfo getTargetType() {
        return targetType;
    }

    public Object getResolvedShape() {
        return resolvedShape;
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

}