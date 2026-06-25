package com.checkaboy.comparison.spi;

/**
 * Exception thrown during execution (node/edge/strategy).
 *
 * @author Taras Shaptala
 */
public class ExecutionException
        extends RuntimeException {

    private final String errorType;
    private final String context;

    public ExecutionException(String message) {
        super(message);
        this.errorType = "EXECUTION_ERROR";
        this.context = null;
    }

    public ExecutionException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
        this.context = null;
    }

    public ExecutionException(String message, String errorType, String context) {
        super(message);
        this.errorType = errorType;
        this.context = context;
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = "EXECUTION_ERROR";
        this.context = null;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getContext() {
        return context;
    }

}