package it.ai.protocol;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Action {
    private String from;
    private String to;
    private String turn;

    public Action(String from, String to, String turn) {
        this.from = from;
        this.to = to;
        this.turn = turn;
    }

    public static String mapToCoord(int row, int column) {
        return String.valueOf((char) (column + 97)).concat(String.valueOf(row + 1));
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public String getTurn() {
        return this.turn;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Action)) return false;

        Action action = (Action) o;

        return new EqualsBuilder().append(from, action.from).append(to, action.to).append(turn, action.turn).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(from).append(to).append(turn).toHashCode();
    }

    @Override
    public String toString() {
        return "(" + from + ", " + to + ")";
    }
}
