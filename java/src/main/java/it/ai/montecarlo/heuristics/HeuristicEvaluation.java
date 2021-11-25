package it.ai.montecarlo.heuristics;

import it.ai.game.State;

public interface HeuristicEvaluation {
    double evaluate(State state, int player);
}
