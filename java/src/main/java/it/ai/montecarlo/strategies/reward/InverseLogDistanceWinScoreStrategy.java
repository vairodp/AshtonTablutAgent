//package it.ai.montecarlo.strategies.reward;
//
//public class InverseLogDistanceWinScoreStrategy implements RewardStrategy {
//    private final double winReward;
//    private final double drawReward;
//    private final double k;
//
//
//    public InverseLogDistanceWinScoreStrategy(double winReward, double drawReward, double k) {
//        this.winReward = winReward; //1
//        this.drawReward = drawReward; //0.5
//        this.k = k; //2
//    }
//
//    @Override
//    public double winReward(int distanceFromFinalState) {
//        return distanceFromFinalState == 1 ? 500 : calculateScore(winReward, distanceFromFinalState);
//    }
//
//    @Override
//    public double loseReward(int distanceFromFinalState) {
//        return -winReward(distanceFromFinalState);
//    }
//
//    @Override
//    public double drawReward(int distanceFromFinalState) {
//        return calculateScore(drawReward, distanceFromFinalState);
//    }
//
//    private double calculateScore(double score, int distanceFromFinalState) {
//        return k * score / Math.log10(distanceFromFinalState);
//    }
//}
