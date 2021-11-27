package it.ai.montecarlo.heuristics;

import it.ai.game.tablut.Board;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.Pawn;
import it.ai.game.tablut.TablutState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class HeuristicUtils {
    public static Coords kingPosition(TablutState state) {
        return state.getBoard().getPawnCoords(Pawn.KING).iterator().next();
    }

    public static int countPawnsSurroundingPosition(TablutState state, Coords position, int targetPawn) {
        Set<Coords> opponentPositions = state.getBoard().getPawnCoords(targetPawn);
        Coords[] surroundingPositions = Coords.surroundingPositions(position);

        int count = 0;
        for (Coords surroundingPosition : surroundingPositions) {
            if (opponentPositions.contains(surroundingPosition)) count++;
        }

        return count;
    }

    public static List<Coords> positionOccupiedNearPawn(TablutState state, Coords pawnPosition, int opponentPawn) {
        Set<Coords> opponentPositions = state.getBoard().getPawnCoords(opponentPawn);
        Coords[] surroundingPositions = Coords.surroundingPositions(pawnPosition);

        List<Coords> occupiedPositions = new ArrayList<>();
        for (Coords surroundingPosition : surroundingPositions) {
            if (opponentPositions.contains(surroundingPosition))
                occupiedPositions.add(surroundingPosition);
        }

        return occupiedPositions;
    }

    /**
     * @return number of positions needed to eat king in the current state
     */
    public static int numberOfPawnsToEatKing(Coords kingPosition) {
        //king on throne
        if (kingPosition.equals(Coords.E5))
            return 4;

        //king near throne
        if (kingPosition.equals(Coords.E4)
                || kingPosition.equals(Coords.F5)
                || kingPosition.equals(Coords.E6)
                || kingPosition.equals(Coords.D5))
            return 3;

        return 2;
    }

    /**
     * SAFE POSITION = The square near the throne where the king has no way to escape
     *
     * @return number of escapes which king can reach
     */
    public static int countKingEscapes(TablutState state) {
        Coords kingPosition = HeuristicUtils.kingPosition(state);
        return countKingEscapes(state, kingPosition);
    }

    public static int countKingEscapes(TablutState state, Coords kingPosition) {
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
    private static int countFreeColumns(TablutState state, Coords position) {
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
    public static int countFreeRows(TablutState state, Coords position) {
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
