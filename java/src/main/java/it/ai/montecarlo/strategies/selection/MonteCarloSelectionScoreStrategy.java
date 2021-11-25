package it.ai.montecarlo.strategies.selection;

import it.ai.montecarlo.MonteCarloNode;

public interface MonteCarloSelectionScoreStrategy {
    double score(MonteCarloNode node);
}
