# Declarative Comparison Engine V4
## COMPONENT REFERENCE GUIDE (A-Z)

**Purpose:** Complete reference of every component with details  
**Format:** Alphabetical listing with specifications  
**Audience:** Developers, architects  

---

# CONFIGURATION LAYER COMPONENTS

## AccessorDefinition
**Layer:** Configuration  
**Purpose:** Specify how to extract a value from an object  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- reference: AccessorReference
- metadata: Map<String, Object>

**Lifecycle:** Configuration only (never reaches runtime)

**Dependencies:**
- AccessorReference (required)

---

## AccessorReference
**Layer:** Configuration  
**Purpose:** Reference a value extraction mechanism  
**Reusable:** YES (structural)  
**Package:** com.checkaboy.structural.accessor  

**Fields:**
- accessorId: String (unique identifier)
- path: String (extraction path, e.g., "customer.name")
- providerType: String? (optional provider hint)
- metadata: Map<String, String>

**Lifecycle:** Configuration → Compilation (compiled to CompiledAccessor)

**Invariants:**
- accessorId must be non-empty
- path must be valid expression
- path must not be null

---

## BehaviorDefinition
**Layer:** Configuration  
**Purpose:** Define behavior for node or edge  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Contains:**
- reference: BehaviorReference
- parameters: Map<String, Object>?

---

## BehaviorReference
**Layer:** Configuration  
**Purpose:** Reference a behavior implementation  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- behaviorId: String (e.g., "default.scalar", "custom.fallback")
- parameters: Map<String, Object>?
- metadata: Map<String, Object>

**Lifecycle:** Configuration → Compilation (compiled by BehaviorProvider)

---

## CollectionHandlingMode
**Layer:** Configuration  
**Purpose:** Specify how to match collection elements  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Values:**
- UNORDERED_BY_IDENTITY
- UNORDERED_BY_EQUALITY
- UNORDERED_BY_GRAPH
- ORDERED_BY_POSITION
- ORDERED_BY_LCS
- ORDERED_BY_EDIT_DISTANCE
- KEYED_BY_KEY
- GRAPH_BY_NODE_ID
- GRAPH_BY_TOPOLOGY
- CUSTOM
- AUTO

**Lifecycle:** Configuration → Effective Definition (pre-selected at compile time)

---

## ComparisonEdgeDefinition
**Layer:** Configuration  
**Purpose:** Define an extraction and comparison step  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- edgeId: String (unique identifier)
- sourceAccessor: AccessorReference
- targetAccessor: AccessorReference
- behavior: BehaviorReference
- strategy: StrategyReference
- childNode: String? (reference to child node for recursion)
- metadata: Map<String, Object>

**Invariants:**
- edgeId must be unique within graph
- sourceAccessor and targetAccessor required
- behavior and strategy required

---

## ComparisonGraphDefinition
**Layer:** Configuration  
**Purpose:** Declare a comparison graph in human-readable form  
**Reusable:** NO (currently comparison-specific, V5 investigation)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- graphId: String
- nodes: Map<String, ComparisonNodeDefinition>
- edges: Map<String, ComparisonEdgeDefinition>
- rootNodeId: String
- metadata: Map<String, Object>
- schemaVersion: String

**Lifecycle:** Configuration → Compilation → Effective Definition → Runtime Plan

**Visibility:**
- Configuration: YES (source)
- Compilation: YES (input)
- Effective Definition: NO (transformed away)
- Runtime: NO (must never reach)

---

## ComparisonNodeDefinition
**Layer:** Configuration  
**Purpose:** Define a node in comparison graph  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- nodeId: String (unique identifier)
- sourceType: TypeReference
- targetType: TypeReference
- shape: ShapeReference
- nodeBehavior: BehaviorReference? (optional, defaults assigned)
- collectionHandling: CollectionHandlingMode? (optional, inferred)
- metadata: Map<String, Object>

---

## ExecutionPolicy
**Layer:** Configuration  
**Purpose:** Configure execution behavior (fail-fast, collect-all, limits)  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- executionMode: ExecutionMode (FAIL_FAST or COLLECT_ALL)
- timeout: long? (ms, null = no timeout)
- maxDepth: int? (null = no limit)
- maxBreadth: int? (null = no limit)
- errorHandling: ErrorHandlingMode
- cycleHandling: CycleHandlingMode
- streamingEnabled: boolean
- maxDifferences: int? (truncate results after limit)
- metadata: Map<String, Object>

**Lifecycle:** Configuration → Effective Definition → Execution Semantics

---

## PolicyDefinition
**Layer:** Configuration  
**Purpose:** Declare conditional rules that modify effective graph  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- policyId: String
- rules: List<PolicyRule>
- metadata: Map<String, Object>

**Lifecycle:** Configuration → Policy Compilation → Effective Definition

---

## ShapeReference
**Layer:** Configuration  
**Purpose:** Reference structural capabilities  
**Reusable:** YES (structural)  
**Package:** com.checkaboy.structural.shape  

**Fields:**
- shapeId: String
- metadata: Map<String, Object>

**Lifecycle:** Configuration → Shape Resolution → Effective Definition

---

## StrategyReference
**Layer:** Configuration  
**Purpose:** Reference terminal comparison strategy  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.config  

**Fields:**
- strategyId: String (e.g., "equality.exact", "fuzzy.phonetic")
- configuration: Map<String, Object>?
- metadata: Map<String, Object>

**Lifecycle:** Configuration → Compilation (compiled by StrategyProvider)

---

## TypeReference
**Layer:** Configuration  
**Purpose:** Reference a type  
**Reusable:** YES (structural)  
**Package:** com.checkaboy.structural.type  

**Fields:**
- typeName: String (fully qualified, e.g., "java.lang.String")
- metadata: Map<String, Object>

**Lifecycle:** Configuration → Type Resolution → Effective Definition

---

# EFFECTIVE DEFINITION LAYER COMPONENTS

## EffectiveComparisonDefinition
**Layer:** Effective Definition  
**Purpose:** Configuration after policy application  
**Reusable:** NO  
**Package:** com.checkaboy.comparison.effective  

**Contains:**
- effectiveGraph: EffectiveGraphDefinition
- executionProfile: ExecutionProfile
- shapeProfile: ShapeProfile?
- policyDecisions: Map<String, PolicyDecision>
- metadata: Map<String, Object>

**Visibility:**
- Configuration: NO
- Compilation: YES (input to further stages)
- Effective: YES (source)
- Runtime: NO (must never reach)

---

## EffectiveEdgeDefinition
**Layer:** Effective Definition  
**Purpose:** Edge after policies applied  
**Reusable:** NO  

**Fields:**
- edgeId: String
- sourceAccessor: AccessorReference (may be modified by policy)
- targetAccessor: AccessorReference (may be modified by policy)
- behavior: BehaviorReference (may be modified by policy)
- strategy: StrategyReference (may be modified by policy)
- childNode: String? (may be modified by policy)

---

## EffectiveGraphDefinition
**Layer:** Effective Definition  
**Purpose:** Graph after policies applied  
**Reusable:** NO  

**Fields:**
- graphId: String
- nodes: Map<String, EffectiveNodeDefinition>
- edges: Map<String, EffectiveEdgeDefinition>
- rootNodeId: String

---

## EffectiveNodeDefinition
**Layer:** Effective Definition  
**Purpose:** Node after policies applied  
**Reusable:** NO  

**Fields:**
- nodeId: String
- sourceType: ResolvedType (resolved, not reference)
- targetType: ResolvedType (resolved, not reference)
- shape: ResolvedShape (resolved, not reference)
- nodeBehavior: BehaviorReference (mandatory, policy-modified)
- collectionHandling: CollectionHandlingMode (mandatory, policy-inferred)

**Differences from ComparisonNodeDefinition:**
- Types are resolved (not references)
- Shape is resolved (not reference)
- collectionHandling is mandatory (not optional)
- nodeBehavior is mandatory (not optional)

---

## ExecutionProfile
**Layer:** Effective Definition  
**Purpose:** Carry execution configuration through compilation  
**Reusable:** NO  

**Fields:**
- executionSemantics: ExecutionSemantics
- executionLimits: ExecutionLimits
- errorHandling: ErrorHandlingMode
- cycleHandling: CycleHandlingMode
- streamingEnabled: boolean

---

## PolicyDecision
**Layer:** Effective Definition  
**Purpose:** Record which policies affected which components  
**Reusable:** NO  

**Fields:**
- targetId: String (node or edge id affected)
- appliedPolicies: List<String> (which policies affected this)
- ruleMatches: List<RuleMatch> (which rules matched)
- changedFields: Map<String, Object> (what was changed)
- timestamp: long (when decision was made)

---

## ShapeProfile
**Layer:** Effective Definition  
**Purpose:** Carry shape information through compilation  
**Reusable:** NO  

**Fields:**
- resolvedShapes: Map<String, ResolvedShape>
- shapeCompatibility: Map<String, ShapeCompatibilityStatus>

---

# COMPILATION LAYER COMPONENTS

## CompilationContext
**Layer:** Compilation  
**Purpose:** Carry state during compilation  
**Reusable:** NO  

**Fields:**
- definition: ComparisonGraphDefinition
- providers: ProviderRegistry
- cache: ResolutionCache
- diagnostics: List<CompilationDiagnostic>
- metadata: Map<String, Object>

**Methods:**
- addDiagnostic(diagnostic): void
- cacheResolution(key, value): void
- getResolution(key): Object?
- hasErrors(): boolean

---

## CompilationReport
**Layer:** Compilation  
**Purpose:** Report compilation results  
**Reusable:** NO  

**Fields:**
- success: boolean
- warnings: List<String>
- errors: List<String>
- compilerVersion: String
- compiledAt: long
- duration: long
- statistics: CompilationStatistics

---

## ResolvedShape
**Layer:** Compilation  
**Purpose:** Shape after resolution  
**Reusable:** YES (structural)  

**Fields:**
- reference: ShapeReference
- descriptor: ShapeDescriptor
- resolvedAt: long
- providerVersion: String
- providerName: String

---

## ResolvedType
**Layer:** Compilation  
**Purpose:** Type after resolution  
**Reusable:** YES (structural)  

**Fields:**
- reference: TypeReference
- descriptor: TypeDescriptor
- resolvedAt: long
- providerVersion: String
- providerName: String

---

# RUNTIME LAYER COMPONENTS

## CompiledAccessor
**Layer:** Runtime  
**Purpose:** Executable value extraction  
**Reusable:** YES (stored and reused)  

**Methods:**
- execute(source: Object): Object
- getMetadata(): Map<String, Object>

**Properties:**
- Stateless (no instance state)
- Thread-safe (safe to share)
- Immutable (no modifications)

---

## CompiledBehavior
**Layer:** Runtime  
**Purpose:** Executable behavior  
**Reusable:** YES (stored in plan)  

**For EdgeBehavior:**
- execute(sourceValue, targetValue, context, strategyCallback): ExecutionOutcome

**For NodeBehavior:**
- orchestrate(sourceValue, targetValue, context, edgeCallback): ExecutionOutcome

**Properties:**
- Stateless
- Immutable
- Reusable

---

## CompiledComparisonPlan
**Layer:** Runtime  
**Purpose:** Complete, executable comparison plan  
**Reusable:** YES (cached)  
**Package:** com.checkaboy.comparison.runtime  

**Fields:**
- planId: String
- rootNodePlan: CompiledNodePlan
- nodeIndex: Map<String, CompiledNodePlan>
- edgeIndex: Map<String, CompiledEdgePlan>
- executionSemantics: ExecutionSemantics
- identity: PlanIdentity (SHA-256 fingerprint)
- compiledAt: long

**Lifecycle:**
- Creation: Produced by Compilation layer
- Ownership: Runtime layer
- Caching: Cached by PlanCache using identity
- Visibility: Runtime only

---

## CompiledEdgePlan
**Layer:** Runtime  
**Purpose:** Compiled edge execution plan  
**Reusable:** Stored in plan  

**Fields:**
- edgeId: String
- sourceAccessor: CompiledAccessor
- targetAccessor: CompiledAccessor
- behavior: CompiledBehavior
- strategy: ComparisonStrategy
- childNodeId: String?

---

## CompiledNodePlan
**Layer:** Runtime  
**Purpose:** Compiled node execution plan  
**Reusable:** Stored in plan  

**Fields:**
- nodeId: String
- nodeDefinition: EffectiveNodeDefinition
- nodeBehavior: CompiledNodeBehavior
- edgeReferences: List<String>
- childNodeId: String?

---

## ComparisonResultTree
**Layer:** Runtime  
**Purpose:** Result of comparison  
**Reusable:** NO (owned by result collector)  

**Fields:**
- rootResult: ResultNode
- status: ResultStatus
- differences: List<DifferenceNode>
- executionMetadata: Map<String, Object>

---

## ComparisonStrategy
**Layer:** Runtime  
**Purpose:** Terminal comparison logic  
**Reusable:** Stored in plan  
**Package:** com.checkaboy.comparison.runtime  

**Methods:**
- compare(sourceValue, targetValue, context): ExecutionOutcome

**Properties:**
- Stateless
- Thread-safe
- Reusable

---

## DifferenceNode
**Layer:** Runtime  
**Purpose:** Record a specific difference  

**Fields:**
- path: ComparisonPath (location in tree)
- type: DifferenceType (VALUE, TYPE, STRUCTURE, MISSING, EXTRA)
- sourceValue: Object?
- targetValue: Object?
- detail: String? (explanation)
- nestedDifferences: List<DifferenceNode>?

---

## ExecutionContext
**Layer:** Runtime  
**Purpose:** Immutable configuration for execution  
**Reusable:** Passed through all executors  
**Package:** com.checkaboy.comparison.runtime  

**Fields (all final):**
- currentPath: ComparisonPath
- remainingLimits: ExecutionLimits
- executionSemantics: ExecutionSemantics
- resultSink: ResultSink
- accessorCache: AccessorResultCache
- shapeResolver: ShapeResolver
- metadata: Map<String, Object>

**Properties:**
- Immutable (enforced by final fields)
- Thread-safe (can be shared)
- No setters

---

## ExecutionOutcome
**Layer:** Runtime  
**Purpose:** Result of executing a node or edge  

**Fields:**
- eventId: String
- timestamp: long
- eventType: ExecutionEventType
- status: ResultStatus
- sourceValue: Object?
- targetValue: Object?
- extractedSource: Object?
- extractedTarget: Object?
- difference: Difference?
- error: ExecutionError?
- path: ComparisonPath
- metadata: Map<String, Object>

---

## ExecutionSemantics
**Layer:** Runtime  
**Purpose:** Configuration controlling execution behavior  

**Fields:**
- executionMode: ExecutionMode
- timeout: long?
- maxDepth: int
- maxBreadth: int
- errorHandling: ErrorHandlingMode
- cycleHandling: CycleHandlingMode
- resultPolicy: ResultPolicy

**Lifecycle:**
- Creation: Compilation layer produces
- Ownership: Runtime layer
- Immutability: Immutable at runtime
- Visibility: Runtime only

---

## ResultNode
**Layer:** Runtime  
**Purpose:** Node in result tree  

**Fields:**
- nodeId: String
- status: ResultStatus
- sourceValue: Object?
- targetValue: Object?
- differences: List<DifferenceNode>
- childResults: List<ResultNode>
- executionTime: long

---

# SPI LAYER COMPONENTS

## AccessorProvider
**Layer:** SPI  
**Purpose:** Supply accessor implementations  
**Reusable:** YES (structural)  
**Package:** com.checkaboy.structural.accessor  

**Methods:**
- canHandle(ref, type, context): boolean
- compile(ref, type, context): CompiledAccessor
- validate(def, context): ValidationResult? [optional]

---

## BehaviorProvider
**Layer:** SPI  
**Purpose:** Supply behavior implementations  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.spi  

**Methods:**
- canHandle(ref, graph, context): boolean
- compile(ref, graph, context): CompiledBehavior

---

## StrategyProvider
**Layer:** SPI  
**Purpose:** Supply strategy implementations  
**Reusable:** NO (comparison-specific)  
**Package:** com.checkaboy.comparison.spi  

**Methods:**
- canHandle(ref, srcType, tgtType, context): boolean
- compile(ref, srcType, tgtType, context): ComparisonStrategy

---

## TypeProvider
**Layer:** SPI  
**Purpose:** Supply type resolution  
**Reusable:** YES (structural, optional)  
**Package:** com.checkaboy.structural.type  

**Methods:**
- canResolve(ref, context): boolean
- resolve(ref, context): TypeDescriptor
- inferType(instance, context): TypeDescriptor

---

# INFRASTRUCTURE COMPONENTS

## AccessorResultCache
**Layer:** Infrastructure  
**Purpose:** Cache extracted values during execution  
**Package:** com.checkaboy.comparison.runtime  

**Methods:**
- getCachedOrCompute(accessor, source): Object
- get(key): Object?
- put(key, value): void

**Properties:**
- Per-execution scope
- Reduces redundant extraction
- Cleared after execution

---

## ComparisonPath
**Layer:** Infrastructure  
**Purpose:** Track location in comparison tree  
**Reusable:** YES (structural)  
**Package:** com.checkaboy.structural.path  

**Methods:**
- append(segment): ComparisonPath
- removeLast(): ComparisonPath
- toString(): String

**Example paths:**
- "root"
- "root.order"
- "root.order.customer"
- "root.order.items[0]"
- "root.order.items[0].product"

---

## CycleGuard
**Layer:** Infrastructure  
**Purpose:** Detect cycles during traversal  
**Package:** com.checkaboy.comparison.runtime  

**Methods:**
- canTraverse(nodeId, src, tgt): boolean
- recordTraversal(nodeId, src, tgt): void

**Properties:**
- Tracks traversed (node, source, target) tuples
- Detects cycles by object identity
- Throws exception or skips based on semantics

---

## ExecutionStateManager
**Layer:** Infrastructure  
**Purpose:** Manage traversal state during execution  
**Package:** com.checkaboy.comparison.runtime  
**Visibility:** Package-private (not in public API)  

**Methods (internal only):**
- canTraverse(nodeId, src, tgt): boolean
- recordTraversal(nodeId, src, tgt): void
- beginPath(segment): void
- endPath(): void

**Properties:**
- Mutable during execution
- Hidden from executors
- Prevents state leakage

---

## PlanCache
**Layer:** Infrastructure  
**Purpose:** Cache compiled plans  
**Package:** com.checkaboy.comparison.runtime  

**Implementations:**
- LocalPlanCache (in-memory, LRU eviction)
- DistributedPlanCache (abstract, for Redis/Memcached)

**Methods:**
- get(fingerprint): CompiledComparisonPlan?
- put(fingerprint, plan): void
- invalidate(fingerprint): void

---

## PlanIdentity
**Layer:** Infrastructure  
**Purpose:** Uniquely identify a compiled plan  
**Package:** com.checkaboy.comparison.runtime  

**Fields:**
- planId: String
- planHash: String (SHA-256)
- compiledAt: long
- compilerVersion: String
- providerVersions: Map<String, String>

**Used for:** Plan caching, cache invalidation

---

## ResultSink
**Layer:** Infrastructure  
**Purpose:** Collect or stream results  
**Package:** com.checkaboy.comparison.runtime  

**Implementations:**
- MemoryResultSink (buffers results in memory)
- StreamingResultSink (writes results to stream)

**Methods:**
- reportOutcome(outcome): void
- getResult(): ComparisonResultTree [memory only]

---

---

**COMPONENT REFERENCE COMPLETE**

Total Components: 60+
All documented with lifecycle, visibility, reusability, and dependencies.

