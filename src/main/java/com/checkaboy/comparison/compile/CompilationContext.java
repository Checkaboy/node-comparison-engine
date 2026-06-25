package com.checkaboy.comparison.compile;

import com.checkaboy.comparison.config.ComparisonGraphDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compilation context - passed through all compilation stages.
 *
 * @author Taras Shaptala
 */
public final class CompilationContext {
    private final ComparisonGraphDefinition definition;
    private final ProviderRegistry providers;
    private final ResolutionCache cache;
    private final List<CompilationDiagnostic> diagnostics;
    private final long compilationStartTime;

    public CompilationContext(
            ComparisonGraphDefinition definition,
            ProviderRegistry providers,
            ResolutionCache cache
    ) {
        this.definition = definition;
        this.providers = providers;
        this.cache = cache;
        this.diagnostics = new ArrayList<>();
        this.compilationStartTime = System.currentTimeMillis();
    }

    public ComparisonGraphDefinition getDefinition() {
        return definition;
    }

    public ProviderRegistry getProviders() {
        return providers;
    }

    public ResolutionCache getCache() {
        return cache;
    }

    /**
     * Add a diagnostic.
     */
    public void addDiagnostic(CompilationDiagnostic diagnostic) {
        diagnostics.add(diagnostic);
    }

    /**
     * Get all diagnostics.
     */
    public List<CompilationDiagnostic> getDiagnostics() {
        return Collections.unmodifiableList(diagnostics);
    }

    /**
     * Get compilation duration.
     */
    public long getCompilationDurationMs() {
        return System.currentTimeMillis() - compilationStartTime;
    }

}