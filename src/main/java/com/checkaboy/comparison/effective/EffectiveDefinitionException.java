package com.checkaboy.comparison.effective;

/**
 * Exception thrown during effective definition creation.
 *
 * @author Taras Shaptal
 */
public class EffectiveDefinitionException
        extends RuntimeException {

    public EffectiveDefinitionException(String message) {
        super(message);
    }

    public EffectiveDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

}