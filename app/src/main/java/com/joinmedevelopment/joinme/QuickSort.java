//For sorting friends by arrival time
package com.joinmedevelopment.joinme;

import java.util.ArrayList;

public class QuickSort{

    private static void swap(ArrayList array, int i1, int i2)
    {
        Object temp = array.get(i1);
        array.set(i1, array.get(i2));
        array.set(i2, temp);
    }

    public static void quickSortLocationReport(ArrayList<LocationReport> array)
    {
        quickSortRecursiveLocationReport(array, 0, array.size() - 1);
    }

    private static int quickSortPartitionLocationReport(ArrayList<LocationReport> array, int left, int right)
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

    private static void quickSortRecursiveLocationReport(ArrayList<LocationReport> array, int left, int right)
    {
        int pivot;

        if (left >= right) return;

        pivot = quickSortPartitionLocationReport(array, left, right);
        quickSortRecursiveLocationReport(array, left, pivot - 1);
        quickSortRecursiveLocationReport(array, pivot + 1, right);
    }

    // Sorts a list of Friend objects alphabetically
    public static void quickSortFriend(ArrayList<Friend> array) {
        quickSortRecursiveFriend(array, 0, array.size() - 1);
    }

    private static int quickSortPartitionFriend(ArrayList<Friend> array, int left, int right)
    {
        // pre: left <= right
        // post: data[left] placed in correct (returned) location
        while (true)
        {
            // Move right "pointer" toward left
            while  (left < right && array.get(left).getName().compareTo(array.get(right).getName()) < 0)
                right--;

            if (left < right)
                swap(array, left++, right);

            else
                return left;

            while (left < right && array.get(left).getName().compareTo(array.get(right).getName()) < 0)
                left++;

            if (left < right)
                swap(array, left, right--);

            else
                return right;
        }
    }

    private static void quickSortRecursiveFriend(ArrayList<Friend> array, int left, int right)
    {
        int pivot;

        if (left >= right) return;

        pivot = quickSortPartitionFriend(array, left, right);
        quickSortRecursiveFriend(array, left, pivot - 1);
        quickSortRecursiveFriend(array, pivot + 1, right);
    }


}
