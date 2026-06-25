package com.checkaboy.structural.accessor;

/**
 * Exception thrown during accessor compilation.
 *
 * @author Taras Shaptala
 */
public class AccessorCompilationException
        extends RuntimeException {

    public AccessorCompilationException(String message) {
        super(message);
    }

    public AccessorCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

}