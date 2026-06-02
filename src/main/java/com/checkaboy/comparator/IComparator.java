package com.checkaboy.comparator;

/**
 * @author Taras Shaptala
 */
@FunctionalInterface
public interface IComparator<S, T> {

    /**
     * Basic comparison method
     *
     * @param source object being compared
     * @param target object being compared
     * @return equal or not
     */
    boolean compare(S source, T target);

}
