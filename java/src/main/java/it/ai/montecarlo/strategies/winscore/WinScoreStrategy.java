package it.ai.montecarlo.strategies.winscore;

public interface WinScoreStrategy {
    double winScore(int distanceFromFinalState);

    double loseScore(int distanceFromFinalState);

    double drawScore(int distanceFromFinalState);
}
