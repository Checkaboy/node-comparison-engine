# Declarative Comparison Engine V4
## DETAILED COMPILATION PIPELINE SPECIFICATION

**Purpose:** Exhaustive specification of all 10 compilation stages  
**Audience:** Implementation teams, compiler developers  
**Detail Level:** Implementation-grade  

---

# STAGE 1: DEFINITION VALIDATION

## Input
```
ComparisonGraphDefinition {
  graphId: String
  nodes: Map<String, ComparisonNodeDefinition>
  edges: Map<String, ComparisonEdgeDefinition>
  rootNodeId: String
  metadata: Map<String, Object>
}
```

## Validation Rules

### GraphDefinition Level
```
RULE 1.1: Graph Identity
  ✓ graphId must be non-empty
  ✓ graphId must follow naming convention (alphanumeric + underscore)
  ✗ graphId must not be null or whitespace-only
  Error: ValidationException("Invalid graph ID: " + graphId)

RULE 1.2: Root Node Reference
  ✓ rootNodeId must reference existing node in nodes map
  ✗ rootNodeId must not be null
  ✗ Non-existent nodeId references are errors
  Error: ValidationException("Root node not found: " + rootNodeId)

RULE 1.3: Node Uniqueness
  ✓ All nodeIds must be unique
  ✗ Duplicate nodeIds produce error
  Error: ValidationException("Duplicate node ID: " + nodeId)

RULE 1.4: Edge Uniqueness
  ✓ All edgeIds must be unique
  ✗ Duplicate edgeIds produce error
  Error: ValidationException("Duplicate edge ID: " + edgeId)
```

### NodeDefinition Level
```
RULE 1.5: Type References
  ✓ sourceType and targetType must be valid TypeReference objects
  ✗ Null type references are errors
  ✓ Type format must be parseable (e.g., "java.lang.String")
  Error: ValidationException("Invalid type reference: " + typeRef)

RULE 1.6: Shape Reference
  ✓ shape must be valid ShapeReference
  ✗ Null shape is error
  Error: ValidationException("Shape reference required for node: " + nodeId)

RULE 1.7: Node Behavior (Optional)
  ✓ If provided, must be valid BehaviorReference
  ✓ If not provided, default behavior will be assigned
  Error: ValidationException("Invalid behavior reference: " + behRef)

RULE 1.8: Collection Handling (Optional)
  ✓ If provided, must be valid CollectionHandlingMode enum value
  ✓ If not provided, will be inferred during compilation
  Error: ValidationException("Unknown collection handling mode: " + mode)
```

### EdgeDefinition Level
```
RULE 1.9: Accessor References
  ✓ sourceAccessor and targetAccessor must be valid AccessorReference
  ✗ Null accessors are errors
  ✓ Accessor paths must be non-empty
  Error: ValidationException("Invalid accessor reference: " + accRef)

RULE 1.10: Behavior Reference
  ✓ Must be valid BehaviorReference
  ✗ Null behavior is error
  Error: ValidationException("Behavior required for edge: " + edgeId)

RULE 1.11: Strategy Reference
  ✓ Must be valid StrategyReference
  ✗ Null strategy is error
  Error: ValidationException("Strategy required for edge: " + edgeId)

RULE 1.12: Child Node Reference (Optional)
  ✓ If provided, must reference existing node
  ✓ If provided, nodeId must be different from parent
  ✗ Null nodeId is ignored (treated as optional)
  Error: ValidationException("Child node not found: " + childNodeId)

RULE 1.13: No Direct Cycles
  ✓ Must not have immediate self-reference (edge → same node)
  ✓ Optional: Detect deeper cycles (design decision for V4)
  Error: ValidationException("Cycle detected: node " + nodeId)
```

## Output
```
ValidationReport {
  valid: boolean
  errors: List<ValidationError>
  warnings: List<ValidationWarning>
  statistics: ValidationStatistics {
    nodesValidated: int
    edgesValidated: int
    errorsFound: int
    warningsFound: int
  }
}

ValidationError {
  code: String (e.g., "INVALID_NODE_ID")
  message: String
  location: String (e.g., "node[orderId].behavior")
  severity: ERROR
}
```

## Failure Conditions
```
FAIL: Graph ID is null
FAIL: Root node doesn't exist
FAIL: Node definition is invalid
FAIL: Edge definition is invalid
FAIL: Type references are invalid
FAIL: Accessor paths are invalid
```

## Success Criteria
```
✓ All nodes are valid
✓ All edges are valid
✓ All references are present
✓ All IDs are unique
✓ No validation errors (warnings are allowed)
```

---

# STAGE 2: DEFINITION NORMALIZATION

## Input
```
Validated ComparisonGraphDefinition
```

## Transformations

### DSL Translation (Object DSL → Graph DSL)
```
TRANSFORM 2.1: ObjectComparisonDefinition → ComparisonGraphDefinition

Input:
  ObjectComparisonDefinition {
    sourceType: TypeReference
    targetType: TypeReference
    fields: List<FieldComparisonDefinition>
  }

Process:
  1. Create ComparisonGraphDefinition
  2. Create root node with source/target types
  3. For each field:
     - Create edge with source/target accessor
     - Reference node behavior (default or specified)
     - Reference strategy (default or specified)
  4. Return ComparisonGraphDefinition

Output:
  ComparisonGraphDefinition (normalized from object DSL)
```

### Alias Resolution
```
TRANSFORM 2.2: Resolve Type Aliases

Input:
  TypeReference with potential alias (e.g., "Order" → "com.example.Order")

Process:
  1. Look up alias in alias registry
  2. Replace with fully qualified name
  3. If no alias, leave as-is

Output:
  Fully qualified type reference
```

### Import Expansion
```
TRANSFORM 2.3: Expand Imports

Input:
  Graph with import statements
  
Process:
  1. Process all import statements
  2. Qualify all types with imported packages
  3. Remove import statements (resolved)

Output:
  Graph with fully qualified references
```

### Behavior Defaults
```
TRANSFORM 2.4: Assign Default Behaviors

Input:
  Nodes/edges without explicit behavior

Process:
  1. For each node without behavior:
     - Assign default node behavior (e.g., "default.scalar" or "default.collection")
  2. For each edge without behavior:
     - Assign default edge behavior (e.g., "default.extract")

Output:
  Graph with all behaviors specified
```

## Output
```
Normalized ComparisonGraphDefinition {
  - All DSL translated to graph form
  - All aliases resolved
  - All imports expanded
  - All defaults assigned
  - All references fully qualified
}
```

---

# STAGE 3: STRUCTURAL RESOLUTION

## Substage 3A: Type Resolution

### Input
```
Normalized ComparisonGraphDefinition with TypeReferences
TypeProvider (SPI)
```

### Process
```
For each TypeReference in graph:
  1. Check type resolution cache
  2. If cached, return cached TypeDescriptor
  3. If not cached:
     a. Invoke TypeProvider.canResolve(typeRef)
     b. If provider can handle:
        - Invoke TypeProvider.resolve(typeRef)
        - Cache result
        - Return TypeDescriptor
     c. If no provider can handle:
        - Throw TypeResolutionException
        
Caching Strategy:
  - Cache TypeDescriptor by TypeReference
  - Avoid re-resolving same type
  - Cache is valid for entire compilation
```

### Output
```
Annotated graph with ResolvedType:
  ResolvedType {
    reference: TypeReference
    descriptor: TypeDescriptor {
      typeName: String
      typeClass: Class<?>?
      isGeneric: boolean
      typeArguments: List<TypeDescriptor>?
      properties: Map<String, PropertyDescriptor>? (if object)
      elementType: TypeDescriptor? (if collection)
    }
    providerName: String
    resolvedAt: long
  }
```

## Substage 3B: Shape Resolution

### Input
```
Annotated graph with ResolvedTypes
ShapeProvider (SPI)
```

### Process
```
For each ShapeReference in graph:
  1. Check shape resolution cache
  2. If cached, return cached ResolvedShape
  3. If not cached:
     a. Get corresponding ResolvedType
     b. Invoke ShapeProvider.canResolve(shapeRef, resolvedType)
     c. If provider can handle:
        - Invoke ShapeProvider.resolve() or infer()
        - Cache result
        - Return ResolvedShape
     d. If no provider can handle:
        - Throw ShapeResolutionException

Validation:
  - Shape must be compatible with Type
  - Shape properties must match type properties
  - Shape element type must match collection element type
```

### Output
```
Annotated graph with ResolvedShape:
  ResolvedShape {
    reference: ShapeReference
    descriptor: ShapeDescriptor {
      elementType: ElementType (OBJECT, COLLECTION, KEYED, SCALAR)
      properties: Map<String, PropertyShape>?
      collectionElementType: ShapeDescriptor?
      isOrdered: boolean?
      isKeyed: boolean?
      keyExtractor: AccessorReference?
      capabilities: ShapeCapabilities
    }
    providerName: String
    resolvedAt: long
  }
```

## Substage 3C: Accessor Compilation

### Input
```
Annotated graph with ResolvedShapes
AccessorProvider (SPI)
```

### Process
```
For each AccessorReference in edges:
  1. Check accessor compilation cache
  2. If cached, return cached CompiledAccessor
  3. If not cached:
     a. Get source/target ResolvedType and ResolvedShape
     b. Invoke AccessorProvider.canHandle(accRef, type, shape)
     c. If provider can handle:
        - Invoke AccessorProvider.compile()
        - Cache result
        - Return CompiledAccessor
     d. If no provider can handle:
        - Throw AccessorCompilationException

Validation:
  - Accessor path must be valid for type
  - Accessor must extract value compatible with shape
  - Accessor must not be null
```

### CompiledAccessor Contract
```
CompiledAccessor {
  Methods:
    Object execute(source: Object) throws AccessorException
      - Extract value from source
      - Return extracted value
      - Throw AccessorException if extraction fails
      
    Map<String, Object> getMetadata()
      - Return accessor metadata
      - Used for diagnostics
      
  Properties:
    - Must be STATELESS
    - Must be REUSABLE across invocations
    - Must be THREAD-SAFE
    - Must be IMMUTABLE
}
```

### Output
```
Annotated graph with CompiledAccessor:
  AccessorCompilation {
    reference: AccessorReference
    compiledAccessor: CompiledAccessor
    providerName: String
    compiledAt: long
  }
```

## Substage 3D: Path Compilation

### Input
```
All path expressions in graph
```

### Process
```
For each path pattern:
  1. Parse path expression
  2. Build PathMatcher (for evaluation)
  3. Cache matcher

Example paths:
  "customer.address.street" → PathNode[customer] → PathNode[address] → PathNode[street]
  "items[*].name" → PathNode[items] → PathNode[*] → PathNode[name]
  "map['key'].value" → PathNode[map] → PathNode[key] → PathNode[value]
```

### Output
```
Compiled path matchers for use at runtime
```

## Output of Stage 3
```
Structurally Resolved Graph:
  - Every TypeReference → ResolvedType
  - Every ShapeReference → ResolvedShape
  - Every AccessorReference → CompiledAccessor
  - Every PathPattern → CompiledPathMatcher
  - All structural metadata resolved
  - All structural validation passed
```

---

# STAGE 4: POLICY APPLICATION

## Input
```
Structurally Resolved Graph
PolicyDefinition {
  rules: List<PolicyRule>
}
```

## Process

### Policy Rule Evaluation
```
For each PolicyRule:
  1. Evaluate condition against graph
  2. If condition matches:
     a. Apply all actions
     b. Record decision in EffectivePolicyDecision
  3. Continue to next rule
  
Conditions can reference:
  - Node properties
  - Edge properties
  - Type characteristics
  - Shape characteristics
  - Metadata
```

### Policy Actions
```
Action Types:
  1. MODIFY_NODE: Change node properties
  2. MODIFY_EDGE: Change edge properties
  3. ADD_CONSTRAINT: Add validation constraint
  4. OVERRIDE_BEHAVIOR: Change behavior reference
  5. OVERRIDE_STRATEGY: Change strategy reference
  6. SKIP_NODE: Mark node for skipping
  7. SKIP_EDGE: Mark edge for skipping
```

### Example Policy
```
PolicyRule {
  ruleId: "sensitive-data-policy"
  condition: "nodeId.startsWith('sensitive_')"
  actions: [
    OVERRIDE_STRATEGY: "privacy-aware-comparison",
    ADD_CONSTRAINT: "no-value-logging"
  ]
  priority: 100
}
```

## Output
```
EffectiveGraphDefinition {
  effectiveGraph: EffectiveGraphDefinition (modified graph)
  policyDecisions: Map<String, PolicyDecision> {
    nodeId → PolicyDecision {
      appliedPolicies: List<String>
      ruleMatches: List<RuleMatch>
      changedFields: Map<String, Object>
    }
  }
}

Modified nodes/edges carry:
  - Original definition
  - Policy-applied modifications
  - Audit trail of decisions
```

---

# STAGES 5-10: BEHAVIOR, STRATEGY, SEMANTICS, COMPOSITION, ASSEMBLY

Each stage follows similar pattern:

**STAGE 5: Collection Handling Strategy Compilation**
- Input: EffectiveGraphDefinition
- Output: CompiledCollectionStrategy embedded in nodes
- Process: Select matching strategy for each collection node

**STAGE 6: Behavior Compilation**
- Input: Graph with behaviors
- Output: CompiledBehavior for each behavior reference
- Process: Invoke BehaviorProvider for each behavior

**STAGE 7: Strategy Compilation**
- Input: Graph with strategies
- Output: ComparisonStrategy for each strategy reference
- Process: Invoke StrategyProvider for each strategy

**STAGE 8: Execution Semantics**
- Input: ExecutionPolicy
- Output: ExecutionSemantics (immutable configuration)
- Process: Validate and compile execution configuration

**STAGE 9: Composition Validation**
- Input: All compiled components
- Output: Validated EffectiveComposition
- Process: Verify cross-partition contracts

**STAGE 10: Plan Assembly**
- Input: Validated components
- Output: CompiledComparisonPlan with fingerprint
- Process: Assemble final runtime plan

---

# COMPILATION DIAGNOSTICS

## Diagnostic Reporting

Every stage produces diagnostics:

```
CompilationDiagnostic {
  stage: String (e.g., "TYPE_RESOLUTION")
  severity: TRACE | DEBUG | INFO | WARNING | ERROR
  code: String (e.g., "TYPE_NOT_FOUND")
  message: String
  location: String (e.g., "edge[orderId→orderId]")
  details: Map<String, Object>
  timestamp: long
}

Collected in CompilationReport:
  - Stage-by-stage diagnostics
  - Error summary
  - Warning summary
  - Success/failure status
  - Compilation duration
  - Statistics
```

---

# COMPILATION CACHING

## Fingerprint Computation

```
CompilationFingerprint = SHA256(
  graphId +
  graphVersion +
  policyVersion +
  collectionHandlingModes +
  behaviorVersions (sorted) +
  strategyVersions (sorted) +
  executionSemanticsVersion +
  compilerVersion +
  providerVersions (sorted)
)

Deterministic:
  ✓ Same input → same fingerprint
  ✓ Different input → different fingerprint (with high probability)
  ✓ Used for plan caching
```

## Cache Operations

```
Cache.get(fingerprint) → Option<CompiledComparisonPlan>
  - If present: return cached plan (no recompilation)
  - If absent: compile and cache

Cache.put(fingerprint, plan)
  - Store plan in cache
  - Set TTL (e.g., 1 hour)
  - Set max entries (e.g., 10,000)
  - Evict LRU when full
```

---

**DETAILED COMPILATION SPECIFICATION COMPLETE**

This provides implementation-grade detail for all 10 compilation stages.
