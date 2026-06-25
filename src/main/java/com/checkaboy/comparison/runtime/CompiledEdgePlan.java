package com.checkaboy.comparison.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Compiled edge plan - compiled edge execution plan.
 *
 * @author Taras Shaptala
 */
public final class CompiledEdgePlan {

    private final String edgeId;
    private final Object sourceAccessor;
    private final Object targetAccessor;
    private final Object behavior;
    private final Object strategy;
    private final String childNodeId;
    private final Map<String, Object> metadata;

    public CompiledEdgePlan(
            String edgeId,
            Object sourceAccessor,
            Object targetAccessor,
            Object behavior,
            Object strategy,
            String childNodeId,
            Map<String, Object> metadata
    ) {
        this.edgeId = edgeId;
        this.sourceAccessor = sourceAccessor;
        this.targetAccessor = targetAccessor;
        this.behavior = behavior;
        this.strategy = strategy;
        this.childNodeId = childNodeId;
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

    public Object getBehavior() {
        return behavior;
    }

    public Object getStrategy() {
        return strategy;
    }

    public String getChildNodeId() {
        return childNodeId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public boolean hasChildNode() {
        return childNodeId != null;
    }

}