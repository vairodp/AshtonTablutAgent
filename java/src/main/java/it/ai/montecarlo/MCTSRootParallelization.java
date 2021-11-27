package it.ai.montecarlo;

import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.phases.Backpropagation;
import it.ai.montecarlo.phases.Expansion;
import it.ai.montecarlo.phases.Selection;
import it.ai.montecarlo.phases.Simulation;
import it.ai.montecarlo.strategies.bestaction.MonteCarloBestActionStrategy;
import it.ai.montecarlo.termination.TerminationCondition;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class MCTSRootParallelization extends MCTS {
    private final Logger logger = Logger.getLogger(MCTSRootParallelization.class.getName());

    private final int numberOfCores;
    private final ExecutorService threadPool;

    public MCTSRootParallelization(Game game, MonteCarloBestActionStrategy bestActionStrategy, Selection selection, Expansion expansion, Simulation simulation, Backpropagation backpropagation, int numberOfCores) {
        super(game, bestActionStrategy, selection, expansion, simulation, backpropagation);
        this.numberOfCores = numberOfCores;
        this.threadPool = Executors.newWorkStealingPool(numberOfCores);
    }

    public MCTSRootParallelization(Game game, MonteCarloBestActionStrategy bestActionStrategy, Selection selection, Expansion expansion, Simulation simulation, Backpropagation backpropagation) {
        this(game, bestActionStrategy, selection, expansion, simulation, backpropagation, Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected void search() {
        super.search();
    }

    /***
     * From given state, run as many simulations as possible until the time limit (in seconds), building statistics.
     */
    @SneakyThrows
    @Override
    public void runSearch(State state, Supplier<TerminationCondition> terminationConditionFactory) {
        //TODO fix it
        TerminationCondition terminationCondition = terminationConditionFactory.get();
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfCores - 1; i++) {
            futures.add(threadPool.submit(() -> super.runSearch(state, () -> terminationCondition)));
        }
        for (Future<?> future : futures) {
            future.get();
        }
    }

}
