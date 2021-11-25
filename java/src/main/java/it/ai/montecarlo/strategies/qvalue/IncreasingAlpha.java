package it.ai.montecarlo.strategies.qvalue;

public class IncreasingAlpha implements DynamicAlpha {
    private final double t;
    private final double d;
    private final double kIncrement;

    private double k;

    public IncreasingAlpha(double start, double stop, double k, double kIncrement) {
        this.k = k - kIncrement;
        this.kIncrement = kIncrement;

        t = start / stop;
        d = stop - start;
    }

    public IncreasingAlpha() {
        this(0.2, 0.7, 4, 8 / 20.0);
    }

    @Override
    public double getValue() {
        k += kIncrement;
        return ((1 / (1 + Math.exp(-k)) + t) * d);
    }
}
