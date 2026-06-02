package com.checkaboy.comparator.matcher;

import com.google.common.reflect.TypeToken;

/**
 * @author Taras Shaptala
 */
public final class AssignableTypeMatcher
        implements ITypeMatcher {

    @Override
    public boolean matches(TypeToken<?> registeredSource, TypeToken<?> registeredTarget, TypeToken<?> actualSource, TypeToken<?> actualTarget) {
        return registeredSource.isSupertypeOf(actualSource) && registeredTarget.isSupertypeOf(actualTarget);
    }

}