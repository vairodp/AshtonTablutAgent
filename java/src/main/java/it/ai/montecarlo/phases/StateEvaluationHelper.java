package it.ai.montecarlo.phases;

import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.RewardHelper;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.strategies.reward.RewardStrategy;

import java.util.Optional;

public class StateEvaluationHelper {
    public static double evaluateState(Game game, RewardStrategy rewardStrategy,
                                       HeuristicEvaluation heuristic, State state) {
        int parentPlayer = game.previousPlayer(state.getTurn());
        Optional<Integer> winner = game.getWinner(state);

        return winner.map(winnerPlayer -> RewardHelper.getReward(rewardStrategy, state, winnerPlayer, parentPlayer))
                .orElseGet(() -> heuristic.evaluate(state, parentPlayer));
    }
}
