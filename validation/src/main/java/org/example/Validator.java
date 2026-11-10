package org.example;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class Validator {

    private Validator() {
    }

    public static ValidationResult validate(Object object) {
        if (object == null) {
            throw new NullPointerException("Объект для валидации не может быть null");
        }

        List<ValidationError> errors = new ArrayList<>();
        for (Class<?> c = object.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                validateField(object, field, errors);
            }
        }
        return new ValidationResult(errors);
    }

    private static void validateField(Object object, Field field, List<ValidationError> errors) {
        NotNull notNull = field.getAnnotation(NotNull.class);
        Size size = field.getAnnotation(Size.class);
        Range range = field.getAnnotation(Range.class);
        Email email = field.getAnnotation(Email.class);

        if (notNull == null && size == null && range == null && email == null) {
            return;
        }

        if (size != null) {
            checkSizeConfiguration(field, size);
        }
        if (range != null) {
            checkRangeConfiguration(field, range);
        }
        if (email != null) {
            checkCharSequenceType(field, email);
        }

        Object value = readValue(object, field);

        if (notNull != null && value == null) {
            addError(errors, field, notNull, notNull.message());
        }
        if (value == null) {
            return;
        }
        if (size != null) {
            int length = ((CharSequence) value).length();
            if (length < size.min() || length > size.max()) {
                addError(errors, field, size, size.message());
            }
        }
        if (range != null) {
            double number = ((Number) value).doubleValue();
            if (Double.isNaN(number) || number < range.min() || number > range.max()) {
                addError(errors, field, range, range.message());
            }
        }
        if (email != null && !isValidEmail(value.toString())) {
            addError(errors, field, email, email.message());
        }
    }

    private static void checkSizeConfiguration(Field field, Size size) {
        checkCharSequenceType(field, size);
        if (size.min() < 0 || size.min() > size.max()) {
            throw new ValidationConfigurationException("Некорректные границы @Size на поле "
                    + describe(field) + ": min=" + size.min() + ", max=" + size.max());
        }
    }

    private static void checkRangeConfiguration(Field field, Range range) {
        if (!isNumericType(field.getType())) {
            throw new ValidationConfigurationException("@Range не поддерживает тип "
                    + field.getType().getSimpleName() + " у поля " + describe(field));
        }
        double min = range.min();
        double max = range.max();
        if (Double.isNaN(min) || Double.isNaN(max)
                || Double.isInfinite(min) || Double.isInfinite(max)
                || min > max) {
            throw new ValidationConfigurationException("Некорректные границы @Range на поле "
                    + describe(field) + ": min=" + min + ", max=" + max);
        }
    }

    private static void checkCharSequenceType(Field field, Annotation annotation) {
        if (!CharSequence.class.isAssignableFrom(field.getType())) {
            throw new ValidationConfigurationException("@" + annotation.annotationType().getSimpleName()
                    + " не поддерживает тип " + field.getType().getSimpleName()
                    + " у поля " + describe(field));
        }
    }

    private static boolean isNumericType(Class<?> type) {
        return type == byte.class || type == short.class || type == int.class
                || type == long.class || type == float.class || type == double.class
                || Number.class.isAssignableFrom(type);
    }

    private static Object readValue(Object object, Field field) {
        if (!field.trySetAccessible()) {
            throw new ValidationConfigurationException("Нет доступа к полю " + describe(field));
        }
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new ValidationConfigurationException("Не удалось прочитать поле " + describe(field), e);
        }
    }

    private static boolean isValidEmail(String email) {
        if (email.isBlank()) {
            return false;
        }
        for (int i = 0; i < email.length(); i++) {
            if (Character.isWhitespace(email.charAt(i))) {
                return false;
            }
        }
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@')) {
            return false;
        }
        String domain = email.substring(at + 1);
        int dot = domain.indexOf('.');
        return dot > 0 && !domain.endsWith(".");
    }

    private static void addError(List<ValidationError> errors, Field field, Annotation annotation, String message) {
        errors.add(new ValidationError(field.getName(), annotation.annotationType().getSimpleName(), message));
    }

    private static String describe(Field field) {
        return field.getDeclaringClass().getName() + "." + field.getName();
    }
}
