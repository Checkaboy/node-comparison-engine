package com.checkaboy.structural.accessor;

import java.util.Collections;

/**
 * SPI interface for accessor provision (structural, reusable).
 */
public interface AccessorProvider {

    /**
     * Check if this provider can handle the accessor reference.
     */
    boolean canHandle(
            AccessorReference reference,
            Object typeDescriptor,
            Object context
    );

    /**
     * Compile an accessor reference to executable accessor.
     */
    CompiledAccessor compile(
            AccessorReference reference,
            Object typeDescriptor,
            Object context
    ) throws AccessorCompilationException;

    /**
     * Optional validation of accessor definition.
     */
    default ValidationResult validate(Object definition, Object context) {
        return new ValidationResult(true, Collections.emptyList(), Collections.emptyList());
    }

}

