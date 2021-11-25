package it.ai.montecarlo.heuristics;

import it.ai.game.State;
import it.ai.game.tablut.Board;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.TablutState;

import java.util.Set;

public class PawnsWellPositioned implements HeuristicEvaluation {
    //Threshold used to decide whether to use rhombus configuration
    private final int threshold;
    private final Coords[] pawnsConfiguration;
    private final int pawn;

    /***
     *  @param threshold used to decide whether to use the provided configuration.
     */
    public PawnsWellPositioned(int threshold, Coords[] pawnsConfiguration, int pawn) {
        this.threshold = threshold;
        this.pawnsConfiguration = pawnsConfiguration;
        this.pawn = pawn;
    }

    @Override
    public double evaluate(State s, int player) {
        TablutState state = (TablutState) s;
        Board board = state.getBoard();

        if (board.count(pawn) < threshold) return 0;

        return (double) countPawnsWellPositioned(board) / pawnsConfiguration.length;
    }

    private int countPawnsWellPositioned(Board board) {
        int count = 0;
        Set<Coords> blackPositions = board.getPawnCoords(pawn);

        for (Coords position : pawnsConfiguration) {
            if (blackPositions.contains(position)) count++;
        }

        return count;
    }
}
