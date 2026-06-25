package com.checkaboy.structural.path;

/**
 * Path node for parsed path expressions.
 *
 * @author Taras Shaptala
 */
public final class PathNode {

    private final String segment;
    private final int index;
    private final String key;

    public PathNode(String segment) {
        this(segment, -1, null);
    }

    public PathNode(String segment, int index, String key) {
        this.segment = segment;
        this.index = index;
        this.key = key;
    }

    public String getSegment() {
        return segment;
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public boolean hasIndex() {
        return index >= 0;
    }

    public boolean hasKey() {
        return key != null;
    }

}