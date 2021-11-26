package it.ai.montecarlo;

import it.ai.game.Action;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class MonteCarloStats {
    @Getter
    private final int numberOfSimulations;
    @Getter
    private final double rewards;
    @Getter
    private final double heuristicValue;
    @Getter
    private final List<MonteCarloNodeStats> children = new ArrayList<>();

    public MonteCarloStats(int numberOfSimulations, double rewards, double heuristicValue) {
        this.numberOfSimulations = numberOfSimulations;
        this.rewards = rewards;
        this.heuristicValue = heuristicValue;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("MonteCarloStats{");
        stringBuilder.append("numberOfSimulations=").append(numberOfSimulations)
                .append(", rewards=").append(rewards)
                .append(", heuristicValue=").append(heuristicValue)
                .append(",\nchildren=[");

        for (MonteCarloNodeStats child : children)
            stringBuilder.append('\n').append(child).append(',');
        stringBuilder.append("\n]");

        return stringBuilder.append('}').toString();
    }

    public static final class MonteCarloNodeStats {
        @Getter
        private final Action action;
        @Getter
        private final int numberOfSimulations;
        @Getter
        private final double rewards;
        @Getter
        private final double heuristicValue;

        public MonteCarloNodeStats(Action action, int numberOfSimulations, double rewards, double heuristicValue) {
            this.action = action;
            this.numberOfSimulations = numberOfSimulations;
            this.rewards = rewards;
            this.heuristicValue = heuristicValue;
        }

        public MonteCarloNodeStats(Action action) {
            this(action, 0, 0, 0);
        }

        @Override
        public String toString() {
            return "MonteCarloNodeStats{" +
                    "action=" + action +
                    ", numberOfSimulations=" + numberOfSimulations +
                    ", rewards=" + rewards +
                    ", heuristicValue=" + heuristicValue +
                    '}';
        }
    }
}
