package it.ai.montecarlo.strategies.selection;

import it.ai.montecarlo.MonteCarloNode;
import it.ai.montecarlo.strategies.qvalue.QEvaluation;

public class Ucb1SelectionScoreStrategy implements MonteCarloSelectionScoreStrategy {
    private final double exploration;
    private final QEvaluation qValueEvaluation;

    /***
     *
     * @param exploration The square of the exploration parameter in the UCB1 algorithm.
     */
    public Ucb1SelectionScoreStrategy(double exploration, QEvaluation qValueEvaluation) {
        this.exploration = exploration;
        this.qValueEvaluation = qValueEvaluation;
    }

    @Override
    public double score(MonteCarloNode node) {
        double q = qValueEvaluation.qValue(node);
        double exp = Math.sqrt(Math.log(node.getParent().getNumberOfSimulations()) / node.getNumberOfSimulations());
        return q + exploration * exp;
    }
}


