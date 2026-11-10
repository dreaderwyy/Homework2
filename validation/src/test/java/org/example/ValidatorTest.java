package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    static class Person {
        @NotNull(message = "Имя обязательно")
        @Size(min = 2, max = 5, message = "Имя от 2 до 5 символов")
        private String name;

        @NotNull(message = "Email обязателен")
        @Email(message = "Некорректный email")
        private String email;

        @Range(min = 0, max = 150, message = "Возраст от 0 до 150")
        private Integer age;

        Person(String name, String email, Integer age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }
    }

    static class SizeModel {
        @Size(min = 2, max = 5, message = "size")
        private String value;

        SizeModel(String value) {
            this.value = value;
        }
    }

    static class StringBuilderSizeModel {
        @Size(min = 1, max = 3, message = "size")
        private StringBuilder value;

        StringBuilderSizeModel(StringBuilder value) {
            this.value = value;
        }
    }

    static class RangeModel {
        @Range(min = -10, max = 10, message = "range")
        private double value;

        RangeModel(double value) {
            this.value = value;
        }
    }

    static class NumberTypesModel {
        @Range(min = 0, max = 10, message = "int")
        private int primitiveInt;

        @Range(min = 0, max = 10, message = "long")
        private Long boxedLong;

        @Range(min = 0, max = 10, message = "float")
        private Float boxedFloat;

        @Range(min = 0, max = 10, message = "byte")
        private byte primitiveByte;

        NumberTypesModel(int primitiveInt, Long boxedLong, Float boxedFloat, byte primitiveByte) {
            this.primitiveInt = primitiveInt;
            this.boxedLong = boxedLong;
            this.boxedFloat = boxedFloat;
            this.primitiveByte = primitiveByte;
        }
    }

    static class EmailModel {
        @Email(message = "email")
        private String value;

        EmailModel(String value) {
            this.value = value;
        }
    }

    static class NullableModel {
        @Size(min = 2, max = 5)
        private String size;

        @Range(min = 0, max = 10)
        private Integer range;

        @Email
        private String email;
    }

    static class PrimitiveNotNullModel {
        @NotNull
        private int count;
    }

    static class DefaultMessageModel {
        @NotNull
        private String value;
    }

    static class SeveralViolationsOneField {
        @NotNull(message = "nn")
        @Size(min = 10, max = 20, message = "size")
        @Email(message = "email")
        private String value = "bad";
    }

    static class BaseModel {
        @NotNull(message = "Базовое поле обязательно")
        private String baseField;
    }

    static class ChildModel extends BaseModel {
        @Range(min = 1, max = 5, message = "Уровень от 1 до 5")
        private int level = 0;
    }

    static class StaticFieldModel {
        @NotNull(message = "static")
        private static String staticField = null;

        @NotNull(message = "instance")
        private String instanceField = "ok";
    }

    static class NoAnnotationsModel {
        private String anything;
        private int number;
    }

    static class SizeNegativeMin {
        @Size(min = -1, max = 5)
        private String value = "abc";
    }

    static class SizeMinGreaterThanMax {
        @Size(min = 5, max = 2)
        private String value = "abc";
    }

    static class SizeOnInteger {
        @Size(min = 1, max = 3)
        private Integer value;
    }

    static class RangeMinGreaterThanMax {
        @Range(min = 10, max = 1)
        private int value = 5;
    }

    static class RangeNaNBound {
        @Range(min = Double.NaN, max = 10)
        private int value = 5;
    }

    static class RangeInfiniteBound {
        @Range(min = 0, max = Double.POSITIVE_INFINITY)
        private int value = 5;
    }

    static class RangeOnString {
        @Range(min = 0, max = 10)
        private String value;
    }

    static class RangeOnBoolean {
        @Range(min = 0, max = 1)
        private boolean value;
    }

    static class EmailOnInteger {
        @Email
        private Integer value;
    }

    private static Set<ValidationError> errorsOf(Object object) {
        return new HashSet<>(Validator.validate(object).getErrors());
    }

    private static ValidationError error(String field, String annotation, String message) {
        return new ValidationError(field, annotation, message);
    }

    @Test
    void validObjectHasNoErrors() {
        ValidationResult result = Validator.validate(new Person("Анна", "anna@mail.ru", 20));

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void notNullDetectsNull() {
        ValidationResult result = Validator.validate(new Person(null, "anna@mail.ru", 20));

        assertFalse(result.isValid());
        assertEquals(List.of(error("name", "NotNull", "Имя обязательно")), result.getErrors());
    }

    @Test
    void sizeDetectsTooShortValue() {
        assertEquals(Set.of(error("name", "Size", "Имя от 2 до 5 символов")),
                errorsOf(new Person("А", "anna@mail.ru", 20)));
    }

    @Test
    void rangeDetectsValueOutOfRange() {
        assertEquals(Set.of(error("age", "Range", "Возраст от 0 до 150")),
                errorsOf(new Person("Анна", "anna@mail.ru", 151)));
    }

    @Test
    void emailDetectsInvalidAddress() {
        assertEquals(Set.of(error("email", "Email", "Некорректный email")),
                errorsOf(new Person("Анна", "invalid-email", 20)));
    }

    @Test
    void collectsViolationsOfDifferentFieldsInOneCall() {
        Set<ValidationError> expected = Set.of(
                error("name", "Size", "Имя от 2 до 5 символов"),
                error("email", "NotNull", "Email обязателен"),
                error("age", "Range", "Возраст от 0 до 150"));

        assertEquals(expected, errorsOf(new Person("А", null, -1)));
    }

    @Test
    void collectsSeveralViolationsOfOneField() {
        Set<ValidationError> expected = Set.of(
                error("value", "Size", "size"),
                error("value", "Email", "email"));

        assertEquals(expected, errorsOf(new SeveralViolationsOneField()));
    }

    @ParameterizedTest
    @CsvSource({"ab, true", "abcde, true", "abc, true", "a, false", "abcdef, false", "'', false"})
    void sizeBoundsAreInclusive(String value, boolean valid) {
        assertEquals(valid, Validator.validate(new SizeModel(value)).isValid());
    }

    @Test
    void sizeSupportsAnyCharSequence() {
        assertTrue(Validator.validate(new StringBuilderSizeModel(new StringBuilder("abc"))).isValid());
        assertFalse(Validator.validate(new StringBuilderSizeModel(new StringBuilder("abcd"))).isValid());
    }

    @ParameterizedTest
    @CsvSource({"-10, true", "10, true", "0, true", "-10.001, false", "10.001, false"})
    void rangeBoundsAreInclusive(double value, boolean valid) {
        assertEquals(valid, Validator.validate(new RangeModel(value)).isValid());
    }

    @Test
    void rangeTreatsNaNAsInvalid() {
        assertEquals(Set.of(error("value", "Range", "range")), errorsOf(new RangeModel(Double.NaN)));
    }

    @Test
    void rangeSupportsPrimitiveAndBoxedNumbers() {
        assertTrue(Validator.validate(new NumberTypesModel(10, 0L, 5.5f, (byte) 3)).isValid());

        Set<ValidationError> expected = Set.of(
                error("primitiveInt", "Range", "int"),
                error("boxedLong", "Range", "long"),
                error("boxedFloat", "Range", "float"),
                error("primitiveByte", "Range", "byte"));
        assertEquals(expected, errorsOf(new NumberTypesModel(11, -1L, Float.NaN, (byte) 20)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@b.c", "user.name@mail.ru", "anna@example.com"})
    void validEmailsPass(String email) {
        assertTrue(Validator.validate(new EmailModel(email)).isValid());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "plain", "@mail.ru", "user@", "user@mail", "us er@mail.ru",
            "user@ma il.ru", "a@b@c.ru", "user@.ru", "user@mail."})
    void invalidEmailsFail(String email) {
        assertEquals(Set.of(error("value", "Email", "email")), errorsOf(new EmailModel(email)));
    }

    @Test
    void sizeRangeAndEmailSkipNull() {
        assertTrue(Validator.validate(new NullableModel()).isValid());
    }

    @Test
    void notNullOnPrimitiveIsAlwaysSatisfied() {
        assertTrue(Validator.validate(new PrimitiveNotNullModel()).isValid());
    }

    @Test
    void defaultMessageIsUsedWhenNotSpecified() {
        List<ValidationError> errors = Validator.validate(new DefaultMessageModel()).getErrors();

        assertEquals(1, errors.size());
        assertEquals("Значение не должно быть null", errors.get(0).message());
    }

    @Test
    void validateNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Validator.validate(null));
    }

    @Test
    void privateAndInheritedFieldsAreValidated() {
        Set<ValidationError> expected = Set.of(
                error("baseField", "NotNull", "Базовое поле обязательно"),
                error("level", "Range", "Уровень от 1 до 5"));

        assertEquals(expected, errorsOf(new ChildModel()));
    }

    @Test
    void staticFieldsAreIgnored() {
        assertTrue(Validator.validate(new StaticFieldModel()).isValid());
    }

    @Test
    void objectWithoutAnnotationsIsValid() {
        assertTrue(Validator.validate(new NoAnnotationsModel()).isValid());
    }

    @Test
    void validatorDoesNotChangeObject() {
        Person person = new Person("А", "invalid", 200);

        Validator.validate(person);

        assertEquals("А", person.name);
        assertEquals("invalid", person.email);
        assertEquals(200, person.age);
    }

    @Test
    void resultErrorsCannotBeModified() {
        List<ValidationError> errors = Validator.validate(new Person(null, null, null)).getErrors();

        assertThrows(UnsupportedOperationException.class,
                () -> errors.add(error("x", "NotNull", "x")));
    }

    @Test
    void sizeWithNegativeMinIsConfigurationError() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new SizeNegativeMin()));
    }

    @Test
    void sizeWithMinGreaterThanMaxIsConfigurationError() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new SizeMinGreaterThanMax()));
    }

    @Test
    void sizeOnUnsupportedTypeIsConfigurationErrorEvenForNull() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new SizeOnInteger()));
    }

    @Test
    void rangeWithMinGreaterThanMaxIsConfigurationError() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new RangeMinGreaterThanMax()));
    }

    @Test
    void rangeWithNaNBoundIsConfigurationError() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new RangeNaNBound()));
    }

    @Test
    void rangeWithInfiniteBoundIsConfigurationError() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new RangeInfiniteBound()));
    }

    @Test
    void rangeOnNonNumericTypeIsConfigurationError() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new RangeOnString()));
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new RangeOnBoolean()));
    }

    @Test
    void emailOnUnsupportedTypeIsConfigurationError() {
        assertThrows(ValidationConfigurationException.class, () -> Validator.validate(new EmailOnInteger()));
    }

    @Test
    void configurationErrorMentionsClassAndField() {
        ValidationConfigurationException e = assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new EmailOnInteger()));

        assertTrue(e.getMessage().contains("EmailOnInteger"));
        assertTrue(e.getMessage().contains("value"));
    }
}
