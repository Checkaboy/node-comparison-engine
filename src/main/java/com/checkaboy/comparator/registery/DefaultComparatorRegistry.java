package com.checkaboy.comparator.registery;

import com.checkaboy.comparator.IComparatorFactory;
import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.matcher.ITypeMatcher;
import com.checkaboy.comparator.context.cache.IComparisonContext;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Taras Shaptala
 */
public final class DefaultComparatorRegistry
        implements IComparatorRegistry {

    private final List<Entry> entries;
    private final ITypeMatcher typeMatcher;

    DefaultComparatorRegistry(List<Entry> entries, ITypeMatcher typeMatcher) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
        this.typeMatcher = Objects.requireNonNull(typeMatcher, "typeMatcher must not be null");
    }

    @Override
    public <S, T> IContextualComparator<S, T> resolve(
            TypeToken<S> source,
            TypeToken<T> target,
            IComparisonContext context
    ) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(target, "target must not be null");
        for (Entry entry : entries) {
            if (entry.source.equals(source) && entry.target.equals(target)) {
                return castFactory(entry.factory, source, target).create(context);
            }
        }
        for (Entry entry : entries) {
            if (typeMatcher.matches(entry.source, entry.target, source, target)) {
                return castFactory(entry.factory, source, target).create(context);
            }
        }
        throw new IllegalStateException("No comparator registered for " + source + " -> " + target);
    }

    @SuppressWarnings("unchecked")
    private static <S, T> IComparatorFactory<S, T> castFactory(
            IComparatorFactory<?, ?> f,
            TypeToken<S> sTypeToken,
            TypeToken<T> tTypeToken
    ) {
        return (IComparatorFactory<S, T>) f;
    }

    static final class Entry {

        private final TypeToken<?> source;
        private final TypeToken<?> target;
        private final IComparatorFactory<?, ?> factory;

        Entry(TypeToken<?> source, TypeToken<?> target, IComparatorFactory<?, ?> factory) {
            this.source = source;
            this.target = target;
            this.factory = factory;
        }

    }

}
