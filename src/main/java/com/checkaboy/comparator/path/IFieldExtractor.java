package com.checkaboy.comparator.path;

/**
 * @author Taras Shaptala
 */
@FunctionalInterface
public interface IFieldExtractor<S, V> {

    V extract(S source);

}