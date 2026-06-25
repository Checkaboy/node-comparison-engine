package com.checkaboy.structural.shape;

/**
 * Descriptor for a shape property.
 *
 * @author Taras Shaptala
 */
public final class PropertyShape {

    private final String propertyName;
    private final ElementType elementType;
    private final boolean required;
    private final Object defaultValue;

    public PropertyShape(
            String propertyName,
            ElementType elementType,
            boolean required,
            Object defaultValue
    ) {
        this.propertyName = propertyName;
        this.elementType = elementType;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public boolean isRequired() {
        return required;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

}
