package com.checkaboy.comparison.config;

import java.util.*;

/**
 * Policy definition - declares conditional rules.
 *
 * @author Taras Shaptala
 */
public final class PolicyDefinition {

    private final String policyId;
    private final List<PolicyRule> rules;
    private final Map<String, Object> metadata;

    public PolicyDefinition(
            String policyId,
            List<PolicyRule> rules,
            Map<String, Object> metadata
    ) {
        if (policyId == null || policyId.isEmpty()) {
            throw new IllegalArgumentException("policyId must be non-empty");
        }
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("rules must not be empty");
        }

        this.policyId = policyId;
        // Sort rules by priority (descending)
        List<PolicyRule> sortedRules = new ArrayList<>(rules);
        sortedRules.sort((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()));
        this.rules = Collections.unmodifiableList(sortedRules);
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getPolicyId() {
        return policyId;
    }

    public List<PolicyRule> getRules() {
        return rules;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "PolicyDefinition{" + policyId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyDefinition)) return false;
        PolicyDefinition policy = (PolicyDefinition) o;
        return policyId.equals(policy.policyId);
    }

    @Override
    public int hashCode() {
        return policyId.hashCode();
    }

}
