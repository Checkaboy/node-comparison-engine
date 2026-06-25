package com.checkaboy.comparison.effective;

import com.checkaboy.comparison.config.BehaviorReference;
import com.checkaboy.comparison.config.StrategyReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Effective edge definition (after policies applied).
 *
 * @author Taras Shaptala
 */
public final class EffectiveEdgeDefinition {

    private final String edgeId;
    private final Object sourceAccessor;
    private final Object targetAccessor;
    private final BehaviorReference behavior;
    private final StrategyReference strategy;
    private final String childNode;
    private final Map<String, Object> metadata;

    public EffectiveEdgeDefinition(
            String edgeId,
            Object sourceAccessor,
            Object targetAccessor,
            BehaviorReference behavior,
            StrategyReference strategy,
            String childNode,
            Map<String, Object> metadata
    ) {
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

    public Object getSourceAccessor() {
        return sourceAccessor;
    }

    public Object getTargetAccessor() {
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

}