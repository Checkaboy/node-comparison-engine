package com.checkaboy.comparison.config;

/**
 * Policy rule condition specification.
 *
 * @author Taras Shaptala
 */
public interface RuleCondition {

    /**
     * Evaluate condition.
     */
    boolean evaluate(Object context);

}
