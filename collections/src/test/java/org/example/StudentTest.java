package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentTest {

    @Test
    void createsValidStudent() {
        Student student = new Student(1, "Анна", 8.5);

        assertEquals(1, student.id());
        assertEquals("Анна", student.name());
        assertEquals(8.5, student.grade());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 10.0})
    void acceptsBoundaryGrades(double grade) {
        Student student = new Student(1, "Анна", grade);

        assertEquals(grade, student.grade());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveId(int id) {
        assertThrows(IllegalArgumentException.class, () -> new Student(id, "Анна", 5.0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void rejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Student(1, name, 5.0));
    }

    @Test
    void rejectsNullName() {
        assertThrows(IllegalArgumentException.class, () -> new Student(1, null, 5.0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 10.1, Double.NaN})
    void rejectsGradeOutOfRange(double grade) {
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "Анна", grade));
    }

    @Test
    void equalStudentsHaveEqualHashCodes() {
        Student first = new Student(7, "Олег", 9.0);
        Student second = new Student(7, "Олег", 9.0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void studentsWithDifferentFieldsAreNotEqual() {
        Student student = new Student(7, "Олег", 9.0);

        assertNotEquals(student, new Student(8, "Олег", 9.0));
        assertNotEquals(student, new Student(7, "Ольга", 9.0));
        assertNotEquals(student, new Student(7, "Олег", 9.5));
    }
}
