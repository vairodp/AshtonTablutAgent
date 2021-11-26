package it.ai.montecarlo.phases;

import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.MonteCarloNode;
import it.ai.util.RandomUtils;

import java.util.List;
import java.util.Optional;

/***
 * Phase 3, Simulation: Play game to terminal state using random actions, return winner.
 */
public class Simulation {
    protected final Game game;

    public Simulation(Game game) {
        this.game = game;
    }

    public Iterable<Integer> run(MonteCarloNode node) {
        int winner = runSingle(node);
        return List.of(winner);
    }

    protected Integer runSingle(MonteCarloNode node) {
        State state = node.getState();
        Optional<Integer> winner = game.getWinner(state);

        while (!winner.isPresent()) {
            List<Action> validActions = game.getValidActions(state);
            if (validActions.isEmpty())
                throw new RuntimeException("No valid actions for state" + state);

            Action action = RandomUtils.choice(validActions);
            state = game.nextState(state, action);
            winner = estimateWinner(state);
//            distance.increment();
        }
        return winner.get();
    }


    protected Optional<Integer> estimateWinner(State state) {
        return game.getWinner(state);
    }
}
