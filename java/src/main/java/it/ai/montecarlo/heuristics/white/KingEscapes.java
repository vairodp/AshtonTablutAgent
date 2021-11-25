package it.ai.montecarlo.heuristics.white;

import it.ai.game.State;
import it.ai.game.tablut.Board;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.TablutState;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.heuristics.HeuristicUtils;

public class KingEscapes implements HeuristicEvaluation {
    @Override
    public double evaluate(State s, int player) {
        TablutState state = (TablutState) s;

        return countWinWays(state) / 4.0;
    }

    /**
     * SAFE POSITION = The square near the throne where the king has no way to escape
     *
     * @return number of escapes which king can reach
     */
    private int countWinWays(TablutState state) {
        Coords kingPosition = HeuristicUtils.kingPosition(state);
        int kingPositionRow = kingPosition.getRow();
        int kingPositionColumn = kingPosition.getColumn();

        boolean safeRow = kingPositionColumn >= 3 && kingPositionColumn <= 5;
        boolean safeColumn = kingPositionRow >= 3 && kingPositionRow <= 5;
        boolean safeSquare = safeRow && safeColumn;

        int freeColumns = 0;
        int freeRows = 0;
        if (!safeSquare) {
            if (safeRow) {
                // safe row not safe col
                freeRows = countFreeRows(state, kingPosition);
            } else if (safeColumn) {
                // safe col not safe row
                freeColumns = countFreeColumns(state, kingPosition);
            } else {
                freeColumns = countFreeColumns(state, kingPosition);
                freeRows = countFreeRows(state, kingPosition);
            }
        }

        return freeColumns + freeRows;
    }

    /**
     * @return number of free columns
     */
    private int countFreeColumns(TablutState state, Coords position) {
        Board board = state.getBoard();
        int row = position.getRow();
        int column = position.getColumn();

        int freeWays = 2;

        //going down
        for (int i = row + 1; i < board.numberOfRows(); i++) {
            if (!board.isEmpty(i, column)) {
                freeWays -= 1;
                break;
            }
        }

        //going up
        for (int i = row - 1; i >= 0; i--) {
            if (!board.isEmpty(i, column)) {
                freeWays -= 1;
                break;
            }
        }

        return freeWays;
    }

    /**
     * @return number of free rows that a Pawn has
     */
    public int countFreeRows(TablutState state, Coords position) {
        Board board = state.getBoard();
        int row = position.getRow();
        int column = position.getColumn();

        int freeWays = 2;

        //going right
        for (int j = column + 1; j < board.numberOfColumns(); j++) {
            if (!board.isEmpty(row, j)) {
                freeWays -= 1;
                break;
            }
        }

        //going left
        for (int j = column - 1; j >= 0; j--) {
            if (!board.isEmpty(row, j)) {
                freeWays -= 1;
                break;
            }
        }

        return freeWays;
    }

}
