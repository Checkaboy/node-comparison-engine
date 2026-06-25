package com.checkaboy.comparison.spi;

/**
 * Exception thrown during strategy execution.
 *
 * @author Taras Shaptala
 */
public class StrategyException
        extends RuntimeException {

    private final String errorCode;
    private final boolean recoverable;

    public StrategyException(String message) {
        super(message);
        this.errorCode = "STRATEGY_ERROR";
        this.recoverable = false;
    }

    public StrategyException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.recoverable = false;
    }

    public StrategyException(String message, String errorCode, boolean recoverable) {
        super(message);
        this.errorCode = errorCode;
        this.recoverable = recoverable;
    }

    public StrategyException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "STRATEGY_ERROR";
        this.recoverable = false;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

}