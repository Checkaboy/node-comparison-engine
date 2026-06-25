package com.checkaboy.structural.path;

import java.util.List;

/**
 * Path matcher for evaluating paths at runtime.
 *
 * @author Taras Shaptala
 */
public class PathMatcher {

    private final PathPattern pattern;

    public PathMatcher(String pathExpression) {
        List<PathNode> nodes = PathParser.parse(pathExpression);
        this.pattern = new PathPattern(nodes);
    }

    public boolean matches(ComparisonPath path) {
        return pattern.matches(path);
    }

}