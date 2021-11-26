package it.ai.montecarlo;

import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.MonteCarloStats.MonteCarloNodeStats;
import it.ai.montecarlo.phases.Backpropagation;
import it.ai.montecarlo.phases.Expansion;
import it.ai.montecarlo.phases.Selection;
import it.ai.montecarlo.phases.Simulation;
import it.ai.montecarlo.strategies.bestaction.MonteCarloBestActionStrategy;
import it.ai.montecarlo.termination.TerminationCondition;
import it.ai.util.MathUtils;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MCTS implements IMCTS {
    protected final Logger logger = Logger.getLogger(MCTS.class.getName());
    protected final Game game;
    protected final MonteCarloBestActionStrategy bestActionStrategy;

    protected final Selection selection;
    protected final Expansion expansion;
    protected final Simulation simulation;
    protected final Backpropagation backpropagation;

    protected MonteCarloNode rootNode;

    public MCTS(Game game, MonteCarloBestActionStrategy bestActionStrategy, Selection selection, Expansion expansion, Simulation simulation, Backpropagation backpropagation) {
        this.game = game;
        this.bestActionStrategy = bestActionStrategy;
        this.selection = selection;
        this.expansion = expansion;
        this.simulation = simulation;
        this.backpropagation = backpropagation;
    }


    /***
     * If state does not exist, create dangling node.
     *
     * @param state The state to make a dangling node for; its parent is set to null.
     */
    protected void createRootNode(State state) {
//        while (rootNode == null || !rootNode.getState().equals(state)) {
        Iterable<Action> unexpandedActions = game.getValidActions(state);
        rootNode = new MonteCarloNode(null, null, state, unexpandedActions);
//        }
    }

    /***
     *
     * @return the best action from available statistics.
     */
    @Override
    public Action getBestAction(State state) {
        // If not all children are expanded, not enough information
        if (!rootNode.isFullyExpanded()) {
            long unexpanded = rootNode.getUnexpandedActions().count();
            long total = rootNode.getAllActions().count();
            long expanded = total - unexpanded;
            logger.severe("Expanded = " + expanded + " / " + total);
//            throw new RuntimeException("Not enough information!\nExpanded = " + expanded + " / " + total);
        }

        Action bestAction = MathUtils.argmax(rootNode.getAllActions()::iterator, this::getActionScore);

        logger.fine("Best action = ".concat(bestAction.toString()));
        logger.info("Win probability: ".concat(String.valueOf(1 - rootNode.getRewards() / rootNode.getNumberOfSimulations())));
        return bestAction;
    }

    private double getActionScore(Action action) {
        MonteCarloNode node = rootNode.getChildNode(action);
        return bestActionStrategy.score(node);
    }

    /***
     * From given state, run as many simulations as possible until the time limit (in seconds), building statistics.
     */
    @Override
    public void runSearch(State state, Supplier<TerminationCondition> terminationConditionFactory) {
        createRootNode(state);

        TerminationCondition terminationCondition = terminationConditionFactory.get();
        while (!terminationCondition.reached()) {
            search();
        }

        if (logger.isLoggable(Level.FINE)) {
            MonteCarloNode node = rootNode;
            long unexpanded = node.getUnexpandedActions().count();
            long total = node.getAllActions().count();
            long expanded = total - unexpanded;
            logger.fine("Expanded = " + expanded + " / " + total);
            logger.fine("n_simulations " + node.getNumberOfSimulations() + ", action_value " + node.getRewards());
        }
    }

    protected void search() {
        MonteCarloNode node = selection.run(rootNode);
        Optional<Integer> winner = game.getWinner(node.getState());

        if (winner.isPresent()) {
            backpropagation.run(node, winner.get());
        } else {
            node = expansion.run(node);
            Iterable<Integer> winners = simulation.run(node);

            backpropagation.run(node, winners);
        }

//            DistanceFromFinalState distance = new DistanceFromFinalState();

        //if the match is not closed and there are possible actions from the selected node
//        if (!winner.isPresent() && !node.isLeaf()) {
//            node = expansion.expansion(node);
//            winner = simulation.simulation(node);
//        }
//
//        if (!winner.isPresent()) throw new RuntimeException("No actions available.");
//
//        backpropagation.backpropagation(node, winner.get());
    }

    /***
     * Return MCTS statistics for this node and children nodes.
     */
    @Override
    public MonteCarloStats getStats() {
        MonteCarloNode node = rootNode;
        MonteCarloStats stats = new MonteCarloStats(node.getNumberOfSimulations(), node.getRewards(), node.getHeuristicValue());

        node.getChildren().forEach(child -> {
            if (child.getNode() == null)
                stats.getChildren().add(new MonteCarloNodeStats(child.getLink()));
            else {
                MonteCarloNode childNode = (MonteCarloNode) child.getNode();
                stats.getChildren().add(
                        new MonteCarloNodeStats(childNode.getAction(),
                                childNode.getNumberOfSimulations(), childNode.getRewards(), childNode.getHeuristicValue()));
            }
        });

        return stats;
    }

    /***
     * Commentare tutta sta porcheria RAVE che tanto non funziona (per ora)
     * TODO: add move priority in order to choice move in simulation phase
     * They can be summarised by applying four prioritised rules after any opponent move a:
     *      1. If a put some of our stones into atari, play a saving move at random.
     *      2. Otherwise, if one of the 8 intersections surrounding a matches a simple pattern for cutting or hane,
     *          randomly play one.
     *      3. Otherwise, if any opponent stone can be captured, play a capturing move at random.
     *      4. Otherwise play a random move.
     *
     *  Successivamente è possibile usare supervised learning per stimare i pesi della default_policy
     */

    protected static class DistanceFromFinalState {
        @Getter
        private int value = 1;

        public void increment() {
            value++;
        }
    }

}
