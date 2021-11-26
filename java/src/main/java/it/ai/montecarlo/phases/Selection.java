package it.ai.montecarlo.phases;

import it.ai.game.Action;
import it.ai.montecarlo.MonteCarloNode;
import it.ai.montecarlo.strategies.selection.MonteCarloSelectionScoreStrategy;
import it.ai.util.MathUtils;

/***
 * Phase 1, Selection: Select until not fully expanded OR leaf.
 */
public class Selection {
    protected final MonteCarloSelectionScoreStrategy selectionScoreStrategy;

    public Selection(MonteCarloSelectionScoreStrategy selectionScoreStrategy) {
        this.selectionScoreStrategy = selectionScoreStrategy;
    }

    public MonteCarloNode selection(MonteCarloNode rootNode) {
        MonteCarloNode node = rootNode;

        while (node.isFullyExpanded() && !node.isLeaf()) {
            Action bestAction = applySelectionPolicy(node);
            node = node.getChildNode(bestAction);
        }

        return node;
    }


    protected Action applySelectionPolicy(MonteCarloNode node) {
        return MathUtils.argmax(node.getAllActions()::iterator,
                action -> selectionScoreStrategy.score(node.getChildNode(action)));
    }
}
