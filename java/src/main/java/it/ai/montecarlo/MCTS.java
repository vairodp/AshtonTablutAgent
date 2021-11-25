package it.ai.montecarlo;

import it.ai.game.Action;
import it.ai.game.State;
import it.ai.montecarlo.termination.TerminationCondition;

import java.util.function.Supplier;

public interface MCTS {
    double getActionScore(Action action);

    /***
     *
     * @return the best action from available statistics.
     */
    Action getBestAction(State state);

    /***
     * From given state, run as many simulations as possible until the time limit (in seconds), building statistics.
     */
    void runSearch(State state, Supplier<TerminationCondition> terminationConditionFactory);

    /***
     * Return MCTS statistics for this node and children nodes.
     */
    MonteCarloStats getStats();
}
