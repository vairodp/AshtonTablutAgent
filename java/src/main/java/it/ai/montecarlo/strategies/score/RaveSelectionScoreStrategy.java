package it.ai.montecarlo.strategies.score;

import it.ai.montecarlo.MonteCarloNode;
import it.ai.montecarlo.strategies.bestaction.MonteCarloBestActionStrategy;

public class RaveSelectionScoreStrategy implements MonteCarloSelectionScoreStrategy, MonteCarloBestActionStrategy {
    private final double exploration;
    private final double rave;

    public RaveSelectionScoreStrategy() {
        this(2, 300);
    }

    public RaveSelectionScoreStrategy(double exploration, double rave) {
        this.exploration = exploration;
        this.rave = rave;
    }

    @Override
    public double score(MonteCarloNode node) {
        double alpha = Math.max(0, (rave - node.numberOfSimulations()) / rave);
        double uct = new Ucb1SelectionScoreStrategy(exploration).score(node);
        double amaf = node.numberOfRave() != 0 ? node.raveScore() / node.numberOfRave() : 0;

        return (1 - alpha) * uct + alpha * amaf;
    }
}
