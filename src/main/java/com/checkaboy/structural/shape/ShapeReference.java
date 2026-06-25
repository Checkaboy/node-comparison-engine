package com.checkaboy.structural.shape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Shape reference (configuration layer) - describes structural capabilities.
 *
 * @author Taras Shaptala
 */
public final class ShapeReference {

    private final String shapeId;
    private final Map<String, Object> metadata;

    public ShapeReference(String shapeId, Map<String, Object> metadata) {
        if (shapeId == null || shapeId.isEmpty()) {
            throw new IllegalArgumentException("shapeId must be non-empty");
        }
        this.shapeId = shapeId;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getShapeId() {
        return shapeId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "ShapeReference{" + shapeId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShapeReference)) return false;
        ShapeReference ref = (ShapeReference) o;
        return shapeId.equals(ref.shapeId);
    }

    @Override
    public int hashCode() {
        return shapeId.hashCode();
    }

}
