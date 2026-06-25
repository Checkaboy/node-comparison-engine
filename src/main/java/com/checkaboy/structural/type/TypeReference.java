package com.checkaboy.structural.type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference to a type (configuration layer).
 * Used to identify types without requiring full resolution.
 *
 * @author Taras Shaptala
 */
public final class TypeReference {

    private final String typeName;
    private final Map<String, Object> metadata;

    public TypeReference(String typeName, Map<String, Object> metadata) {
        if (typeName == null || typeName.isEmpty()) {
            throw new IllegalArgumentException("typeName must be non-empty");
        }
        this.typeName = typeName;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getTypeName() {
        return typeName;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "TypeReference{" + typeName + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeReference)) return false;
        TypeReference ref = (TypeReference) o;
        return typeName.equals(ref.typeName);
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

}
