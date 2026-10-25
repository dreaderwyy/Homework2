package org.example;

public class ArrayUtils {
    public static <T> int findFirst(T[] array, T element) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (element == null) {
                if (array[i] == null) {
                    return i;
                }
            } else if (element.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        final String[] names = {"Alice", "Bob", "Charlie"};
        final int index = ArrayUtils.findFirst(names, "Bob");
        System.out.println("Индекс \"Bob\": " + index);

        System.out.println("Индекс \"Dave\" (нет в массиве): " + ArrayUtils.findFirst(names, "Dave"));
        System.out.println("Поиск в массиве null: " + ArrayUtils.findFirst(null, "Bob"));
        System.out.println("Поиск элемента null: " + ArrayUtils.findFirst(names, null));

        final String[] withNull = {"Alice", null, "Bob"};
        System.out.println("Поиск \"Bob\" в массиве с null внутри: " + ArrayUtils.findFirst(withNull, "Bob"));
        System.out.println("Поиск null в массиве с null внутри: " + ArrayUtils.findFirst(withNull, null));

        final Integer[] numbers = {10, 20, 30, 20};
        System.out.println("Индекс первого 20: " + ArrayUtils.findFirst(numbers, 20));
    }
}
