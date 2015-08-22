package motocitizen.draw;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import motocitizen.accident.Accident;
import motocitizen.accident.Message;

public class Sort {
    public static Integer[] getSortedMessagesKeys(Map<Integer, Message> messages) {
        Integer[] sorted = messages.keySet().toArray(new Integer[messages.keySet().size()]);
        Arrays.sort(sorted);
        return sorted;
    }

    public static Integer[] getSortedAccidentsKeys(Map<Integer, Accident> accidents) {
        Integer[] sorted = accidents.keySet().toArray(new Integer[accidents.keySet().size()]);
        Arrays.sort(sorted, new ReverseOrder());
        return sorted;
    }

    public static class ReverseOrder implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }
    }
}
