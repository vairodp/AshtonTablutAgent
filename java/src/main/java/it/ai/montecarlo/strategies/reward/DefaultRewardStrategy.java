package it.ai.montecarlo.strategies.reward;

public class DefaultRewardStrategy implements RewardStrategy {
    @Override
    public double winReward() {
        return 1;
    }

    @Override
    public double drawReward() {
        return 0.2;
    }

    @Override
    public double loseReward() {
        return 0;
    }

}
