package com.checkaboy.comparison.effective;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Effective graph definition (after policies applied).
 *
 * @author Taras Shaptala
 */
public final class EffectiveGraphDefinition {

    private final String graphId;
    private final Map<String, EffectiveNodeDefinition> nodes;
    private final Map<String, EffectiveEdgeDefinition> edges;
    private final String rootNodeId;
    private final Map<String, Object> metadata;

    public EffectiveGraphDefinition(
            String graphId,
            Map<String, EffectiveNodeDefinition> nodes,
            Map<String, EffectiveEdgeDefinition> edges,
            String rootNodeId,
            Map<String, Object> metadata
    ) {
        this.graphId = graphId;
        this.nodes = Collections.unmodifiableMap(
                nodes != null ? new HashMap<>(nodes) : new HashMap<>()
        );
        this.edges = Collections.unmodifiableMap(
                edges != null ? new HashMap<>(edges) : new HashMap<>()
        );
        this.rootNodeId = rootNodeId;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getGraphId() {
        return graphId;
    }

    public Map<String, EffectiveNodeDefinition> getNodes() {
        return nodes;
    }

    public Map<String, EffectiveEdgeDefinition> getEdges() {
        return edges;
    }

    public String getRootNodeId() {
        return rootNodeId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public EffectiveNodeDefinition getRootNode() {
        return nodes.get(rootNodeId);
    }

}