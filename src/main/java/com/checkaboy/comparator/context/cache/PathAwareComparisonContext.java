package com.checkaboy.comparator.context.cache;

import com.checkaboy.comparator.path.IComparisonPath;
import com.checkaboy.comparator.policy.IComparisonPolicy;
import com.checkaboy.comparator.registery.IComparatorRegistry;

/**
 * @author Taras Shaptala
 */
public final class PathAwareComparisonContext implements IComparisonContext {

    private final IComparisonContext delegate;
    private final IComparisonPath path;

    public PathAwareComparisonContext(IComparisonContext delegate, IComparisonPath path) {
        this.delegate = delegate;
        this.path = path;
    }

    @Override
    public boolean enter(Object a, Object b) {
        return delegate.enter(a, b);
    }

    @Override
    public IComparatorRegistry registry() {
        return delegate.registry();
    }

    @Override
    public IComparisonPolicy policy() {
        return delegate.policy();
    }

    @Override
    public IComparisonPath path() {
        return path;
    }

}