package org.example;

/**
 * Упорядоченный список элементов с доступом по индексу.
 *
 * @param <E> тип элементов списка
 */
public interface CustomList<E> extends Iterable<E> {

    /**
     * Добавляет элемент в конец списка.
     *
     * @param element добавляемый элемент, не {@code null}
     * @throws NullPointerException если {@code element} равен {@code null}
     */
    void add(E element);

    /**
     * Возвращает элемент по индексу.
     *
     * @param index индекс элемента в диапазоне {@code [0, size)}
     * @return элемент, находящийся по указанному индексу
     * @throws IndexOutOfBoundsException если индекс вне диапазона {@code [0, size)}
     */
    E get(int index);

    /**
     * Удаляет элемент по индексу и сдвигает последующие элементы влево.
     *
     * @param index индекс удаляемого элемента в диапазоне {@code [0, size)}
     * @return удалённый элемент
     * @throws IndexOutOfBoundsException если индекс вне диапазона {@code [0, size)}
     */
    E remove(int index);

    /**
     * Возвращает количество элементов в списке.
     *
     * @return количество элементов
     */
    int size();

    /**
     * Проверяет, пуст ли список.
     *
     * @return {@code true}, если в списке нет элементов
     */
    boolean isEmpty();
}
