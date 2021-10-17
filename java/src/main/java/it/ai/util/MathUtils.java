package it.ai.util;

import java.util.function.Function;

public class MathUtils {
    public static <T> T argmax(Iterable<T> items, Function<T, Double> map) {
        double maxValue = -Double.MAX_VALUE;
        T maxItem = null;

        for (T item : items) {
            double value = map.apply(item);
            if (value > maxValue) {
                maxValue = value;
                maxItem = item;
            } else {
                int x = 0;
            }
        }

        return maxItem;
    }
}
