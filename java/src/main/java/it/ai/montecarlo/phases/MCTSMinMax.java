package it.ai.montecarlo.phases;

import it.ai.game.Game;
import it.ai.montecarlo.MonteCarloNode;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.strategies.reward.RewardStrategy;

import java.util.Optional;
import java.util.stream.Stream;

public class MCTSMinMax {
    private final Game game;
    private final RewardStrategy rewardStrategy;
    private final HeuristicEvaluation heuristic;
    private final int player;

    public MCTSMinMax(Game game, RewardStrategy rewardStrategy, HeuristicEvaluation heuristic, int player) {
        this.game = game;
        this.rewardStrategy = rewardStrategy;
        this.heuristic = heuristic;
        this.player = player;
    }

    public MinMaxExpansion getExpansion() {
        return new MinMaxExpansion();
    }

    public MinMaxBackPropagation getBackPropagation() {
        return new MinMaxBackPropagation();
    }

    public class MinMaxExpansion extends Expansion {

        public MinMaxExpansion() {
            super(MCTSMinMax.this.game);
        }

        @Override
        public MonteCarloNode run(MonteCarloNode node) {
            node = super.run(node);
            double value = StateEvaluationHelper.evaluateState(game, rewardStrategy, heuristic, node.getState());
            node.setHeuristicValue(value);
            return node;
        }
    }

    public class MinMaxBackPropagation extends Backpropagation {

        public MinMaxBackPropagation() {
            super(MCTSMinMax.this.game, MCTSMinMax.this.rewardStrategy);
        }

        @Override
        protected void update(MonteCarloNode node, int winner, int parentPlayer) {
            super.update(node, winner, parentPlayer);
            updateHeuristicValue(node);
        }


        private void updateHeuristicValue(MonteCarloNode node) {
            Stream<Double> values = node.getExpandedNodes().map(MonteCarloNode::getHeuristicValue);
            Optional<Double> value = node.getState().isPlayerTurn(player)
                    ? values.max(Double::compare)
                    : values.min(Double::compare);
//            Optional<Double> value = values.min(Double::compare);
            value.ifPresent(node::setHeuristicValue);
        }
    }
}
