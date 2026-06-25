package com.checkaboy.comparison.spi;

/**
 * Exception thrown during strategy compilation.
 *
 * @author Taras Shaptala
 */
public class StrategyCompilationException
        extends RuntimeException {

    public StrategyCompilationException(String message) {
        super(message);
    }

    public StrategyCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

}

