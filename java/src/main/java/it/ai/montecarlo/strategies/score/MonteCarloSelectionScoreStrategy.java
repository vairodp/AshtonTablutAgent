package it.ai.montecarlo.strategies.score;

import it.ai.montecarlo.MonteCarloNode;

public interface MonteCarloSelectionScoreStrategy {
    double score(MonteCarloNode node);
}
