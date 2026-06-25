package com.checkaboy.comparison.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Plan identity - uniquely identifies a compiled plan.
 *
 * @author Taras Shaptala
 */
public final class PlanIdentity {

    private final String planId;
    private final String planHash;
    private final long compiledAt;
    private final String compilerVersion;
    private final Map<String, String> providerVersions;

    public PlanIdentity(
            String planId,
            String planHash,
            long compiledAt,
            String compilerVersion,
            Map<String, String> providerVersions
    ) {
        this.planId = planId;
        this.planHash = planHash;
        this.compiledAt = compiledAt;
        this.compilerVersion = compilerVersion;
        this.providerVersions = Collections.unmodifiableMap(
                providerVersions != null ? new HashMap<>(providerVersions) : new HashMap<>()
        );
    }

    public String getPlanId() {
        return planId;
    }

    public String getPlanHash() {
        return planHash;
    }

    public long getCompiledAt() {
        return compiledAt;
    }

    public String getCompilerVersion() {
        return compilerVersion;
    }

    public Map<String, String> getProviderVersions() {
        return providerVersions;
    }

}
