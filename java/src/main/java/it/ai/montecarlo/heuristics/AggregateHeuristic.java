package it.ai.montecarlo.heuristics;

import it.ai.game.State;
import lombok.Getter;

import java.util.Arrays;

public class AggregateHeuristic implements HeuristicEvaluation {
    private final WeightedHeuristic[] heuristics;

    public AggregateHeuristic(WeightedHeuristic[] heuristics) {
        this.heuristics = heuristics;
    }

    @Override
    public double evaluate(State state, int player) {
//        double value = 0;
//        for (WeightedHeuristic weightedHeuristic : heuristics) {
//            value += getValue(state, player, weightedHeuristic);
//        }
//        return value;
        double value = Arrays.stream(heuristics).parallel()
                .map(weightedHeuristic -> getValue(state, player, weightedHeuristic))
                .reduce(0.0, Double::sum);
        return value / 100;
    }

    private double getValue(State state, int player, WeightedHeuristic weightedHeuristic) {
        return weightedHeuristic.weight * weightedHeuristic.heuristic.evaluate(state, player);
    }

    public static class WeightedHeuristic {
        @Getter
        final double weight;
        @Getter
        final HeuristicEvaluation heuristic;

        public WeightedHeuristic(double weight, HeuristicEvaluation heuristic) {
            this.weight = weight;
            this.heuristic = heuristic;
        }
    }
}
