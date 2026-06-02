package com.checkaboy.comparator.context.factory;

import com.checkaboy.comparator.context.cache.IComparisonContext;

/**
 * @author Taras Shaptala
 */
@FunctionalInterface
public interface IComparisonContextFactory {

    IComparisonContext create();

}
