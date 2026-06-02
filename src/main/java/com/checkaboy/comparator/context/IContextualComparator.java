package com.checkaboy.comparator.context;

import com.checkaboy.comparator.context.cache.IComparisonContext;

/**
 * @author Taras Shaptala
 */
@FunctionalInterface
public interface IContextualComparator<S, T> {

    /**
     * Basic comparison method
     *
     * @param comparisonContext comparison context
     * @param source            object being compared
     * @param target            object being compared
     * @return equal or not
     */
    boolean compare(IComparisonContext comparisonContext, S source, T target);

}
