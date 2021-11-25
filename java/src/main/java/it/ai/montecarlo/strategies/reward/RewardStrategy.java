package it.ai.montecarlo.strategies.reward;

public interface RewardStrategy {
    double winReward();

    double drawReward();

    double loseReward();

}
