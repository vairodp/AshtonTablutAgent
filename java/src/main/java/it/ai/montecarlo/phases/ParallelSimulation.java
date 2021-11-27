package it.ai.montecarlo.phases;

import it.ai.collections.Iterables;
import it.ai.montecarlo.MonteCarloNode;
import lombok.SneakyThrows;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelSimulation extends SimulationDecorator {
    private final int numberOfCores;
    private final CompletionService<Integer> executorCompletionService;

    public ParallelSimulation(Simulation simulation, int numberOfCores) {
        super(simulation);
        this.numberOfCores = numberOfCores;
        ExecutorService threadPool = Executors.newWorkStealingPool(numberOfCores);
        this.executorCompletionService = new ExecutorCompletionService<>(threadPool);
    }

    public ParallelSimulation(Simulation simulation) {
        this(simulation, Runtime.getRuntime().availableProcessors());
    }

    @SneakyThrows
    @Override
    public Iterable<Integer> run(MonteCarloNode node) {
        return Iterables.parallel(() -> super.runSingle(node), numberOfCores, executorCompletionService);
    }
}
