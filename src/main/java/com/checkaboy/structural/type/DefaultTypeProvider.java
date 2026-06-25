package com.checkaboy.structural.type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default type provider for Java types.
 *
 * @author Taras Shaptala
 */
public class DefaultTypeProvider
        implements TypeProvider {

    @Override
    public boolean canResolve(TypeReference reference, Object context) {
        return true; // Handles all Java types
    }

    @Override
    public TypeDescriptor resolve(TypeReference reference, Object context) {
        try {
            Class<?> clazz = Class.forName(reference.getTypeName());
            return describeClass(clazz);
        } catch (ClassNotFoundException e) {
            throw new TypeResolutionException(
                    "Cannot resolve type: " + reference.getTypeName(),
                    e
            );
        }
    }

    @Override
    public TypeDescriptor inferType(Object instance, Object context) {
        if (instance == null) {
            return new TypeDescriptor("null", null, false, null, null, null, false);
        }
        return describeClass(instance.getClass());
    }

    private TypeDescriptor describeClass(Class<?> clazz) {
        Map<String, PropertyDescriptor> properties = new HashMap<>();

        try {
            for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                TypeDescriptor fieldType = describeClass(field.getType());
                properties.put(
                        field.getName(),
                        new PropertyDescriptor(
                                field.getName(),
                                fieldType,
                                true,
                                !java.lang.reflect.Modifier.isFinal(field.getModifiers())
                        )
                );
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }

        boolean isGeneric = clazz.getTypeParameters().length > 0;
        TypeDescriptor elementType = null;

        if (clazz.isArray()) {
            elementType = describeClass(clazz.getComponentType());
        } else if (Iterable.class.isAssignableFrom(clazz)) {
            elementType = new TypeDescriptor("java.lang.Object", Object.class, false,
                    null, null, null, true);
        }

        return new TypeDescriptor(
                clazz.getName(),
                clazz,
                isGeneric,
                Collections.emptyList(),
                properties,
                elementType,
                true
        );
    }

}
