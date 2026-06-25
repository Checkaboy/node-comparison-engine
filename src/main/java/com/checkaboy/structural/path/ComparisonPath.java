package com.checkaboy.structural.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a path through the comparison tree.
 * Example: "root.order.customer.name"
 *
 * @author Taras Shaptala
 */
public final class ComparisonPath {

    private final List<String> segments;
    private final String stringRepresentation;

    private ComparisonPath(List<String> segments) {
        this.segments = Collections.unmodifiableList(
                new ArrayList<>(segments)
        );
        this.stringRepresentation = String.join(".", this.segments);
    }

    /**
     * Get root path.
     */
    public static ComparisonPath root() {
        return new ComparisonPath(Collections.singletonList("root"));
    }

    /**
     * Append a segment to this path.
     */
    public ComparisonPath append(String segment) {
        if (segment == null || segment.isEmpty()) {
            throw new IllegalArgumentException("segment must be non-empty");
        }
        List<String> newSegments = new ArrayList<>(segments);
        newSegments.add(segment);
        return new ComparisonPath(newSegments);
    }

    /**
     * Append an indexed segment (e.g., "items[0]").
     */
    public ComparisonPath appendIndexed(String fieldName, int index) {
        return append(fieldName + "[" + index + "]");
    }

    /**
     * Remove last segment.
     */
    public ComparisonPath removeLast() {
        if (segments.size() <= 1) {
            return root();
        }
        List<String> newSegments = new ArrayList<>(segments);
        newSegments.remove(newSegments.size() - 1);
        return new ComparisonPath(newSegments);
    }

    /**
     * Get all segments.
     */
    public List<String> getSegments() {
        return segments;
    }

    /**
     * Get last segment.
     */
    public String getLastSegment() {
        return segments.get(segments.size() - 1);
    }

    /**
     * Get depth (number of segments).
     */
    public int getDepth() {
        return segments.size();
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparisonPath)) return false;
        ComparisonPath path = (ComparisonPath) o;
        return segments.equals(path.segments);
    }

    @Override
    public int hashCode() {
        return segments.hashCode();
    }

}
