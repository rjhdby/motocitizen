package motocitizen.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class SortedHashMap<T> extends HashMap<Integer, T> {
    public Integer[] sortedKeySet() {
        Integer[] sorted = keySet().toArray(new Integer[keySet().size()]);
        Arrays.sort(sorted);
        return sorted;
    }

    public Integer[] reverseSortedKeySet() {
        Integer[] sorted = keySet().toArray(new Integer[keySet().size()]);
        Arrays.sort(sorted, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
        return sorted;
    }
}
