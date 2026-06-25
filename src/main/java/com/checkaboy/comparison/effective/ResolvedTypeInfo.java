package com.checkaboy.comparison.effective;

import com.checkaboy.structural.type.TypeDescriptor;

/**
 * Resolved type (TypeReference resolved to descriptor).
 *
 * @author Taras Shaptala
 */
public final class ResolvedTypeInfo {

    private final String typeReference;
    private final TypeDescriptor descriptor;
    private final String resolvedByProvider;

    public ResolvedTypeInfo(
            String typeReference,
            TypeDescriptor descriptor,
            String resolvedByProvider
    ) {
        this.typeReference = typeReference;
        this.descriptor = descriptor;
        this.resolvedByProvider = resolvedByProvider;
    }

    public String getTypeReference() {
        return typeReference;
    }

    public TypeDescriptor getDescriptor() {
        return descriptor;
    }

    public String getResolvedByProvider() {
        return resolvedByProvider;
    }

}