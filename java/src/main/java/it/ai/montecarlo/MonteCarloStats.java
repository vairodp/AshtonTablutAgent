package it.ai.montecarlo;

import it.ai.game.Action;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public final class MonteCarloStats {
    private final int numberOfSimulations;
    private final double actionValue;

    private final Collection<MonteCarloNodeStats> children = new ArrayList<>();

    public MonteCarloStats(int numberOfSimulations, double actionValue) {
        this.numberOfSimulations = numberOfSimulations;
        this.actionValue = actionValue;
    }

    public int getNumberOfSimulations() {
        return this.numberOfSimulations;
    }

    public double getActionValue() {
        return this.actionValue;
    }

    public Collection<MonteCarloNodeStats> getChildren() {
        return this.children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MonteCarloStats)) return false;

        MonteCarloStats that = (MonteCarloStats) o;

        return new EqualsBuilder()
                .append(numberOfSimulations, that.numberOfSimulations)
                .append(actionValue, that.actionValue)
                .append(children, that.children).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(numberOfSimulations).append(actionValue)
                .append(children).toHashCode();
    }

    @Override
    public String toString() {
        return "MonteCarloStats{" +
                "numberOfSimulations=" + numberOfSimulations +
                ", actionValue=" + actionValue +
                ", children=" + children +
                '}';
    }

    public static final class MonteCarloNodeStats {
        private final Action action;
        private final int numberOfSimulations;
        private final double actionValue;

        public MonteCarloNodeStats(Action action, int numberOfSimulations, double actionValue) {
            this.action = action;
            this.numberOfSimulations = numberOfSimulations;
            this.actionValue = actionValue;
        }

        public MonteCarloNodeStats(Action action) {
            this(action, 0, 0);
        }

        public Action getAction() {
            return this.action;
        }

        public int getNumberOfSimulations() {
            return this.numberOfSimulations;
        }

        public double getActionValue() {
            return this.actionValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof MonteCarloNodeStats)) return false;

            MonteCarloNodeStats that = (MonteCarloNodeStats) o;

            return new EqualsBuilder().append(numberOfSimulations, that.numberOfSimulations).append(actionValue, that.actionValue).append(action, that.action).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(action).append(numberOfSimulations).append(actionValue).toHashCode();
        }

        @Override
        public String toString() {
            return "MonteCarloNodeStats{" +
                    "action=" + action +
                    ", numberOfSimulations=" + numberOfSimulations +
                    ", actionValue=" + actionValue +
                    '}';
        }
    }
}
