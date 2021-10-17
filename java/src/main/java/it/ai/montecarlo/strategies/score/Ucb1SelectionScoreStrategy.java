package it.ai.montecarlo.strategies.score;

import it.ai.montecarlo.MonteCarloNode;

public class Ucb1SelectionScoreStrategy implements MonteCarloSelectionScoreStrategy {
    private final double exploration;

    /***
     *
     * @param exploration The square of the exploration parameter in the UCB1 algorithm.
     */
    public Ucb1SelectionScoreStrategy(double exploration) {
        this.exploration = exploration;
    }

    @Override
    public double score(MonteCarloNode node) {
        return (node.winScore() / node.numberOfSimulations())
                + Math.sqrt(exploration * Math.log(node.getParent().numberOfSimulations()) / node.numberOfSimulations());
    }
}


