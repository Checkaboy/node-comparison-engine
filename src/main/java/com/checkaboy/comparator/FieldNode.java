package com.checkaboy.comparator;

import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.context.cache.IComparisonContext;
import com.checkaboy.comparator.path.IFieldExtractor;

import java.util.Objects;

/**
 * @author Taras Shaptala
 */
public final class FieldNode<S, T, SV, TV>
        implements IContextualComparator<S, T> {

    private final String name;
    private final IFieldExtractor<S, SV> sourceExtractor;
    private final IFieldExtractor<T, TV> targetExtractor;
    private final IContextualComparator<SV, TV> comparator;

    FieldNode(String name, IFieldExtractor<S, SV> sourceExtractor, IFieldExtractor<T, TV> targetExtractor, IContextualComparator<SV, TV> comparator) {
        this.name = Objects.requireNonNull(name);
        this.sourceExtractor = sourceExtractor;
        this.targetExtractor = targetExtractor;
        this.comparator = comparator;
    }

    public String getName() {
        return name;
    }

    public IFieldExtractor<S, SV> getSourceExtractor() {
        return sourceExtractor;
    }

    public IFieldExtractor<T, TV> getTargetExtractor() {
        return targetExtractor;
    }

    public IContextualComparator<SV, TV> getComparator() {
        return comparator;
    }

    @Override
    public boolean compare(IComparisonContext ctx, S source, T target) {
        return comparator.compare(ctx, sourceExtractor.extract(source), targetExtractor.extract(target));
    }

}
