package it.ai.montecarlo.strategies.bestaction;

import it.ai.montecarlo.MonteCarloNode;

public interface MonteCarloBestActionStrategy {
    double score(MonteCarloNode node);
}
