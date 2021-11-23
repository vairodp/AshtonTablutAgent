package it.ai.util;

import it.ai.constants.Constants;
import it.ai.Mapper;
import it.ai.game.State;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.TablutState;
import it.ai.game.tablut.ashton.AshtonBoard;
import it.ai.protocol.Action;
import it.ai.protocol.Pawn;
import it.ai.protocol.Turn;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AshtonMapper implements Mapper {
    private static final Map<Integer, String> protocolTurns = new HashMap<>() {{
        put(Constants.Player.WHITE, Turn.WHITE);
        put(Constants.Player.BLACK, Turn.BLACK);
    }};
    private static final Map<String, Integer> gameTurns =
            protocolTurns.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private static final Map<Integer, String> protocolPawns = new HashMap<>() {{
        put(it.ai.game.tablut.Pawn.WHITE, Pawn.WHITE);
        put(it.ai.game.tablut.Pawn.BLACK, Pawn.BLACK);
        put(it.ai.game.tablut.Pawn.EMPTY, Pawn.EMPTY);
        put(it.ai.game.tablut.Pawn.THRONE, Pawn.THRONE);
        put(it.ai.game.tablut.Pawn.KING, Pawn.KING);
    }};

    private static final Map<String, Integer> gamePawns =
            protocolPawns.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    @Override
    public Action mapToProtocolAction(State state, it.ai.game.Action action) {
        Coords from = (Coords) action.getFrom();
        Coords to = (Coords) action.getTo();

        return new Action(Action.mapToCoord(from.getRow(), from.getColumn()),
                Action.mapToCoord(to.getRow(), to.getColumn()),
                mapToProtocolTurn(state.getTurn()));
    }

    private String mapToProtocolTurn(int turn) {
        return protocolTurns.get(turn);
    }

    @Override
    public State mapToGameState(it.ai.protocol.State state) {
        int[][] board = mapBoard(state.getBoard());
        int turn = gameTurns.get(state.getTurn());
        return new TablutState(new AshtonBoard(board), turn);
    }

    private int[][] mapBoard(String[][] board) {
        int[][] newBoard = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = new int[board[0].length];
            for (int j = 0; j < board[0].length; j++) {
                newBoard[i][j] = gamePawns.get(board[i][j]);
            }
        }
        return newBoard;
    }
}
