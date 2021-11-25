package it.ai.montecarlo.strategies.qvalue;

import it.ai.montecarlo.MonteCarloNode;

public class HeuristicQValue implements QEvaluation{
    private final double alpha;

    public HeuristicQValue(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double qValue(MonteCarloNode node) {
        double winRatio = node.getRewards() / node.getNumberOfSimulations();
        return (1-alpha) * winRatio + alpha * node.getHeuristicValue();
    }
}
