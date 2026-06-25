package com.checkaboy.structural.shape;

/**
 * Resolved shape (ShapeReference → ShapeDescriptor mapping).
 *
 * @author Taras Shaptala
 */
public final class ResolvedShape {

    private final ShapeReference reference;
    private final ShapeDescriptor descriptor;
    private final String providerName;
    private final long resolvedAt;

    public ResolvedShape(
            ShapeReference reference,
            ShapeDescriptor descriptor,
            String providerName,
            long resolvedAt
    ) {
        this.reference = reference;
        this.descriptor = descriptor;
        this.providerName = providerName;
        this.resolvedAt = resolvedAt;
    }

    public ShapeReference getReference() {
        return reference;
    }

    public ShapeDescriptor getDescriptor() {
        return descriptor;
    }

    public String getProviderName() {
        return providerName;
    }

    public long getResolvedAt() {
        return resolvedAt;
    }

}