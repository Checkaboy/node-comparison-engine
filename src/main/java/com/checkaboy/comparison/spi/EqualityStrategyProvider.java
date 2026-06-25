package com.checkaboy.comparison.spi;

import com.checkaboy.comparison.config.StrategyReference;

import java.util.Map;
import java.util.Objects;

/**
 * Default equality strategy provider.
 *
 * @author Taras Shaptala
 */
public class EqualityStrategyProvider
        implements StrategyProvider {

    @Override
    public boolean canHandle(
            StrategyReference reference,
            Object sourceType,
            Object targetType,
            Object context
    ) {
        return "equality.exact".equals(reference.getStrategyId()) ||
                "default".equals(reference.getStrategyId());
    }

    @Override
    public ComparisonStrategy compile(
            StrategyReference reference,
            Object sourceType,
            Object targetType,
            Object context
    ) throws StrategyCompilationException {
        return new EqualityStrategy();
    }

    /**
     * Simple equality strategy implementation.
     */
    private static class EqualityStrategy
            implements ComparisonStrategy {

        @Override
        public Object compare(
                Object sourceValue,
                Object targetValue,
                Object executionContext
        ) throws StrategyException {
            boolean match = Objects.equals(sourceValue, targetValue);
            // In real implementation, return ExecutionOutcome
            // This is simplified for example
            return match;
        }

        @Override
        public Map<String, Object> getMetadata() {
            return Map.of(
                    "strategy_type", "equality",
                    "null_safe", true,
                    "symmetric", true
            );
        }

    }

}