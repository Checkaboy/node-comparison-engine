# Declarative Comparison Engine V4
## COMPLETE IMPLEMENTATION SPECIFICATION

**Document Purpose:** Implementation-grade technical documentation  
**Scope:** Every model, contract, lifecycle, responsibility, and interaction  
**Audience:** Implementation teams, architects, code generators  
**Status:** SPECIFICATION READY  

---

# TABLE OF CONTENTS

1. [PART 1: OVERVIEW & PRINCIPLES](#part-1-overview--principles)
2. [PART 2: CONFIGURATION LAYER](#part-2-configuration-layer)
3. [PART 3: EFFECTIVE DEFINITION LAYER](#part-3-effective-definition-layer)
4. [PART 4: COMPILATION LAYER](#part-4-compilation-layer)
5. [PART 5: SHAPE SYSTEM](#part-5-shape-system)
6. [PART 6: RUNTIME LAYER](#part-6-runtime-layer)
7. [PART 7: SPI SPECIFICATION](#part-7-spi-specification)
8. [PART 8: EXECUTION SEMANTICS](#part-8-execution-semantics)
9. [PART 9: RESULT MODEL](#part-9-result-model)
10. [PART 10: ARCHITECTURAL INVARIANTS](#part-10-architectural-invariants)
11. [PART 11: PACKAGE STRUCTURE](#part-11-package-structure)
12. [PART 12: EXECUTION FLOWS](#part-12-execution-flows)
13. [PART 13: V5 BOUNDARY](#part-13-v5-boundary)

---

# PART 1: OVERVIEW & PRINCIPLES

## Core Principles (Non-Negotiable)

```
1. Graph-first architecture
   - ComparisonGraphDefinition is primary model
   - Object DSL is facade only
   
2. Configuration describes
   - Raw metadata specifies intent
   - No business logic in configuration
   
3. Compilation decides
   - All decisions made at compile time
   - Runtime has no discretion
   
4. Runtime executes
   - Runtime is fully precompiled
   - No provider lookups at runtime
   - No metadata interpretation at runtime
   
5. Provider-owned semantics
   - SPI providers supply behavior
   - Core does not implement
   
6. Metadata-free runtime
   - No configuration read at runtime
   - Runtime artifact is immutable
   
7. Execution is deterministic
   - Same inputs → same execution path
   - No random variation
   - Reproducible results
   
8. Immutable at runtime
   - No mutations during execution
   - ExecutionContext is immutable
   - Plans are immutable
```

---

## Layered Architecture

```
CONFIGURATION LAYER
├─ Human-readable definitions
├─ Validation only (no compilation)
└─ Can be in JSON, YAML, DSL

EFFECTIVE DEFINITION LAYER
├─ Configuration + policies applied
├─ Normalized and validated
└─ Intermediate representation

COMPILATION LAYER
├─ Resolves all references
├─ Validates all relationships
├─ Produces runtime artifacts

RUNTIME LAYER
├─ Fully precompiled
├─ Immutable execution
├─ Registry-free execution
```

---

# PART 2: CONFIGURATION LAYER

## 2.1 ComparisonGraphDefinition

### Purpose
Declare a comparison graph structure in human-readable form.

### Responsibility
- Define nodes in graph
- Define edges (relationships between nodes)
- Reference behaviors and strategies
- Specify traversal rules

NOT ALLOWED:
- Include compilation logic
- Include policy rules (use PolicyDefinition)
- Include execution semantics (use ExecutionPolicy)

### Lifecycle
- **Creation:** From external source (JSON, YAML, DSL)
- **Ownership:** Configuration layer only
- **Mutability:** Immutable (final fields)
- **Visibility:** Configuration layer only (never reaches runtime)
- **Validation:** Structural validation only

### Dependencies
```
ComparisonGraphDefinition depends on:
✓ ComparisonNodeDefinition
✓ ComparisonEdgeDefinition
✓ TypeReference
✓ ShapeReference
✓ AccessorReference
✓ BehaviorReference
✓ StrategyReference

ComparisonGraphDefinition must NOT depend on:
✗ Policy system
✗ Compilation artifacts
✗ Runtime artifacts
✗ Execution artifacts
```

### Public Contract

```
Fields:
  graphId: String (unique identifier)
  nodes: Map<String, ComparisonNodeDefinition>
  edges: Map<String, ComparisonEdgeDefinition>
  rootNodeId: String
  metadata: Map<String, Object>
  schemaVersion: String

Invariants:
  - graphId must be non-empty
  - rootNodeId must reference existing node
  - No cyclic node references (in node hierarchy)
  - All referenced accessors must be valid paths
```

### Runtime Visibility
- **Configuration:** YES (source)
- **Compilation:** YES (input to compiler)
- **Effective Definition:** NO (replaced by EffectiveGraphDefinition)
- **Runtime:** NO (must never reach)
- **Infrastructure:** NO

---

## 2.2 ComparisonNodeDefinition

### Purpose
Define a single node in the comparison graph.

### Responsibility
- Specify node identity
- Specify source and target types
- Specify shape for traversal
- Reference node behavior
- Declare edges

### Lifecycle
- **Creation:** Part of ComparisonGraphDefinition
- **Ownership:** Configuration layer
- **Mutability:** Immutable
- **Visibility:** Configuration layer only

### Public Contract

```
Fields:
  nodeId: String
  sourceType: TypeReference
  targetType: TypeReference
  shape: ShapeReference
  nodeBehavior: BehaviorReference? (optional)
  collectionHandling: CollectionHandlingMode? (optional)
  metadata: Map<String, Object>

Invariants:
  - nodeId must be non-empty
  - sourceType must be resolvable
  - targetType must be resolvable
  - shape must be resolvable
  - If collectionHandling specified, must be valid enum
```

---

## 2.3 ComparisonEdgeDefinition

### Purpose
Define an extraction and comparison step from one value to another.

### Responsibility
- Specify accessor paths
- Reference behavior
- Reference strategy
- Optionally reference child node

### Public Contract

```
Fields:
  edgeId: String
  sourceAccessor: AccessorReference
  targetAccessor: AccessorReference
  behavior: BehaviorReference
  strategy: StrategyReference
  childNode: String? (optional node to recursively compare)
  metadata: Map<String, Object>

Invariants:
  - edgeId must be non-empty
  - sourceAccessor must be valid
  - targetAccessor must be valid
  - If childNode specified, must reference existing node
```

---

## 2.4 AccessorReference

### Purpose
Reference a value extraction mechanism (property, method, etc.)

### Responsibility
- Identify extraction source
- Specify extraction path
- Carry metadata about extraction

### Public Contract

```
Fields:
  accessorId: String
  path: String (e.g., "customer.address.street")
  providerType: String? (if custom provider)
  metadata: Map<String, String>

Invariants:
  - accessorId must be non-empty
  - path must be valid expression
```

---

## 2.5 BehaviorReference

### Purpose
Reference a behavior implementation from provider.

### Responsibility
- Identify behavior
- Carry behavior parameters
- Link to behavior definition

### Public Contract

```
Fields:
  behaviorId: String
  parameters: Map<String, Object>? (behavior-specific config)
  metadata: Map<String, Object>

Invariants:
  - behaviorId must be non-empty
  - parameters must be valid for referenced behavior
```

---

## 2.6 StrategyReference

### Purpose
Reference a comparison strategy from provider.

### Responsibility
- Identify terminal strategy
- Carry strategy configuration

### Public Contract

```
Fields:
  strategyId: String
  configuration: Map<String, Object>? (strategy-specific config)
  metadata: Map<String, Object>

Invariants:
  - strategyId must be non-empty
```

---

## 2.7 PolicyDefinition

### Purpose
Declare conditional rules that modify effective graph.

### Responsibility
- Define conditions
- Define transformations
- Modify graph structure
- Record decisions

### Public Contract

```
Fields:
  policyId: String
  rules: List<PolicyRule>
  metadata: Map<String, Object>

Invariants:
  - policyId must be non-empty
  - rules must be non-empty
  - All rule conditions must be valid
```

### PolicyRule

```
Fields:
  ruleId: String
  condition: RuleCondition
  actions: List<RuleAction>
  priority: int (higher = apply first)

Invariants:
  - At least one action must be specified
```

---

## 2.8 ExecutionPolicy

### Purpose
Configure execution behavior (fail-fast, collect-all, limits, etc.)

### Public Contract

```
Fields:
  executionMode: ExecutionMode (FAIL_FAST or COLLECT_ALL)
  timeout: long? (milliseconds, null = no timeout)
  maxDepth: int? (null = no limit)
  maxBreadth: int? (null = no limit)
  errorHandling: ErrorHandlingMode
  cycleHandling: CycleHandlingMode
  streamingEnabled: boolean
  maxDifferences: int? (truncate after limit)
  metadata: Map<String, Object>

Invariants:
  - executionMode must not be null
  - maxDepth (if specified) must be > 0
  - maxBreadth (if specified) must be > 0
  - timeout (if specified) must be > 0
```

---

# PART 3: EFFECTIVE DEFINITION LAYER

## 3.1 EffectiveComparisonDefinition

### Purpose
Represent configuration after policy application.

### Responsibility
- Hold effective graph
- Carry policy decisions (audit trail)
- Carry execution configuration
- Carry shape information

### Lifecycle
- **Creation:** From PolicyCompiler
- **Ownership:** Compilation layer
- **Mutability:** Immutable
- **Visibility:** Compilation only (never reaches runtime)
- **Caching:** Not cached (policies may change)

### Public Contract

```
Fields:
  effectiveGraph: EffectiveGraphDefinition
  executionProfile: ExecutionProfile
  shapeProfile: ShapeProfile? (optional)
  policyDecisions: Map<String, PolicyDecision>
  metadata: Map<String, Object>

Invariants:
  - effectiveGraph must not be null
  - All policy decisions must be recorded
```

---

## 3.2 EffectiveGraphDefinition

### Purpose
Graph structure after policies applied.

### Public Contract

```
Fields:
  graphId: String
  nodes: Map<String, EffectiveNodeDefinition>
  edges: Map<String, EffectiveEdgeDefinition>
  rootNodeId: String
  schemaVersion: String
  metadata: Map<String, Object>

Invariants:
  - All edges must reference valid nodes
  - All accessors must be resolvable
  - All behaviors must be resolvable
  - rootNodeId must reference existing node
```

---

## 3.3 EffectiveNodeDefinition

### Purpose
Node definition after policies applied.

### Public Contract

```
Fields:
  nodeId: String
  sourceType: ResolvedType
  targetType: ResolvedType
  shape: ResolvedShape
  nodeBehavior: BehaviorReference (may be modified by policy)
  collectionHandling: CollectionHandlingMode (must be specified)
  metadata: Map<String, Object>

Differences from ComparisonNodeDefinition:
  - Types are resolved (not references)
  - Shape is resolved (not reference)
  - collectionHandling is mandatory (not optional)
  - nodeBehavior is mandatory (not optional)
```

---

## 3.4 PolicyDecision

### Purpose
Record which policies affected which nodes/edges.

### Public Contract

```
Fields:
  targetId: String (node or edge id)
  appliedPolicies: List<String>
  ruleMatches: List<RuleMatch>
  changedFields: Map<String, Object>
  timestamp: long
  metadata: Map<String, Object>

Purpose: Audit trail, troubleshooting, debugging
```

---

# PART 4: COMPILATION LAYER

## 4.1 CompilationContext

### Purpose
Carry state during compilation process.

### Responsibility
- Hold provider registries
- Track compilation progress
- Collect diagnostics
- Maintain resolution cache

### Lifecycle
- **Creation:** CompilationEntry point
- **Ownership:** Compilation layer
- **Mutability:** Mutable during compilation, immutable after
- **Visibility:** Compilation layer only
- **Disposal:** Disposed after compilation

### Public Contract

```
Fields:
  definition: ComparisonGraphDefinition
  providers: ProviderRegistry
  cache: ResolutionCache
  diagnostics: List<CompilationDiagnostic>
  metadata: Map<String, Object>

Methods:
  addDiagnostic(diagnostic)
  cacheResolution(key, value)
  getResolution(key)
  hasErrors(): boolean
  getReport(): CompilationReport
```

---

## 4.2 CompilationReport

### Purpose
Report results of compilation (success or failure).

### Public Contract

```
Fields:
  success: boolean
  warnings: List<String>
  errors: List<String>
  compilerVersion: String
  compiledAt: long
  duration: long
  statistics: CompilationStatistics

Methods:
  isValid(): boolean
  getWarningCount(): int
  getErrorCount(): int
```

---

## 4.3 Compilation Pipeline

### Stage 1: Definition Validation

**Input:** ComparisonGraphDefinition  
**Output:** Validation report

**Responsibilities:**
- Validate graph structure
- Validate all references exist
- Validate type compatibility
- Validate accessor expressions

**Validation Rules:**
- graphId non-empty
- All node IDs unique
- All edge IDs unique
- rootNodeId references existing node
- All type references valid
- All shape references valid
- All accessor paths valid
- All behavior references valid
- All strategy references valid

---

### Stage 2: Definition Normalization

**Input:** Validated ComparisonGraphDefinition  
**Output:** Normalized ComparisonGraphDefinition

**Responsibilities:**
- Resolve aliases
- Resolve imports
- Expand inheritance
- Translate Object DSL to graph DSL

**Output:** ComparisonGraphDefinition (normalized)

---

### Stage 3: Structural Resolution (Type, Shape, Accessor, Path)

**Inputs:**
- Normalized ComparisonGraphDefinition
- TypeProvider, ShapeProvider, AccessorProvider

**Outputs:**
- Annotated graph with ResolvedType, ResolvedShape, CompiledAccessor

**Type Resolution:**
- Invoke TypeProvider for each TypeReference
- Cache resolved types
- Validate type compatibility

**Shape Resolution:**
- Invoke ShapeProvider for each ShapeReference
- Cache resolved shapes
- Validate shape/type compatibility

**Accessor Resolution & Compilation:**
- Invoke AccessorProvider for each AccessorReference
- Compile to CompiledAccessor
- Validate accessor/shape compatibility

**Path Resolution:**
- Parse all PathPattern expressions
- Validate path expressions
- Compile path matchers

**Output:** Annotated graph with all structural metadata

---

### Stage 4: Policy Application

**Input:** Annotated graph + PolicyDefinition  
**Output:** EffectiveGraphDefinition

**Responsibilities:**
- Evaluate policy conditions
- Apply policy transformations
- Record policy decisions
- Modify effective graph

**Output:** EffectiveGraphDefinition with inline policy decisions

---

### Stage 5: Collection Handling Strategy Compilation

**Input:** EffectiveGraphDefinition  
**Output:** Annotated with collection strategies

**Responsibilities:**
- For each node with collection handling:
  - Select matching strategy (LCS, edit-distance, identity, etc.)
  - Validate strategy compatibility
  - Compile strategy reference into behavior

**Output:** Effective graph with strategies compiled into behaviors

---

### Stage 6: Behavior Compilation

**Input:** Annotated graph + BehaviorProvider  
**Output:** Graph with CompiledNodeBehavior and CompiledBehavior

**Responsibilities:**
- Resolve BehaviorReference → BehaviorDefinition
- Invoke BehaviorProvider.compile()
- Produce CompiledNodeBehavior
- Produce CompiledBehavior (for edges)

**Output:** Compiled behaviors embedded in graph

---

### Stage 7: Strategy Compilation

**Input:** Graph with strategy references + StrategyProvider  
**Output:** Graph with compiled strategies

**Responsibilities:**
- Resolve StrategyReference → StrategyDefinition
- Invoke StrategyProvider.compile()
- Produce ComparisonStrategy
- Embed in graph

**Output:** Compiled strategies ready for runtime

---

### Stage 8: Execution Semantics Compilation

**Input:** ExecutionPolicy  
**Output:** ExecutionSemantics

**Responsibilities:**
- Validate execution policy constraints
- Produce ExecutionSemantics (immutable)
- Validate limits compatibility with graph depth

**Output:** ExecutionSemantics configuration

---

### Stage 9: Composition & Validation

**Input:** Effective graph, execution semantics, shape info  
**Output:** Validated EffectiveComposition

**Responsibilities:**
- Assemble all components
- Invoke CompositionValidator
- Validate cross-partition contracts
- Verify invariants

**Output:** Validated EffectiveComposition

---

### Stage 10: Runtime Plan Assembly

**Input:** Compiled components + EffectiveComposition  
**Output:** CompiledComparisonPlan

**Responsibilities:**
- Assemble CompiledComparisonPlan
- Assemble CompiledNodePlan(s)
- Assemble CompiledEdgePlan(s)
- Compute PlanIdentity (SHA-256 fingerprint)
- Validate plan completeness

**Output:** CompiledComparisonPlan (ready for caching and execution)

---

# PART 5: SHAPE SYSTEM

## 5.1 Purpose

Shape describes HOW a structure can be navigated.  
Shape does NOT describe WHAT a structure should be compared as.

### Type vs Shape

```
TYPE SYSTEM:
- Describes data structure
- Identifies types
- Type compatibility rules
- Answers: "What is this?"

SHAPE SYSTEM:
- Describes navigation capability
- Describes traversal rules
- Describes accessor compatibility
- Answers: "How can I traverse this?"
```

---

## 5.2 ShapeDescriptor

### Purpose
Describe structural properties of a type.

### Responsibility
- Declare element type (OBJECT, COLLECTION, KEYED, SCALAR)
- List properties (for OBJECT)
- Declare ordering (for COLLECTION)
- Declare keying (for KEYED)
- Declare traversal capabilities

### Public Contract

```
Fields:
  shapeId: String
  elementType: ElementType (OBJECT, COLLECTION, KEYED, SCALAR)
  properties: Map<String, PropertyShape>? (if OBJECT)
  collectionElementType: ShapeDescriptor? (if COLLECTION)
  isOrdered: boolean? (if COLLECTION)
  isKeyed: boolean? (if KEYED)
  keyExtractor: AccessorReference? (if KEYED)
  capabilities: ShapeCapabilities
  metadata: Map<String, Object>

Invariants:
  - elementType must match actual structure
  - Properties consistent with type
  - Collection element type resolvable
```

---

## 5.3 ResolvedShape

### Purpose
Shape after resolution and validation.

### Lifecycle
- **Creation:** ShapeProvider.infer() or ShapeProvider.resolve()
- **Ownership:** Compilation layer
- **Mutability:** Immutable
- **Caching:** Cached in compilation context
- **Visibility:** Compilation + Effective Definition

### Public Contract

```
Fields:
  descriptor: ShapeDescriptor
  resolvedAt: long
  providerVersion: String
  providerName: String
  metadata: Map<String, Object>
```

---

## 5.4 ShapeProvider (SPI)

### Purpose
Supply shape inference and resolution.

### When Implemented
- Custom type systems
- Domain-specific shapes
- Advanced shape capabilities

### Contract

```
Method: canResolve(ref: ShapeReference, type: TypeDescriptor): boolean
  - Return true if this provider handles the reference

Method: infer(type: TypeDescriptor): ShapeDescriptor
  - Infer shape from type
  - Required for all types

Method: validate(shape: ShapeDescriptor): ValidationResult
  - Validate shape correctness
  - Optional validation
```

---

## 5.5 Future Structural Platform Candidates

These are ready for V5 extraction to standalone platform:

```
CANDIDATE FOR EXTRACTION:
- Type system (fully reusable)
- Shape system (fully reusable)
- Accessor system (fully reusable)
- Path system (fully reusable)
- Traversal system (fully reusable)
- TypeProvider SPI
- AccessorProvider SPI
- (optionally) ShapeProvider SPI
```

---

# PART 6: RUNTIME LAYER

## 6.1 CompiledComparisonPlan

### Purpose
Complete, precompiled comparison plan ready for execution.

### Responsibility
- Hold compiled graph
- Hold execution configuration
- Hold plan identity (for caching)
- Provide entry point to execution

### Lifecycle
- **Creation:** Compilation layer produces
- **Ownership:** Runtime layer caches
- **Mutability:** Immutable
- **Visibility:** Runtime layer
- **Caching:** Cached by PlanCache

### Public Contract

```
Fields:
  planId: String
  rootNodePlan: CompiledNodePlan
  nodeIndex: Map<String, CompiledNodePlan>
  edgeIndex: Map<String, CompiledEdgePlan>
  executionSemantics: ExecutionSemantics
  identity: PlanIdentity
  compiledAt: long
  metadata: Map<String, Object>

Methods:
  getRootNodePlan(): CompiledNodePlan
  getNodePlan(nodeId): CompiledNodePlan
  getEdgePlan(edgeId): CompiledEdgePlan
  getExecutionSemantics(): ExecutionSemantics
  getIdentity(): PlanIdentity
```

---

## 6.2 CompiledNodePlan

### Purpose
Compiled specification for executing a node comparison.

### Public Contract

```
Fields:
  nodeId: String
  nodeDefinition: EffectiveNodeDefinition
  nodeBehavior: CompiledNodeBehavior
  edgeReferences: List<String> (edges to execute)
  childNodeId: String? (if recursive)
  metadata: Map<String, Object>

Methods:
  getNodeId(): String
  getNodeBehavior(): CompiledNodeBehavior
  getEdgeReferences(): List<String>
```

---

## 6.3 CompiledEdgePlan

### Purpose
Compiled specification for executing an edge extraction and comparison.

### Public Contract

```
Fields:
  edgeId: String
  sourceAccessor: CompiledAccessor
  targetAccessor: CompiledAccessor
  behavior: CompiledBehavior
  strategy: ComparisonStrategy
  childNodeId: String? (if recursive)
  metadata: Map<String, Object>

Methods:
  getSourceAccessor(): CompiledAccessor
  getTargetAccessor(): CompiledAccessor
  getBehavior(): CompiledBehavior
  getStrategy(): ComparisonStrategy
```

---

## 6.4 ExecutionContext

### Purpose
Immutable runtime configuration for executors.

### Lifecycle
- **Creation:** ComparisonExecutor at execution start
- **Ownership:** Runtime layer
- **Mutability:** IMMUTABLE (enforced by final fields)
- **Visibility:** Runtime only
- **Disposal:** Automatic (cleaned up after execution)

### Public Contract

```
Fields (all final):
  currentPath: ComparisonPath
  remainingLimits: ExecutionLimits
  executionSemantics: ExecutionSemantics
  resultSink: ResultSink
  accessorCache: AccessorResultCache
  shapeResolver: ShapeResolver
  metadata: Map<String, Object>

Invariants:
  - No setters (enforced by immutability)
  - All fields final
  - Thread-safe to share across executors
```

---

## 6.5 GraphTraversalState

### Purpose
Track traversal progress during execution.

### Lifecycle
- **Creation:** ExecutionStateManager at execution start
- **Ownership:** ExecutionStateManager (internal)
- **Mutability:** Mutable
- **Visibility:** Internal (ExecutionStateManager only)

### Public Contract

```
Fields:
  traversalPath: Stack<ComparisonPath>
  cycleGuard: CycleGuard
  traversalHistory: Set<TraversalKey>
  currentDepth: int
  currentBreadth: int

Methods (internal only):
  canTraverse(nodeId, src, tgt): boolean
  recordTraversal(nodeId, src, tgt): void
  beginPath(segment): void
  endPath(): void
```

---

## 6.6 ComparisonResultTree

### Purpose
Hierarchical result of comparison.

### Lifecycle
- **Creation:** ResultCollector during execution
- **Ownership:** Runtime layer
- **Mutability:** Immutable after completion
- **Visibility:** Runtime layer
- **Disposal:** GC eligible after use

### Public Contract

```
Fields:
  rootResult: ResultNode
  status: ResultStatus
  differences: List<DifferenceNode>
  executionMetadata: Map<String, Object>

Methods:
  getStatus(): ResultStatus
  getDifferences(): List<DifferenceNode>
  getAllDifferences(): List<DifferenceNode> (flattened)
  getPath(diffNode): ComparisonPath
```

---

## 6.7 ResultNode

### Purpose
Node in result tree (corresponds to ComparisonNodeDefinition).

### Public Contract

```
Fields:
  nodeId: String
  status: ResultStatus
  sourceValue: Object?
  targetValue: Object?
  differences: List<DifferenceNode>
  childResults: List<ResultNode>
  executionTime: long
  metadata: Map<String, Object>
```

---

## 6.8 DifferenceNode

### Purpose
Record a specific difference found during comparison.

### Public Contract

```
Fields:
  path: ComparisonPath
  type: DifferenceType (VALUE, TYPE, STRUCTURE, MISSING, EXTRA)
  sourceValue: Object?
  targetValue: Object?
  detail: String?
  nestedDifferences: List<DifferenceNode>?
  metadata: Map<String, Object>
```

---

# PART 7: SPI SPECIFICATION

## 7.1 AccessorProvider (STRUCTURAL - Reusable)

### Purpose
Supply custom value extraction mechanisms.

### When Implemented
- Custom property access
- Remote accessors
- Database column access
- Virtual properties

### Lifecycle
- **Creation:** Compile-time
- **Visibility:** Structural Platform (reusable)
- **Caching:** Compiled accessors cached

### Contract

```
Method: canHandle(ref: AccessorReference, type: TypeDescriptor): boolean
  Return: Whether this provider handles this accessor type

Method: compile(ref, type, context): CompiledAccessor
  Input: AccessorReference, source type, compilation context
  Output: CompiledAccessor (stateless, reusable)
  Error: Throw AccessorCompilationException if invalid
  
Method: validate(def: AccessorDefinition): ValidationResult
  Input: AccessorDefinition
  Output: Validation result
  Purpose: Optional pre-compilation validation
```

### Compiled Accessor Contract

```
Method: execute(source: Object): Object throws AccessorException
  Input: Source object
  Output: Extracted value
  Error: Throw AccessorException if extraction fails
  
Method: getMetadata(): Map<String, Object>
  Output: Metadata about this accessor
```

---

## 7.2 BehaviorProvider (COMPARISON-SPECIFIC)

### Purpose
Supply behavior implementations for nodes and edges.

### When Implemented
- Custom orchestration
- Fallback chains
- Error recovery
- Performance optimizations

### Contract

```
Method: canHandle(ref: BehaviorReference, graph: EffectiveGraph): boolean
  Determine if this provider handles this behavior

Method: compile(ref, graph, context): CompiledBehavior
  Input: BehaviorReference, effective graph, context
  Output: CompiledBehavior (stateless, reusable)
  Error: Throw BehaviorCompilationException
  
Method: validate(def: BehaviorDefinition): ValidationResult
  Validate behavior definition
```

### CompiledBehavior Contracts

**For NodeBehavior:**

```
Method: orchestrate(source, target, context, edgeCallback): ExecutionOutcome
  Input: Source, target, execution context, edge callback
  Output: ExecutionOutcome (aggregated from edges)
  Responsibility: Invoke edges, aggregate results, handle errors
```

**For EdgeBehavior:**

```
Method: execute(srcVal, tgtVal, context, strategyCallback): ExecutionOutcome
  Input: Extracted values, context, strategy callback
  Output: ExecutionOutcome
  Responsibility: Invoke strategy, handle errors
```

---

## 7.3 StrategyProvider (COMPARISON-SPECIFIC)

### Purpose
Supply terminal comparison strategies.

### When Implemented
- Custom equality definitions
- Approximate matching
- Domain-specific comparison
- Performance optimizations

### Contract

```
Method: canHandle(ref, srcType, tgtType): boolean
  Determine if this provider handles this strategy

Method: compile(ref, srcType, tgtType, context): ComparisonStrategy
  Input: StrategyReference, types, context
  Output: ComparisonStrategy (stateless, reusable)
  Error: Throw StrategyCompilationException
```

### ComparisonStrategy Contract

```
Method: compare(sourceValue, targetValue, context): ExecutionOutcome
  Input: Values to compare, execution context
  Output: ExecutionOutcome (MATCH or MISMATCH)
  Responsibility: Determine match/mismatch, provide difference details
  Error: Throw StrategyException (wrapped by framework)
```

---

## 7.4 TypeProvider (STRUCTURAL - Optional)

### Purpose
Support custom type systems.

### When Implemented
- Custom type systems
- DSL type definitions
- External type registries

### Contract

```
Method: canResolve(ref: TypeReference): boolean
Method: resolve(ref: TypeReference, context): TypeDescriptor
Method: inferType(instance: Object): TypeDescriptor
```

---

## 7.5 Provider Responsibilities vs Core Responsibilities

**Core Responsibilities:**
```
- Orchestrate provider invocation
- Manage execution state
- Handle provider exceptions
- Enforce architectural invariants
- Manage traversal
```

**Provider Responsibilities:**
```
- Implement specific semantics
- Extract values (AccessorProvider)
- Orchestrate execution (BehaviorProvider)
- Compare values (StrategyProvider)
- Report errors appropriately
```

---

# PART 8: EXECUTION SEMANTICS

## 8.1 ExecutionSemantics

### Purpose
Configure execution behavior (fail-fast, collect-all, limits, errors).

### Public Contract

```
Fields:
  executionMode: ExecutionMode (FAIL_FAST or COLLECT_ALL)
  timeout: long? (ms, null = no limit)
  maxDepth: int (global depth limit)
  maxBreadth: int (per-node element limit)
  errorHandling: ErrorHandlingMode
  cycleHandling: CycleHandlingMode
  resultPolicy: ResultPolicy
  
ExecutionMode:
  FAIL_FAST: Stop on first mismatch or error
  COLLECT_ALL: Collect all differences
  
ErrorHandlingMode:
  FAIL_FAST: Stop on any error
  CONTINUE: Record error, continue
  
CycleHandlingMode:
  SKIP: Skip cycle, mark as skipped
  ERROR: Treat cycle as error
  RECORD: Record cycle but continue
```

---

## 8.2 Execution Error Handling

### Error Wrapping

```
Provider throws Exception
  ↓
Framework catches and wraps in ExecutionError
  ↓
Framework produces ExecutionOutcome with ERROR status
  ↓
ExecutionSemantics decides: continue or fail-fast
  ↓
Results reported to ResultSink
```

### ExecutionError

```
Fields:
  errorType: String (provider name)
  cause: Throwable?
  providerType: String
  providerId: String
  recoverable: boolean
  message: String
  context: Map<String, Object>
```

---

## 8.3 Partial Execution

**FAIL_FAST Mode:**
- First error/mismatch → stop
- Return partial result tree

**COLLECT_ALL Mode:**
- Collect all differences
- Collect all errors
- Continue to completion

---

# PART 9: RESULT MODEL

## 9.1 Result Representation

### Memory Representation
```
ComparisonResultTree (in memory)
├─ ResultNode (per node in graph)
│  ├─ status: MATCH | MISMATCH | ERROR | PARTIAL
│  ├─ differences: List<DifferenceNode>
│  └─ childResults: List<ResultNode>
└─ Flattened: List<DifferenceNode>
```

### Ownership
```
ComparisonResultTree owns:
- ResultNode hierarchy
- DifferenceNode details
- Execution metadata

Does NOT own:
- Source/target objects (references only)
```

---

## 9.2 Streaming Results

### Streaming Mode

```
ResultSink.onOutcomeCompleted(outcome)
  ↓ (immediately)
ResultWriter.write(outcome)
  ↓ (to stream)
No buffering
```

### Advantages
- Constant memory usage
- No result tree buffering
- Scales to large comparisons

### Trade-off
- Cannot traverse result tree
- Must process outcomes as they arrive

---

## 9.3 Projections from Result Tree

### Difference Projection

```
ComparisonResultTree
  ↓ (project)
List<DifferenceNode>
  ↓ (flat list)
All differences in execution order
```

### Match Projection

```
ComparisonResultTree
  ↓ (project)
List<String>
  ↓ (paths)
All matching paths
```

---

# PART 10: ARCHITECTURAL INVARIANTS

## Complete Invariant Catalog (26 Total)

### Core Principles (1-10)

**Invariant 1: Graph-First Architecture**
- Description: ComparisonGraphDefinition is primary model
- Why: Enables all other systems
- Violation: Object DSL becomes primary → complexity explosion
- Validation: Verify DSL translates to graph
- Stage: Compilation

**Invariant 2: Declarative Configuration**
- Description: Configuration describes intent, not execution
- Why: Enables reuse, testing, understanding
- Violation: Business logic in configuration → maintenance nightmare
- Validation: No conditional logic in configuration layer
- Stage: Configuration validation

**Invariant 3: Compilation Decides**
- Description: All decisions made at compile time
- Why: Runtime can be simple, deterministic
- Violation: Runtime interpretation → non-determinism
- Validation: No decision logic in executor
- Stage: Runtime validation

**Invariant 4: Runtime Executes**
- Description: Runtime executes precompiled plans
- Why: Performance, predictability, safety
- Violation: Runtime makes decisions → unpredictable behavior
- Validation: Executor is thin orchestrator only
- Stage: Runtime

**Invariant 5: Provider-Owned Semantics**
- Description: SPI providers supply behavior
- Why: Extensibility without code modification
- Violation: Core implements behavior → lock-in
- Validation: All behavior comes from providers
- Stage: Compilation

**Invariant 6: Registry-Free at Runtime**
- Description: No provider lookups at runtime
- Why: Deterministic, fast, predictable
- Violation: Runtime provider lookup → non-determinism
- Validation: No ProviderRegistry accessed at runtime
- Stage: Runtime

**Invariant 7: Fully Precompiled Runtime**
- Description: All compilation before execution
- Why: Simple runtime, no unexpected delays
- Violation: Lazy compilation → unpredictable pauses
- Validation: Plan is complete before execution
- Stage: Runtime

**Invariant 8: Metadata-Free at Runtime**
- Description: No metadata interpretation at runtime
- Why: Runtime purity, reproducibility
- Violation: Runtime reads configuration → different outcomes
- Validation: No configuration references in executor
- Stage: Runtime

**Invariant 9: Policy-Free at Runtime**
- Description: Policies applied at compile time
- Why: Clean separation of concerns
- Violation: Runtime policy evaluation → complexity
- Validation: Policy decisions recorded in effective definition
- Stage: Compilation

**Invariant 10: Immutable at Runtime**
- Description: No mutations during execution
- Why: Thread safety, reproducibility
- Violation: Mutable execution state → race conditions
- Validation: Executors read-only, no setters
- Stage: Runtime

---

### V4 Redesign (11-20)

**Invariant 11: Immutable ExecutionContext**
- Description: ExecutionContext uses final fields only
- Why: Thread-safe context passing, no hidden state
- Violation: Setters on context → unexpected changes
- Validation: No setter methods on ExecutionContext
- Stage: Runtime
- Test: Reflection test for final fields

**Invariant 12: Execution State Management Isolated**
- Description: ExecutionStateManager is package-private, not public API
- Why: Prevents state leakage to executors
- Violation: State manager exposed → executors modify state
- Validation: State manager not visible in public API
- Stage: Runtime

**Invariant 13: Outcome Carries Results Only**
- Description: ExecutionOutcome has no configuration
- Why: Runtime purity (same inputs → same outcome)
- Violation: Configuration in outcome → different outcomes
- Validation: No diagnostic configuration in outcome
- Stage: Runtime

**Invariant 14: Fingerprint-Based Caching**
- Description: Plans identified and cached by SHA-256 hash
- Why: Deterministic cache hits, correctness
- Violation: Non-deterministic cache key → cache misses
- Validation: Fingerprints are SHA-256 based
- Stage: Caching

**Invariant 15: Composition Contracts Explicit**
- Description: Partitions have explicit composition validation
- Why: Prevents silent composition failures
- Violation: Silent divergence → hard-to-debug failures
- Validation: CompositionValidator runs on every plan
- Stage: Compilation

**Invariant 16: Collection Strategy Pre-Selected**
- Description: Matching strategy selected at compile time
- Why: Restores "compiler decides" principle
- Violation: Runtime selection → non-determinism
- Validation: Strategy embedded in CompiledNodeBehavior
- Stage: Compilation

**Invariant 17: Diagnostics Post-Execution**
- Description: Diagnostics applied after execution
- Why: Execution remains pure
- Violation: Runtime interpretation → different outcomes
- Validation: DiagnosticsPostProcessor runs after
- Stage: Post-execution

**Invariant 18: Reduced SPI Surface**
- Description: 3 core + 2 optional providers
- Why: Clarity, implementability
- Violation: 8+ providers → confusion
- Validation: Exactly 5 providers total
- Stage: API

**Invariant 19: Result Streaming Supported**
- Description: Results can be streamed without buffering
- Why: Scales to large comparisons
- Violation: Mandatory buffering → memory explosion
- Validation: StreamingResultSink exists and works
- Stage: Runtime

**Invariant 20: Schema Version Compatibility**
- Description: Cross-partition version compatibility checked
- Why: Prevents schema divergence
- Violation: Version skew → silent failures
- Validation: CompositionValidator checks versions
- Stage: Compilation

---

### Structural Platform (21-26)

**Invariant 21: Structural Systems Are Reusable**
- Description: Type, Shape, Accessor, Path, Traversal work for all operations
- Why: Enables future engines
- Validation: No operation-specific code in structural systems
- Stage: Design

**Invariant 22: Structural Systems Are Operation-Agnostic**
- Description: Structural systems never mention comparison
- Why: True reusability
- Validation: No "Comparison" references in structural packages
- Stage: Design

**Invariant 23: Behavior Systems Are Engine-Specific**
- Description: Each engine has its own behavior
- Why: Different operations need different orchestration
- Validation: No behavior reuse across engines
- Stage: Design

**Invariant 24: Execution Systems Are Engine-Specific**
- Description: Each engine has its own runtime
- Why: Different execution models per engine
- Validation: No execution reuse across engines
- Stage: Design

**Invariant 25: Structural Platform Enables Future Engines**
- Description: Design intentionally supports clone, mapper, merger, validator
- Why: 10+ year platform evolution
- Validation: Future engines can be built using platform
- Stage: Design

**Invariant 26: SPI Providers Are Properly Classified**
- Description: Providers marked as structural or comparison-specific
- Why: Clear reusability boundaries
- Validation: Documentation indicates reusability
- Stage: Documentation

---

# PART 11: PACKAGE STRUCTURE

## Package Dependency Rules

```
ALLOWED DEPENDENCIES:
comparison → structural (imports)
compilation → configuration, effective, structural
runtime → compilation, structural

FORBIDDEN DEPENDENCIES:
structural → comparison (no reverse imports)
configuration → compilation, runtime
runtime → configuration, effective (no metadata reading)
```

---

## Package Specifications

### com.checkaboy.structural

**Purpose:** Operation-agnostic structural metadata  
**Reusable:** YES (future engines)  
**Extraction:** V5 candidate

**Subpackages:**
- type: TypeReference, TypeDescriptor, TypeProvider
- shape: ShapeReference, ShapeDescriptor, ResolvedShape, ShapeProvider
- accessor: AccessorReference, AccessorDefinition, CompiledAccessor, AccessorProvider
- path: PathNode, PathPattern, PathParser, PathMatcher
- traversal: TraversalDescriptor, TraversalMetadata, CycleGuard

**Allowed Dependencies:**
- Internal only (no external)

**Forbidden Dependencies:**
- No comparison concepts
- No execution concepts

---

### com.checkaboy.comparison.config

**Purpose:** Configuration layer definitions  
**Reusable:** NO (comparison-specific)  
**Visibility:** Configuration layer only

**Contains:**
- ComparisonGraphDefinition
- ComparisonNodeDefinition, ComparisonEdgeDefinition
- BehaviorReference, StrategyReference
- PolicyDefinition, ExecutionPolicy
- ShapeReference, TypeReference, AccessorReference

**Allowed Dependencies:**
- structural.* (imports)

**Forbidden Dependencies:**
- compilation.*
- runtime.*
- effective.*

---

### com.checkaboy.comparison.effective

**Purpose:** Effective definition layer  
**Reusable:** NO  
**Visibility:** Compilation layer only

**Contains:**
- EffectiveComparisonDefinition
- EffectiveGraphDefinition, EffectiveNodeDefinition, EffectiveEdgeDefinition
- PolicyDecision
- ExecutionProfile, ShapeProfile

**Allowed Dependencies:**
- config.*
- structural.*

**Forbidden Dependencies:**
- runtime.*
- compile.* (only used to create)

---

### com.checkaboy.comparison.compile

**Purpose:** Compilation layer  
**Reusable:** NO  
**Visibility:** Compilation only

**Contains:**
- All compiler stages (Definition validation through plan assembly)
- CompilationContext, CompilationReport
- ProviderRegistry
- Fingerprint computation

**Allowed Dependencies:**
- config.*
- effective.*
- structural.*
- spi.*
- runtime.* (to create compiled artifacts)

**Forbidden Dependencies:**
- No runtime execution

---

### com.checkaboy.comparison.runtime

**Purpose:** Runtime execution layer  
**Reusable:** NO  
**Visibility:** Runtime only

**Contains:**
- CompiledComparisonPlan, CompiledNodePlan, CompiledEdgePlan
- ComparisonExecutor, NodeExecutor, EdgeExecutor
- ExecutionContext, ExecutionStateManager
- ComparisonResultTree, ResultNode, DifferenceNode
- GraphTraversalState, CycleGuard
- ResultSink, ResultCollector

**Allowed Dependencies:**
- structural.* (read-only, no modifications)
- spi.* (invoke providers)

**Forbidden Dependencies:**
- config.*
- effective.*
- compile.*

---

### com.checkaboy.comparison.spi

**Purpose:** SPI definitions  
**Reusable:** PARTIALLY (structural providers reusable)

**Contains:**
- AccessorProvider
- TypeProvider
- BehaviorProvider
- StrategyProvider
- DefinitionProvider (optional)

**Allowed Dependencies:**
- structural.*
- config.*

**Forbidden Dependencies:**
- runtime.*
- compile.*

---

# PART 12: EXECUTION FLOWS

## Flow 1: Metadata Loading to Compilation

```
Start: ComparisonGraphDefinition (JSON/YAML)
  ↓
Schema Validation (structural)
  ↓
Definition Normalization (aliases, imports)
  ↓
Type Resolution (TypeProvider)
  ↓
Shape Resolution (ShapeProvider)
  ↓
Accessor Compilation (AccessorProvider)
  ↓
Policy Application (PolicyCompiler)
  ↓ [EffectiveGraphDefinition produced]
Behavior Compilation (BehaviorProvider)
  ↓
Strategy Compilation (StrategyProvider)
  ↓
Execution Semantics Setup
  ↓
Composition Validation
  ↓
Plan Assembly
  ↓ [CompiledComparisonPlan produced]
Plan Caching (by fingerprint)
  ↓
End: Ready for execution
```

---

## Flow 2: Execution

```
Start: ComparisonExecutor.execute(plan, source, target, context, collector)
  ↓
Create ExecutionContext (immutable)
Create ExecutionStateManager (hidden)
  ↓
NodeExecutor.executeNode(rootNode, src, tgt, context)
  ├─ Check cycle (stateManager.canTraverse)
  ├─ Call behavior.orchestrate(src, tgt, context, edgeCallback)
  │  └─ For each edge:
  │     EdgeExecutor.executeEdge(edge, srcVal, tgtVal, context)
  │     ├─ Extract via accessor (with cache)
  │     ├─ Call behavior.execute(srcVal, tgtVal, context, strategyCallback)
  │     ├─ Call strategy.compare(srcVal, tgtVal)
  │     └─ Return ExecutionOutcome
  ├─ Aggregate outcomes
  └─ Return ExecutionOutcome
  ↓
Report to ResultSink (memory or streaming)
  ↓
Cleanup ExecutionStateManager
  ↓
End: ResultTree (or streamed results)
```

---

## Flow 3: Error Handling

```
Provider throws Exception
  ↓
EdgeExecutor or BehaviorProvider catches
  ↓
Wrap in ExecutionError
  ↓
Produce ExecutionOutcome with ERROR status
  ↓
ExecutionSemantics decides:
  ├─ FAIL_FAST → stop traversal
  └─ CONTINUE → record error, continue
  ↓
Result reported to ResultSink
```

---

# PART 13: V5 BOUNDARY

## Classification: Remain in Comparison Engine

**MUST STAY IN V4:**
```
- PolicyDefinition (comparison-specific rules)
- BehaviorDefinition (comparison orchestration)
- StrategyDefinition (comparison semantics)
- ComparisonGraphDefinition (currently comparison-specific)
- NodeBehaviorDefinition (comparison-specific)
- ExecutionSemantics (comparison-specific)
- ResultTree (comparison-specific)
```

---

## Classification: Candidate for Structural Platform

**GOOD CANDIDATES FOR V5 EXTRACTION:**
```
- Type system (fully reusable by all engines)
- Shape system (fully reusable by all engines)
- Accessor system (fully reusable by all engines)
- Path system (fully reusable by all engines)
- Traversal system (fully reusable by all engines)
```

---

## Classification: Investigation Items for V5

**REQUIRES V5 INVESTIGATION:**
```
Graph Model
  - Currently in Comparison Engine
  - Could be reusable across engines
  - Decision deferred to V5
  - Depends on needs of other engines
```

---

## Extraction Criteria for V5

Extract to standalone Structural Platform artifact **ONLY WHEN:**

```
1. At least 2 additional production engines exist
2. Shared structural needs are proven
3. API stability is established
4. Versioning strategy is defined
5. Committee decision is made
```

---

## V5 Roadmap (Explicit Deferral)

**V5 Work Items:**

```
1. Platform Extraction
   - Create standalone Structural Platform artifact
   - Define public API
   - Version management
   
2. Graph Model Evaluation
   - Determine reusability
   - Decide on extraction
   - Refactor if appropriate
   
3. Clone Engine
   - Design clone semantics
   - Reuse Structural Platform
   - Implement provider SPIs
   
4. Mapper Engine
   - Design mapping semantics
   - Reuse Structural Platform
   - Support transformation
   
5. Merger Engine
   - Design merge semantics
   - Reuse Structural Platform
   - Conflict resolution
   
6. Cross-Engine Runtime
   - Engine composition
   - Shared state management
   - Result coordination
```

---

# SUMMARY

This specification defines every layer, component, contract, lifecycle, responsibility boundary, and interaction flow required to implement the Declarative Comparison Engine V4.

The specification is detailed enough that multiple development teams could independently implement compatible systems.

All architectural decisions are frozen and documented.

**Ready for implementation.**

---

**END OF IMPLEMENTATION SPECIFICATION**
