package com.checkaboy.comparison.config;

/**
 * Error handling mode enumeration.
 *
 * @author Taras Shaptala
 */
public enum ErrorHandlingMode {
    FAIL_FAST,      // Stop execution on error
    CONTINUE        // Continue execution
}