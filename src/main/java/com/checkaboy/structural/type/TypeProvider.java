package com.checkaboy.structural.type;

/**
 * SPI interface for type resolution (optional, structural, reusable).
 *
 * @author Taras Shaptala
 */
public interface TypeProvider {

    /**
     * Check if this provider can resolve the type reference.
     */
    boolean canResolve(TypeReference reference, Object context);

    /**
     * Resolve a type reference to a descriptor.
     */
    TypeDescriptor resolve(TypeReference reference, Object context);

    /**
     * Infer type from an object instance.
     */
    TypeDescriptor inferType(Object instance, Object context);

}
