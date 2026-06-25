package com.checkaboy.comparison.compile;

import java.util.*;

/**
 * Compilation report.
 *
 * @author Taras Shaptala
 */
public final class CompilationReport {

    private final boolean successful;
    private final List<CompilationDiagnostic> diagnostics;
    private final int errorCount;
    private final int warningCount;
    private final long compilationDurationMs;
    private final Map<String, Object> statistics;

    public CompilationReport(
            boolean successful,
            List<CompilationDiagnostic> diagnostics,
            int errorCount,
            int warningCount,
            long compilationDurationMs,
            Map<String, Object> statistics
    ) {
        this.successful = successful;
        this.diagnostics = Collections.unmodifiableList(
                diagnostics != null ? new ArrayList<>(diagnostics) : new ArrayList<>()
        );
        this.errorCount = errorCount;
        this.warningCount = warningCount;
        this.compilationDurationMs = compilationDurationMs;
        this.statistics = Collections.unmodifiableMap(
                statistics != null ? new HashMap<>(statistics) : new HashMap<>()
        );
    }

    public boolean isSuccessful() {
        return successful;
    }

    public List<CompilationDiagnostic> getDiagnostics() {
        return diagnostics;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public long getCompilationDurationMs() {
        return compilationDurationMs;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

}