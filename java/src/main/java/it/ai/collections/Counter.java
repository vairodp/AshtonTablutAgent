package it.ai.collections;

import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.HashMap;

public class Counter<T> implements Serializable, Cloneable {
    private HashMap<T, Integer> counts = new HashMap<>();

    public void add(T t) {
        counts.merge(t, 1, Integer::sum);
    }

    public int get(T t) {
        return count(t);
    }

    public int count(T t) {
        return counts.getOrDefault(t, 0);
    }

    @SneakyThrows
    @Override
    public Counter<T> clone() {
        Counter<T> counter = (Counter<T>) super.clone();
        counter.counts = new HashMap<>(counts);
        return counter;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "counts=" + counts +
                '}';
    }
}
