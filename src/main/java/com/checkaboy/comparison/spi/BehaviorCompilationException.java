package com.checkaboy.comparison.spi;

/**
 * Exception thrown during behavior compilation.
 *
 * @author Taras Shaptala
 */
public class BehaviorCompilationException
        extends RuntimeException {

    public BehaviorCompilationException(String message) {
        super(message);
    }

    public BehaviorCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

}