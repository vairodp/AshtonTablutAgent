package it.ai.montecarlo.heuristics.white;

import it.ai.game.State;
import it.ai.game.tablut.Pawn;
import it.ai.game.tablut.TablutState;
import it.ai.game.tablut.ashton.AshtonBoard;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;

public class WhiteAlive implements HeuristicEvaluation {

    @Override
    public double evaluate(State s, int player) {
        TablutState state = (TablutState) s;
        return (double) state.getBoard().count(Pawn.WHITE) / AshtonBoard.NUM_WHITE;
    }
}
