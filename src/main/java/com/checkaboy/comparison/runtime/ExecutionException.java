package com.checkaboy.comparison.runtime;

/**
 * Exception thrown during execution.
 *
 * @author Taras Shaptala
 */
public class ExecutionException
        extends RuntimeException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, String errorType) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}