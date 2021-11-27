package it.ai.montecarlo.phases;

import it.ai.montecarlo.MonteCarloNode;

public abstract class SimulationDecorator implements Simulation {
    private final Simulation simulation;

    public SimulationDecorator(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Iterable<Integer> run(MonteCarloNode node) {
        return simulation.run(node);
    }

    @Override
    public Integer runSingle(MonteCarloNode node) {
        return simulation.runSingle(node);
    }
}
