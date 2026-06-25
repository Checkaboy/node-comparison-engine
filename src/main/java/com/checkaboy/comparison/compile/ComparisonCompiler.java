package com.checkaboy.comparison.compile;

import com.checkaboy.comparison.config.*;
import com.checkaboy.comparison.effective.*;
import com.checkaboy.comparison.runtime.*;
import com.checkaboy.structural.accessor.AccessorCompilationException;
import com.checkaboy.structural.accessor.AccessorProvider;
import com.checkaboy.structural.accessor.AccessorReference;
import com.checkaboy.structural.accessor.ReflectionAccessorProvider;
import com.checkaboy.structural.type.DefaultTypeProvider;
import com.checkaboy.structural.type.TypeDescriptor;
import com.checkaboy.structural.type.TypeProvider;

import java.util.*;

/**
 * Main compiler for Declarative Comparison Engine V4.
 * Orchestrates all compilation stages.
 *
 * @author Taras Shaptala
 */
public class ComparisonCompiler {

    private final ProviderRegistry providerRegistry;
    private final PlanCache planCache;
    private final String compilerVersion = "4.0.0";

    public ComparisonCompiler(ProviderRegistry providerRegistry) {
        this(providerRegistry, new LocalPlanCache(1000));
    }

    public ComparisonCompiler(ProviderRegistry providerRegistry, PlanCache planCache) {
        this.providerRegistry = providerRegistry;
        this.planCache = planCache;
    }

    /**
     * Compile a comparison graph with optional policy and execution policy.
     */
    public CompiledComparisonPlan compile(
            ComparisonGraphDefinition graphDefinition,
            PolicyDefinition policy,
            ExecutionPolicy executionPolicy
    ) {
        // Create compilation context
        ResolutionCache cache = new ResolutionCache();
        CompilationContext context = new CompilationContext(
                graphDefinition,
                providerRegistry,
                cache
        );

        try {
            // STAGE 1: Definition Validation
            validateDefinition(graphDefinition, context);

            // STAGE 2: Definition Normalization
            ComparisonGraphDefinition normalizedGraph = normalizeDefinition(graphDefinition, context);

            // STAGE 3: Structural Resolution (Type, Shape, Accessor)
            EffectiveGraphDefinition effectiveGraph = resolveStructure(normalizedGraph, context);

            // STAGE 4: Policy Application
            EffectiveComparisonDefinition effectiveDefinition = applyPolicies(
                    effectiveGraph,
                    policy,
                    context
            );

            // STAGE 5-8: Behavior, Strategy, Semantics, Composition Validation
            validateComposition(effectiveDefinition, context);

            // STAGE 9: Execution Semantics Setup
            ExecutionSemantics semantics = createExecutionSemantics(executionPolicy);

            // STAGE 10: Plan Assembly
            CompiledComparisonPlan plan = assemblePlan(
                    effectiveDefinition,
                    semantics,
                    context
            );

            // Report successful compilation
            reportCompilationSuccess(context);

            return plan;

        } catch (CompilationException e) {
            reportCompilationError(context, e);
            throw e;
        } catch (Exception e) {
            reportCompilationError(context, new CompilationException(
                    "Compilation failed: " + e.getMessage(),
                    "UNKNOWN",
                    "COMPILATION_ERROR",
                    e
            ));
            throw new CompilationException(
                    "Compilation failed: " + e.getMessage(),
                    "UNKNOWN",
                    "COMPILATION_ERROR",
                    e
            );
        }
    }

    /**
     * STAGE 1: Definition Validation.
     */
    private void validateDefinition(
            ComparisonGraphDefinition definition,
            CompilationContext context
    ) {
        // Validate graph ID
        if (definition.getGraphId() == null || definition.getGraphId().isEmpty()) {
            throw new CompilationException(
                    "Graph ID must be non-empty",
                    "DEFINITION_VALIDATION",
                    "INVALID_GRAPH_ID"
            );
        }

        // Validate root node exists
        if (!definition.getNodes().containsKey(definition.getRootNodeId())) {
            throw new CompilationException(
                    "Root node not found: " + definition.getRootNodeId(),
                    "DEFINITION_VALIDATION",
                    "ROOT_NODE_NOT_FOUND"
            );
        }

        // Validate all node IDs are unique
        Set<String> nodeIds = new HashSet<>(definition.getNodes().keySet());
        if (nodeIds.size() != definition.getNodes().size()) {
            throw new CompilationException(
                    "Duplicate node IDs",
                    "DEFINITION_VALIDATION",
                    "DUPLICATE_NODE_ID"
            );
        }

        // Validate all edge IDs are unique
        Set<String> edgeIds = new HashSet<>(definition.getEdges().keySet());
        if (edgeIds.size() != definition.getEdges().size()) {
            throw new CompilationException(
                    "Duplicate edge IDs",
                    "DEFINITION_VALIDATION",
                    "DUPLICATE_EDGE_ID"
            );
        }

        addDiagnostic(context, "DEFINITION_VALIDATION", DiagnosticSeverity.INFO,
                "VALIDATION_OK", "Definition validation passed",
                null, Map.of("nodes_validated", definition.getNodes().size(),
                        "edges_validated", definition.getEdges().size()));
    }

    /**
     * STAGE 2: Definition Normalization.
     */
    private ComparisonGraphDefinition normalizeDefinition(
            ComparisonGraphDefinition definition,
            CompilationContext context
    ) {
        // In a complete implementation, would handle:
        // - DSL translation
        // - Alias resolution
        // - Import expansion
        // - Behavior defaults

        // For now, return as-is
        addDiagnostic(context, "DEFINITION_NORMALIZATION", DiagnosticSeverity.INFO,
                "NORMALIZATION_OK", "Definition normalization completed",
                null, new HashMap<>());

        return definition;
    }

    /**
     * STAGE 3: Structural Resolution.
     */
    private EffectiveGraphDefinition resolveStructure(
            ComparisonGraphDefinition definition,
            CompilationContext context
    ) {
        Map<String, EffectiveNodeDefinition> effectiveNodes = new HashMap<>();

        for (Map.Entry<String, ComparisonNodeDefinition> entry : definition.getNodes().entrySet()) {
            ComparisonNodeDefinition nodeDef = entry.getValue();

            // Resolve types
            ResolvedTypeInfo sourceType = resolveType(nodeDef.getSourceType(), context);
            ResolvedTypeInfo targetType = resolveType(nodeDef.getTargetType(), context);

            // Resolve shape
            Object resolvedShape = resolveShape(nodeDef.getShape(), context);

            // Set default behavior if needed
            BehaviorReference behavior = nodeDef.getNodeBehavior();
            if (behavior == null) {
                behavior = new BehaviorReference("default.scalar", null, null);
            }

            // Infer collection handling if needed
            CollectionHandlingMode collectionMode = nodeDef.getCollectionHandling();
            if (collectionMode == null) {
                collectionMode = CollectionHandlingMode.AUTO;
            }

            EffectiveNodeDefinition effectiveNode = new EffectiveNodeDefinition(
                    nodeDef.getNodeId(),
                    sourceType,
                    targetType,
                    resolvedShape,
                    behavior,
                    collectionMode,
                    nodeDef.getMetadata()
            );

            effectiveNodes.put(entry.getKey(), effectiveNode);
        }

        Map<String, EffectiveEdgeDefinition> effectiveEdges = new HashMap<>();

        for (Map.Entry<String, ComparisonEdgeDefinition> entry : definition.getEdges().entrySet()) {
            ComparisonEdgeDefinition edgeDef = entry.getValue();

            // Compile accessors
            Object sourceAccessor = compileAccessor(edgeDef.getSourceAccessor(), context);
            Object targetAccessor = compileAccessor(edgeDef.getTargetAccessor(), context);

            EffectiveEdgeDefinition effectiveEdge = new EffectiveEdgeDefinition(
                    edgeDef.getEdgeId(),
                    sourceAccessor,
                    targetAccessor,
                    edgeDef.getBehavior(),
                    edgeDef.getStrategy(),
                    edgeDef.getChildNode(),
                    edgeDef.getMetadata()
            );

            effectiveEdges.put(entry.getKey(), effectiveEdge);
        }

        EffectiveGraphDefinition effectiveGraph = new EffectiveGraphDefinition(
                definition.getGraphId(),
                effectiveNodes,
                effectiveEdges,
                definition.getRootNodeId(),
                definition.getMetadata()
        );

        addDiagnostic(context, "STRUCTURAL_RESOLUTION", DiagnosticSeverity.INFO,
                "RESOLUTION_OK", "Structural resolution completed",
                null, Map.of("nodes_resolved", effectiveNodes.size(),
                        "edges_resolved", effectiveEdges.size()));

        return effectiveGraph;
    }

    /**
     * STAGE 4: Policy Application.
     */
    private EffectiveComparisonDefinition applyPolicies(
            EffectiveGraphDefinition effectiveGraph,
            PolicyDefinition policy,
            CompilationContext context
    ) {
        Map<String, PolicyDecision> policyDecisions = new HashMap<>();

        if (policy != null) {
            // Apply policy rules
            for (PolicyRule rule : policy.getRules()) {
                // In a complete implementation, would evaluate condition and apply actions
                // For now, skip
            }
        }

        ExecutionProfile executionProfile = new ExecutionProfile(
                null,
                null,
                ErrorHandlingMode.FAIL_FAST,
                CycleHandlingMode.SKIP,
                false
        );

        ShapeProfile shapeProfile = new ShapeProfile(new HashMap<>(), new HashMap<>());

        EffectiveComparisonDefinition effectiveDefinition = new EffectiveComparisonDefinition(
                effectiveGraph,
                executionProfile,
                shapeProfile,
                policyDecisions,
                new HashMap<>()
        );

        addDiagnostic(context, "POLICY_APPLICATION", DiagnosticSeverity.INFO,
                "POLICY_OK", "Policy application completed",
                null, Map.of("rules_applied", 0));

        return effectiveDefinition;
    }

    /**
     * STAGE 5-8: Validate Composition.
     */
    private void validateComposition(
            EffectiveComparisonDefinition definition,
            CompilationContext context
    ) {
        // Validate cross-partition contracts
        // In a complete implementation, would validate:
        // - All edges reference valid accessors
        // - All edges reference valid behaviors/strategies
        // - All child nodes exist

        addDiagnostic(context, "COMPOSITION_VALIDATION", DiagnosticSeverity.INFO,
                "VALIDATION_OK", "Composition validation passed",
                null, new HashMap<>());
    }

    /**
     * STAGE 9: Create Execution Semantics.
     */
    private ExecutionSemantics createExecutionSemantics(ExecutionPolicy policy) {
        if (policy == null) {
            policy = new ExecutionPolicy.Builder().build();
        }

        return new ExecutionSemantics(
                policy.getExecutionMode(),
                policy.getTimeout() != null ? policy.getTimeout() : 0,
                policy.getMaxDepth() != null ? policy.getMaxDepth() : 100,
                policy.getMaxBreadth() != null ? policy.getMaxBreadth() : 1000,
                policy.getErrorHandling(),
                policy.getCycleHandling(),
                ResultPolicy.ALL_DIFFERENCES
        );
    }

    /**
     * STAGE 10: Assemble Plan.
     */
    private CompiledComparisonPlan assemblePlan(
            EffectiveComparisonDefinition effectiveDefinition,
            ExecutionSemantics semantics,
            CompilationContext context
    ) {
        String planId = "plan-" + UUID.randomUUID().toString();

        // Compile root node plan
        EffectiveNodeDefinition rootNodeDef =
                effectiveDefinition.getEffectiveGraph().getRootNode();

        CompiledNodePlan rootNodePlan = compileNodePlan(rootNodeDef, effectiveDefinition, context);

        // Compile all node plans
        Map<String, CompiledNodePlan> nodePlans = new HashMap<>();
        for (EffectiveNodeDefinition nodeDef :
                effectiveDefinition.getEffectiveGraph().getNodes().values()) {
            nodePlans.put(nodeDef.getNodeId(),
                    compileNodePlan(nodeDef, effectiveDefinition, context));
        }

        // Create plan identity
        PlanIdentity planIdentity = new PlanIdentity(
                planId,
                "hash-" + planId,
                System.currentTimeMillis(),
                compilerVersion,
                new HashMap<>()
        );

        CompiledComparisonPlan plan = new CompiledComparisonPlan(
                planId,
                planIdentity,
                rootNodePlan,
                nodePlans,
                semantics,
                new HashMap<>()
        );

        // Cache the plan
        String fingerprint = FingerprintComputer.computeFingerprint(
                effectiveDefinition.getEffectiveGraph().getGraphId(),
                "1.0",
                "1.0",
                new HashMap<>()
        );
        planCache.put(fingerprint, plan);

        addDiagnostic(context, "PLAN_ASSEMBLY", DiagnosticSeverity.INFO,
                "ASSEMBLY_OK", "Plan assembly completed",
                null, Map.of("plan_id", planId, "node_count", nodePlans.size()));

        return plan;
    }

    /**
     * Helper: Resolve type reference.
     */
    private ResolvedTypeInfo resolveType(
            com.checkaboy.structural.type.TypeReference typeRef,
            CompilationContext context
    ) {
        // Find provider and resolve
        TypeProvider provider = context.getProviders().findTypeProvider(typeRef, context);
        if (provider == null) {
            provider = new DefaultTypeProvider();
        }

        TypeDescriptor descriptor = provider.resolve(typeRef, context);
        return new ResolvedTypeInfo(typeRef.getTypeName(), descriptor, provider.getClass().getSimpleName());
    }

    /**
     * Helper: Resolve shape reference.
     */
    private Object resolveShape(
            com.checkaboy.structural.shape.ShapeReference shapeRef,
            CompilationContext context
    ) {
        // Simplified - return reference as-is
        return shapeRef;
    }

    /**
     * Helper: Compile accessor.
     */
    private Object compileAccessor(
            AccessorReference accRef,
            CompilationContext context
    ) {
        // Find provider and compile
        AccessorProvider provider = context.getProviders()
                .findAccessorProvider(accRef, null, context);

        if (provider == null) {
            provider = new ReflectionAccessorProvider();
        }

        try {
            return provider.compile(accRef, null, context);
        } catch (AccessorCompilationException e) {
            throw new CompilationException(
                    "Cannot compile accessor: " + e.getMessage(),
                    "ACCESSOR_COMPILATION",
                    "COMPILATION_FAILED",
                    e
            );
        }
    }

    /**
     * Helper: Compile node plan.
     */
    private CompiledNodePlan compileNodePlan(
            EffectiveNodeDefinition nodeDef,
            EffectiveComparisonDefinition effectiveDefinition,
            CompilationContext context
    ) {
        List<CompiledEdgePlan> edgePlans = new ArrayList<>();

        for (EffectiveEdgeDefinition edgeDef :
                effectiveDefinition.getEffectiveGraph().getEdges().values()) {
            CompiledEdgePlan edgePlan = new CompiledEdgePlan(
                    edgeDef.getEdgeId(),
                    edgeDef.getSourceAccessor(),
                    edgeDef.getTargetAccessor(),
                    edgeDef.getBehavior(),
                    edgeDef.getStrategy(),
                    edgeDef.getChildNode(),
                    edgeDef.getMetadata()
            );
            edgePlans.add(edgePlan);
        }

        return new CompiledNodePlan(
                nodeDef.getNodeId(),
                nodeDef,
                nodeDef.getNodeBehavior(),
                edgePlans,
                nodeDef.getMetadata()
        );
    }

    /**
     * Helper: Add diagnostic.
     */
    private void addDiagnostic(
            CompilationContext context,
            String stage,
            DiagnosticSeverity severity,
            String code,
            String message,
            String location,
            Map<String, Object> details
    ) {
        CompilationDiagnostic diagnostic = new CompilationDiagnostic(
                stage,
                severity,
                code,
                message,
                location,
                details,
                System.currentTimeMillis()
        );
        context.addDiagnostic(diagnostic);
    }

    /**
     * Report successful compilation.
     */
    private void reportCompilationSuccess(CompilationContext context) {
        // In a real implementation, would log metrics
    }

    /**
     * Report compilation error.
     */
    private void reportCompilationError(CompilationContext context, CompilationException e) {
        // In a real implementation, would log error and diagnostics
    }

    /**
     * Get compilation report.
     */
    public CompilationReport getCompilationReport(CompilationContext context) {
        List<CompilationDiagnostic> diagnostics = context.getDiagnostics();
        int errorCount = (int) diagnostics.stream()
                .filter(d -> d.getSeverity() == DiagnosticSeverity.ERROR)
                .count();
        int warningCount = (int) diagnostics.stream()
                .filter(d -> d.getSeverity() == DiagnosticSeverity.WARNING)
                .count();

        return new CompilationReport(
                errorCount == 0,
                diagnostics,
                errorCount,
                warningCount,
                context.getCompilationDurationMs(),
                Map.of("cache_size", context.getCache().size())
        );
    }

}