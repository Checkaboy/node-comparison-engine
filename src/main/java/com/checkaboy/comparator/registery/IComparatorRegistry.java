package com.checkaboy.comparator.registery;

import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.context.cache.IComparisonContext;
import com.google.common.reflect.TypeToken;

/**
 * @author Taras Shaptala
 */
public interface IComparatorRegistry {

    <S, T> IContextualComparator<S, T> resolve(TypeToken<S> source, TypeToken<T> target, IComparisonContext context);

}
