package it.ai.montecarlo.strategies.qvalue;

import it.ai.montecarlo.MonteCarloNode;

public interface QEvaluation {
    double qValue(MonteCarloNode node);
}
