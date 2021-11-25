package it.ai.montecarlo.strategies.qvalue;

import it.ai.montecarlo.MonteCarloNode;

public class HeuristicQValue implements QEvaluation {
    private final DynamicAlpha alpha;

    public HeuristicQValue(DynamicAlpha alpha) {
        this.alpha = alpha;
    }

    public HeuristicQValue(double alpha) {
        this(new ConstAlpha(alpha));
    }

    @Override
    public double qValue(MonteCarloNode node) {
        double winRatio = node.getRewards() / node.getNumberOfSimulations();
        return (1 - alpha.getValue()) * winRatio + alpha.getValue() * node.getHeuristicValue();
    }

    private static class ConstAlpha implements DynamicAlpha {
        private final double alpha;

        private ConstAlpha(double alpha) {
            this.alpha = alpha;
        }

        @Override
        public double getValue() {
            return alpha;
        }
    }
}
