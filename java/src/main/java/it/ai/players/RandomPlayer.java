package it.ai.players;

import it.ai.Player;
import it.ai.game.Action;
import it.ai.game.Coords;
import it.ai.game.State;

import java.util.concurrent.ThreadLocalRandom;

public class RandomPlayer implements Player {
    private final String team;

    public RandomPlayer(String team) {
        this.team = team;
    }

    @Override
    public String getName() {
        return "Random Player";
    }

    @Override
    public Action getAction(State state) {
        return new RandomAction();
    }

    public String getTeam() {
        return this.team;
    }

    private class RandomAction implements Action {
        private final Coords from = newCoords();
        private final Coords to = newCoords();

        private Coords newCoords() {
            int row = ThreadLocalRandom.current().nextInt(0, 9);
            int column = ThreadLocalRandom.current().nextInt(0, 9);
            return new it.ai.game.tablut.Coords(row, column);
        }

        public Coords getFrom() {
            return this.from;
        }

        public Coords getTo() {
            return this.to;
        }
    }
}
