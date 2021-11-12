package it.ai.montecarlo.strategies.winscore;

public interface WinScoreStrategy {
    double winScore();

    double drawScore();

    double loseScore();

}
