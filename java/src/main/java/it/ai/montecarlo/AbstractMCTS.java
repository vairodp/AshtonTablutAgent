package it.ai.montecarlo;

import it.ai.game.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public abstract class AbstractMCTS implements MCTS {

    @Getter
    @Setter
    protected MonteCarloNode rootNode;

    protected abstract void createRootNode(State state);

    /***
     * Phase 1, Selection: Select until not fully expanded OR leaf.
     */
    protected abstract MonteCarloNode selection();

    /***
     * Phase 2, Expansion: Expand a random unexpanded child node.
     */
    protected abstract MonteCarloNode expansion(MonteCarloNode node);

    /***
     * Phase 3, Simulation: Play game to terminal state using random actions, return winner.
     */
    protected abstract Optional<Integer> simulation(MonteCarloNode node);

    protected abstract Optional<Integer> evaluateWinner(State state);

    /***
     * Phase 4, Backpropagation: Update ancestor statistics.
     */
    protected abstract void backpropagation(MonteCarloNode node, int winner);

}
