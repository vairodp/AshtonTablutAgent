package it.ai.agents;

import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.MonteCarlo;
import it.ai.montecarlo.MonteCarloStats;

import java.util.Optional;
import java.util.logging.Logger;

public class MctsAgent implements Agent {
    private final Logger logger = Logger.getLogger(MctsAgent.class.getName());

    private final Game game;
    private final int timeout;
    private final MonteCarlo mcts;
    private State rootState;

    public MctsAgent(Game game, MonteCarlo mcts, int timeout_s) {
        this.game = game;
        this.timeout = timeout_s;
        this.mcts = mcts;
        rootState = game.start();
    }

    /***
     * Update state after an opponent move
     */
    @Override
    public State updateState(State state) {
        Optional<Action> action = game.getAction(rootState, state);
        action.ifPresent(a -> {
            addOpponentAction(a);
            logger.info("Opponent action: from " + a.getFrom() + " to " + a.getTo());
        });

        return rootState;
    }

    private void addOpponentAction(Action action) {
        rootState = game.nextState(rootState, action);
        mcts.updateRootNode(action, rootState);
    }

    /***
     * Get action from given state
     */
    @Override
    public Action getAction(State state) {
        logger.info("Computing best action ...");

        mcts.runSearch(state, timeout);
        Action bestAction = mcts.bestAction(state);

        // Update root state and node
        rootState = game.nextState(state, bestAction);
        mcts.updateRootNode(bestAction, rootState);

        MonteCarloStats stats = mcts.getStats(state);
        logger.fine(stats.toString());

        return bestAction;
    }
}
