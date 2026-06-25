package com.checkaboy.comparison.effective;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Shape profile - carries shape information through compilation.
 *
 * @author Taras Shaptal
 */
public final class ShapeProfile {

    private final Map<String, Object> shapesByShapeId;
    private final Map<String, Object> metadata;

    public ShapeProfile(
            Map<String, Object> shapesByShapeId,
            Map<String, Object> metadata
    ) {
        this.shapesByShapeId = Collections.unmodifiableMap(
                shapesByShapeId != null ? new HashMap<>(shapesByShapeId) : new HashMap<>()
        );
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public Map<String, Object> getShapesByShapeId() {
        return shapesByShapeId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

}