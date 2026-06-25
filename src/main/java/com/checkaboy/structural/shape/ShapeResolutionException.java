package com.checkaboy.structural.shape;

/**
 * Exception thrown during shape resolution.
 *
 * @author Taras Shaptala
 */
public class ShapeResolutionException
        extends RuntimeException {

    public ShapeResolutionException(String message) {
        super(message);
    }

    public ShapeResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

}