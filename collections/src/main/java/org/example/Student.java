package org.example;

/**
 * Неизменяемая модель студента. Методы {@code equals} и {@code hashCode}
 * генерируются record-ом по всем компонентам.
 *
 * @param id положительный уникальный идентификатор
 * @param name непустое имя
 * @param grade оценка от 0.0 до 10.0 включительно
 */
public record Student(int id, String name, double grade) {
    /** Минимально допустимая оценка. */
    public static final double MIN_GRADE = 0.0;
    /** Максимально допустимая оценка. */
    public static final double MAX_GRADE = 10.0;

    /**
     * Проверяет компоненты при создании студента.
     *
     * @throws IllegalArgumentException если id не положительный, имя пустое
     *                                  или оценка вне диапазона [0.0, 10.0]
     */
    public Student {
        if (id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным: " + id);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (!(grade >= MIN_GRADE && grade <= MAX_GRADE)) {
            throw new IllegalArgumentException("Оценка должна быть от 0.0 до 10.0: " + grade);
        }
    }
}
