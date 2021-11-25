package it.ai.montecarlo;

import it.ai.game.Action;
import it.ai.game.State;
import it.ai.montecarlo.termination.TerminationCondition;

import java.util.Optional;
import java.util.function.Supplier;

public class MCTSDecorator extends AbstractMCTS {
    private final AbstractMCTS mcts;

    public MCTSDecorator(AbstractMCTS mcts) {
        this.mcts = mcts;
    }

    @Override
    public double getActionScore(Action action) {
        return mcts.getActionScore(action);
    }

    /***
     *
     * @return the best action from available statistics.
     * @param state
     */
    @Override
    public Action getBestAction(State state) {
        return mcts.getBestAction(state);
    }

    /***
     * From given state, run as many simulations as possible until the time limit (in seconds), building statistics.
     * @param state
     * @param terminationConditionFactory
     */
    @Override
    public void runSearch(State state, Supplier<TerminationCondition> terminationConditionFactory) {
        mcts.runSearch(state, terminationConditionFactory);
    }

    @Override
    protected void createRootNode(State state) {
        mcts.createRootNode(state);
    }

    /***
     * Phase 1, Selection: Select until not fully expanded OR leaf.
     */
    @Override
    protected MonteCarloNode selection() {
        return mcts.selection();
    }

    /***
     * Phase 2, Expansion: Expand a random unexpanded child node.
     * @param node
     */
    @Override
    protected MonteCarloNode expansion(MonteCarloNode node) {
        return mcts.expansion(node);
    }

    /***
     * Phase 3, Simulation: Play game to terminal state using random actions, return winner.
     * @param node
     */
    @Override
    protected Optional<Integer> simulation(MonteCarloNode node) {
        return mcts.simulation(node);
    }

    @Override
    protected Optional<Integer> evaluateWinner(State state) {
        return mcts.evaluateWinner(state);
    }

    /***
     * Phase 4, Backpropagation: Update ancestor statistics.
     * @param node
     * @param winner
     */
    @Override
    protected void backpropagation(MonteCarloNode node, int winner) {
        mcts.backpropagation(node, winner);
    }

    /***
     * Return MCTS statistics for this node and children nodes.
     */
    @Override
    public MonteCarloStats getStats() {
        return mcts.getStats();
    }
}
