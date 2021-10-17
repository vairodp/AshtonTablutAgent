package it.ai.collections;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {
    public static Stream<Integer> range(int endExclusive) {
        return range(0, endExclusive, 1);
    }

    public static Stream<Integer> range(int startInclusive, int endExclusive) {
        return range(startInclusive, endExclusive, 1);
    }

    public static Stream<Integer> range(int startInclusive, int endExclusive, int step) {
        return StreamSupport.stream(Iterables.range(startInclusive, endExclusive, step).spliterator(), false);
    }

    public static Stream<Integer> rangeInclusive(int endInclusive) {
        return rangeInclusive(0, endInclusive, 1);
    }

    public static Stream<Integer> rangeInclusive(int startInclusive, int endInclusive) {
        return rangeInclusive(startInclusive, endInclusive, 1);
    }

    public static Stream<Integer> rangeInclusive(int startInclusive, int endInclusive, int step) {
        return StreamSupport.stream(Iterables.rangeInclusive(startInclusive, endInclusive, step).spliterator(), false);
    }
}
