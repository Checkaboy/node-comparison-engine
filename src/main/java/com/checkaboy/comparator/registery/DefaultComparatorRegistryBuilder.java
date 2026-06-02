package com.checkaboy.comparator.registery;

import com.checkaboy.comparator.matcher.AssignableTypeMatcher;
import com.checkaboy.comparator.IComparatorFactory;
import com.checkaboy.comparator.matcher.ITypeMatcher;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Taras Shaptala
 */
public final class DefaultComparatorRegistryBuilder
        implements IComparatorRegistryBuilder {

    private final List<DefaultComparatorRegistry.Entry> entries = new ArrayList<>();
    private ITypeMatcher typeMatcher = new AssignableTypeMatcher();

    @Override
    public <S, T> IComparatorRegistryBuilder register(TypeToken<S> source, TypeToken<T> target, IComparatorFactory<S, T> factory) {
        entries.add(new DefaultComparatorRegistry.Entry(source, target, factory));
        return this;
    }

    public DefaultComparatorRegistryBuilder typeMatcher(ITypeMatcher typeMatcher) {
        this.typeMatcher = Objects.requireNonNull(typeMatcher, "typeMatcher must not be null");
        return this;
    }

    @Override
    public IComparatorRegistry build() {
        return new DefaultComparatorRegistry(entries, typeMatcher);
    }

}
