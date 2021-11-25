package it.ai.montecarlo;

import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.strategies.bestaction.MonteCarloBestActionStrategy;
import it.ai.montecarlo.strategies.selection.MonteCarloSelectionScoreStrategy;
import it.ai.montecarlo.strategies.reward.RewardStrategy;

import java.util.Optional;

public class MCTSMinMax extends MCTSImpl {
    private final HeuristicEvaluation heuristic;

    public MCTSMinMax(Game game, MonteCarloSelectionScoreStrategy selectionScoreStrategy, MonteCarloBestActionStrategy bestActionStrategy, RewardStrategy rewardStrategy, HeuristicEvaluation heuristic) {
        super(game, selectionScoreStrategy, bestActionStrategy, rewardStrategy);
        this.heuristic = heuristic;
    }

    @Override
    protected double evaluate(MonteCarloNode node) {
        State state = node.getState();
        int parentPlayer = game.previousPlayer(state.getTurn());
        return heuristic.evaluate(state, parentPlayer);
    }

    @Override
    protected void updateHeuristicValue(MonteCarloNode node, int parentPlayer) {
        Optional<Double> value = node.getExpandedNodes().map(n -> -n.getHeuristicValue()).max(Double::compare);
        value.ifPresent(node::setHeuristicValue);
    }
}
