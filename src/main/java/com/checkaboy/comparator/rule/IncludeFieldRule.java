package com.checkaboy.comparator.rule;

import java.util.Objects;

/**
 * @author Taras Shaptala
 */
public final class IncludeFieldRule
        implements IComparisonRule {

    private final String path;

    public IncludeFieldRule(String path) {
        this.path = validate(path);
    }

    public String path() {
        return path;
    }

    static String validate(String path) {
        Objects.requireNonNull(path, "path must not be null");
        if (path.isEmpty()) {
            throw new IllegalArgumentException("path must not be empty");
        }
        return path;
    }

}