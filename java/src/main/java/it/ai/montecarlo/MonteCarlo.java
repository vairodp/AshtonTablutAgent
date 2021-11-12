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

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MonteCarlo {
    protected final Logger logger = Logger.getLogger(MonteCarlo.class.getName());

    protected final Game game;
    protected final MonteCarloSelectionScoreStrategy selectionScoreStrategy;
    protected final MonteCarloBestActionStrategy bestActionStrategy;
    protected final WinScoreStrategy winScoreStrategy;

    protected Map<State, MonteCarloNode> nodes = new HashMap<>();

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
        if (!nodes.containsKey(state)) {
            Iterable<Action> unexpandedActions = game.getValidActions(state);
            MonteCarloNode node = new MonteCarloNode(null, null, state, unexpandedActions);
            nodes.put(state, node);
        }
    }

    public void updateNodes(State state) {
        //TODO: usare la libreria java che storicizza in memory e on disk, basta fare map.clearWithExpires(). dbMap
        MonteCarloNode node = nodes.get(state);

        nodes = new HashMap<>(); //TODO: Forse meglio HashTable
        if (node != null) {
            logger.fine("Node found.");
            nodes = node.getExpandedNodes().collect(
                    Collectors.toMap(MonteCarloNode::getState, Function.identity()));
            nodes.put(state, node);
//            System.gc();
        }
    }

    /***
     *
     * @return the best action from available statistics.
     */
    public Action bestAction(State state) {
        MonteCarloNode node = nodes.get(state);
        if (node == null) throw new RuntimeException("Run search before getting best move.");

        // If not all children are expanded, not enough information
        if (!node.isFullyExpanded()) {
            long unexpanded = node.getUnexpandedActions().count();
            long total = node.getAllActions().count();
            long expanded = total - unexpanded;
            logger.severe("Expanded = " + expanded + " / " + total);
            throw new RuntimeException("Not enough information!\nExpanded = " + expanded + " / " + total);
        }

        Action bestAction = MathUtils.argmax(node.getAllActions()::iterator,
                action -> bestActionStrategy.score(node.getChildNode(action)));

        logger.fine("Best action = ".concat(bestAction.toString()));
        return bestAction;
    }

    /***
     * From given state, run as many simulations as possible until the time limit (in seconds), building statistics.
     * @param timeout_s timeout in seconds
     */
    public void runSearch(State state, int timeout_s) {
        updateNodes(state);
        makeNode(state);

        long end = System.currentTimeMillis() + timeout_s * 1000L;
        while (System.currentTimeMillis() < end) {
            MonteCarloNode node = selection(state);
            Optional<Integer> winner = game.getWinner(node.getState());

            DistanceFromFinalState distance = new DistanceFromFinalState();
            //if the match is not closed and there are possible actions from the selected node
            if (!winner.isPresent() && !node.isLeaf()) {
                node = expansion(node);
                winner = simulation(node, RandomUtils::choice);
            }

            if (!winner.isPresent()) throw new RuntimeException("No actions available.");

            backpropagation(node, winner.get());
        }

        MonteCarloNode node = nodes.get(state);
        long unexpanded = node.getUnexpandedActions().count();
        long total = node.getAllActions().count();
        long expanded = total - unexpanded;
        logger.fine("Expanded = " + expanded + " / " + total);
        logger.fine("n_simulations " + node.numberOfSimulations() + ", win_score " + node.winScore());
    }

    /***
     * Phase 1, Selection: Select until not fully expanded OR leaf.
     */
    protected MonteCarloNode selection(State state) {
        MonteCarloNode node = nodes.get(state);

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
    protected MonteCarloNode expansion(MonteCarloNode node) {
        List<Action> unexpandedActions = node.getUnexpandedActions().collect(Collectors.toList());
        if (unexpandedActions.isEmpty())
            logger.severe("Len = 0, state = ".concat(node.getState().toString()));

        Action action = RandomUtils.choice(unexpandedActions);

        State childState = game.nextState(node.getState(), action);
        Iterable<Action> childUnexpandedActions = game.getValidActions(childState);
        MonteCarloNode childNode = node.expand(action, childState, childUnexpandedActions);

        nodes.put(childState, childNode);

        //if child_state.turn == 0:
        //logger.debug(f'Expanding action {action}, parent = {self._nodes[node.state.history_hash()].action}, turn = {child_state.turn}')
        return childNode;
    }

    /***
     * Phase 3, Simulation: Play game to terminal state using random actions, return winner.
     */
    protected Optional<Integer> simulation(MonteCarloNode node, Function<List<Action>, Action> actionChoicePolicy) {
        State state = node.getState();
        Optional<Integer> winner = game.getWinner(state);

        while (!winner.isPresent()) {
            List<Action> validActions = game.getValidActions(state);
            if (validActions.isEmpty())
                throw new RuntimeException("No valid actions for state" + state);

            Action action = actionChoicePolicy.apply(validActions);
            state = game.nextState(state, action);
            winner = game.getWinner(state);
//            distance.increment();
        }

        return winner;
    }

    /***
     * Phase 4, Backpropagation: Update ancestor statistics.
     */
    protected void backpropagation(MonteCarloNode node, int winner) {
//        Map<Integer, Set<Coords>> cellsOccupiedByPlayers = new HashMap<>();
//        if (finalState != null)
//            cellsOccupiedByPlayers = getCellsOccupiedByPlayers(finalState);

        int nextPlayer = game.nextPlayer(winner);

        while (node != null) {
            node.numberOfSimulations += 1;

            incrementScore(node, winner, nextPlayer);

//            incrementRaveScore();


//            distance.increment();
            node = node.getParent();
        }
    }

    private void incrementScore(MonteCarloNode node, int winner, int nextPlayer) {
        double reward;
        if (winner == Game.DRAW)
            reward = winScoreStrategy.drawScore();

            //Score of child node is used by the parent.
            //Therefore, we increment the child node score if the parent player has won.
        else if (node.getState().isPlayerTurn(nextPlayer))
            reward = winScoreStrategy.winScore();
        else
            reward = winScoreStrategy.loseScore();

        node.winScore += reward;
    }

    private Map<Integer, Set<Coords>> getCellsOccupiedByPlayers(State state) {
        return game.getPlayers().collect(Collectors.toMap(Function.identity(),
                player -> game.cellsOccupiedBy(state, player).collect(Collectors.toSet())));
    }

    private void incrementRaveScore() {
//        if (finalState != null) {
//            Set<Coords> occupiedCells = cellsOccupiedByPlayers.get(node.getState().getTurn());
//            double finalReward = -reward;
//            node.getChildrenNodes().forEach(child_node -> {
//                //If the pawn moved is still in the final state and the player has won (lost), the action was good(bad)
//                //Therefore, we increase the child score
//
//                if (occupiedCells.contains(child_node.getAction().getTo())) {
//                    child_node.numberOfRave += 1;
//                    child_node.raveScore += finalReward;
//                }
//            });
//        }
    }

    /***
     * Return MCTS statistics for this node and children nodes.
     */
    public MonteCarloStats getStats(State state) {
        MonteCarloNode node = nodes.get(state);
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

    protected static class DistanceFromFinalState {
        @Getter
        private int value = 1;

        public void increment() {
            value++;
        }
    }

}
