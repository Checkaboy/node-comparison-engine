package com.checkaboy.structural.accessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of CompiledAccessor.
 *
 * @author Taras Shaptala
 */
public abstract class AbstractCompiledAccessor
        implements CompiledAccessor {

    protected final String accessorPath;

    protected AbstractCompiledAccessor(String accessorPath) {
        this.accessorPath = accessorPath;
    }

    protected Map<String, Object> getBaseMetadata() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("path", accessorPath);
        return meta;
    }

}
