package it.ai.collections;

import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Iterables {
    public static Iterable<Integer> range(int endExclusive) {
        return range(0, endExclusive, 1);
    }

    public static Iterable<Integer> range(int startInclusive, int endExclusive) {
        return range(startInclusive, endExclusive, 1);
    }

    public static Iterable<Integer> range(int startInclusive, int endExclusive, int step) {
        return step > 0
                ? forwardRange(startInclusive, endExclusive, step)
                : backwardRange(startInclusive, endExclusive, step);
    }

    public static Iterable<Integer> rangeInclusive(int endInclusive) {
        return rangeInclusive(0, endInclusive, 1);
    }

    public static Iterable<Integer> rangeInclusive(int startInclusive, int endInclusive) {
        return rangeInclusive(startInclusive, endInclusive, 1);
    }

    public static Iterable<Integer> rangeInclusive(int startInclusive, int endInclusive, int step) {
        int end = step > 0 ? endInclusive + 1 : endInclusive - 1;
        return range(startInclusive, end, step);
    }

    private static abstract class RangeIterator implements Iterator<Integer> {
        protected final int stop;
        protected final int step;

        protected int i;

        public RangeIterator(int start, int stop, int step) {
            this.i = start;
            this.stop = stop;
            this.step = step;
        }

        @Override
        public Integer next() {
            int value = i;
            i += step;
            return value;
        }
    }

    private static class ForwardRangeIterator extends RangeIterator {
        public ForwardRangeIterator(int start, int stop, int step) {
            super(start, stop, step);
        }

        @Override
        public boolean hasNext() {
            return i < stop;
        }
    }

    private static class BackwardRangeIterator extends RangeIterator {
        public BackwardRangeIterator(int start, int stop, int step) {
            super(start, stop, step);
        }

        @Override
        public boolean hasNext() {
            return i > stop;
        }
    }

    private static Iterable<Integer> forwardRange(int start, int stop, int step) {
        return () -> new ForwardRangeIterator(start, stop, step);
    }

    private static Iterable<Integer> backwardRange(int start, int stop, int step) {
        return () -> new BackwardRangeIterator(start, stop, step);
    }

    public static <T> Iterable<T> empty() {
        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }

    public static <T> Iterable<T> parallel(Supplier<T> action) {
        return parallel(action, Runtime.getRuntime().availableProcessors());
    }

    public static <T> Iterable<T> parallel(Supplier<T> action, int instances) {
        ExecutorService threadPool = Executors.newWorkStealingPool(instances);
        ExecutorCompletionService<T> executorCompletionService = new ExecutorCompletionService<>(threadPool);
        return parallel(action, instances, executorCompletionService);
    }

    public static <T> Iterable<T> parallel(Supplier<T> action, int instances, CompletionService<T> completionService) {
        for (int i = 0; i < instances; i++) {
            completionService.submit(action::get);
        }
        return () -> new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < instances;
            }

            @SneakyThrows
            @Override
            public T next() {
                i++;
                return completionService.take().get();
            }
        };
    }
}
