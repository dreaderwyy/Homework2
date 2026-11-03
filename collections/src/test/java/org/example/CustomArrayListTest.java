package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomArrayListTest {
    private CustomArrayList<String> list;

    @BeforeEach
    void setUp() {
        list = new CustomArrayList<>();
    }

    private void fillABC() {
        list.add("A");
        list.add("B");
        list.add("C");
    }

    @Test
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addOneElement() {
        list.add("A");

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals("A", list.get(0));
    }

    @Test
    void addSeveralElementsKeepsOrder() {
        fillABC();

        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void addMoreThanTenElementsGrowsWithoutDataLoss() {
        assertEquals(10, list.capacity());

        for (int i = 0; i < 25; i++) {
            list.add("item" + i);
        }

        assertEquals(25, list.size());
        assertEquals(33, list.capacity());
        for (int i = 0; i < 25; i++) {
            assertEquals("item" + i, list.get(i));
        }
    }

    @Test
    void capacityGrowsByFactorOneAndHalf() {
        for (int i = 0; i < 10; i++) {
            list.add("x");
        }
        assertEquals(10, list.capacity());

        list.add("x");
        assertEquals(15, list.capacity());

        for (int i = 0; i < 5; i++) {
            list.add("x");
        }
        assertEquals(22, list.capacity());
    }

    @Test
    void zeroInitialCapacityStillGrows() {
        CustomArrayList<String> small = new CustomArrayList<>(0);

        small.add("A");
        small.add("B");

        assertEquals(2, small.size());
        assertEquals("A", small.get(0));
        assertEquals("B", small.get(1));
    }

    @Test
    void negativeInitialCapacityIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CustomArrayList<String>(-1));
    }

    @Test
    void getFirstMiddleAndLast() {
        fillABC();

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(list.size() - 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    void getRejectsIndexOutOfRange(int index) {
        fillABC();

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    void removeRejectsIndexOutOfRange(int index) {
        fillABC();

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(index));
        assertEquals(3, list.size());
    }

    @Test
    void getOnEmptyListIsRejected() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    static List<Arguments> removeCases() {
        return List.of(
                Arguments.of(0, "A", List.of("B", "C")),
                Arguments.of(1, "B", List.of("A", "C")),
                Arguments.of(2, "C", List.of("A", "B"))
        );
    }

    @ParameterizedTest
    @MethodSource("removeCases")
    void removeReturnsElementAndShiftsTail(int index, String expectedRemoved, List<String> expectedRest) {
        fillABC();

        String removed = list.remove(index);

        assertEquals(expectedRemoved, removed);
        assertEquals(expectedRest.size(), list.size());
        for (int i = 0; i < expectedRest.size(); i++) {
            assertEquals(expectedRest.get(i), list.get(i));
        }
    }

    @Test
    void removeAllElementsMakesListEmpty() {
        fillABC();

        list.remove(0);
        list.remove(0);
        list.remove(0);

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addAfterRemoveWorks() {
        fillABC();
        list.remove(2);

        list.add("D");

        assertEquals(3, list.size());
        assertEquals("D", list.get(2));
    }

    @Test
    void addNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> list.add(null));
        assertTrue(list.isEmpty());
    }

    @Test
    void iteratorTraversesInListOrder() {
        fillABC();

        Iterator<String> iterator = list.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void forEachLoopTraversesInListOrder() {
        fillABC();
        StringBuilder result = new StringBuilder();

        for (String element : list) {
            result.append(element);
        }

        assertEquals("ABC", result.toString());
    }

    @Test
    void nextOnFinishedIteratorThrowsNoSuchElementException() {
        fillABC();
        Iterator<String> iterator = list.iterator();
        iterator.next();
        iterator.next();
        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorOfEmptyListHasNoElements() {
        Iterator<String> iterator = list.iterator();

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void removeClearsFreedSlot() {
        fillABC();

        list.remove(0);

        assertEquals("B", list.slot(0));
        assertEquals("C", list.slot(1));
        assertNull(list.slot(2));
    }
}
