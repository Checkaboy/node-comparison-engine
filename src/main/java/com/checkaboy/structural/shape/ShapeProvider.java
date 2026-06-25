package com.checkaboy.structural.shape;

/**
 * SPI interface for shape resolution (structural, reusable).
 *
 * @author Taras Shaptala
 */
public interface ShapeProvider {

    /**
     * Check if provider can resolve the shape reference.
     */
    boolean canResolve(ShapeReference reference, Object typeDescriptor, Object context);

    /**
     * Resolve shape reference to descriptor.
     */
    ShapeDescriptor resolve(
            ShapeReference reference,
            Object typeDescriptor,
            Object context
    );

    /**
     * Infer shape from type.
     */
    ShapeDescriptor infer(Object typeDescriptor, Object context);

}
