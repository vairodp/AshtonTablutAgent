//package it.ai.montecarlo.strategies.winscore;
//
//public class InverseLogDistanceWinScoreStrategy implements WinScoreStrategy {
//    private final double winScore;
//    private final double drawScore;
//    private final double k;
//
//
//    public InverseLogDistanceWinScoreStrategy(double winScore, double drawScore, double k) {
//        this.winScore = winScore; //1
//        this.drawScore = drawScore; //0.5
//        this.k = k; //2
//    }
//
//    @Override
//    public double winScore(int distanceFromFinalState) {
//        return distanceFromFinalState == 1 ? 500 : calculateScore(winScore, distanceFromFinalState);
//    }
//
//    @Override
//    public double loseScore(int distanceFromFinalState) {
//        return -winScore(distanceFromFinalState);
//    }
//
//    @Override
//    public double drawScore(int distanceFromFinalState) {
//        return calculateScore(drawScore, distanceFromFinalState);
//    }
//
//    private double calculateScore(double score, int distanceFromFinalState){
//        return k * score / Math.log10(distanceFromFinalState);
//    }
//}
