package com.checkaboy.comparison.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference to a comparison strategy.
 *
 * @author Taras Shaptala
 */
public final class StrategyReference {

    private final String strategyId;
    private final Map<String, Object> configuration;
    private final Map<String, Object> metadata;

    public StrategyReference(
            String strategyId,
            Map<String, Object> configuration,
            Map<String, Object> metadata
    ) {
        if (strategyId == null || strategyId.isEmpty()) {
            throw new IllegalArgumentException("strategyId must be non-empty");
        }
        this.strategyId = strategyId;
        this.configuration = Collections.unmodifiableMap(
                configuration != null ? new HashMap<>(configuration) : new HashMap<>()
        );
        this.metadata = Collections.unmodifiableMap(
                metadata != null ? new HashMap<>(metadata) : new HashMap<>()
        );
    }

    public String getStrategyId() {
        return strategyId;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "StrategyReference{" + strategyId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StrategyReference)) return false;
        StrategyReference ref = (StrategyReference) o;
        return strategyId.equals(ref.strategyId);
    }

    @Override
    public int hashCode() {
        return strategyId.hashCode();
    }

}