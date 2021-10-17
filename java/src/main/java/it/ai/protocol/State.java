package it.ai.protocol;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

public class State {
    private String[][] board;
    private String turn;

    public State() {
    }

    public String[][] getBoard() {
        return this.board;
    }

    public String getTurn() {
        return this.turn;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof State)) return false;

        State state = (State) o;

        return new EqualsBuilder().append(board, state.board).append(turn, state.turn).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(board).append(turn).toHashCode();
    }

    @Override
    public String toString() {
        return "State{" +
                "board=" + Arrays.deepToString(board) +
                ", turn='" + turn + '\'' +
                '}';
    }
}
