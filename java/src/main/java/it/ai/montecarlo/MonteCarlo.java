package it.ai.montecarlo;

import it.ai.game.Action;
import it.ai.game.Coords;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.MonteCarloStats.MonteCarloNodeStats;
import it.ai.montecarlo.strategies.bestaction.MonteCarloBestActionStrategy;
import it.ai.montecarlo.strategies.score.MonteCarloSelectionScoreStrategy;
import it.ai.montecarlo.strategies.winscore.WinScoreStrategy;
import it.ai.util.MathUtils;
import it.ai.util.RandomUtils;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MonteCarlo {
    private final Logger logger = Logger.getLogger(MonteCarlo.class.getName());

    private final Game game;
    private final MonteCarloSelectionScoreStrategy selectionScoreStrategy;
    private final MonteCarloBestActionStrategy bestActionStrategy;
    private final WinScoreStrategy winScoreStrategy;

    private final Map<Integer, MonteCarloNode> nodes = new HashMap<>();
    private MonteCarloNode rootNode = null;

    public MonteCarlo(Game game, MonteCarloSelectionScoreStrategy selectionScoreStrategy, MonteCarloBestActionStrategy bestActionStrategy, WinScoreStrategy winScoreStrategy) {
        this.game = game;
        this.selectionScoreStrategy = selectionScoreStrategy;
        this.bestActionStrategy = bestActionStrategy;
        this.winScoreStrategy = winScoreStrategy;
    }

    /***
     * If state does not exist, create dangling node.
     *
     * @param state The state to make a dangling node for; its parent is set to null.
     */
    public void makeNode(State state) {
        int actionHistory = state.getHistoryHash();
        if (!nodes.containsKey(actionHistory)) {
            Collection<Action> unexpandedActions = game.getValidActions(state);
            MonteCarloNode node = new MonteCarloNode(null, null, state, unexpandedActions);
            nodes.put(actionHistory, node);
        }
    }

    // TODO: rivedere questo metodo perchè accede a node.children che dovrebbe essere inaccessibile
    public void updateRootNode(Action action, State state) {
//        int actionHistory = state.getHistoryHash();
//        MonteCarloNode node = nodes.get(actionHistory);
//
//        if (node == null && rootNode != null && rootNode.getChildren().containsKey(action)) {
//            logger.fine("Node found for action ".concat(action.toString()));
//            MonteCarloNode.MonteCarloChild child_node = rootNode.getChildren().get(action);
//            action = child_node.getAction();
//            node = child_node.getNode();
//        }
//
//        if (node == null) {
//            Collection<Action> unexpanded_actions = game.getValidActions(state);
//            node = new MonteCarloNode(rootNode, action, state, unexpanded_actions);
//        }
//
//        nodes.put(actionHistory, node);
//        rootNode = node;
    }

    /***
     *
     * @return the best action from available statistics.
     */
    public Action bestAction(State state) {
        MonteCarloNode node = nodes.get(state.getHistoryHash());
        if (node == null) throw new RuntimeException("Run search before getting best move");

        // If not all children are expanded, not enough information
        if (!node.isFullyExpanded()) {
            long unexpanded = node.getUnexpandedActions().count();
            long total = node.getAllActions().count();
            long expanded = total - unexpanded;
            logger.severe("Expanded = " + expanded + " / " + total);
            throw new RuntimeException("Not enough information!\nExpanded = " + expanded + " / " + total);
        }

        Action best_action = MathUtils.argmax(node.getAllActions()::iterator,
                action -> bestActionStrategy.score(node.getChildNode(action)));

        logger.fine("Best action = ".concat(best_action.toString()));
        return best_action;
    }

    /***
     * From given state, run as many simulations as possible until the time limit (in seconds), building statistics.
     * @param timeout_s timeout in seconds
     */
    public void runSearch(State state, int timeout_s) {
        makeNode(state);

        long end = System.currentTimeMillis() + timeout_s * 1000L;
        while (System.currentTimeMillis() < end) {
            MonteCarloNode node = select(state);
            Optional<Integer> winner = game.getWinner(node.getState());

            State finalState = null;
            DistanceFromFinalState distance = new DistanceFromFinalState();
            //if the match is not closed and there are possible actions from the selected node
            if (!winner.isPresent() && !node.isLeaf()) {
                node = expand(node);
                Pair<State, Optional<Integer>> simulationResult = simulate(node, RandomUtils::choice, distance);
                finalState = simulationResult.getLeft();
                winner = simulationResult.getRight();
            }

            if (!winner.isPresent()) throw new RuntimeException("No actions available.");

            backpropagation(node, finalState, winner.get(), distance);
        }

        MonteCarloNode node = nodes.get(state.getHistoryHash());
        long unexpanded = node.getUnexpandedActions().count();
        long total = node.getAllActions().count();
        long expanded = total - unexpanded;
        logger.fine("Expanded = " + expanded + " / " + total);
        logger.fine("n_simulations " + node.numberOfSimulations() + ", win_score " + node.winScore());
    }

    /***
     * Phase 1, Selection: Select until not fully expanded OR leaf.
     */
    private MonteCarloNode select(State state) {
        MonteCarloNode node = nodes.get(state.getHistoryHash());

        while (node.isFullyExpanded() && !node.isLeaf()) {

            MonteCarloNode finalNode = node;
            Action bestAction = MathUtils.argmax(node.getAllActions()::iterator,
                    action -> finalNode.getChildNode(action).score(selectionScoreStrategy));

            node = node.getChildNode(bestAction);
        }

        return node;
    }

    /***
     * Phase 2, Expansion: Expand a random unexpanded child node.
     */
    private MonteCarloNode expand(MonteCarloNode node) {
        List<Action> unexpandedActions = node.getUnexpandedActions().collect(Collectors.toList());
        if (unexpandedActions.isEmpty())
            logger.severe("Len = 0, state = ".concat(node.getState().toString()));

        Action action = RandomUtils.choice(unexpandedActions);

        State childState = game.nextState(node.getState(), action);
        Collection<Action> childUnexpandedActions = game.getValidActions(childState);
        MonteCarloNode childNode = node.expand(action, childState, childUnexpandedActions);

//        nodes.put(childState.getHistoryHash(), childNode);

        //if child_state.turn == 0:
        //logger.debug(f'Expanding action {action}, parent = {self._nodes[node.state.history_hash()].action}, turn = {child_state.turn}')
        return childNode;
    }

    /***
     * Phase 3, Simulation: Play game to terminal state using random actions, return winner.
     */
    private Pair<State, Optional<Integer>> simulate(MonteCarloNode node, Function<List<Action>, Action> defaultPolicy, DistanceFromFinalState distance) {
        State state = node.getState();
        Optional<Integer> winner = game.getWinner(state);

        while (!winner.isPresent()) {
            List<Action> validActions = game.getValidActions(state);
            if (validActions.isEmpty())
                throw new RuntimeException("No valid actions for state" + state);

            Action action = defaultPolicy.apply(validActions);
            state = game.nextState(state, action);
            winner = game.getWinner(state);
            distance.increment();
        }

        return Pair.of(state, winner);
    }

    /***
     * Phase 4, Backpropagation: Update ancestor statistics.
     */
    private void backpropagation(MonteCarloNode node, State finalState, int winner, DistanceFromFinalState distance) {
//        Map<Integer, Set<Coords>> cellsOccupiedByPlayers = new HashMap<>();
//        if (finalState != null)
//            cellsOccupiedByPlayers = getCellsOccupiedByPlayers(finalState);
        int previousPlayer = game.previousPlayer(winner);

        while (node != null) {
            node.numberOfSimulations += 1;

            double reward;
            if (winner == Game.DRAW)
                reward = winScoreStrategy.drawScore(distance.getValue());

                //Score of child node is used by the parent.
                //Therefore, we increment the child node score if the parent player has won.
            else if (node.getState().isPlayerTurn(previousPlayer))
                reward = winScoreStrategy.winScore(distance.getValue());
            else
                reward = winScoreStrategy.loseScore(distance.getValue());

            node.winScore += reward;

//            if (finalState != null) {
//                Set<Coords> occupiedCells = cellsOccupiedByPlayers.get(node.getState().getTurn());
//                double finalReward = -reward;
//                node.getChildrenNodes().forEach(child_node -> {
//                    //If the pawn moved is still in the final state and the player has won (lost), the action was good(bad)
//                    //Therefore, we increase the child score
//
//                    if (occupiedCells.contains(child_node.getAction().getTo())) {
//                        child_node.numberOfRave += 1;
//                        child_node.raveScore += finalReward;
//                    }
//                });
//            }

            distance.increment();
            node = node.getParent();
        }
    }

    private Map<Integer, Set<Coords>> getCellsOccupiedByPlayers(State state) {
        return game.getPlayers().collect(Collectors.toMap(Function.identity(),
                player -> game.cellsOccupiedBy(state, player).collect(Collectors.toSet())));
    }

    /***
     * Return MCTS statistics for this node and children nodes.
     */
    public MonteCarloStats getStats(State state) {
        MonteCarloNode node = nodes.get(state.getHistoryHash());
        MonteCarloStats stats = new MonteCarloStats(node.numberOfSimulations, node.winScore,
                node.numberOfRave, node.raveScore);

        for (MonteCarloNode.MonteCarloChild child : node.getChildren().values()) {
            if (child.getNode() == null)
                stats.getChildren().add(new MonteCarloNodeStats(child.getAction()));
            else
                stats.getChildren().add(
                        new MonteCarloNodeStats(child.getAction(),
                                child.getNode().numberOfSimulations, child.getNode().winScore,
                                child.getNode().numberOfRave, child.getNode().raveScore));
        }
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

    private static class DistanceFromFinalState {
        @Getter
        private int value = 0;

        public void increment() {
            value++;
        }
    }

}
