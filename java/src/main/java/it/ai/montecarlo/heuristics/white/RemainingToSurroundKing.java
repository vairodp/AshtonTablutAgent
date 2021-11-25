package it.ai.montecarlo.heuristics.white;

import it.ai.game.State;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.Pawn;
import it.ai.game.tablut.TablutState;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.heuristics.HeuristicUtils;

public class RemainingToSurroundKing implements HeuristicEvaluation {
    @Override
    public double evaluate(State s, int player) {
        TablutState state = (TablutState) s;

        Coords kingPosition = HeuristicUtils.kingPosition(state);
        int blacksSurroundingKing = HeuristicUtils.countPawnsSurroundingPosition(state, kingPosition, Pawn.BLACK);
        int numberOfPawnsToEatKing = HeuristicUtils.numberOfPawnsToEatKing(kingPosition);
        return (double) (numberOfPawnsToEatKing - blacksSurroundingKing) / numberOfPawnsToEatKing;
    }
}
