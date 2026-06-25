package com.checkaboy.structural.accessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of accessor validation.
 *
 * @author Taras Shaptala
 */
public final class ValidationResult {

    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.errors = Collections.unmodifiableList(
                errors != null ? new ArrayList<>(errors) : new ArrayList<>()
        );
        this.warnings = Collections.unmodifiableList(
                warnings != null ? new ArrayList<>(warnings) : new ArrayList<>()
        );
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

}