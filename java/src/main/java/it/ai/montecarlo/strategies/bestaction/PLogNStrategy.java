package it.ai.montecarlo.strategies.bestaction;

import it.ai.montecarlo.MonteCarloNode;


public class PLogNStrategy implements MonteCarloBestActionStrategy {
    @Override
    public double score(MonteCarloNode node) {
        return (node.getActionValue() / node.numberOfSimulations()) * Math.log(node.numberOfSimulations());
    }
}