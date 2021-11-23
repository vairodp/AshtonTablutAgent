package it.ai.montecarlo.strategies.winscore;

public class DefaultWinScoreStrategy implements WinScoreStrategy {
    @Override
    public double winScore() {
        return 1;
    }

    @Override
    public double drawScore() {
        return 0.2;
    }

    @Override
    public double loseScore() {
        return 0;
    }

}
