package com.checkaboy.structural.shape;

/**
 * Capabilities of a shape.
 *
 * @author Taras Shaptala
 */
public final class ShapeCapabilities {

    private final boolean ordered;
    private final boolean keyed;
    private final boolean queryable;

    public ShapeCapabilities(boolean ordered, boolean keyed, boolean queryable) {
        this.ordered = ordered;
        this.keyed = keyed;
        this.queryable = queryable;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public boolean isKeyed() {
        return keyed;
    }

    public boolean isQueryable() {
        return queryable;
    }

}