package it.ai.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CopyUtils {
    /***
     * Clone the Hashmap and the Set inside, not V1 and V2 items.
     */
    public static <V1, V2> HashMap<V1, Set<V2>> clone(HashMap<V1, Set<V2>> original) {
        HashMap<V1, Set<V2>> copy = new HashMap<>();
        for (Map.Entry<V1, Set<V2>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * Clones the provided array
     *
     * @return a new clone of the provided array
     */
    public static int[][] clone(int[][] src) {
        int length = src.length;
        if (length == 0) return new int[0][0];

        int[][] target = new int[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }
}
