package it.ai.montecarlo.phases;

import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.strategies.reward.RewardStrategy;
import it.ai.util.MathUtils;
import it.ai.util.RandomUtils;

import java.util.List;

public class HeuristicSimulation extends Simulation {
    private final HeuristicEvaluation heuristic;
    private final RewardStrategy rewardStrategy;
    private final int actionsToEvaluate;

    public HeuristicSimulation(Game game, HeuristicEvaluation heuristic, RewardStrategy rewardStrategy, int actionsToEvaluate) {
        super(game);
        this.heuristic = heuristic;
        this.rewardStrategy = rewardStrategy;
        this.actionsToEvaluate = actionsToEvaluate;
    }

    @Override
    protected Action chooseAction(State state, List<Action> validActions) {
        int n = Math.min(actionsToEvaluate, validActions.size());
        validActions = RandomUtils.choice(validActions, n);

        return MathUtils.argmax(validActions, action -> {
            State newState = game.nextState(state, action);
            return StateEvaluationHelper.evaluateState(game, rewardStrategy, heuristic, newState);
        });
    }
}