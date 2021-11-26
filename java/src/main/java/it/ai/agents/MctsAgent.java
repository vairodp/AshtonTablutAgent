package it.ai.agents;

import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.IMCTS;
import it.ai.montecarlo.MonteCarloStats;
import it.ai.montecarlo.termination.TerminationCondition;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MctsAgent implements Agent {
    private final Logger logger = Logger.getLogger(MctsAgent.class.getName());

    @Getter
    private final List<Action> actions;
    private final Game game;
    Supplier<TerminationCondition> terminationConditionFactory;
    private final IMCTS mcts;
    private State rootState;

    public MctsAgent(Game game, IMCTS mcts, Supplier<TerminationCondition> terminationConditionFactory) {
        this.game = game;
        this.terminationConditionFactory = terminationConditionFactory;
        this.mcts = mcts;
        rootState = game.start();
        actions = new ArrayList<>();
    }

    /***
     * Update state after an opponent move
     */
    @Override
    public State updateStateWithOpponentAction(State state) {
        Optional<Action> action = game.getAction(rootState, state);
        action.ifPresent(a -> {
            addOpponentAction(a);
            logger.info("Opponent action: from " + a.getFrom() + " to " + a.getTo());
        });

        return rootState;
    }

    private void addOpponentAction(Action action) {
        actions.add(action);

        rootState = game.nextState(rootState, action);
    }

    /***
     * Get action from given state
     */
    @Override
    public Action getAction(State state) {
        logger.info("Computing best action ...");

        mcts.runSearch(state, terminationConditionFactory);
        Action bestAction = mcts.getBestAction(state);
        actions.add(bestAction);

        // Update root state
        rootState = game.nextState(state, bestAction);

        if(logger.isLoggable(Level.FINE)) {
            MonteCarloStats stats = mcts.getStats();
            logger.fine(stats.toString());
        }

        return bestAction;
    }
}
