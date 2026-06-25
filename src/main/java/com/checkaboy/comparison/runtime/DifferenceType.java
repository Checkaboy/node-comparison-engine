package com.checkaboy.comparison.runtime;

/**
 * Difference type enumeration.
 *
 * @author Taras Shaptala
 */
public enum DifferenceType {
    VALUE,        // Values don't match
    TYPE,         // Types differ
    STRUCTURE,    // Structure differs
    MISSING,      // Value missing in source
    EXTRA         // Extra value in source
}