package com.checkaboy.structural.path;

import java.util.ArrayList;
import java.util.List;

/**
 * Path parser for parsing path expressions.
 *
 * @author Taras Shaptala
 */
public class PathParser {

    public static List<PathNode> parse(String pathExpression) {
        if (pathExpression == null || pathExpression.isEmpty()) {
            throw new IllegalArgumentException("pathExpression must be non-empty");
        }

        List<PathNode> nodes = new ArrayList<>();
        String[] segments = pathExpression.split("\\.");

        for (String segment : segments) {
            // Handle indexed access: "items[0]"
            if (segment.contains("[")) {
                int bracketIndex = segment.indexOf('[');
                String fieldName = segment.substring(0, bracketIndex);
                String indexStr = segment.substring(
                        bracketIndex + 1,
                        segment.indexOf(']')
                );
                try {
                    int index = Integer.parseInt(indexStr);
                    nodes.add(new PathNode(fieldName, index, null));
                } catch (NumberFormatException e) {
                    // Key-based access
                    nodes.add(new PathNode(fieldName, -1, indexStr));
                }
            } else {
                nodes.add(new PathNode(segment));
            }
        }

        return nodes;
    }

}