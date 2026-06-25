package com.checkaboy.comparison.spi;

import com.checkaboy.comparison.config.BehaviorReference;

/**
 * SPI interface for behavior provision (comparison-specific).
 *
 * @author Taras Shaptala
 */
public interface BehaviorProvider {

    /**
     * Check if this provider can handle the behavior reference.
     */
    boolean canHandle(
            BehaviorReference reference,
            Object effectiveDefinition,
            Object context
    );

    /**
     * Compile behavior reference to executable behavior.
     */
    CompiledBehavior compile(
            BehaviorReference reference,
            Object effectiveDefinition,
            Object context
    ) throws BehaviorCompilationException;

}