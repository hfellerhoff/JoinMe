package com.joinmedevelopment.joinme;

import java.util.ArrayList;

public class QuickSort{

    private static void swap (ArrayList<LocationReport> array, int i1, int i2)
    {
        LocationReport temp = array.get(i1);
        array.set(i1, array.get(i2));
        array.set(i2, temp);
    }

    private static int quickSortPartition(ArrayList<LocationReport> array, int left, int right)
    {
        // pre: left <= right
        // post: data[left] placed in correct (returned) location
        while (true)
        {
            // Move right "pointer" toward left
            while  (left < right && array.get(left).getTimeCreated() < array.get(right).getTimeCreated())
                right--;

            if (left < right)
                swap(array, left++, right);

            else
                return left;

            while (left < right && array.get(left).getTimeCreated() < array.get(right).getTimeCreated())
                left++;

            if (left < right)
                swap(array, left, right--);

            else
                return right;
        }
    }

    private static void quickSortRecursive(ArrayList<LocationReport> array, int left, int right)
    {
        int pivot;

        if (left >= right) return;

        pivot = quickSortPartition(array, left, right);
        quickSortRecursive(array, left, pivot - 1);
        quickSortRecursive(array, pivot + 1, right);
    }

    public static void quickSortLocationReport(ArrayList<LocationReport> array)
    {

        quickSortRecursive(array, 0, array.size() - 1);
    }
}
