package it.ai.agents;

import it.ai.game.Action;
import it.ai.game.State;

public interface Agent {
    /***
     * Update state after an opponent move
     */
    State updateState(State state);

    /***
     * Get action from given state
     */
    Action getAction(State state);

    Iterable<Action> getActions();
}
