package com.checkaboy.comparison.runtime;

/**
 * Execution event type enumeration.
 *
 * @author Taras Shaptala
 */
public enum ExecutionEventType {
    NODE_START,
    NODE_COMPLETED,
    NODE_ERROR,
    EDGE_START,
    EDGE_COMPLETED,
    EDGE_ERROR,
    COLLECTION_ITEM_MATCHED,
    COLLECTION_ITEM_UNMATCHED
}