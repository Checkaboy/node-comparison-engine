package com.checkaboy.comparison.config;

import com.checkaboy.structural.accessor.AccessorReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Edge definition - specifies extraction and comparison step.
 *
 * @author Taras Shaptala
 */
public final class ComparisonEdgeDefinition {

    private final String edgeId;
    private final AccessorReference sourceAccessor;
    private final AccessorReference targetAccessor;
    private final BehaviorReference behavior;
    private final StrategyReference strategy;
    private final String childNode;
    private final Map<String, Object> metadata;

    public ComparisonEdgeDefinition(
            String edgeId,
            AccessorReference sourceAccessor,
            AccessorReference targetAccessor,
            BehaviorReference behavior,
            StrategyReference strategy,
            String childNode,
            Map<String, Object> metadata
    ) {
        if (edgeId == null || edgeId.isEmpty()) {
            throw new IllegalArgumentException("edgeId must be non-empty");
        }
        if (sourceAccessor == null) {
            throw new IllegalArgumentException("sourceAccessor must not be null");
        }
        if (targetAccessor == null) {
            throw new IllegalArgumentException("targetAccessor must not be null");
        }
        if (behavior == null) {
            throw new IllegalArgumentException("behavior must not be null");
        }
        if (strategy == null) {
            throw new IllegalArgumentException("strategy must not be null");
        }

        this.edgeId = edgeId;
        this.sourceAccessor = sourceAccessor;
        this.targetAccessor = targetAccessor;
        this.behavior = behavior;
        this.strategy = strategy;
        this.childNode = childNode;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getEdgeId() {
        return edgeId;
    }

    public AccessorReference getSourceAccessor() {
        return sourceAccessor;
    }

    public AccessorReference getTargetAccessor() {
        return targetAccessor;
    }

    public BehaviorReference getBehavior() {
        return behavior;
    }

    public StrategyReference getStrategy() {
        return strategy;
    }

    public String getChildNode() {
        return childNode;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "ComparisonEdgeDefinition{" + edgeId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparisonEdgeDefinition)) return false;
        ComparisonEdgeDefinition edge = (ComparisonEdgeDefinition) o;
        return edgeId.equals(edge.edgeId);
    }

    @Override
    public int hashCode() {
        return edgeId.hashCode();
    }

}