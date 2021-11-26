package it.ai.montecarlo;

import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.phases.Backpropagation;
import it.ai.montecarlo.phases.Expansion;
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
        public MonteCarloNode expansion(MonteCarloNode node) {
            node = super.expansion(node);
            node.setHeuristicValue(evaluateState(node));
            return node;
        }

        private double evaluateState(MonteCarloNode node) {
            State state = node.getState();
            int parentPlayer = game.previousPlayer(state.getTurn());
            Optional<Integer> winner = game.getWinner(state);

            return winner.map(winnerPlayer -> RewardHelper.getReward(rewardStrategy, node, winnerPlayer, parentPlayer))
                    .orElseGet(() -> heuristic.evaluate(state, parentPlayer));
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
            value.ifPresent(node::setHeuristicValue);
        }
    }
}
