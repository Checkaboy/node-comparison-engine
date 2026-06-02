package com.checkaboy.comparator.path;

import java.util.Objects;

/**
 * @author Taras Shaptala
 */
public final class DefaultComparisonPath
        implements IComparisonPath {

    public static final DefaultComparisonPath ROOT = new DefaultComparisonPath("");

    private final String value;

    private DefaultComparisonPath(String value) {
        this.value = value;
    }

    @Override
    public IComparisonPath child(String node) {
        Objects.requireNonNull(node, "node must not be null");
        if (node.isEmpty()) {
            throw new IllegalArgumentException("node must not be empty");
        }
        return new DefaultComparisonPath(value.isEmpty() ? node : value + "." + node);
    }

    @Override
    public String asString() {
        return value;
    }

}
