package it.ai.montecarlo.strategies.bestaction;

import it.ai.montecarlo.MonteCarloNode;


public class PLogNStrategy implements MonteCarloBestActionStrategy {
    @Override
    public double score(MonteCarloNode node) {
        return (node.getRewards() / node.getNumberOfSimulations()) * Math.log(node.getNumberOfSimulations());
    }
}