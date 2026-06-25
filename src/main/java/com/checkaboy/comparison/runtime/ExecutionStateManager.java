package com.checkaboy.comparison.runtime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Execution state manager - hidden from public API.
 * Manages traversal state during execution.
 *
 * @author Taras Shaptala
 */
final class ExecutionStateManager {

    private final CycleGuard cycleGuard;
    private final Deque<String> traversalPath;
    private final Map<String, Set<TraversalKey>> traversalHistory;
    private int currentDepth;
    private ExecutionSemantics executionSemantics;

    ExecutionStateManager(ExecutionSemantics executionSemantics) {
        this.cycleGuard = new CycleGuard();
        this.traversalPath = new LinkedList<>();
        this.traversalHistory = new ConcurrentHashMap<>();
        this.currentDepth = 0;
        this.executionSemantics = executionSemantics;
        this.traversalPath.push("root");
    }

    /**
     * Check if we can traverse a node.
     */
    boolean canTraverse(String nodeId, Object source, Object target) {
        return cycleGuard.canTraverse(nodeId, source, target);
    }

    /**
     * Record a traversal.
     */
    void recordTraversal(String nodeId, Object source, Object target) {
        cycleGuard.recordTraversal(nodeId, source, target);

        TraversalKey key = new TraversalKey(nodeId, source, target);
        traversalHistory.computeIfAbsent(nodeId, k -> ConcurrentHashMap.newKeySet())
                .add(key);
    }

    /**
     * Begin a path segment.
     */
    void beginPath(String segment) {
        traversalPath.push(segment);
        currentDepth++;
    }

    /**
     * End a path segment.
     */
    void endPath() {
        if (!traversalPath.isEmpty()) {
            traversalPath.pop();
        }
        currentDepth--;
    }

    /**
     * Get current depth.
     */
    int getCurrentDepth() {
        return currentDepth;
    }

    /**
     * Get current path.
     */
    String getCurrentPath() {
        return String.join(".", traversalPath.stream()
                .sorted(Collections.reverseOrder())
                .toList());
    }

    /**
     * Check if cycle is detected.
     */
    boolean isCycleDetected(String nodeId, Object source, Object target) {
        return cycleGuard.isCyclic(nodeId, source, target);
    }

}