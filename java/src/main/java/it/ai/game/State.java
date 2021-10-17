package it.ai.game;

import java.util.stream.Stream;

public interface State extends Cloneable {
    Stream<Integer> getActionHistory();

    int getTurn();

    boolean isPlayerTurn(int player);

    int getHistoryHash();
}
