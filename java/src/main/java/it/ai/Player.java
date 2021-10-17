package it.ai;


import it.ai.game.Action;
import it.ai.game.State;

public interface Player {
    String getName();

    String getTeam();

    Action getAction(State state);
}
