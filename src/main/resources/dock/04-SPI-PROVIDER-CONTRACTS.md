# Declarative Comparison Engine V4
## SPI PROVIDER IMPLEMENTATION CONTRACTS - DEEP DIVE

**Purpose:** Complete specification of every SPI provider with full contracts  
**Detail Level:** Implementation-grade  
**Audience:** Provider developers, framework designers  

---

# PART 1: ACCESSOR PROVIDER (STRUCTURAL - REUSABLE)

## Provider Lifecycle

```
1. REGISTRATION PHASE
   ├─ Create AccessorProvider instance
   ├─ Register with ProviderRegistry
   └─ Provider becomes available to compiler

2. COMPILATION PHASE
   ├─ For each AccessorReference:
   │  ├─ Compiler asks: canHandle(ref, type)?
   │  ├─ If YES: compile(ref, type, context)
   │  ├─ CompiledAccessor produced and cached
   │  └─ Compiler continues
   │
   └─ All accessors compiled into CompiledNodePlan

3. CACHING PHASE
   ├─ CompiledAccessor cached by AccessorReference key
   ├─ Same ref → same instance (reused)
   └─ Cache valid for plan lifetime

4. RUNTIME PHASE
   ├─ Execute(source) called many times
   ├─ Must be stateless and thread-safe
   └─ No modifications to compiled accessor
```

---

## Contract: canHandle()

### Signature
```java
boolean canHandle(
  AccessorReference reference,
  TypeDescriptor sourceType,
  CompilationContext context
)
```

### Purpose
Determine if this provider can handle the given accessor reference.

### Input Specification

**AccessorReference:**
```
Fields:
  accessorId: String (unique identifier)
  path: String (e.g., "customer.name", "items[0]", "map['key']")
  providerType: String? (optional hint to which provider)
  metadata: Map<String, String> (optional custom metadata)

Invariants:
  - accessorId must not be empty
  - path must not be empty
  - path must be valid expression (no null bytes, etc.)
```

**TypeDescriptor:**
```
Fields:
  typeName: String (fully qualified, e.g., "java.lang.String")
  typeClass: Class<?>? (if available, can be null)
  isGeneric: boolean
  typeArguments: List<TypeDescriptor>? (if generic)
  properties: Map<String, PropertyDescriptor>? (if object)
  elementType: TypeDescriptor? (if collection)

Methods:
  isReflectionAccessible(): boolean
  getProperty(name): PropertyDescriptor?
  supportsIteration(): boolean
```

**CompilationContext:**
```
Fields:
  definition: ComparisonGraphDefinition
  providers: ProviderRegistry
  cache: ResolutionCache
  diagnostics: List<CompilationDiagnostic>
```

### Return Specification

```
boolean result:
  true  → This provider can compile this accessor
  false → This provider cannot handle it
          (compiler will try next provider)
```

### Implementation Notes

```
✓ Must NOT throw exceptions (use return false instead)
✓ Must NOT modify CompilationContext
✓ Must be deterministic (same inputs → same result)
✓ Must be thread-safe
✓ Should be fast (called many times)

TYPICAL IMPLEMENTATION:
  1. Check if providerType matches (if specified)
  2. Check if path format matches supported syntax
  3. Check if type has required properties
  4. Return true if all checks pass, false otherwise
```

---

## Contract: compile()

### Signature
```java
CompiledAccessor compile(
  AccessorReference reference,
  TypeDescriptor sourceType,
  CompilationContext context
) throws AccessorCompilationException
```

### Purpose
Create an executable accessor from the reference.

### Input Specification

Same as canHandle() inputs.

**Precondition:**
```
canHandle() must have returned true
OR
This is first provider to handle this reference
```

### Return Specification

```
CompiledAccessor {
  
  Method: Object execute(Object source) throws AccessorException
    Purpose: Extract value from source object
    Inputs:
      source: Object (the object to extract from)
    Outputs:
      Object (extracted value)
    Exceptions:
      AccessorException (if extraction fails)
      NullPointerException (if source is null and cannot handle)
    
    Behavior:
      - Extract value at specified path
      - Return extracted value
      - If path doesn't exist: throw AccessorException
      - If source is null: throw AccessorException
      - If extraction fails: throw AccessorException
  
  Method: Map<String, Object> getMetadata()
    Purpose: Return metadata about this accessor
    Outputs:
      Map with keys:
        "accessor_type": String (e.g., "reflection", "method", "custom")
        "path": String (the path this accessor extracts)
        "target_type": String (type of extracted value)
        "cacheable": boolean (whether results can be cached)
        ... (any custom metadata)
}
```

### Implementation Requirements

**Statelessness:**
```
✓ CompiledAccessor must be STATELESS
✓ No instance fields that change between invocations
✓ No shared mutable state
✗ Do NOT store extracted values
✗ Do NOT maintain history
✗ Do NOT accumulate state
```

**Thread Safety:**
```
✓ execute() must be safe to call from multiple threads
✓ No synchronization needed (stateless)
✓ All accessed data must be thread-safe or immutable
```

**Reusability:**
```
✓ Same CompiledAccessor may be used thousands of times
✓ execute() results not cached by accessor itself
✓ Caching handled by runtime (AccessorResultCache)
```

**Error Handling:**
```
✓ Throw AccessorException for all errors
✓ Include helpful error message
✓ Include context about what failed

Example:
  throw new AccessorException(
    "Property 'address' not found on Order",
    "PROPERTY_NOT_FOUND",
    originalException
  )
```

### Example Implementation

```java
public class ReflectionAccessorProvider implements AccessorProvider {
  
  @Override
  public boolean canHandle(
    AccessorReference ref,
    TypeDescriptor type,
    CompilationContext ctx
  ) {
    // Handle simple property paths
    String path = ref.getPath();
    return isSimplePath(path) && 
           type.isReflectionAccessible();
  }
  
  @Override
  public CompiledAccessor compile(
    AccessorReference ref,
    TypeDescriptor type,
    CompilationContext ctx
  ) throws AccessorCompilationException {
    
    String path = ref.getPath();
    List<String> segments = parseSegments(path);
    
    // Validate path on type
    TypeDescriptor currentType = type;
    List<Field> fields = new ArrayList<>();
    
    for (String segment : segments) {
      Field field = findField(currentType, segment);
      if (field == null) {
        throw new AccessorCompilationException(
          "Field '" + segment + "' not found"
        );
      }
      fields.add(field);
      currentType = getFieldType(field);
    }
    
    // Return compiled accessor that chains field accesses
    return new ReflectionChainedAccessor(fields);
  }
  
  private static class ReflectionChainedAccessor 
      implements CompiledAccessor {
    
    private final List<Field> fields;
    
    ReflectionChainedAccessor(List<Field> fields) {
      this.fields = fields;
    }
    
    @Override
    public Object execute(Object source) throws AccessorException {
      if (source == null) {
        throw new AccessorException("Source object is null");
      }
      
      Object current = source;
      
      for (int i = 0; i < fields.size(); i++) {
        if (current == null) {
          throw new AccessorException(
            "Cannot access field at level " + i + ": value is null"
          );
        }
        
        try {
          Field field = fields.get(i);
          field.setAccessible(true);
          current = field.get(current);
        } catch (IllegalAccessException e) {
          throw new AccessorException(
            "Cannot access field: " + e.getMessage(),
            e
          );
        }
      }
      
      return current;
    }
    
    @Override
    public Map<String, Object> getMetadata() {
      return Map.of(
        "accessor_type", "reflection",
        "field_count", fields.size(),
        "cacheable", true
      );
    }
  }
}
```

---

## Contract: validate() [OPTIONAL]

### Signature
```java
ValidationResult validate(
  AccessorDefinition definition,
  CompilationContext context
)
```

### Purpose
Optional pre-compilation validation of accessor definition.

### Return Specification

```
ValidationResult {
  valid: boolean,
  errors: List<String>,
  warnings: List<String>
}

If valid == true:
  - Accessor can be compiled
  
If valid == false:
  - Compilation should not attempt
  - Report errors to user
```

### Implementation Notes

```
✓ Optional to implement
✓ Called before compile()
✓ Allows early error detection
✓ Can reject definitions before compilation
✗ Do NOT throw exceptions
```

---

# PART 2: BEHAVIOR PROVIDER (COMPARISON-SPECIFIC)

## Provider Lifecycle

```
1. REGISTRATION PHASE
   └─ Register BehaviorProvider with ProviderRegistry

2. COMPILATION PHASE
   ├─ For each BehaviorReference:
   │  ├─ Compiler asks: canHandle(ref, graph)?
   │  ├─ If YES: compile(ref, graph, context)
   │  ├─ CompiledBehavior produced
   │  └─ Embedded in CompiledNodePlan or CompiledEdgePlan
   │
   └─ All behaviors compiled

3. RUNTIME PHASE
   ├─ orchestrate() or execute() called once per node/edge
   ├─ Reports outcomes via callback
   └─ No state modification allowed
```

---

## Contract: CompiledNodeBehavior

### Signature (Orchestration Method)
```java
ExecutionOutcome orchestrate(
  Object sourceValue,
  Object targetValue,
  ExecutionContext context,
  EdgeCallback edgeCallback
)
```

### Purpose
Orchestrate comparison of node (invoke edges, aggregate results).

### Inputs

**sourceValue, targetValue:**
```
Objects to compare at this node
Can be any type (scalar, object, collection)
```

**ExecutionContext:**
```
Immutable context with:
  - currentPath (position in comparison tree)
  - remainingLimits (depth, breadth, time, differences)
  - executionSemantics (fail-fast, collect-all, etc.)
  - resultSink (memory or streaming)
  - accessorCache (for value extraction)
```

**EdgeCallback:**
```
Callback to invoke edges:
  
  ExecutionOutcome invokeEdge(
    edgeId: String,
    sourceAccessor: CompiledAccessor,
    targetAccessor: CompiledAccessor,
    behavior: CompiledBehavior,
    strategy: ComparisonStrategy,
    sourceValue: Object,
    targetValue: Object
  )
  
  Returns:
    ExecutionOutcome for this edge
```

### Output Specification

```
ExecutionOutcome {
  status: ResultStatus (MATCH, MISMATCH, ERROR, PARTIAL)
  
  For MATCH:
    - No differences
    - All edges matched
    
  For MISMATCH:
    - Contains differences from edges
    - Some edges mismatched
    
  For ERROR:
    - error field populated
    - Execution error occurred
    
  For PARTIAL:
    - Some edges processed
    - Some edges skipped/errored
}
```

### Implementation Pattern

```
Typical node behavior:
  1. Initialize outcome aggregator
  2. For each edge:
     a. Call edgeCallback.invokeEdge(...)
     b. Get ExecutionOutcome
     c. Add to aggregator
  3. Aggregate all outcomes
  4. Return aggregated ExecutionOutcome

Example behavior for scalar node:
  ├─ Get all edges from node definition
  ├─ For each edge:
  │  ├─ edgeCallback.invokeEdge(...)
  │  ├─ Accumulate result
  │  └─ Check for errors → handle based on semantics
  ├─ Aggregate: all match? → MATCH, else MISMATCH
  └─ Return aggregated outcome

Example behavior for collection node:
  ├─ Extract source and target arrays
  ├─ Apply collection matching strategy
  ├─ Get matched pairs and unmatched items
  ├─ For each matched pair:
  │  ├─ If childNode: recursively compare
  │  └─ edgeCallback.invokeEdge(...)
  ├─ Report unmatched as EXTRA/MISSING
  └─ Return aggregated outcome
```

---

## Contract: CompiledBehavior (EdgeBehavior)

### Signature
```java
ExecutionOutcome execute(
  Object sourceValue,
  Object targetValue,
  ExecutionContext context,
  StrategyCallback strategyCallback
)
```

### Purpose
Execute edge behavior (extract, compare, handle errors).

### StrategyCallback

```
ExecutionOutcome invokeStrategy(
  strategy: ComparisonStrategy,
  sourceValue: Object,
  targetValue: Object
)

Purpose: Invoke terminal comparison strategy
Returns: ExecutionOutcome from strategy
```

### Implementation Pattern

```
Typical edge behavior:
  1. Call strategyCallback.invokeStrategy(...)
  2. Get ExecutionOutcome from strategy
  3. Return outcome

Advanced edge behavior (with error handling):
  1. Try:
     a. Call strategyCallback.invokeStrategy(...)
     b. Return ExecutionOutcome
  2. Catch StrategyException:
     a. Wrap in ExecutionError
     b. Create ExecutionOutcome with ERROR status
     c. Return error outcome

Fallback behavior (try multiple strategies):
  1. Try primary strategy
  2. If fails, try fallback strategy
  3. If both fail, report error
```

---

# PART 3: STRATEGY PROVIDER (COMPARISON-SPECIFIC)

## Contract: ComparisonStrategy

### Signature
```java
ExecutionOutcome compare(
  Object sourceValue,
  Object targetValue,
  ExecutionContext context
) throws StrategyException
```

### Purpose
Determine if two values match or mismatch.

### Inputs

**sourceValue, targetValue:**
```
Values to compare
Can be null, scalar, object, collection, anything
```

**ExecutionContext:**
```
Immutable context (read-only for strategy)
```

### Output Specification

```
ExecutionOutcome {
  status: MATCH or MISMATCH (never ERROR from strategy)
  
  For MATCH:
    status: MATCH
    sourceValue: the source value
    targetValue: the target value
    difference: null
    
  For MISMATCH:
    status: MISMATCH
    sourceValue: the source value
    targetValue: the target value
    difference: Difference {
      path: ComparisonPath
      type: DifferenceType
      sourceValue: Object
      targetValue: Object
      detail: String? (explanation)
    }
}
```

### Implementation Pattern

```
Equality Strategy:
  1. Compare using Objects.equals()
  2. If equal: return MATCH
  3. If not equal: return MISMATCH with difference

Custom Strategy (e.g., approximate matching):
  1. Implement custom comparison logic
  2. Return MATCH or MISMATCH based on logic
  3. Include helpful difference details

Null-Safe Strategy:
  1. Handle null values specially
  2. null == null → MATCH
  3. null != non-null → MISMATCH
  4. Continue with normal comparison
```

### Error Handling

```
✓ Can throw StrategyException for fatal errors
✓ Framework catches and wraps in ExecutionError
✓ Execution continues or fails based on semantics

✗ Must NOT return null (always return ExecutionOutcome)
✗ Must NOT throw other exceptions (wrap in StrategyException)
```

---

# PART 4: TYPE PROVIDER (OPTIONAL - STRUCTURAL)

## Contract: TypeProvider

### Signature
```java
boolean canResolve(TypeReference reference, CompilationContext context)
TypeDescriptor resolve(TypeReference reference, CompilationContext context)
TypeDescriptor inferType(Object instance, CompilationContext context)
```

### Purpose
Resolve types and infer types from objects.

### When Implemented
```
- Custom type systems
- DSL type systems
- External type registries
- Dynamic type resolution
```

### Default Implementation
```
Default TypeProvider handles:
  - Java primitive types (int, String, etc.)
  - Fully qualified class names
  - Generic types (List<String>, etc.)
  - Array types
```

---

# PART 5: PROVIDER ERROR HANDLING

## Error Categories

### AccessorException

```
Thrown by: AccessorProvider.compile(), CompiledAccessor.execute()
Caught by: AccessorCompiler, EdgeExecutor
Wrapped in: ExecutionError

Typical causes:
  - Property not found
  - Path invalid
  - Type mismatch
  - Reflection errors
  - Custom accessor failures
```

### BehaviorCompilationException

```
Thrown by: BehaviorProvider.compile()
Caught by: BehaviorCompiler
Wrapped in: CompilationError

Typical causes:
  - Invalid behavior reference
  - Missing behavior implementation
  - Type incompatibility
  - Custom behavior errors
```

### StrategyException

```
Thrown by: ComparisonStrategy.compare()
Caught by: EdgeExecutor
Wrapped in: ExecutionError

Typical causes:
  - Comparison logic failure
  - Custom strategy errors
  - Unexpected value types
```

---

# PART 6: PROVIDER REGISTRATION

## Registration Mechanism

```
ProviderRegistry registry = new ProviderRegistry()

// Register providers
registry.register(new ReflectionAccessorProvider())
registry.register(new DefaultBehaviorProvider())
registry.register(new EqualityStrategyProvider())

// Compiler uses registry
ComparisonCompiler compiler = new ComparisonCompiler(registry)
CompiledComparisonPlan plan = compiler.compile(graph, policy)
```

## Provider Resolution Order

```
Compiler iterates through providers in registration order:

1. Ask provider 1: canHandle()?
   ├─ YES: use provider 1
   └─ NO: continue to provider 2

2. Ask provider 2: canHandle()?
   ├─ YES: use provider 2
   └─ NO: continue to provider 3

3. ... continue until provider found

4. If no provider handles: throw ProviderNotFoundException
```

## Custom Provider Chaining

```
// Chain multiple providers as fallback

ChainedAccessorProvider chain = new ChainedAccessorProvider(
  Arrays.asList(
    new CustomAccessorProvider(),     // Try custom first
    new DatabaseAccessorProvider(),   // Try DB second
    new ReflectionAccessorProvider()  // Fallback to reflection
  )
)

registry.register(chain)
```

---

# COMPLETE PROVIDER CHECKLIST

For each provider implementation:

```
BEFORE IMPLEMENTATION
☐ Understand provider purpose
☐ Understand when provider applies
☐ Understand contract requirements
☐ Understand error handling
☐ Plan stateless implementation

IMPLEMENTATION
☐ Implement canHandle()
☐ Implement compile() or resolve()
☐ Ensure stateless compiled artifacts
☐ Error handling (throw appropriate exceptions)
☐ Metadata population
☐ Thread safety verification

TESTING
☐ Unit tests for happy path (10+ tests)
☐ Unit tests for error cases (10+ tests)
☐ Integration tests (5+ tests)
☐ Performance tests
☐ Thread safety tests (concurrent invocation)
☐ Contract compliance tests

DOCUMENTATION
☐ Provider purpose documented
☐ Supported patterns documented
☐ Error cases documented
☐ Performance characteristics documented
☐ Examples provided
☐ Integration guide provided
```

---

**SPI PROVIDER DEEP DIVE COMPLETE**

This provides exhaustive implementation-grade specifications for all providers.
