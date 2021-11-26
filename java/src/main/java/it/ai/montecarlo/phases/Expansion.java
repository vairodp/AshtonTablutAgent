package it.ai.montecarlo.phases;

import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.MonteCarloNode;
import it.ai.util.RandomUtils;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/***
 * Phase 2, Expansion: Expand a random unexpanded child node.
 */
public class Expansion {
    protected final Logger logger = Logger.getLogger(Expansion.class.getName());
    protected final Game game;

    public Expansion(Game game) {
        this.game = game;
    }

    public MonteCarloNode expansion(MonteCarloNode node) {
        List<Action> unexpandedActions = node.getUnexpandedActions().collect(Collectors.toList());
        if (unexpandedActions.isEmpty())
            logger.severe("Len = 0, state = ".concat(node.getState().toString()));

        Action action = RandomUtils.choice(unexpandedActions);

        State childState = game.nextState(node.getState(), action);
        Iterable<Action> childUnexpandedActions = game.getValidActions(childState);

        return node.expand(action, childState, childUnexpandedActions);
    }
}
