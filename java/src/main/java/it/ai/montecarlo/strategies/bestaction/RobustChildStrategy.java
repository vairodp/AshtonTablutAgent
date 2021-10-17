package it.ai.montecarlo.strategies.bestaction;

import it.ai.montecarlo.MonteCarloNode;

public class RobustChildStrategy implements MonteCarloBestActionStrategy {
    @Override
    public double score(MonteCarloNode node) {
        return node.numberOfSimulations();
    }
}
