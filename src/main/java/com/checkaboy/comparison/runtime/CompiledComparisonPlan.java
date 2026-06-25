package com.checkaboy.comparison.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Compiled comparison plan - ready for execution.
 *
 * @author Taras Shaptala
 */
public final class CompiledComparisonPlan {

    private final String planId;
    private final PlanIdentity planIdentity;
    private final CompiledNodePlan rootNodePlan;
    private final Map<String, CompiledNodePlan> nodePlansByNodeId;
    private final ExecutionSemantics executionSemantics;
    private final Map<String, Object> metadata;

    public CompiledComparisonPlan(
            String planId,
            PlanIdentity planIdentity,
            CompiledNodePlan rootNodePlan,
            Map<String, CompiledNodePlan> nodePlansByNodeId,
            ExecutionSemantics executionSemantics,
            Map<String, Object> metadata
    ) {
        this.planId = planId;
        this.planIdentity = planIdentity;
        this.rootNodePlan = rootNodePlan;
        this.nodePlansByNodeId = Collections.unmodifiableMap(
                nodePlansByNodeId != null ? new HashMap<>(nodePlansByNodeId) : new HashMap<>()
        );
        this.executionSemantics = executionSemantics;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getPlanId() {
        return planId;
    }

    public PlanIdentity getPlanIdentity() {
        return planIdentity;
    }

    public CompiledNodePlan getRootNodePlan() {
        return rootNodePlan;
    }

    public CompiledNodePlan getNodePlan(String nodeId) {
        return nodePlansByNodeId.get(nodeId);
    }

    public Map<String, CompiledNodePlan> getNodePlans() {
        return nodePlansByNodeId;
    }

    public ExecutionSemantics getExecutionSemantics() {
        return executionSemantics;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

}