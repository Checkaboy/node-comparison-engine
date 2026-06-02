package com.checkaboy.comparator.context.cache;

import com.checkaboy.comparator.path.IComparisonPath;
import com.checkaboy.comparator.policy.IComparisonPolicy;
import com.checkaboy.comparator.registery.IComparatorRegistry;

/**
 * @author Taras Shaptala
 */
public interface IComparisonContext {

    boolean enter(Object a, Object b);

    IComparatorRegistry registry();

    IComparisonPolicy policy();

    IComparisonPath path();
}
