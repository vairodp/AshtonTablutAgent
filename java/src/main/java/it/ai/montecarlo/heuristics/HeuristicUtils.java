package it.ai.montecarlo.heuristics;

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
        Coords[] surroundingPositions = surroundingPositions(state, position);

        int count = 0;
        for (Coords surroundingPosition : surroundingPositions) {
            if (opponentPositions.contains(surroundingPosition)) count++;
        }

        return count;
    }

    public static List<Coords> positionOccupiedNearPawn(TablutState state, Coords pawnPosition, int opponentPawn) {
        Set<Coords> opponentPositions = state.getBoard().getPawnCoords(opponentPawn);
        Coords[] surroundingPositions = surroundingPositions(state, pawnPosition);

        List<Coords> occupiedPositions = new ArrayList<>();
        for (Coords surroundingPosition : surroundingPositions) {
            if (opponentPositions.contains(surroundingPosition))
                occupiedPositions.add(surroundingPosition);
        }

        return occupiedPositions;
    }

    public static Coords[] surroundingPositions(TablutState state, Coords pawnPosition) {
        int row = pawnPosition.getRow();
        int column = pawnPosition.getColumn();

        return new Coords[]{
                new Coords(row - 1, column), //top
                new Coords(row + 1, column), //bottom
                new Coords(row, column - 1), //left
                new Coords(row, column + 1) //right
        };
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
}
