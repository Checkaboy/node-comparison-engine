package com.checkaboy.structural.type;

/**
 * Resolved type (TypeReference → TypeDescriptor mapping).
 *
 * @author Taras Shaptala
 */
public final class ResolvedType {

    private final TypeReference reference;
    private final TypeDescriptor descriptor;
    private final String providerName;
    private final long resolvedAt;

    public ResolvedType(
            TypeReference reference,
            TypeDescriptor descriptor,
            String providerName,
            long resolvedAt
    ) {
        this.reference = reference;
        this.descriptor = descriptor;
        this.providerName = providerName;
        this.resolvedAt = resolvedAt;
    }

    public TypeReference getReference() {
        return reference;
    }

    public TypeDescriptor getDescriptor() {
        return descriptor;
    }

    public String getProviderName() {
        return providerName;
    }

    public long getResolvedAt() {
        return resolvedAt;
    }

}
