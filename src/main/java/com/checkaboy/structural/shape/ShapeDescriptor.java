package com.checkaboy.structural.shape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Shape descriptor - result of shape resolution.
 *
 * @author Taras Shaptala
 */
public final class ShapeDescriptor {

    private final ElementType elementType;
    private final Map<String, PropertyShape> properties;
    private final ShapeDescriptor collectionElementType;
    private final boolean ordered;
    private final boolean keyed;
    private final Object keyExtractor;
    private final ShapeCapabilities capabilities;

    public ShapeDescriptor(
            ElementType elementType,
            Map<String, PropertyShape> properties,
            ShapeDescriptor collectionElementType,
            boolean ordered,
            boolean keyed,
            Object keyExtractor,
            ShapeCapabilities capabilities
    ) {
        this.elementType = elementType;
        this.properties = properties != null ?
                Collections.unmodifiableMap(new HashMap<>(properties)) :
                Collections.emptyMap();
        this.collectionElementType = collectionElementType;
        this.ordered = ordered;
        this.keyed = keyed;
        this.keyExtractor = keyExtractor;
        this.capabilities = capabilities;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public Map<String, PropertyShape> getProperties() {
        return properties;
    }

    public ShapeDescriptor getCollectionElementType() {
        return collectionElementType;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public boolean isKeyed() {
        return keyed;
    }

    public Object getKeyExtractor() {
        return keyExtractor;
    }

    public ShapeCapabilities getCapabilities() {
        return capabilities;
    }

}
