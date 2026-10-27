package com.epam.foundation.algo;

import java.util.Comparator;

@FunctionalInterface
public interface SortStrategy<T> {
    void sort(T[] array, Comparator<? super T> comparator);

    default void sort(T[] array) {
        sort(array, (a, b) -> ((Comparable<T>) a).compareTo(b));
    }
}
