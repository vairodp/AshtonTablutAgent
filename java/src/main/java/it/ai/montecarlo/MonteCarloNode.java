package it.ai.montecarlo;

import it.ai.game.Action;
import it.ai.game.State;
import it.ai.montecarlo.strategies.score.MonteCarloSelectionScoreStrategy;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/***
 * Class representing a node in the search tree. Stores tree search stats.
 */
public class MonteCarloNode {
    static final class MonteCarloChild {
        private final Action action;
        private final MonteCarloNode node;

        public MonteCarloChild(Action action) {
            this(action, null);
        }

        public MonteCarloChild(Action action, MonteCarloNode node) {
            this.action = action;
            this.node = node;
        }

        public Action getAction() {
            return this.action;
        }

        public MonteCarloNode getNode() {
            return this.node;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof MonteCarloChild)) return false;

            MonteCarloChild that = (MonteCarloChild) o;

            return new EqualsBuilder().append(action, that.action).append(node, that.node).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(action).append(node).toHashCode();
        }

        @Override
        public String toString() {
            return "MonteCarloChild{" +
                    "action=" + action +
                    ", node=" + node +
                    '}';
        }
    }

    private final Action action;
    private final State state;

    // MonteCarlo stuff
    int numberOfSimulations = 0;
    double winScore = 0;

    // Rave stuff
    int numberOfRave = 0;
    double raveScore = 0;

    // Tree stuff
    private final MonteCarloNode parent;
    @Getter
    private final Map<Action, MonteCarloChild> children = new HashMap<>();

    public MonteCarloNode(MonteCarloNode parent, Action action, State state, Iterable<Action> unexpandedActions) {
        this.action = action;
        this.state = state;
        this.parent = parent;

        for (Action a : unexpandedActions) {
            children.put(a, new MonteCarloChild(a));
        }
    }

    /***
     * Get the MonteCarloNode corresponding to the given action.
     */
    public MonteCarloNode getChildNode(Action action) {
        MonteCarloChild child = children.get(action);

        if (child == null)
            throw new RuntimeException("No such action!");
        if (child.getNode() == null)
            throw new RuntimeException("Child is not expanded!");

        return child.getNode();
    }

    /***
     * Expand the specified child action and return the new child node.
     *           Add the node to the array of children nodes.
     *           Remove the action from the array of unexpanded plays.
     */
    public MonteCarloNode expand(Action action, State childState, Iterable<Action> childUnexpandedMoves) {
        if (!children.containsKey(action)) throw new RuntimeException("No such action!");

        MonteCarloNode childNode = new MonteCarloNode(this, action, childState, childUnexpandedMoves);
        children.put(action, new MonteCarloChild(action, childNode));

        return childNode;
    }

    /***
     * Get all legal moves from this node.
     */
    public Stream<Action> getAllActions() {
        return children.values().stream().map(MonteCarloChild::getAction);
    }

    /***
     * Get all unexpanded legal actions from this node.
     */
    public Stream<Action> getUnexpandedActions() {
        return children.values().stream().filter(child -> child.getNode() == null).map(MonteCarloChild::getAction);
    }

    public Stream<MonteCarloNode> getExpandedNodes() {
        return children.values().stream().map(MonteCarloChild::getNode).filter(Objects::nonNull);
    }

    public boolean isFullyExpanded() {
        return children.values().stream().allMatch(child -> child.getNode() != null);
    }

    /***
     * Whether self node is terminal in the game tree, NOT INCLUSIVE of termination due to winning.
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    public double score(MonteCarloSelectionScoreStrategy scoreStrategy) {
        return scoreStrategy.score(this);
    }


    public Action getAction() {
        return this.action;
    }

    public State getState() {
        return this.state;
    }

    public MonteCarloNode getParent() {
        return this.parent;
    }

    public int numberOfSimulations() {
        return this.numberOfSimulations;
    }

    public double winScore() {
        return this.winScore;
    }

    public int numberOfRave() {
        return this.numberOfRave;
    }

    public double raveScore() {
        return this.raveScore;
    }
}
