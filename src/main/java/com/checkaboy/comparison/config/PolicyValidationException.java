package com.checkaboy.comparison.config;

/**
 * Exception thrown during policy validation.
 *
 * @author Taras Shaptala
 */
public class PolicyValidationException
        extends RuntimeException {

    public PolicyValidationException(String message) {
        super(message);
    }

    public PolicyValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}