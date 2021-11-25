package it.ai.montecarlo.heuristics.black;

import it.ai.game.State;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.Pawn;
import it.ai.game.tablut.TablutState;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.heuristics.HeuristicUtils;

public class BlackSurroundKing implements HeuristicEvaluation {
    @Override
    public double evaluate(State s, int player) {
        TablutState state = (TablutState) s;

        Coords kingPosition = HeuristicUtils.kingPosition(state);
        int blacksSurroundingKing = HeuristicUtils.countPawnsSurroundingPosition(state, kingPosition, Pawn.BLACK);
        return (double) blacksSurroundingKing / HeuristicUtils.numberOfPawnsToEatKing(kingPosition);
    }
}
