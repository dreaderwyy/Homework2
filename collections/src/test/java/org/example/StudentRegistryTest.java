package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentRegistryTest {
    private final Student anna = new Student(3, "Анна", 9.5);
    private final Student boris = new Student(1, "Борис", 4.0);
    private final Student vera = new Student(5, "Вера", 7.0);
    private final Student gleb = new Student(2, "Глеб", 10.0);
    private final Student dina = new Student(4, "Дина", 0.0);

    private List<Student> students;

    @BeforeEach
    void setUp() {
        students = List.of(anna, boris, vera, gleb, dina);
    }

    @Test
    void hashMapUsesIdAsKey() {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);

        assertTrue(map instanceof HashMap);
        assertEquals(5, map.size());
        for (Student student : students) {
            assertEquals(student, map.get(student.id()));
        }
    }

    @Test
    void treeMapSortsKeysDescending() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(students);

        assertTrue(map instanceof TreeMap);
        assertEquals(5, map.firstKey());
        assertEquals(1, map.lastKey());
        assertEquals(List.of(5, 4, 3, 2, 1), List.copyOf(map.keySet()));
    }

    @Test
    void duplicateIdIsRejected() {
        List<Student> withDuplicate = List.of(anna, new Student(3, "Другая Анна", 5.0));

        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.createHashMap(withDuplicate));
        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.createDescendingTreeMap(withDuplicate));
    }

    @Test
    void findsStudentsInRegularRange() {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);

        List<Student> result = StudentRegistry.findStudentsByGradeRange(map, 5.0, 9.6);

        assertEquals(Set.of(anna, vera), new HashSet<>(result));
        assertEquals(2, result.size());
    }

    @Test
    void rangeIncludesBothBounds() {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);

        List<Student> result = StudentRegistry.findStudentsByGradeRange(map, 4.0, 9.5);

        assertEquals(Set.of(boris, vera, anna), new HashSet<>(result));
    }

    @Test
    void fullRangeReturnsEveryone() {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);

        List<Student> result = StudentRegistry.findStudentsByGradeRange(map, 0.0, 10.0);

        assertEquals(new HashSet<>(students), new HashSet<>(result));
    }

    @Test
    void rangeOfSingleValueWorks() {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);

        List<Student> result = StudentRegistry.findStudentsByGradeRange(map, 7.0, 7.0);

        assertEquals(List.of(vera), result);
    }

    @Test
    void rangeWithoutMatchesReturnsEmptyList() {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);

        List<Student> result = StudentRegistry.findStudentsByGradeRange(map, 1.0, 3.0);

        assertTrue(result.isEmpty());
    }

    @Test
    void emptyMapGivesEmptyRangeResult() {
        List<Student> result = StudentRegistry.findStudentsByGradeRange(new HashMap<>(), 0.0, 10.0);

        assertTrue(result.isEmpty());
    }

    static List<Arguments> invalidRanges() {
        return List.of(
                Arguments.of(8.0, 2.0),
                Arguments.of(-0.1, 5.0),
                Arguments.of(5.0, 10.1),
                Arguments.of(Double.NaN, 5.0)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRanges")
    void invalidRangeIsRejected(double min, double max) {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);

        assertThrows(IllegalArgumentException.class,
                () -> StudentRegistry.findStudentsByGradeRange(map, min, max));
    }

    @Test
    void rangeSearchDoesNotChangeMapAndReturnsMutableList() {
        Map<Integer, Student> map = StudentRegistry.createHashMap(students);
        Map<Integer, Student> copy = new HashMap<>(map);

        List<Student> result = StudentRegistry.findStudentsByGradeRange(map, 0.0, 10.0);
        result.add(new Student(100, "Новый", 5.0));

        assertEquals(copy, map);
    }

    @Test
    void topNReturnsLargestIdsInDescendingOrder() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(students);

        List<Student> result = StudentRegistry.getTopNStudentsById(map, 3);

        assertEquals(List.of(vera, dina, anna), result);
    }

    @Test
    void topNLargerThanMapReturnsAllInDescendingOrder() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(students);

        List<Student> result = StudentRegistry.getTopNStudentsById(map, 100);

        assertEquals(List.of(vera, dina, anna, gleb, boris), result);
    }

    @Test
    void topNEqualToMapSizeReturnsAll() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(students);

        List<Student> result = StudentRegistry.getTopNStudentsById(map, 5);

        assertEquals(5, result.size());
    }

    @Test
    void topZeroReturnsEmptyList() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(students);

        List<Student> result = StudentRegistry.getTopNStudentsById(map, 0);

        assertTrue(result.isEmpty());
    }

    @Test
    void topNOfEmptyMapReturnsEmptyList() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(List.of());

        List<Student> result = StudentRegistry.getTopNStudentsById(map, 3);

        assertTrue(result.isEmpty());
    }

    @Test
    void negativeNIsRejected() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(students);

        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.getTopNStudentsById(map, -1));
    }

    @Test
    void ascendingMapIsRejected() {
        NavigableMap<Integer, Student> ascending = new TreeMap<>();
        ascending.put(anna.id(), anna);
        ascending.put(vera.id(), vera);

        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.getTopNStudentsById(ascending, 1));
    }

    @Test
    void topNDoesNotChangeMapAndReturnsMutableList() {
        NavigableMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(students);
        Map<Integer, Student> copy = new HashMap<>(map);

        List<Student> result = StudentRegistry.getTopNStudentsById(map, 2);
        result.add(new Student(100, "Новый", 5.0));

        assertEquals(copy, map);
        assertFalse(map.containsKey(100));
    }
}
