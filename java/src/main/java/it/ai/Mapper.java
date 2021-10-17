package it.ai;

import it.ai.protocol.Action;
import it.ai.protocol.State;

public interface Mapper {
    Action mapToProtocolAction(it.ai.game.State state, it.ai.game.Action action);

    it.ai.game.State mapToGameState(State state);
}
