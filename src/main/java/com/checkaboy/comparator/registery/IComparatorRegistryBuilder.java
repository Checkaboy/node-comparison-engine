package com.checkaboy.comparator.registery;

import com.checkaboy.comparator.IComparatorFactory;
import com.google.common.reflect.TypeToken;

/**
 * @author Taras Shaptala
 */
public interface IComparatorRegistryBuilder {

    <S, T> IComparatorRegistryBuilder register(TypeToken<S> source, TypeToken<T> target, IComparatorFactory<S, T> factory);

    IComparatorRegistry build();

}
