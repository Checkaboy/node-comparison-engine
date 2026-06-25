package com.checkaboy.comparison.spi;

import com.checkaboy.comparison.config.StrategyReference;

/**
 * SPI interface for strategy provision (comparison-specific).
 *
 * @author Taras Shaptala
 */
public interface StrategyProvider {

    /**
     * Check if this provider can handle the strategy reference.
     */
    boolean canHandle(
            StrategyReference reference,
            Object sourceType,
            Object targetType,
            Object context
    );

    /**
     * Compile strategy reference to executable strategy.
     */
    ComparisonStrategy compile(
            StrategyReference reference,
            Object sourceType,
            Object targetType,
            Object context
    ) throws StrategyCompilationException;

}