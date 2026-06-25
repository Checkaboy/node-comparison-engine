package com.checkaboy.comparison.effective;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Effective comparison definition - configuration + policies applied.
 *
 * @author Taras Shaptal
 */
public final class EffectiveComparisonDefinition {

    private final EffectiveGraphDefinition effectiveGraph;
    private final ExecutionProfile executionProfile;
    private final ShapeProfile shapeProfile;
    private final Map<String, PolicyDecision> policyDecisions;
    private final Map<String, Object> metadata;

    public EffectiveComparisonDefinition(
            EffectiveGraphDefinition effectiveGraph,
            ExecutionProfile executionProfile,
            ShapeProfile shapeProfile,
            Map<String, PolicyDecision> policyDecisions,
            Map<String, Object> metadata
    ) {
        this.effectiveGraph = effectiveGraph;
        this.executionProfile = executionProfile;
        this.shapeProfile = shapeProfile;
        this.policyDecisions = Collections.unmodifiableMap(
                policyDecisions != null ? new HashMap<>(policyDecisions) : new HashMap<>()
        );
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public EffectiveGraphDefinition getEffectiveGraph() {
        return effectiveGraph;
    }

    public ExecutionProfile getExecutionProfile() {
        return executionProfile;
    }

    public ShapeProfile getShapeProfile() {
        return shapeProfile;
    }

    public Map<String, PolicyDecision> getPolicyDecisions() {
        return policyDecisions;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

}