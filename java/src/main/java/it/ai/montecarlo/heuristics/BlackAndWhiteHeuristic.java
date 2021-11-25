package it.ai.montecarlo.heuristics;

import it.ai.constants.Constants;
import it.ai.game.State;

public class BlackAndWhiteHeuristic implements HeuristicEvaluation {
    private final HeuristicEvaluation blackHeuristic;
    private final HeuristicEvaluation whiteHeuristic;

    public BlackAndWhiteHeuristic(HeuristicEvaluation blackHeuristic, HeuristicEvaluation whiteHeuristic) {
        this.blackHeuristic = blackHeuristic;
        this.whiteHeuristic = whiteHeuristic;
    }

    @Override
    public double evaluate(State state, int player) {
        HeuristicEvaluation heuristic = (player == Constants.Player.BLACK ? blackHeuristic : whiteHeuristic);

        return heuristic.evaluate(state, player);
    }
}
