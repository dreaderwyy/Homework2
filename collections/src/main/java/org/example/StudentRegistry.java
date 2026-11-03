package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Реестр студентов: создание словарей по id и выборки из них.
 */
public final class StudentRegistry {

    private StudentRegistry() {
    }

    /**
     * Создаёт {@link HashMap}, где ключ равен {@link Student#id()}.
     *
     * @param students список студентов
     * @return новый словарь «id — студент»
     * @throws NullPointerException если список или один из студентов равен {@code null}
     * @throws IllegalArgumentException если id студентов повторяются
     */
    public static Map<Integer, Student> createHashMap(List<Student> students) {
        Map<Integer, Student> result = new HashMap<>();
        fill(result, students);
        return result;
    }

    /**
     * Создаёт {@link TreeMap} с сортировкой ключей по убыванию, где ключ равен {@link Student#id()}.
     *
     * @param students список студентов
     * @return новый словарь «id — студент» с ключами по убыванию
     * @throws NullPointerException если список или один из студентов равен {@code null}
     * @throws IllegalArgumentException если id студентов повторяются
     */
    public static NavigableMap<Integer, Student> createDescendingTreeMap(List<Student> students) {
        Comparator<Integer> descending = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Integer.compare(b, a);
            }
        };
        NavigableMap<Integer, Student> result = new TreeMap<>(descending);
        fill(result, students);
        return result;
    }

    /**
     * Возвращает студентов с оценкой в замкнутом диапазоне {@code [minGrade, maxGrade]}.
     * Переданный словарь не изменяется.
     *
     * @param students словарь «id — студент»
     * @param minGrade нижняя граница оценки, включительно
     * @param maxGrade верхняя граница оценки, включительно
     * @return новый изменяемый список подходящих студентов
     * @throws NullPointerException если словарь равен {@code null}
     * @throws IllegalArgumentException если {@code minGrade > maxGrade}
     *                                  или границы вне диапазона [0.0, 10.0]
     */
    public static List<Student> findStudentsByGradeRange(
            Map<Integer, Student> students,
            double minGrade,
            double maxGrade) {
        if (students == null) {
            throw new NullPointerException("Словарь студентов не может быть null");
        }
        if (!(minGrade >= Student.MIN_GRADE && maxGrade <= Student.MAX_GRADE && minGrade <= maxGrade)) {
            throw new IllegalArgumentException(
                    "Некорректный диапазон оценок: [" + minGrade + ", " + maxGrade + "]");
        }

        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.grade() >= minGrade && student.grade() <= maxGrade) {
                result.add(student);
            }
        }
        return result;
    }

    /**
     * Возвращает не более {@code n} студентов с наибольшими id в порядке убывания id.
     * Словарь должен быть отсортирован по убыванию ключей. Переданный словарь не изменяется.
     *
     * @param students словарь «id — студент» с сортировкой ключей по убыванию
     * @param n максимальное количество студентов в результате
     * @return новый изменяемый список студентов
     * @throws NullPointerException если словарь равен {@code null}
     * @throws IllegalArgumentException если {@code n < 0}
     *                                  или словарь отсортирован не по убыванию ключей
     */
    public static List<Student> getTopNStudentsById(
            NavigableMap<Integer, Student> students,
            int n) {
        if (students == null) {
            throw new NullPointerException("Словарь студентов не может быть null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("n не может быть отрицательным: " + n);
        }
        if (students.size() > 1 && students.firstKey() < students.lastKey()) {
            throw new IllegalArgumentException("Словарь должен быть отсортирован по убыванию ключей");
        }

        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (result.size() == n) {
                break;
            }
            result.add(student);
        }
        return result;
    }

    private static void fill(Map<Integer, Student> map, List<Student> students) {
        if (students == null) {
            throw new NullPointerException("Список студентов не может быть null");
        }
        for (Student student : students) {
            if (student == null) {
                throw new NullPointerException("Студент не может быть null");
            }
            if (map.containsKey(student.id())) {
                throw new IllegalArgumentException("Повторяющийся id: " + student.id());
            }
            map.put(student.id(), student);
        }
    }
}
