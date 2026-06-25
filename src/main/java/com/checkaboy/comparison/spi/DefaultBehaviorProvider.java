package com.checkaboy.comparison.spi;

import com.checkaboy.comparison.config.BehaviorReference;

/**
 * Default behavior provider.
 *
 * @author Taras Shaptala
 */
public class DefaultBehaviorProvider
        implements BehaviorProvider {

    @Override
    public boolean canHandle(
            BehaviorReference reference,
            Object effectiveDefinition,
            Object context
    ) {
        return reference.getBehaviorId().startsWith("default.");
    }

    @Override
    public CompiledBehavior compile(
            BehaviorReference reference,
            Object effectiveDefinition,
            Object context
    ) throws BehaviorCompilationException {
        String behaviorId = reference.getBehaviorId();

        if ("default.scalar".equals(behaviorId)) {
            return new ScalarNodeBehavior();
        } else if ("default.collection".equals(behaviorId)) {
            return new CollectionNodeBehavior();
        } else {
            throw new BehaviorCompilationException(
                    "Unknown default behavior: " + behaviorId
            );
        }
    }

    /**
     * Scalar node behavior - orchestrates edges for scalar values.
     */
    private static class ScalarNodeBehavior
            implements CompiledBehavior {

        @Override
        public Object execute(
                Object sourceValue,
                Object targetValue,
                Object executionContext,
                Object callback
        ) throws ExecutionException {
            // Simplified - would invoke edges via callback
            return sourceValue;
        }

    }

    /**
     * Collection node behavior - handles collection matching.
     */
    private static class CollectionNodeBehavior
            implements CompiledBehavior {

        @Override
        public Object execute(
                Object sourceValue,
                Object targetValue,
                Object executionContext,
                Object callback
        ) throws ExecutionException {
            // Simplified - would handle collection matching
            return sourceValue;
        }

    }

}
