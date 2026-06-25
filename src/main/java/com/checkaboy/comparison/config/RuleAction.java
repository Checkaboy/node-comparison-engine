package com.checkaboy.comparison.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Rule action specification.
 *
 * @author Taras Shaptala
 */
public final class RuleAction {

    private final RuleActionType actionType;
    private final Map<String, Object> parameters;

    public RuleAction(RuleActionType actionType, Map<String, Object> parameters) {
        this.actionType = actionType;
        this.parameters = Collections.unmodifiableMap(
                parameters != null ? new HashMap<>(parameters) : new HashMap<>()
        );
    }

    public RuleActionType getActionType() {
        return actionType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

}