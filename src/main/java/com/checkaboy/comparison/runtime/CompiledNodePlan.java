package com.checkaboy.comparison.runtime;

import java.util.*;

/**
 * Compiled node plan - compiled node execution plan.
 *
 * @author Taras Shaptala
 */
public final class CompiledNodePlan {

    private final String nodeId;
    private final Object nodeDefinition;
    private final Object nodeBehavior;
    private final List<CompiledEdgePlan> edgePlans;
    private final Map<String, Object> metadata;

    public CompiledNodePlan(
            String nodeId,
            Object nodeDefinition,
            Object nodeBehavior,
            List<CompiledEdgePlan> edgePlans,
            Map<String, Object> metadata
    ) {
        this.nodeId = nodeId;
        this.nodeDefinition = nodeDefinition;
        this.nodeBehavior = nodeBehavior;
        this.edgePlans = Collections.unmodifiableList(
                edgePlans != null ? new ArrayList<>(edgePlans) : new ArrayList<>()
        );
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getNodeId() {
        return nodeId;
    }

    public Object getNodeDefinition() {
        return nodeDefinition;
    }

    public Object getNodeBehavior() {
        return nodeBehavior;
    }

    public List<CompiledEdgePlan> getEdgePlans() {
        return edgePlans;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

}
