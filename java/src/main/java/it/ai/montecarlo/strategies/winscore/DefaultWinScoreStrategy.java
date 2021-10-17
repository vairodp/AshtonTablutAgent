package it.ai.montecarlo.strategies.winscore;

public class DefaultWinScoreStrategy implements WinScoreStrategy {
    private final double winScore;
    private final double drawScore;

    public DefaultWinScoreStrategy(double winScore, double drawScore) {
        this.winScore = winScore;
        this.drawScore = drawScore;
    }

    @Override
    public double winScore(int distanceFromFinalState) {
        return distanceFromFinalState == 0 ? Double.MAX_VALUE : winScore;
    }

    @Override
    public double loseScore(int distanceFromFinalState) {
        return distanceFromFinalState == 0 ? -Double.MAX_VALUE : 0;
    }

    @Override
    public double drawScore(int distanceFromFinalState) {
        return drawScore;
    }
}
