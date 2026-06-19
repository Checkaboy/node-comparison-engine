# Declarative Comparison Engine V4
## DETAILED EXECUTION FLOWS & SCENARIOS

**Purpose:** Comprehensive specification of runtime execution flows  
**Detail Level:** Implementation-grade  
**Audience:** Runtime developers, architects  

---

# EXECUTION FLOW 1: FULL COMPARISON EXECUTION

## High-Level Flow

```
ComparisonExecutor.execute(plan, source, target, context, resultSink)
  │
  ├─ Create ExecutionContext (immutable)
  ├─ Create ExecutionStateManager (hidden, internal)
  │
  ├─ Call getRootNodePlan()
  │
  ├─ NodeExecutor.executeNode(rootNodePlan, src, tgt)
  │  │
  │  ├─ ExecutionStateManager.canTraverse(nodeId, src, tgt)?
  │  │  ├─ YES → Continue
  │  │  └─ NO → Return SKIPPED outcome
  │  │
  │  ├─ ExecutionStateManager.recordTraversal(nodeId, src, tgt)
  │  ├─ ExecutionStateManager.beginPath(nodeId)
  │  │
  │  ├─ CompiledNodeBehavior.orchestrate(src, tgt, context, edgeCallback)
  │  │  │
  │  │  ├─ For each edge in node:
  │  │  │  │
  │  │  │  ├─ EdgeExecutor.executeEdge(edgePlan, srcVal, tgtVal)
  │  │  │  │  │
  │  │  │  │  ├─ Extract source value
  │  │  │  │  │  └─ accessorCache.getCachedOrCompute(sourceAccessor)
  │  │  │  │  │     └─ CompiledAccessor.execute(src)
  │  │  │  │  │
  │  │  │  │  ├─ Extract target value
  │  │  │  │  │  └─ accessorCache.getCachedOrCompute(targetAccessor)
  │  │  │  │  │     └─ CompiledAccessor.execute(tgt)
  │  │  │  │  │
  │  │  │  │  ├─ CompiledBehavior.execute(srcVal, tgtVal, context, strategyCallback)
  │  │  │  │  │  │
  │  │  │  │  │  ├─ Invoke ComparisonStrategy.compare(srcVal, tgtVal, context)
  │  │  │  │  │  │  └─ Return ExecutionOutcome (MATCH or MISMATCH)
  │  │  │  │  │  │
  │  │  │  │  │  ├─ If MATCH:
  │  │  │  │  │  │  └─ Report EDGE_COMPLETED with status MATCH
  │  │  │  │  │  │
  │  │  │  │  │  └─ If MISMATCH:
  │  │  │  │  │     └─ Report EDGE_COMPLETED with status MISMATCH
  │  │  │  │  │
  │  │  │  │  ├─ If edge has childNode:
  │  │  │  │  │  └─ Recursively call executeNode(childNode, srcVal, tgtVal)
  │  │  │  │  │
  │  │  │  │  └─ Report outcome to resultSink
  │  │  │  │
  │  │  │  └─ Continue to next edge
  │  │  │
  │  │  └─ Aggregate all edge outcomes
  │  │
  │  ├─ ExecutionStateManager.endPath()
  │  │
  │  └─ Return aggregated ExecutionOutcome
  │
  ├─ Cleanup ExecutionStateManager
  │
  └─ Return ComparisonResultTree (or stream results)
```

---

## Detailed State Management

### ExecutionContext Creation

```
ExecutionContext context = new ExecutionContext(
  currentPath: ComparisonPath.root(),
  remainingLimits: ExecutionLimits {
    depthRemaining: executionSemantics.maxDepth,
    breadthRemaining: executionSemantics.maxBreadth,
    timeRemaining: executionSemantics.timeout,
    differencesRemaining: executionSemantics.maxDifferences
  },
  executionSemantics: ExecutionSemantics { ... },
  resultSink: ResultSink (memory or streaming),
  accessorCache: AccessorResultCache(),
  shapeResolver: ShapeResolver(shapes from plan),
  metadata: Map()
)

IMMUTABLE: All fields are final
THREAD-SAFE: Can be shared across executors
SHARED: Passed to all nested executors
```

### ExecutionStateManager (Hidden)

```
ExecutionStateManager stateManager = new ExecutionStateManager(
  cycleGuard: CycleGuard {
    traversedPaths: Set<TraversalKey>
  },
  traversalPath: Stack<ComparisonPath> {
    [root, order, items[0]]
  },
  traversalHistory: Map<NodeId, Set<TraversalKey>> {
    nodeId → set of (source, target) pairs already traversed
  },
  currentDepth: 1,
  currentBreadth: 3,
  executionSemantics: ExecutionSemantics
)

HIDDEN: Never exposed in public API
MUTABLE: Modified during execution
INTERNAL: Only used by runtime core
```

---

## Path Tracking Through Execution

```
Start: ComparisonPath = Path.root()

Execute node "order":
  Path = root → "order"
  
  Execute edge "orderId":
    Path = root → "order" → "orderId"
    Extract values, compare, report
    
  Execute edge "customer":
    Path = root → "order" → "customer"
    Extract values
    
    If recursive node "customerNode":
      Execute node "customer":
        Path = root → "order" → "customer" → "name"
        Execute edge "name": ...
        Execute edge "email": ...
    
  Execute edge "items":
    Path = root → "order" → "items"
    Extract array values
    
    For each item [0]:
      Path = root → "order" → "items[0]"
      
      Execute node "item":
        Path = root → "order" → "items[0]" → "productId"
        Execute edge "productId": ...
```

---

# EXECUTION FLOW 2: COLLECTION HANDLING

## Collection Execution Pattern

```
ExecutionContext executionContext = ...
CompiledNodeBehavior nodeBehavior = ... // includes selected strategy

NodeExecutor.executeNode(nodeDefWithCollection, sourceArray, targetArray)
  │
  ├─ Check if node has collection handling strategy
  │
  ├─ Extract arrays/collections from source and target
  │  └─ sourceArray = accessor.execute(source)
  │  └─ targetArray = accessor.execute(target)
  │
  ├─ Invoke collection matching strategy:
  │  │
  │  ├─ Strategy: UNORDERED_BY_IDENTITY
  │  │  └─ Match by object identity (using ==)
  │  │  └─ Return: Alignment {
  │  │       pairs: [(item1_src, item1_tgt), ...],
  │  │       unmatched: {source: [...], target: [...]}
  │  │     }
  │  │
  │  ├─ Strategy: UNORDERED_BY_EQUALITY
  │  │  └─ Match by equals() method
  │  │  └─ Return: Alignment with equals-based matching
  │  │
  │  ├─ Strategy: ORDERED_BY_POSITION
  │  │  └─ Match item[i] with item[i]
  │  │  └─ Return: Alignment with positional pairs
  │  │
  │  ├─ Strategy: ORDERED_BY_LCS
  │  │  └─ Longest common subsequence
  │  │  └─ Optimal matching respecting order
  │  │  └─ Return: Alignment with LCS-based pairs
  │  │
  │  └─ Strategy: KEYED_BY_KEY
  │     └─ Match by key extractor (e.g., ID field)
  │     └─ Return: Alignment with key-based matching
  │
  ├─ For each matched pair (src_item, tgt_item):
  │  │
  │  ├─ Execute item comparison:
  │  │  │
  │  │  ├─ If childNode specified:
  │  │  │  └─ executeNode(childNode, src_item, tgt_item)
  │  │  │  └─ Recursively compare item structure
  │  │  │
  │  │  └─ Report outcome for pair
  │  │
  │  └─ Track in results
  │
  ├─ For each unmatched source item:
  │  └─ Report as EXTRA (in target but not source)
  │
  ├─ For each unmatched target item:
  │  └─ Report as MISSING (in source but not target)
  │
  └─ Return aggregated outcome
```

---

# EXECUTION FLOW 3: ERROR HANDLING

## Error Propagation Pattern

```
Try:
  EdgeExecutor.executeEdge(...)
    │
    Try:
      CompiledAccessor.execute(source)
    Catch AccessorException e:
      │
      └─ Wrap in ExecutionError:
         ExecutionError {
           errorType: "ACCESSOR_ERROR",
           cause: e,
           providerType: "AccessorProvider",
           providerId: e.getProviderId(),
           recoverable: e.isRecoverable(),
           message: e.getMessage(),
           context: {nodeId, edgeId, path}
         }
      │
      └─ Create ExecutionOutcome:
         ExecutionOutcome {
           status: ERROR,
           error: executionError,
           eventType: EDGE_ERROR
         }
      │
      └─ Check ExecutionSemantics.errorHandling:
         │
         ├─ FAIL_FAST:
         │  └─ Throw ExecutionException
         │  └─ Stop entire comparison
         │  └─ Return partial results
         │
         └─ CONTINUE:
            └─ Record error in outcome
            └─ Continue execution
            └─ Accumulate errors

    Try:
      ComparisonStrategy.compare(srcVal, tgtVal, context)
    Catch StrategyException e:
      │
      └─ Similar error wrapping and handling
      │
      └─ Can be more recoverable (default comparison used)
```

## Error Outcome Representation

```
ExecutionOutcome {
  status: ERROR,
  error: ExecutionError {
    errorType: String,
    cause: Throwable,
    providerType: String,
    providerId: String,
    recoverable: boolean,
    message: String,
    context: Map<String, Object>
  },
  sourceValue: Object (if extracted before error),
  targetValue: Object (if extracted before error),
  path: ComparisonPath,
  timestamp: long,
  eventType: EDGE_ERROR or NODE_ERROR
}
```

---

# EXECUTION FLOW 4: CYCLE DETECTION

## Cycle Detection Pattern

```
GraphTraversalState {
  traversedPaths: Set<TraversalKey>
    where TraversalKey = (nodeId, srcObjectId, tgtObjectId)
}

Before executing node:
  │
  ├─ Compute traversal key:
  │  └─ key = TraversalKey(nodeId, System.identityHashCode(src), System.identityHashCode(tgt))
  │
  ├─ Check if already traversed:
  │  └─ if (traversedPaths.contains(key)) → CYCLE DETECTED
  │
  ├─ If cycle detected:
  │  │
  │  ├─ Check ExecutionSemantics.cycleHandling:
  │  │  │
  │  │  ├─ SKIP:
  │  │  │  └─ Return ExecutionOutcome {status: SKIPPED}
  │  │  │  └─ Continue execution
  │  │  │
  │  │  └─ ERROR:
  │  │     └─ Throw ExecutionException
  │  │     └─ Report cycle as error
  │  │     └─ Handle as per error handling mode
  │  │
  │  └─ Record cycle in results
  │
  └─ If no cycle:
     └─ Add key to traversedPaths
     └─ Execute node normally
```

---

# EXECUTION FLOW 5: LIMIT ENFORCEMENT

## Depth Limit Checking

```
Before recursing into childNode:
  │
  ├─ Check currentDepth vs maxDepth:
  │  └─ if (currentDepth >= maxDepth) → DEPTH LIMIT EXCEEDED
  │
  ├─ If limit exceeded:
  │  │
  │  ├─ Check ExecutionSemantics.depthLimitMode:
  │  │  │
  │  │  ├─ FAIL_FAST:
  │  │  │  └─ Throw ExecutionException
  │  │  │
  │  │  └─ TRUNCATE:
  │  │     └─ Return PARTIAL outcome
  │  │     └─ Do not recurse further
  │  │
  │  └─ Record limit exceeded in metadata
  │
  └─ If not exceeded:
     └─ Increment currentDepth
     └─ Recurse
     └─ Decrement currentDepth
```

## Breadth Limit Checking (for collections)

```
Before processing collection items:
  │
  ├─ Check collection.size() vs maxBreadth:
  │  └─ if (collection.size() > maxBreadth) → BREADTH LIMIT EXCEEDED
  │
  ├─ If limit exceeded:
  │  │
  │  ├─ Truncate collection to first maxBreadth items
  │  ├─ Process truncated collection
  │  └─ Record truncation in metadata
  │
  └─ If not exceeded:
     └─ Process entire collection
```

---

# EXECUTION FLOW 6: RESULT COLLECTION

## Memory-Based Result Collection

```
MemoryResultSink sink = new MemoryResultSink()

During execution:
  │
  ├─ Each ExecutionOutcome is reported to sink
  │  └─ sink.reportOutcome(outcome)
  │
  ├─ Sink accumulates outcomes in memory
  │  └─ resultTree.addNode(outcome)
  │
  ├─ Outcomes are linked in tree structure
  │  └─ resultTree.linkChild(parentPath, childOutcome)
  │
  └─ Differences are extracted and indexed
     └─ differenceIndex.add(difference)

After execution:
  │
  └─ sink.getResult() → ComparisonResultTree
     {
       rootResult: ResultNode,
       allDifferences: List<DifferenceNode>,
       status: MATCH | MISMATCH | ERROR | PARTIAL,
       executionMetadata: {...}
     }
```

## Streaming-Based Result Collection

```
StreamingResultSink sink = new StreamingResultSink(writer)

During execution:
  │
  ├─ Each ExecutionOutcome is immediately written
  │  └─ sink.reportOutcome(outcome)
  │     └─ writer.writeOutcome(outcome)
  │     └─ No buffering
  │
  ├─ No result tree is built
  ├─ No difference list is accumulated
  │
  └─ Memory usage is constant (independent of result size)

After execution:
  │
  └─ sink.getStatistics() → ExecutionStatistics
     {
       totalOutcomes: int,
       matchedEdges: int,
       mismatchedEdges: int,
       erroredEdges: int,
       executionTime: long
     }
```

---

# EXECUTION SCENARIO 1: SIMPLE SCALAR COMPARISON

```
Input:
  source = Order {id: "123", customer: "John", total: 100.0}
  target = Order {id: "123", customer: "Jane", total: 100.0}

Execution:

1. ExecutionContext created
2. ExecutionStateManager created
3. NodeExecutor.executeNode(orderNode, source, target)
   ├─ Check cycle: No cycle
   ├─ NodeBehavior.orchestrate(source, target)
   │  ├─ Edge "id":
   │  │  ├─ Extract: srcVal=123, tgtVal=123
   │  │  ├─ Strategy.compare(123, 123) → MATCH
   │  │  └─ Report: EDGE_COMPLETED MATCH
   │  │
   │  ├─ Edge "customer":
   │  │  ├─ Extract: srcVal="John", tgtVal="Jane"
   │  │  ├─ Strategy.compare("John", "Jane") → MISMATCH
   │  │  └─ Report: EDGE_COMPLETED MISMATCH
   │  │     └─ Difference: path="customer", src="John", tgt="Jane"
   │  │
   │  └─ Edge "total":
   │     ├─ Extract: srcVal=100.0, tgtVal=100.0
   │     ├─ Strategy.compare(100.0, 100.0) → MATCH
   │     └─ Report: EDGE_COMPLETED MATCH
   │
   └─ Aggregate: 2 matches, 1 mismatch → MISMATCH

Result:
  ComparisonResultTree {
    status: MISMATCH,
    differences: [
      DifferenceNode {
        path: "customer",
        sourceValue: "John",
        targetValue: "Jane"
      }
    ]
  }
```

---

# EXECUTION SCENARIO 2: RECURSIVE OBJECT COMPARISON

```
Input:
  source = Order {
    id: "123",
    customer: Customer {name: "John", email: "john@example.com"}
  }
  target = Order {
    id: "123",
    customer: Customer {name: "John", email: "jane@example.com"}
  }

Execution:

1. NodeExecutor.executeNode(orderNode, source, target)
   ├─ Edge "id": Extract and compare → MATCH
   │
   └─ Edge "customer" (with childNode: CustomerNode):
      ├─ Extract: srcVal=Customer{...}, tgtVal=Customer{...}
      ├─ Behavior.execute(srcVal, tgtVal)
      │
      ├─ childNode specified → Recurse:
      │  └─ NodeExecutor.executeNode(customerNode, srcCustomer, tgtCustomer)
      │     ├─ Edge "name": Extract and compare → MATCH
      │     │
      │     └─ Edge "email": Extract and compare → MISMATCH
      │        └─ Difference: path="customer.email", src="john@...", tgt="jane@..."
      │
      └─ Return aggregated outcome: MISMATCH

Result:
  ComparisonResultTree {
    status: MISMATCH,
    rootResult: ResultNode {
      nodeId: "order",
      differences: [
        ResultNode {
          nodeId: "customer",
          differences: [
            DifferenceNode {
              path: "customer.email",
              sourceValue: "john@example.com",
              targetValue: "jane@example.com"
            }
          ]
        }
      ]
    }
  }
```

---

# EXECUTION SCENARIO 3: COLLECTION COMPARISON WITH LCS MATCHING

```
Input:
  source.items = [Item{id:1}, Item{id:2}, Item{id:3}]
  target.items = [Item{id:1}, Item{id:3}]

Collection Handling Mode: ORDERED_BY_LCS

Execution:

1. Extract collections:
   srcItems = [1, 2, 3]
   tgtItems = [1, 3]

2. Apply LCS matching:
   LCS = [1, 3]
   Matched pairs: [(1,1), (3,3)]
   Unmatched source: [2]
   Unmatched target: []

3. Compare matched pairs:
   ├─ Pair (1,1): MATCH
   └─ Pair (3,3): MATCH

4. Process unmatched:
   └─ Item 2 in source: Report as EXTRA

Result:
  ComparisonResultTree {
    status: MISMATCH,
    differences: [
      DifferenceNode {
        type: EXTRA,
        path: "items[1]",
        sourceValue: Item{id:2},
        targetValue: null
      }
    ]
  }
```

---

**EXECUTION FLOWS COMPLETE**

These detailed flows provide implementation-level guidance for runtime execution.
