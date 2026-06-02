package com.checkaboy.comparator.matcher;

import com.google.common.reflect.TypeToken;

/**
 * @author Taras Shaptala
 */
@FunctionalInterface
public interface ITypeMatcher {

    boolean matches(TypeToken<?> registeredSource, TypeToken<?> registeredTarget, TypeToken<?> actualSource, TypeToken<?> actualTarget);

}
