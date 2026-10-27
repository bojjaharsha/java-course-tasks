package com.epam.foundation.algo;

import java.util.Comparator;

public class InsertionSort<T> implements SortStrategy<T> {

    @Override
    public void sort(T[] array, Comparator<? super T> comparator) {
        if (array == null || array.length <= 1) return;

        for (int i = 1; i < array.length; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(array[j], key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }
}
