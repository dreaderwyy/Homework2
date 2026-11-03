package org.example;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Динамический массив: хранит элементы во внутреннем массиве {@code Object[]}
 * и увеличивает его ёмкость в 1.5 раза, когда место заканчивается.
 *
 * @param <E> тип элементов списка
 */
public class CustomArrayList<E> implements CustomList<E> {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    /**
     * Создаёт пустой список с начальной ёмкостью 10.
     */
    public CustomArrayList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Создаёт пустой список с указанной начальной ёмкостью.
     *
     * @param initialCapacity начальная ёмкость внутреннего массива, не меньше нуля
     * @throws IllegalArgumentException если ёмкость отрицательная
     */
    public CustomArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Ёмкость не может быть отрицательной: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(E element) {
        if (element == null) {
            throw new NullPointerException("Нельзя добавить null в список");
        }
        if (size == elements.length) {
            grow();
        }
        elements[size] = element;
        size++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E removed = (E) elements[index];
        int tailLength = size - index - 1;
        if (tailLength > 0) {
            System.arraycopy(elements, index + 1, elements, index, tailLength);
        }
        size--;
        elements[size] = null;
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает итератор, который обходит элементы от индекса 0 до {@code size - 1}.
     * Метод {@code remove()} итератора не поддерживается.
     *
     * @return итератор по элементам списка
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Элементов больше нет");
                }
                E element = (E) elements[cursor];
                cursor++;
                return element;
            }
        };
    }

    int capacity() {
        return elements.length;
    }

    Object slot(int index) {
        return elements[index];
    }

    private void grow() {
        int oldCapacity = elements.length;
        int newCapacity = oldCapacity + oldCapacity / 2;
        if (newCapacity == oldCapacity) {
            newCapacity = oldCapacity + 1;
        }
        elements = Arrays.copyOf(elements, newCapacity);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + size + ")");
        }
    }
}
