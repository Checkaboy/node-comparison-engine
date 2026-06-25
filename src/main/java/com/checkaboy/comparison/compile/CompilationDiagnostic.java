package com.checkaboy.comparison.compile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Compilation diagnostic.
 *
 * @author Taras Shaptala
 */
public final class CompilationDiagnostic {

    private final String stage;
    private final DiagnosticSeverity severity;
    private final String code;
    private final String message;
    private final String location;
    private final Map<String, Object> details;
    private final long timestamp;

    public CompilationDiagnostic(
            String stage,
            DiagnosticSeverity severity,
            String code,
            String message,
            String location,
            Map<String, Object> details,
            long timestamp
    ) {
        this.stage = stage;
        this.severity = severity;
        this.code = code;
        this.message = message;
        this.location = location;
        this.details = Collections.unmodifiableMap(
                details != null ? new HashMap<>(details) : new HashMap<>()
        );
        this.timestamp = timestamp;
    }

    public String getStage() {
        return stage;
    }

    public DiagnosticSeverity getSeverity() {
        return severity;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public long getTimestamp() {
        return timestamp;
    }

}