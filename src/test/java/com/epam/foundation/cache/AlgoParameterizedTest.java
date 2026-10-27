package com.epam.foundation.algo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AlgoParameterizedTest {

    static Stream<SortStrategy<Integer>> sortStrategies() {
        return Stream.of(new MergeSort<>(), new InsertionSort<>());
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void testSortImplementations(SortStrategy<Integer> sorter) {
        Integer[] array = {5, 2, 8, 1, 9, 3};
        sorter.sort(array, Comparator.naturalOrder());
        assertArrayEquals(new Integer[]{1, 2, 3, 5, 8, 9}, array);
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void testBinarySearchWithDifferentSorters(SortStrategy<Integer> sorter) {
        BinarySearch<Integer> bs = new BinarySearch<>(sorter);
        Integer[] array = {40, 10, 50, 20, 30};

        // Iterative search
        int idxIter = bs.searchIterative(array.clone(), 30, Comparator.naturalOrder());
        assertEquals(2, idxIter); // After sorting [10, 20, 30, 40, 50], 30 is at index 2

        // Recursive search
        int idxRec = bs.searchRecursive(array.clone(), 50, Comparator.naturalOrder());
        assertEquals(4, idxRec); // 50 is at index 4

        // Not found
        int notFound = bs.searchIterative(array.clone(), 99, Comparator.naturalOrder());
        assertEquals(-1, notFound);
    }
}
