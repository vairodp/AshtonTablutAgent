package it.ai.montecarlo.strategies.qvalue;

import it.ai.montecarlo.MonteCarloNode;

public class WinProbabilityQValue implements QEvaluation{
    @Override
    public double qValue(MonteCarloNode node) {
        return node.getRewards() / node.getNumberOfSimulations();
    }
}
