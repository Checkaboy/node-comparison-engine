package com.checkaboy.structural.type;

/**
 * Exception thrown during type resolution.
 *
 * @author Taras Shaptala
 */
public class TypeResolutionException
        extends RuntimeException {

    public TypeResolutionException(String message) {
        super(message);
    }

    public TypeResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
