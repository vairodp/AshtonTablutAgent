package it.ai.montecarlo.heuristics.black;

import it.ai.game.State;
import it.ai.game.tablut.TablutState;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.heuristics.HeuristicUtils;

public class BlockedKingEscapes implements HeuristicEvaluation {
    @Override
    public double evaluate(State s, int player) {
        TablutState state = (TablutState) s;

        return 4 - HeuristicUtils.countKingEscapes(state) / 4.0;
    }


}
