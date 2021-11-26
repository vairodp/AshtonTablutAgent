package it.ai.montecarlo.phases;

import it.ai.game.Game;
import it.ai.montecarlo.MonteCarloNode;
import it.ai.montecarlo.RewardHelper;
import it.ai.montecarlo.strategies.reward.RewardStrategy;

/***
 * Phase 4, Backpropagation: Update ancestor statistics.
 */
public class Backpropagation {
    protected final Game game;
    protected final RewardStrategy rewardStrategy;

    public Backpropagation(Game game, RewardStrategy rewardStrategy) {
        this.game = game;
        this.rewardStrategy = rewardStrategy;
    }

    public void run(MonteCarloNode node, Iterable<Integer> winners) {
        for (int winner : winners) {
            run(node, winner);
        }
    }

    public void run(MonteCarloNode node, int winner) {
        int parentPlayer = game.previousPlayer(winner);

        while (node != null) {
            node.visit();
            update(node, winner, parentPlayer);

//            distance.increment();
            node = node.getParent();
        }
    }

    protected void update(MonteCarloNode node, int winner, int parentPlayer) {
        updateReward(node, winner, parentPlayer);
    }

    private void updateReward(MonteCarloNode node, int winner, int parentPlayer) {
        double reward = RewardHelper.getReward(rewardStrategy, node, winner, parentPlayer);
        node.addReward(reward);
    }


}
