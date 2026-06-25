package com.checkaboy.comparison.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Policy rule specification.
 *
 * @author Taras Shaptala
 */
public final class PolicyRule {

    private final String ruleId;
    private final RuleCondition condition;
    private final List<RuleAction> actions;
    private final int priority;

    public PolicyRule(
            String ruleId,
            RuleCondition condition,
            List<RuleAction> actions,
            int priority
    ) {
        if (ruleId == null || ruleId.isEmpty()) {
            throw new IllegalArgumentException("ruleId must be non-empty");
        }
        if (condition == null) {
            throw new IllegalArgumentException("condition must not be null");
        }
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException("actions must not be empty");
        }

        this.ruleId = ruleId;
        this.condition = condition;
        this.actions = Collections.unmodifiableList(new ArrayList<>(actions));
        this.priority = priority;
    }

    public String getRuleId() {
        return ruleId;
    }

    public RuleCondition getCondition() {
        return condition;
    }

    public List<RuleAction> getActions() {
        return actions;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "PolicyRule{" + ruleId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyRule)) return false;
        PolicyRule rule = (PolicyRule) o;
        return ruleId.equals(rule.ruleId);
    }

    @Override
    public int hashCode() {
        return ruleId.hashCode();
    }

}