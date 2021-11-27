package it.ai.montecarlo.phases;

import it.ai.collections.Iterables;
import it.ai.game.Game;
import it.ai.montecarlo.MonteCarloNode;
import lombok.SneakyThrows;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelSimulation extends Simulation {
    private final int numberOfCores;
    private final CompletionService<Integer> executorCompletionService;

    public ParallelSimulation(Game game, int numberOfCores) {
        super(game);
        this.numberOfCores = numberOfCores;
        ExecutorService threadPool = Executors.newWorkStealingPool(numberOfCores);
        this.executorCompletionService = new ExecutorCompletionService<>(threadPool);
    }

    public ParallelSimulation(Game game) {
        this(game, Runtime.getRuntime().availableProcessors());
    }

    @SneakyThrows
    @Override
    public Iterable<Integer> run(MonteCarloNode node) {
        return Iterables.parallel(() -> super.runSingle(node), numberOfCores, executorCompletionService);
    }
}
