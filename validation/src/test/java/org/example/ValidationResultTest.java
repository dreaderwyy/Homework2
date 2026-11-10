package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationResultTest {
    private final ValidationError error = new ValidationError("name", "NotNull", "Имя обязательно");

    @Test
    void emptyListMeansValid() {
        ValidationResult result = new ValidationResult(List.of());

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void nonEmptyListMeansInvalid() {
        ValidationResult result = new ValidationResult(List.of(error));

        assertFalse(result.isValid());
        assertEquals(List.of(error), result.getErrors());
    }

    @Test
    void nullListIsRejected() {
        assertThrows(NullPointerException.class, () -> new ValidationResult(null));
    }

    @Test
    void constructorMakesDefensiveCopy() {
        List<ValidationError> source = new ArrayList<>();
        source.add(error);
        ValidationResult result = new ValidationResult(source);

        source.add(new ValidationError("age", "Range", "Возраст"));
        source.clear();

        assertEquals(1, result.getErrors().size());
        assertEquals(error, result.getErrors().get(0));
    }

    @Test
    void errorsListCannotBeModified() {
        ValidationResult result = new ValidationResult(List.of(error));

        assertThrows(UnsupportedOperationException.class, () -> result.getErrors().add(error));
        assertThrows(UnsupportedOperationException.class, () -> result.getErrors().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> result.getErrors().clear());
    }

    @Test
    void validationErrorKeepsAllFields() {
        assertEquals("name", error.fieldName());
        assertEquals("NotNull", error.annotationName());
        assertEquals("Имя обязательно", error.message());
    }

    @Test
    void validationErrorRejectsNullParts() {
        assertThrows(NullPointerException.class, () -> new ValidationError(null, "NotNull", "m"));
        assertThrows(NullPointerException.class, () -> new ValidationError("name", null, "m"));
        assertThrows(NullPointerException.class, () -> new ValidationError("name", "NotNull", null));
    }
}
