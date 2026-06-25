package com.checkaboy.structural.type;

/**
 * Descriptor for a property on a type.
 *
 * @author Taras Shaptala
 */
public final class PropertyDescriptor {

    private final String propertyName;
    private final TypeDescriptor propertyType;
    private final boolean readable;
    private final boolean writable;

    public PropertyDescriptor(
            String propertyName,
            TypeDescriptor propertyType,
            boolean readable,
            boolean writable
    ) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.readable = readable;
        this.writable = writable;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public TypeDescriptor getPropertyType() {
        return propertyType;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

}
