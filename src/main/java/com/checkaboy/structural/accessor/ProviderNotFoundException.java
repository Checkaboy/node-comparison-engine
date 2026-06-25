package com.checkaboy.structural.accessor;

/**
 * Exception thrown when no provider can handle an accessor.
 *
 * @author Taras Shaptala
 */
public class ProviderNotFoundException
        extends RuntimeException {

    public ProviderNotFoundException(String message) {
        super(message);
    }

}