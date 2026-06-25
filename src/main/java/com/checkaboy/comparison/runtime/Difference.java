package com.checkaboy.comparison.runtime;

import com.checkaboy.structural.path.ComparisonPath;

/**
 * Difference information.
 *
 * @author Taras Shaptala
 */
public final class Difference {

    private final ComparisonPath path;
    private final DifferenceType type;
    private final Object sourceValue;
    private final Object targetValue;
    private final String detail;

    public Difference(
            ComparisonPath path,
            DifferenceType type,
            Object sourceValue,
            Object targetValue,
            String detail
    ) {
        this.path = path;
        this.type = type;
        this.sourceValue = sourceValue;
        this.targetValue = targetValue;
        this.detail = detail;
    }

    public ComparisonPath getPath() {
        return path;
    }

    public DifferenceType getType() {
        return type;
    }

    public Object getSourceValue() {
        return sourceValue;
    }

    public Object getTargetValue() {
        return targetValue;
    }

    public String getDetail() {
        return detail;
    }

}