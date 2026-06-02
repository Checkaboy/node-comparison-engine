package com.checkaboy.comparator.rule;

/**
 * @author Taras Shaptala
 */
public final class ExcludeFieldRule
        implements IComparisonRule {

    private final String path;

    public ExcludeFieldRule(String path) {
        this.path = IncludeFieldRule.validate(path);
    }

    public String path() {
        return path;
    }

}
