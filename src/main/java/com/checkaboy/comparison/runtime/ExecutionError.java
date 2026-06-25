package com.checkaboy.comparison.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Execution error information.
 *
 * @author Taras Shaptala
 */
public final class ExecutionError {

    private final String errorType;
    private final Throwable cause;
    private final String providerType;
    private final String providerId;
    private final boolean recoverable;
    private final String message;
    private final Map<String, Object> context;

    public ExecutionError(
            String errorType,
            Throwable cause,
            String providerType,
            String providerId,
            boolean recoverable,
            String message,
            Map<String, Object> context
    ) {
        this.errorType = errorType;
        this.cause = cause;
        this.providerType = providerType;
        this.providerId = providerId;
        this.recoverable = recoverable;
        this.message = message;
        this.context = Collections.unmodifiableMap(
                context != null ? new HashMap<>(context) : new HashMap<>()
        );
    }

    public String getErrorType() {
        return errorType;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getProviderType() {
        return providerType;
    }

    public String getProviderId() {
        return providerId;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getContext() {
        return context;
    }

}