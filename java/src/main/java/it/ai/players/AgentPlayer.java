package it.ai.players;

import it.ai.Player;
import it.ai.agents.Agent;
import it.ai.game.Action;
import it.ai.game.State;
import lombok.Getter;

public class AgentPlayer implements Player {
    @Getter
    private final String name;
    @Getter
    private final String team;
    private final Agent agent;

    public AgentPlayer(String name, String team, Agent agent) {
        this.name = name;
        this.team = team;
        this.agent = agent;
    }

    @Override
    public Action getAction(State state) {
        state = agent.updateState(state);
        return agent.getAction(state);
    }
}
