package it.ai.montecarlo;

import it.ai.game.State;
import lombok.Getter;

import java.util.Optional;

public class NeuralNetworkMonteCarlo extends MCTSDecorator {
    private final double threshold;

    public NeuralNetworkMonteCarlo(AbstractMCTS mcts, double threshold) {
        super(mcts);
        this.threshold = threshold;
    }

    @Override
    protected Optional<Integer> evaluateWinner(State state) {
        NeuralNetworkOutcome outcome = executeNN(state); //TODO: pag 11
        if (outcome.getProbability() >= threshold)
            return outcome.getWinner();

        return super.evaluateWinner(state);
    }

    private NeuralNetworkOutcome executeNN(State state) {
//        return new NeuralNetworkOutcome(probability, winner);
        return null;
    }

    @Getter
    private static class NeuralNetworkOutcome {
        private final double probability;
        private final Optional<Integer> winner;

        private NeuralNetworkOutcome(double probability, Optional<Integer> winner) {
            this.probability = probability;
            this.winner = winner;
        }
    }
}

