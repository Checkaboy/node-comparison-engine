package com.checkaboy.comparator;

import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.context.cache.IComparisonContext;
import com.checkaboy.comparator.context.cache.PathAwareComparisonContext;
import com.checkaboy.comparator.path.IComparisonPath;
import com.checkaboy.comparator.path.IFieldExtractor;

import java.util.List;
import java.util.Objects;

/**
 * @author Taras Shaptala
 */
public final class ObjectComparator<S, T>
        implements IContextualComparator<S, T> {

    private final List<FieldNode<S, T, ?, ?>> fields;

    ObjectComparator(List<FieldNode<S, T, ?, ?>> fields) {
        this.fields = List.copyOf(fields);
    }

    @Override
    public boolean compare(IComparisonContext context, S source, T target) {
        if (source == null && target == null) return true;
        if (source == null || target == null) return false;
        if (!context.enter(source, target)) return true;
        for (FieldNode<S, T, ?, ?> f : fields) {
            IComparisonPath child = context.path().child(f.getName());
            if (!context.policy().isFieldEnabled(child)) continue;
            if (!f.compare(new PathAwareComparisonContext(context, child), source, target)) return false;
        }
        return true;
    }

}