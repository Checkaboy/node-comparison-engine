package com.checkaboy.structural.accessor;

import java.util.Map;

/**
 * Reflection-based accessor provider implementation.
 *
 * @author Taras Shaptala
 */
public class ReflectionAccessorProvider
        implements AccessorProvider {

    @Override
    public boolean canHandle(AccessorReference reference, Object typeDescriptor, Object context) {
        // Handles simple property paths
        String path = reference.getPath();
        return isSimplePath(path);
    }

    @Override
    public CompiledAccessor compile(
            AccessorReference reference,
            Object typeDescriptor,
            Object context
    ) throws AccessorCompilationException {
        String path = reference.getPath();
        String[] segments = path.split("\\.");

        return new ChainedReflectionAccessor(segments);
    }

    private boolean isSimplePath(String path) {
        return path != null &&
                !path.isEmpty() &&
                path.matches("[a-zA-Z_][a-zA-Z0-9_.]*");
    }

    /**
     * Reflection-based chained accessor.
     */
    private static class ChainedReflectionAccessor extends AbstractCompiledAccessor {
        private final String[] segments;

        ChainedReflectionAccessor(String[] segments) {
            super(String.join(".", segments));
            this.segments = segments;
        }

        @Override
        public Object execute(Object source) throws AccessorException {
            if (source == null) {
                throw new AccessorException(
                        "Source object is null",
                        "NULL_SOURCE"
                );
            }

            Object current = source;
            for (int i = 0; i < segments.length; i++) {
                if (current == null) {
                    throw new AccessorException(
                            "Cannot access property at segment " + i + ": value is null",
                            "NULL_INTERMEDIATE"
                    );
                }

                try {
                    java.lang.reflect.Field field =
                            current.getClass().getDeclaredField(segments[i]);
                    field.setAccessible(true);
                    current = field.get(current);
                } catch (NoSuchFieldException e) {
                    throw new AccessorException(
                            "Property '" + segments[i] + "' not found",
                            "PROPERTY_NOT_FOUND",
                            e
                    );
                } catch (IllegalAccessException e) {
                    throw new AccessorException(
                            "Cannot access property '" + segments[i] + "'",
                            "ACCESS_DENIED",
                            e
                    );
                }
            }

            return current;
        }

        @Override
        public Map<String, Object> getMetadata() {
            Map<String, Object> meta = getBaseMetadata();
            meta.put("accessor_type", "reflection");
            meta.put("segment_count", segments.length);
            meta.put("cacheable", true);
            return meta;
        }
    }

}