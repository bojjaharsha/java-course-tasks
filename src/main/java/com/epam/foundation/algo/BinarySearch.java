package com.epam.foundation.algo;

import java.util.Comparator;
import java.util.Objects;

/**
 * Binary search supporting recursive and iterative approaches.
 * Integrates SortStrategy (Strategy Pattern) to guarantee sorted precondition.
 */
public final class BinarySearch<T> {

    private final SortStrategy<T> sortStrategy;

    public BinarySearch(SortStrategy<T> sortStrategy) {
        this.sortStrategy = Objects.requireNonNull(sortStrategy, "sortStrategy must not be null");
    }

    /**
     * Ensures array is sorted using the configured SortStrategy, then searches iteratively.
     */
    public int searchIterative(T[] array, T key, Comparator<? super T> comparator) {
        sortStrategy.sort(array, comparator);
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2; // Prevents integer overflow
            int cmp = comparator.compare(array[mid], key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    /**
     * Ensures array is sorted using the configured SortStrategy, then searches recursively.
     */
    public int searchRecursive(T[] array, T key, Comparator<? super T> comparator) {
        sortStrategy.sort(array, comparator);
        return recursiveHelper(array, key, 0, array.length - 1, comparator);
    }

    private int recursiveHelper(T[] arr, T key, int low, int high, Comparator<? super T> comp) {
        if (low > high) return -1;
        int mid = low + (high - low) / 2;
        int cmp = comp.compare(arr[mid], key);

        if (cmp == 0) return mid;
        if (cmp < 0) return recursiveHelper(arr, key, mid + 1, high, comp);
        return recursiveHelper(arr, key, low, mid - 1, comp);
    }
}
