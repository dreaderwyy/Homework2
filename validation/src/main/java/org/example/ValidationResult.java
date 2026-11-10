package org.example;

import java.util.List;

public final class ValidationResult {
    private final List<ValidationError> errors;

    public ValidationResult(List<ValidationError> errors) {
        if (errors == null) {
            throw new NullPointerException("Список ошибок не может быть null");
        }
        this.errors = List.copyOf(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ValidationResult{valid=" + isValid() + ", errors=" + errors + "}";
    }
}
