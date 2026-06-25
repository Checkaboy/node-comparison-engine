package com.checkaboy.structural.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Path pattern for matching paths.
 *
 * @author Taras Shaptala
 */
public final class PathPattern {

    private final List<PathNode> nodes;

    public PathPattern(List<PathNode> nodes) {
        this.nodes = Collections.unmodifiableList(
                new ArrayList<>(nodes)
        );
    }

    public List<PathNode> getNodes() {
        return nodes;
    }

    public boolean matches(ComparisonPath path) {
        // Simplified matching - can be extended
        List<String> pathSegments = path.getSegments();
        return pathSegments.size() == nodes.size();
    }

}