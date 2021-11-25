package it.ai.montecarlo;

import it.ai.game.Action;
import it.ai.game.State;
import it.ai.montecarlo.termination.TerminationCondition;
import it.ai.util.MathUtils;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class MCTSRootParallelization implements MCTS {
    private final Logger logger = Logger.getLogger(MCTSImpl.class.getName());

    private final List<AbstractMCTS> mctsInstances;
    private final ExecutorService threadPool;

    public MCTSRootParallelization(Supplier<AbstractMCTS> mctsFactory, int numberOfCores) {
        this.mctsInstances = new ArrayList<>();
        this.threadPool = Executors.newWorkStealingPool(numberOfCores);

        for (int i = 0; i < numberOfCores; i++)
            mctsInstances.add(mctsFactory.get());
    }

    public MCTSRootParallelization(Supplier<AbstractMCTS> mctsFactory) {
        this(mctsFactory, Runtime.getRuntime().availableProcessors());
    }

    /***
     *
     * @return the best action using a vote scheme.
     * @param state
     */
    @Override
    public Action getBestAction(State state) {
        // If not all children are expanded, not enough information
        MonteCarloNode rootNode = mctsInstances.get(0).getRootNode();
        if (!rootNode.isFullyExpanded()) {
            long unexpanded = rootNode.getUnexpandedActions().count();
            long total = rootNode.getAllActions().count();
            long expanded = total - unexpanded;
            logger.severe("Expanded = " + expanded + " / " + total);
            throw new RuntimeException("Not enough information!\nExpanded = " + expanded + " / " + total);
        }

        Action bestAction = MathUtils.argmax(rootNode.getAllActions()::iterator, this::getActionScore);

        logger.fine("Best action = ".concat(bestAction.toString()));
        return bestAction;
    }

    /***
     * From given state, run as many simulations as possible until the time limit (in seconds), building statistics.
     */
    @SneakyThrows
    @Override
    public void runSearch(State state, Supplier<TerminationCondition> terminationConditionFactory) {
//        long end = System.currentTimeMillis() + timeout_s * 1000L;
//        while (System.currentTimeMillis() < end) {
//
//        }

        TerminationCondition terminationCondition = terminationConditionFactory.get();
        List<Callable<Void>> callables = new ArrayList<>();
        for (MCTS mcts : mctsInstances) {
            callables.add(() -> {
                mcts.runSearch(state, () -> terminationCondition);
                return null;
            });
        }
        threadPool.invokeAll(callables);
    }

    /***
     * Return MCTS statistics for this node and children nodes.
     */
    @Override
    public MonteCarloStats getStats() {
        //TODO: implement

        double actionValue = 0;
        double heuristicValue = 0;
        int numberOfSimulations = 0;

        for (AbstractMCTS mcts : mctsInstances) {
            MonteCarloNode node = mcts.getRootNode();
            actionValue += node.getRewards() * node.getNumberOfSimulations();
            heuristicValue += node.getHeuristicValue() * node.getNumberOfSimulations();
            numberOfSimulations += node.getNumberOfSimulations();
        }
        actionValue /= numberOfSimulations;
        MonteCarloStats stats = new MonteCarloStats(numberOfSimulations, actionValue, heuristicValue);

//        for (MonteCarloNode.MonteCarloChild child : node.getChildren().values()) {
//            if (child.getNode() == null)
//                stats.getChildren().add(new MonteCarloNodeStats(child.getAction()));
//            else
//                stats.getChildren().add(
//                        new MonteCarloStats.MonteCarloNodeStats(child.getAction(),
//                                child.getNode().numberOfSimulations, child.getNode().actionValue));
//        }
        return stats;
    }


    @Override
    public double getActionScore(Action action) {
        double score = 0;
        int numberOfSimulations = 0;

        for (AbstractMCTS mcts : mctsInstances) {
            MonteCarloNode childNode = mcts.getRootNode().getChildNode(action);
            score += mcts.getActionScore(action) * childNode.getNumberOfSimulations();
            numberOfSimulations += childNode.getNumberOfSimulations();
        }

        return score / numberOfSimulations;
    }
}
