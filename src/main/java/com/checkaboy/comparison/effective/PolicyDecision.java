package com.checkaboy.comparison.effective;

import java.util.*;

/**
 * Policy decision - records which policies affected which components.
 *
 * @author Taras Shaptal
 */
public final class PolicyDecision {

    private final String componentId;
    private final String componentType;
    private final List<String> appliedPolicies;
    private final List<String> ruleMatches;
    private final Map<String, Object> changedFields;
    private final long decisionTime;

    public PolicyDecision(
            String componentId,
            String componentType,
            List<String> appliedPolicies,
            List<String> ruleMatches,
            Map<String, Object> changedFields,
            long decisionTime
    ) {
        this.componentId = componentId;
        this.componentType = componentType;
        this.appliedPolicies = Collections.unmodifiableList(
                appliedPolicies != null ? new ArrayList<>(appliedPolicies) : new ArrayList<>()
        );
        this.ruleMatches = Collections.unmodifiableList(
                ruleMatches != null ? new ArrayList<>(ruleMatches) : new ArrayList<>()
        );
        this.changedFields = Collections.unmodifiableMap(
                changedFields != null ? new HashMap<>(changedFields) : new HashMap<>()
        );
        this.decisionTime = decisionTime;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getComponentType() {
        return componentType;
    }

    public List<String> getAppliedPolicies() {
        return appliedPolicies;
    }

    public List<String> getRuleMatches() {
        return ruleMatches;
    }

    public Map<String, Object> getChangedFields() {
        return changedFields;
    }

    public long getDecisionTime() {
        return decisionTime;
    }

}