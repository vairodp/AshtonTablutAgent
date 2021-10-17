package it.ai.montecarlo;

import it.ai.game.Action;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public final class MonteCarloStats {
    private final int numberOfSimulations;
    private final double winScore;
    private final int numberOfRave;
    private final double raveScore;

    private final Collection<MonteCarloNodeStats> children = new ArrayList<>();

    public MonteCarloStats(int numberOfSimulations, double winScore, int numberOfRave, double raveScore) {
        this.numberOfSimulations = numberOfSimulations;
        this.winScore = winScore;
        this.numberOfRave = numberOfRave;
        this.raveScore = raveScore;
    }

    public int getNumberOfSimulations() {
        return this.numberOfSimulations;
    }

    public double getWinScore() {
        return this.winScore;
    }

    public int getNumberOfRave() {
        return this.numberOfRave;
    }

    public double getRaveScore() {
        return this.raveScore;
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
                .append(winScore, that.winScore)
                .append(numberOfRave, that.numberOfRave)
                .append(raveScore, that.raveScore)
                .append(children, that.children).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(numberOfSimulations).append(winScore)
                .append(numberOfRave).append(raveScore).append(children).toHashCode();
    }

    @Override
    public String toString() {
        return "MonteCarloStats{" +
                "numberOfSimulations=" + numberOfSimulations +
                ", winScore=" + winScore +
                ", numberOfRave=" + numberOfRave +
                ", raveScore=" + raveScore +
                ", children=" + children +
                '}';
    }

    public static final class MonteCarloNodeStats {
        private final Action action;
        private final int numberOfSimulations;
        private final double winScore;
        private final int numberOfRave;
        private final double raveScore;

        public MonteCarloNodeStats(Action action, int numberOfSimulations, double winScore, int numberOfRave, double raveScore) {
            this.action = action;
            this.numberOfSimulations = numberOfSimulations;
            this.winScore = winScore;
            this.numberOfRave = numberOfRave;
            this.raveScore = raveScore;
        }

        public MonteCarloNodeStats(Action action) {
            this(action, 0, 0, 0, 0);
        }

        public Action getAction() {
            return this.action;
        }

        public int getNumberOfSimulations() {
            return this.numberOfSimulations;
        }

        public double getWinScore() {
            return this.winScore;
        }

        public int getNumberOfRave() {
            return this.numberOfRave;
        }

        public double getRaveScore() {
            return this.raveScore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof MonteCarloNodeStats)) return false;

            MonteCarloNodeStats that = (MonteCarloNodeStats) o;

            return new EqualsBuilder().append(numberOfSimulations, that.numberOfSimulations).append(winScore, that.winScore).append(numberOfRave, that.numberOfRave).append(raveScore, that.raveScore).append(action, that.action).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(action).append(numberOfSimulations).append(winScore).append(numberOfRave).append(raveScore).toHashCode();
        }

        @Override
        public String toString() {
            return "MonteCarloNodeStats{" +
                    "action=" + action +
                    ", numberOfSimulations=" + numberOfSimulations +
                    ", winScore=" + winScore +
                    ", numberOfRave=" + numberOfRave +
                    ", raveScore=" + raveScore +
                    '}';
        }
    }
}
