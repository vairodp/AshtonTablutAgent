package it.ai.game.tablut;

import lombok.Getter;

import java.util.Objects;

public class Action implements it.ai.game.Action {
    @Getter
    private final Coords from;
    @Getter
    private final Coords to;

    public Action(Coords from, Coords to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        Action action = (Action) o;
        return from.equals(action.from) && to.equals(action.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "(" + from + ", " + to + ")";
    }
}
