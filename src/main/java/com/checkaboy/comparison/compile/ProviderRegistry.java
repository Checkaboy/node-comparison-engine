package com.checkaboy.comparison.compile;

import com.checkaboy.comparison.config.BehaviorReference;
import com.checkaboy.comparison.config.StrategyReference;
import com.checkaboy.comparison.spi.BehaviorProvider;
import com.checkaboy.comparison.spi.StrategyProvider;
import com.checkaboy.structural.accessor.AccessorProvider;
import com.checkaboy.structural.accessor.AccessorReference;
import com.checkaboy.structural.type.TypeProvider;
import com.checkaboy.structural.type.TypeReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provider registry for compilation.
 *
 * @author Taras Shaptala
 */
public class ProviderRegistry {

    private final List<AccessorProvider> accessorProviders = new ArrayList<>();
    private final List<BehaviorProvider> behaviorProviders = new ArrayList<>();
    private final List<StrategyProvider> strategyProviders = new ArrayList<>();
    private final List<TypeProvider> typeProviders = new ArrayList<>();

    /**
     * Register an accessor provider.
     */
    public void registerAccessorProvider(AccessorProvider provider) {
        accessorProviders.add(provider);
    }

    /**
     * Register a behavior provider.
     */
    public void registerBehaviorProvider(BehaviorProvider provider) {
        behaviorProviders.add(provider);
    }

    /**
     * Register a strategy provider.
     */
    public void registerStrategyProvider(StrategyProvider provider) {
        strategyProviders.add(provider);
    }

    /**
     * Register a type provider.
     */
    public void registerTypeProvider(TypeProvider provider) {
        typeProviders.add(provider);
    }

    /**
     * Get all accessor providers.
     */
    public List<AccessorProvider> getAccessorProviders() {
        return Collections.unmodifiableList(accessorProviders);
    }

    /**
     * Get all behavior providers.
     */
    public List<BehaviorProvider> getBehaviorProviders() {
        return Collections.unmodifiableList(behaviorProviders);
    }

    /**
     * Get all strategy providers.
     */
    public List<StrategyProvider> getStrategyProviders() {
        return Collections.unmodifiableList(strategyProviders);
    }

    /**
     * Get all type providers.
     */
    public List<TypeProvider> getTypeProviders() {
        return Collections.unmodifiableList(typeProviders);
    }

    /**
     * Find first accessor provider that can handle reference.
     */
    public AccessorProvider findAccessorProvider(
            AccessorReference reference,
            Object typeDescriptor,
            CompilationContext context
    ) {
        for (AccessorProvider provider : accessorProviders) {
            if (provider.canHandle(reference, typeDescriptor, context)) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Find first behavior provider that can handle reference.
     */
    public BehaviorProvider findBehaviorProvider(
            BehaviorReference reference,
            Object effectiveDefinition,
            CompilationContext context
    ) {
        for (BehaviorProvider provider : behaviorProviders) {
            if (provider.canHandle(reference, effectiveDefinition, context)) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Find first strategy provider that can handle reference.
     */
    public StrategyProvider findStrategyProvider(
            StrategyReference reference,
            Object sourceType,
            Object targetType,
            CompilationContext context
    ) {
        for (StrategyProvider provider : strategyProviders) {
            if (provider.canHandle(reference, sourceType, targetType, context)) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Find first type provider that can handle reference.
     */
    public TypeProvider findTypeProvider(TypeReference reference, CompilationContext context) {
        for (TypeProvider provider : typeProviders) {
            if (provider.canResolve(reference, context)) {
                return provider;
            }
        }
        return null;
    }

}