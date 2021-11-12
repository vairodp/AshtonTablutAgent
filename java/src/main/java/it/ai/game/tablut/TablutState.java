package it.ai.game.tablut;

import it.ai.collections.Counter;
import it.ai.game.Action;
import it.ai.game.State;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.stream.Stream;

public class TablutState implements State {

    @Getter
    private final Board board;

    @Getter
    @Setter
    private int turn;
    private final ArrayList<Integer> actionHistory;

    @Getter
    private final Counter<Integer> boardHistory;

    public TablutState(Board board, int turn) {
        this.board = board;
        this.turn = turn;
        this.actionHistory = new ArrayList<>();
        this.boardHistory = new Counter<>();
    }

    private TablutState(Board board, int turn, ArrayList<Integer> actionHistory, Counter<Integer> boardHistory) {
        this.board = board;
        this.turn = turn;
        this.actionHistory = actionHistory;
        this.boardHistory = boardHistory;
    }

    @Override
    public Stream<Integer> getActionHistory() {
        return actionHistory.stream();
    }

    @Override
    public boolean isPlayerTurn(int player) {
        return this.turn == player;
    }

    @Override
    public int getHistoryHash() {
        return actionHistory.hashCode();
    }

    public TablutState nextState(Action action) {
        TablutState state = clone();
        state.boardHistory.add(this.board.hashCode());
        state.actionHistory.add(action.hashCode());

        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TablutState)) return false;

        TablutState that = (TablutState) o;

        return turn == that.turn && actionHistory.equals(that.actionHistory);
    }

    @Override
    public int hashCode() {
        return actionHistory.hashCode();
    }

    @SneakyThrows
    @Override
    public TablutState clone() {
        return new TablutState(board.clone(), turn,
                (ArrayList<Integer>) actionHistory.clone(), boardHistory.clone());
    }

    @Override
    public String toString() {
        return "TablutState{" +
                "board=" + board +
                ", turn=" + turn +
                ", actionHistory=" + actionHistory +
                ", boardHistory=" + boardHistory +
                '}';
    }

    /* TODO: create ActionHistory interface*/
}
