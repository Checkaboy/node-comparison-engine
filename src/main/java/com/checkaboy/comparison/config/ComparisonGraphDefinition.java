package com.checkaboy.comparison.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Graph definition - complete comparison graph specification.
 *
 * @author Taras Shaptala
 */
public final class ComparisonGraphDefinition {

    private final String graphId;
    private final Map<String, ComparisonNodeDefinition> nodes;
    private final Map<String, ComparisonEdgeDefinition> edges;
    private final String rootNodeId;
    private final String schemaVersion;
    private final Map<String, Object> metadata;

    public ComparisonGraphDefinition(
            String graphId,
            Map<String, ComparisonNodeDefinition> nodes,
            Map<String, ComparisonEdgeDefinition> edges,
            String rootNodeId,
            String schemaVersion,
            Map<String, Object> metadata
    ) {
        if (graphId == null || graphId.isEmpty()) {
            throw new IllegalArgumentException("graphId must be non-empty");
        }
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes must not be empty");
        }
        if (rootNodeId == null || !nodes.containsKey(rootNodeId)) {
            throw new IllegalArgumentException("rootNodeId must reference existing node");
        }

        this.graphId = graphId;
        this.nodes = Collections.unmodifiableMap(new HashMap<>(nodes));
        this.edges = Collections.unmodifiableMap(new HashMap<>(edges != null ? edges : new HashMap<>()));
        this.rootNodeId = rootNodeId;
        this.schemaVersion = schemaVersion != null ? schemaVersion : "1.0";
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getGraphId() {
        return graphId;
    }

    public Map<String, ComparisonNodeDefinition> getNodes() {
        return nodes;
    }

    public Map<String, ComparisonEdgeDefinition> getEdges() {
        return edges;
    }

    public String getRootNodeId() {
        return rootNodeId;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public ComparisonNodeDefinition getRootNode() {
        return nodes.get(rootNodeId);
    }

    @Override
    public String toString() {
        return "ComparisonGraphDefinition{" + graphId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparisonGraphDefinition)) return false;
        ComparisonGraphDefinition graph = (ComparisonGraphDefinition) o;
        return graphId.equals(graph.graphId);
    }

    @Override
    public int hashCode() {
        return graphId.hashCode();
    }

}