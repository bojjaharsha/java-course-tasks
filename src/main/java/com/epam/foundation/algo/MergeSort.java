package com.epam.foundation.algo;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

public class MergeSort<T> implements SortStrategy<T> {

    @Override
    @SuppressWarnings("unchecked")
    public void sort(T[] array, Comparator<? super T> comparator) {
        if (array == null || array.length <= 1) return;
        T[] auxiliary = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);
        mergeSort(array, auxiliary, 0, array.length - 1, comparator);
    }

    private void mergeSort(T[] arr, T[] aux, int low, int high, Comparator<? super T> comp) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        mergeSort(arr, aux, low, mid, comp);
        mergeSort(arr, aux, mid + 1, high, comp);
        merge(arr, aux, low, mid, high, comp);
    }

    private void merge(T[] arr, T[] aux, int low, int mid, int high, Comparator<? super T> comp) {
        System.arraycopy(arr, low, aux, low, high - low + 1);

        int i = low, j = mid + 1, k = low;
        while (i <= mid && j <= high) {
            if (comp.compare(aux[i], aux[j]) <= 0) {
                arr[k++] = aux[i++];
            } else {
                arr[k++] = aux[j++];
            }
        }
        while (i <= mid) {
            arr[k++] = aux[i++];
        }
    }
}
