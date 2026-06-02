package com.checkaboy.comparator;

/**
 * @author Taras Shaptala
 */
@FunctionalInterface
public interface IComparable<S, T> {

    IComparator<S, T> comparator();

}
