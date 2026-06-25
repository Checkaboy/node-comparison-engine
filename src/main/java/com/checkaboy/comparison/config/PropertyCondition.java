package com.checkaboy.comparison.config;

/**
 * Simple property-based rule condition.
 *
 * @author Taras Shaptala
 */
public final class PropertyCondition
        implements RuleCondition {

    private final String propertyPath;
    private final String operator;
    private final Object expectedValue;

    public PropertyCondition(String propertyPath, String operator, Object expectedValue) {
        this.propertyPath = propertyPath;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean evaluate(Object context) {
        // Simplified evaluation
        return true;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public String getOperator() {
        return operator;
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

}