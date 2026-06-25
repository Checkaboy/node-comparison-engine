package com.checkaboy.structural.accessor;

/**
 * Exception thrown during accessor execution.
 *
 * @author Taras Shaptala
 */
public class AccessorException
        extends RuntimeException {

    private final String errorCode;
    private final boolean recoverable;

    public AccessorException(String message) {
        super(message);
        this.errorCode = "ACCESSOR_ERROR";
        this.recoverable = false;
    }

    public AccessorException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.recoverable = false;
    }

    public AccessorException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.recoverable = false;
    }

    public AccessorException(
            String message,
            String errorCode,
            boolean recoverable,
            Throwable cause
    ) {
        super(message, cause);
        this.errorCode = errorCode;
        this.recoverable = recoverable;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

}