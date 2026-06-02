package com.checkaboy.comparator;

import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.context.cache.IComparisonContext;

/**
 * @author Taras Shaptala
 */
public interface IComparatorFactory<S, T> {

    IContextualComparator<S, T> create(IComparisonContext context);

}