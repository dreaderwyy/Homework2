package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Эксперимент, который сравнивает время одинаковых операций
 * над {@link ArrayList} и {@link LinkedList}.
 *
 * <p>Для каждой операции и каждой реализации выполняются прогревочные запуски,
 * затем несколько замеров через {@link System#nanoTime()}; в таблицу попадает медиана.
 * Подготовка исходной коллекции в замер не входит.</p>
 */
public final class CollectionPerformanceExperiment {
    /** Количество элементов в каждой операции. */
    public static final int ELEMENTS = 10_000;

    private static final int WARMUP_RUNS = 3;
    private static final int MEASURED_RUNS = 7;

    private static long checksum = 0;

    private CollectionPerformanceExperiment() {
    }

    /**
     * Операции, время которых сравнивается.
     */
    public enum Operation {
        /** Добавление элементов в конец пустого списка. */
        ADD_LAST("Добавление в конец", false),
        /** Добавление элементов в начало пустого списка. */
        ADD_FIRST("Добавление в начало", false),
        /** Вставка каждого элемента в середину текущего списка. */
        INSERT_MIDDLE("Вставка в середину", false),
        /** Чтение всех элементов по индексу от 0 до size - 1. */
        GET_BY_INDEX("Доступ по индексу", true),
        /** Удаление всех элементов по одному из начала. */
        REMOVE_FIRST("Удаление из начала", true),
        /** Удаление всех элементов по одному из конца. */
        REMOVE_LAST("Удаление из конца", true);

        private final String title;
        private final boolean needsFilledList;

        Operation(String title, boolean needsFilledList) {
            this.title = title;
            this.needsFilledList = needsFilledList;
        }

        /**
         * Возвращает название операции для таблицы.
         *
         * @return название операции
         */
        public String getTitle() {
            return title;
        }
    }

    /**
     * Запускает эксперимент и печатает таблицу результатов.
     *
     * @param args аргументы командной строки не используются
     */
    public static void main(String[] args) {
        System.out.println("Java " + System.getProperty("java.version")
                + ", прогрев: " + WARMUP_RUNS + ", замеров: " + MEASURED_RUNS + ", в таблице медиана");
        System.out.println();
        System.out.printf("| %-22s | %9s | %15s | %15s |%n", "Операция", "Элементов", "ArrayList, мс", "LinkedList, мс");
        System.out.println("|------------------------|-----------|-----------------|-----------------|");

        for (Operation operation : Operation.values()) {
            double arrayListMs = measureMedianNanos(operation, true) / 1_000_000.0;
            double linkedListMs = measureMedianNanos(operation, false) / 1_000_000.0;
            System.out.printf("| %-22s | %9d | %15.3f | %15.3f |%n",
                    operation.getTitle(), ELEMENTS, arrayListMs, linkedListMs);
        }

        System.out.println();
        System.out.println("Контрольная сумма: " + checksum);
    }

    /**
     * Измеряет операцию несколько раз и возвращает медиану времени.
     *
     * @param operation измеряемая операция
     * @param useArrayList {@code true} для {@link ArrayList}, {@code false} для {@link LinkedList}
     * @return медианное время одного запуска в наносекундах
     */
    public static long measureMedianNanos(Operation operation, boolean useArrayList) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            runOnce(operation, useArrayList);
        }
        long[] times = new long[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            times[i] = runOnce(operation, useArrayList);
        }
        Arrays.sort(times);
        return times[MEASURED_RUNS / 2];
    }

    /**
     * Создаёт новую коллекцию, при необходимости заполняет её и измеряет одну операцию.
     *
     * @param operation измеряемая операция
     * @param useArrayList {@code true} для {@link ArrayList}, {@code false} для {@link LinkedList}
     * @return время операции в наносекундах
     */
    public static long runOnce(Operation operation, boolean useArrayList) {
        List<Integer> list = createList(useArrayList);
        if (operation.needsFilledList) {
            for (int i = 0; i < ELEMENTS; i++) {
                list.add(i);
            }
        }

        long start = System.nanoTime();
        long result = execute(operation, list);
        long elapsed = System.nanoTime() - start;

        checksum += result;
        return elapsed;
    }

    private static List<Integer> createList(boolean useArrayList) {
        if (useArrayList) {
            return new ArrayList<>();
        }
        return new LinkedList<>();
    }

    private static long execute(Operation operation, List<Integer> list) {
        long sum = 0;
        switch (operation) {
            case ADD_LAST:
                for (int i = 0; i < ELEMENTS; i++) {
                    list.add(i);
                }
                sum = list.size();
                break;
            case ADD_FIRST:
                for (int i = 0; i < ELEMENTS; i++) {
                    list.add(0, i);
                }
                sum = list.size();
                break;
            case INSERT_MIDDLE:
                for (int i = 0; i < ELEMENTS; i++) {
                    list.add(list.size() / 2, i);
                }
                sum = list.size();
                break;
            case GET_BY_INDEX:
                for (int i = 0; i < list.size(); i++) {
                    sum += list.get(i);
                }
                break;
            case REMOVE_FIRST:
                while (!list.isEmpty()) {
                    sum += list.remove(0);
                }
                break;
            case REMOVE_LAST:
                while (!list.isEmpty()) {
                    sum += list.remove(list.size() - 1);
                }
                break;
            default:
                throw new IllegalArgumentException("Неизвестная операция: " + operation);
        }
        return sum;
    }
}
