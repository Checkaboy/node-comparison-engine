package com.checkaboy.structural.type;

import java.util.*;

/**
 * Descriptor for a resolved type (contains full type information).
 * Result of TypeProvider.resolve().
 *
 * @author Taras Shaptala
 */
public final class TypeDescriptor {

    private final String typeName;
    private final Class<?> typeClass;
    private final boolean isGeneric;
    private final List<TypeDescriptor> typeArguments;
    private final Map<String, PropertyDescriptor> properties;
    private final TypeDescriptor elementType;
    private final boolean isReflectionAccessible;

    public TypeDescriptor(
            String typeName,
            Class<?> typeClass,
            boolean isGeneric,
            List<TypeDescriptor> typeArguments,
            Map<String, PropertyDescriptor> properties,
            TypeDescriptor elementType,
            boolean isReflectionAccessible
    ) {
        this.typeName = typeName;
        this.typeClass = typeClass;
        this.isGeneric = isGeneric;
        this.typeArguments = typeArguments != null ?
                Collections.unmodifiableList(new ArrayList<>(typeArguments)) :
                Collections.emptyList();
        this.properties = properties != null ?
                Collections.unmodifiableMap(new HashMap<>(properties)) :
                Collections.emptyMap();
        this.elementType = elementType;
        this.isReflectionAccessible = isReflectionAccessible;
    }

    public String getTypeName() {
        return typeName;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public List<TypeDescriptor> getTypeArguments() {
        return typeArguments;
    }

    public Map<String, PropertyDescriptor> getProperties() {
        return properties;
    }

    public PropertyDescriptor getProperty(String name) {
        return properties.get(name);
    }

    public TypeDescriptor getElementType() {
        return elementType;
    }

    public boolean isReflectionAccessible() {
        return isReflectionAccessible;
    }

    public boolean supportsIteration() {
        return elementType != null;
    }

    @Override
    public String toString() {
        return "TypeDescriptor{" + typeName + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeDescriptor)) return false;
        TypeDescriptor desc = (TypeDescriptor) o;
        return typeName.equals(desc.typeName);
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

}
