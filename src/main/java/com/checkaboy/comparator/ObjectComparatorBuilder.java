package com.checkaboy.comparator;

import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.path.IFieldExtractor;
import com.checkaboy.comparator.registery.IComparatorRegistryBuilder;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taras Shaptala
 */
public final class ObjectComparatorBuilder<S, T> {

    private final TypeToken<S> sourceType;
    private final TypeToken<T> targetType;
    private final IComparatorRegistryBuilder registry;
    private final List<FieldNode<S, T, ?, ?>> fields = new ArrayList<>();

    public ObjectComparatorBuilder(
            TypeToken<S> sourceType,
            TypeToken<T> targetType,
            IComparatorRegistryBuilder registry
    ) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.registry = registry;
    }

    public <SV, TV> ObjectComparatorBuilder<S, T> field(
            String name,
            IFieldExtractor<S, SV> sourceExtractor,
            IFieldExtractor<T, TV> targetExtractor,
            IContextualComparator<SV, TV> comparator
    ) {
        fields.add(new FieldNode<>(name, sourceExtractor, targetExtractor, comparator));
        return this;
    }

    public <SV, TV> ObjectComparatorBuilder<S, T> field(
            String name,
            TypeToken<SV> sourceFieldType,
            TypeToken<TV> targetFieldType,
            IFieldExtractor<S, SV> sourceExtractor,
            IFieldExtractor<T, TV> targetExtractor
    ) {
        return field(name, sourceExtractor, targetExtractor, (ctx, s, t) ->
                ctx.registry().resolve(sourceFieldType, targetFieldType, ctx).compare(ctx, s, t)
        );
    }

    public IComparatorRegistryBuilder buildAndRegister() {
        ObjectComparator<S, T> comparator = new ObjectComparator<>(fields);
        registry.register(sourceType, targetType, ctx -> comparator);
        return registry;
    }

}
