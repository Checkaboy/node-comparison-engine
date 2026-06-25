package com.checkaboy.structural.shape;

/**
 * Element type enumeration for shapes.
 *
 * @author Taras Shaptala
 */
public enum ElementType {
    OBJECT,      // Regular object
    COLLECTION,  // List, Set, etc.
    KEYED,       // Map, keyed structure
    SCALAR       // Primitive, String, etc.
}