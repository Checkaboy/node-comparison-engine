package com.checkaboy.comparator;

import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.context.cache.IComparisonContext;
import com.google.common.reflect.TypeToken;

/**
 * @author Taras Shaptala
 */
public final class ArrayComparator<S, T>
        implements IContextualComparator<S[], T[]> {

    private final TypeToken<S> sourceElementType;
    private final TypeToken<T> targetElementType;
    private volatile IContextualComparator<S, T> cached;

    public ArrayComparator(TypeToken<S> sourceElementType, TypeToken<T> targetElementType) {
        this.sourceElementType = sourceElementType;
        this.targetElementType = targetElementType;
    }

    @Override
    public boolean compare(IComparisonContext ctx, S[] source, T[] target) {
        if (source == null && target == null) return true;
        if (source == null || target == null || source.length != target.length) return false;
        IContextualComparator<S, T> cmp = cached;

        if (cmp == null) {
            cmp = ctx.registry().resolve(sourceElementType, targetElementType, ctx);
            cached = cmp;
        }

        for (int i = 0; i < source.length; i++) {
            if (!cmp.compare(ctx, source[i], target[i])) return false;
        }

        return true;
    }

}
