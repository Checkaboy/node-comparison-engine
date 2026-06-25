package com.checkaboy.comparison.config;

/**
 * Result policy enumeration.
 *
 * @author Taras Shaptala
 */
public enum ResultPolicy {
    ALL_DIFFERENCES,      // Collect all differences
    FIRST_DIFFERENCE,     // Stop after first difference
    SUMMARY_ONLY          // Only summary, no details
}