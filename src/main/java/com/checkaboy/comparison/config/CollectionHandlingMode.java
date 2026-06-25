package com.checkaboy.comparison.config;

/**
 * Collection handling mode enumeration.
 *
 * @author Taras Shaptala
 */
public enum CollectionHandlingMode {
    UNORDERED_BY_IDENTITY,
    UNORDERED_BY_EQUALITY,
    UNORDERED_BY_GRAPH,
    ORDERED_BY_POSITION,
    ORDERED_BY_LCS,
    ORDERED_BY_EDIT_DISTANCE,
    KEYED_BY_KEY,
    GRAPH_BY_NODE_ID,
    GRAPH_BY_TOPOLOGY,
    CUSTOM,
    AUTO
}