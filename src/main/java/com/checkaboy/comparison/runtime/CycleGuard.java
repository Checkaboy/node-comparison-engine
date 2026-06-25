package com.checkaboy.comparison.runtime;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cycle guard - detects cycles during traversal.
 *
 * @author Taras Shaptala
 */
public final class CycleGuard {

    private final Set<TraversalKey> traversedPaths;

    public CycleGuard() {
        this.traversedPaths = ConcurrentHashMap.newKeySet();
    }

    /**
     * Check if we can traverse this path (not a cycle).
     */
    public boolean canTraverse(String nodeId, Object source, Object target) {
        TraversalKey key = new TraversalKey(nodeId, source, target);
        return !traversedPaths.contains(key);
    }

    /**
     * Record a traversal.
     */
    public void recordTraversal(String nodeId, Object source, Object target) {
        TraversalKey key = new TraversalKey(nodeId, source, target);
        traversedPaths.add(key);
    }

    /**
     * Check if path is cyclic.
     */
    public boolean isCyclic(String nodeId, Object source, Object target) {
        return !canTraverse(nodeId, source, target);
    }

}