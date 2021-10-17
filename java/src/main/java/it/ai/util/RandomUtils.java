package it.ai.util;

import java.util.List;
import java.util.Random;

public class RandomUtils {
    public static <T> T choice(List<T> items) {
        Random random = new java.util.Random();
        int index = random.nextInt(items.size());
        return items.get(index);
    }
}
