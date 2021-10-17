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
    private Board board;

    @Getter
    @Setter
    private int turn;
    private ArrayList<Integer> actionHistory;

    @Getter
    private Counter<Integer> previousStates;

    public TablutState(Board board, int turn) {
        this.board = board;
        this.turn = turn;
        this.actionHistory = new ArrayList<>();
        this.previousStates = new Counter<>();
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
        state.previousStates.add(this.hashCode());
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
        //TODO: rivedere i metodi clone. Usare un costruttore privato e fare tutto final
        TablutState state = (TablutState) super.clone();

        state.board = board.clone();
        state.actionHistory = (ArrayList<Integer>) actionHistory.clone();
        state.previousStates = previousStates.clone();

        return state;
    }

    @Override
    public String toString() {
        return "TablutState{" +
                "board=" + board +
                ", turn=" + turn +
                ", actionHistory=" + actionHistory +
                ", previousStates=" + previousStates +
                '}';
    }

    /* TODO: create ActionHistory interface*/
}
