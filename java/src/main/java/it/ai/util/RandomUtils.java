package it.ai.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtils {
    public static <T> T choice(List<T> items) {
        Random random = new java.util.Random();
        int index = random.nextInt(items.size());
        return items.get(index);
    }

    public static <T> List<T> choice(List<T> items, int numberOfChoices) {
        Random random = new java.util.Random();
        Collections.shuffle(items);
        List<T> chosen = new ArrayList<>(numberOfChoices);
        for (int i = 0; i < numberOfChoices; i++) {
            chosen.add(items.get(i));
        }
        return chosen;
    }
}
