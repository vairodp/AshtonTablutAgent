package it.ai.montecarlo;

import it.ai.game.Action;
import it.ai.game.State;
import it.ai.tree.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.stream.Stream;

/***
 * Class representing a node in the search tree. Stores tree search stats.
 */
public class MonteCarloNode extends Node<State, Action> {
    @Getter
    int numberOfSimulations = 0;
    @Getter
    double rewards = 0;
    @Getter
    @Setter
    double heuristicValue = 0;

    int nodeToExpand = 0;

    public MonteCarloNode(MonteCarloNode parent, Action action, State state, Iterable<Action> unexpandedActions) {
        super(parent, state, action);

        for (Action a : unexpandedActions) {
            addChild(a);
            nodeToExpand++;
        }
    }

    public boolean hasChildNode(Action action) {
        Child<State, Action> child = getChild(action);
        return child != null && child.getNode() != null;
    }

    /***
     * Get the MonteCarloNode corresponding to the given action.
     */
    public MonteCarloNode getChildNode(Action action) {
        Child<State, Action> child = getChild(action);

        if (child == null)
            throw new RuntimeException("No such action!");
        if (child.getNode() == null)
            throw new RuntimeException("Child is not expanded!");

        return (MonteCarloNode) child.getNode();
    }

    /***
     * Expand the specified child action and return the new child node.
     *           Add the node to the array of children nodes.
     *           Remove the action from the array of unexpanded plays.
     */
    public MonteCarloNode expand(Action action, State childState, Iterable<Action> childUnexpandedMoves) {
        Child<State, Action> child = getChild(action);
        if (child == null) throw new RuntimeException("No such action!");
        if (child.getNode() != null) throw new RuntimeException("Node already expanded!");

        MonteCarloNode childNode = new MonteCarloNode(this, action, childState, childUnexpandedMoves);
        addChild(childNode);
        nodeToExpand--;

        return childNode;
    }

    public void visit() {
        numberOfSimulations++;
    }

    public void addReward(double reward) {
        rewards += reward;
    }

    /***
     * Get all legal moves from this node.
     */
    public Stream<Action> getAllActions() {
        return getChildren().map(Child::getLink);
    }

    /***
     * Get all unexpanded legal actions from this node.
     */
    public Stream<Action> getUnexpandedActions() {
        return getChildren().filter(child -> child.getNode() == null).map(Child::getLink);
    }

    public Stream<MonteCarloNode> getExpandedNodes() {
        return getChildren().map(child -> (MonteCarloNode) child.getNode()).filter(Objects::nonNull);
    }

    public boolean isFullyExpanded() {
        return nodeToExpand == 0;
    }

    /***
     * Whether self node is terminal in the game tree, NOT INCLUSIVE of termination due to winning.
     */
    @Override
    public boolean isLeaf() {
        return super.isLeaf();
    }

    @Override
    public Stream<Child<State, Action>> getChildren() {
        return super.getChildren();
    }

    public Action getAction() {
        return this.link;
    }

    public State getState() {
        return getKey();
    }

    public MonteCarloNode getParent() {
        return (MonteCarloNode) this.parent;
    }
}
