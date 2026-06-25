package com.checkaboy.structural.accessor;

import java.util.Map;

/**
 * Compiled accessor - result of AccessorProvider.compile().
 * Stateless, thread-safe, reusable.
 *
 * @author Taras Shaptala
 */
public interface CompiledAccessor {

    /**
     * Extract value from source object.
     *
     * @param source The object to extract from
     * @return Extracted value
     * @throws AccessorException If extraction fails
     */
    Object execute(Object source) throws AccessorException;

    /**
     * Get metadata about this accessor.
     *
     * @return Metadata map
     */
    Map<String, Object> getMetadata();

}