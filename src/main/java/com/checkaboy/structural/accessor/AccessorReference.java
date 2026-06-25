package com.checkaboy.structural.accessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference to an accessor (value extraction mechanism) - configuration layer.
 *
 * @author Taras Shaptala
 */
public final class AccessorReference {

    private final String accessorId;
    private final String path;
    private final String providerType;
    private final Map<String, String> metadata;

    public AccessorReference(
            String accessorId,
            String path,
            String providerType,
            Map<String, String> metadata
    ) {
        if (accessorId == null || accessorId.isEmpty()) {
            throw new IllegalArgumentException("accessorId must be non-empty");
        }
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path must be non-empty");
        }
        this.accessorId = accessorId;
        this.path = path;
        this.providerType = providerType;
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getAccessorId() {
        return accessorId;
    }

    public String getPath() {
        return path;
    }

    public String getProviderType() {
        return providerType;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "AccessorReference{" + accessorId + ":" + path + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessorReference)) return false;
        AccessorReference ref = (AccessorReference) o;
        return accessorId.equals(ref.accessorId);
    }

    @Override
    public int hashCode() {
        return accessorId.hashCode();
    }

}
