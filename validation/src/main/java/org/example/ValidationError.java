package org.example;

import java.util.Objects;

public record ValidationError(String fieldName, String annotationName, String message) {
    public ValidationError {
        Objects.requireNonNull(fieldName, "fieldName не может быть null");
        Objects.requireNonNull(annotationName, "annotationName не может быть null");
        Objects.requireNonNull(message, "message не может быть null");
    }
}
