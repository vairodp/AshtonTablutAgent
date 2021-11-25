package it.ai.montecarlo.heuristics;

import it.ai.game.State;
import lombok.Getter;

public class AggregateHeuristic implements HeuristicEvaluation {
    private final WeightedHeuristic[] heuristics;

    public AggregateHeuristic(WeightedHeuristic[] heuristics) {
        this.heuristics = heuristics;
    }

    @Override
    public double evaluate(State state, int player) {
        //TODO refine
        double value = 0;
        for (WeightedHeuristic weightedHeuristic : heuristics) {
            value += weightedHeuristic.weight * weightedHeuristic.heuristic.evaluate(state, player);
        }

        return value;
    }

    public static class WeightedHeuristic {
        @Getter
        final double weight;
        @Getter
        final HeuristicEvaluation heuristic;

        public WeightedHeuristic(double weight, HeuristicEvaluation heuristic) {
            this.weight = weight / 100;
            this.heuristic = heuristic;
        }
    }
}
