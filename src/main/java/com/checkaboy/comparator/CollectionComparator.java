package com.checkaboy.comparator;

import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.context.cache.IComparisonContext;
import com.google.common.reflect.TypeToken;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Taras Shaptala
 */
public final class CollectionComparator<S, T>
        implements IContextualComparator<Collection<S>, Collection<T>> {

    private final TypeToken<S> sourceElementType;
    private final TypeToken<T> targetElementType;
    private volatile IContextualComparator<S, T> cached;

    public CollectionComparator(TypeToken<S> sourceElementType, TypeToken<T> targetElementType) {
        this.sourceElementType = sourceElementType;
        this.targetElementType = targetElementType;
    }

    @Override
    public boolean compare(IComparisonContext ctx, Collection<S> source, Collection<T> target) {
        if (source == null && target == null) return true;
        if (source == null || target == null || source.size() != target.size()) return false;
        IContextualComparator<S, T> cmp = cached;

        if (cmp == null) {
            cmp = ctx.registry().resolve(sourceElementType, targetElementType, ctx);
            cached = cmp;
        }

        Iterator<S> l = source.iterator();
        Iterator<T> r = target.iterator();

        while (l.hasNext()) {
            if (!cmp.compare(ctx, l.next(), r.next())) return false;
        }

        return true;
    }

}