package it.ai.montecarlo.phases;

import it.ai.montecarlo.MonteCarloNode;

public interface Simulation {
    Iterable<Integer> run(MonteCarloNode node);

    Integer runSingle(MonteCarloNode node);
}
