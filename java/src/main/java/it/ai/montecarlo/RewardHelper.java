package it.ai.montecarlo;

import it.ai.constants.Constants;
import it.ai.game.State;
import it.ai.montecarlo.strategies.reward.RewardStrategy;

public final class RewardHelper {
    public static double getReward(RewardStrategy rewardStrategy, State state, int winner, int parentPlayer) {
        if (winner == Constants.Outcome.DRAW)
            return rewardStrategy.drawReward();

        //Score of child node is used by the parent.
        //Therefore, we increment the child node score if the parent player has won.
        if (state.isPlayerTurn(parentPlayer))
            return rewardStrategy.winReward();

        return rewardStrategy.loseReward();
    }
}
