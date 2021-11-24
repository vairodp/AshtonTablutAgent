package it.ai.montecarlo;

import it.ai.Main;
import it.ai.constants.Constants;
import it.ai.game.State;
import it.ai.neuralnetworks.Outcome;
import it.ai.neuralnetworks.ValueNeuralNetwork;

import java.util.Optional;
import java.util.logging.Logger;

public class NeuralNetworkMonteCarlo extends MCTSDecorator {
    Logger logger = Logger.getLogger(NeuralNetworkMonteCarlo.class.getName());

    private final ValueNeuralNetwork blackNetwork;
    private final ValueNeuralNetwork whiteNetwork;
    private final double threshold;

    public NeuralNetworkMonteCarlo(AbstractMCTS mcts, ValueNeuralNetwork blackNetwork, ValueNeuralNetwork whiteNetwork, double threshold) {
        super(mcts);
        this.blackNetwork = blackNetwork;
        this.whiteNetwork = whiteNetwork;
        this.threshold = threshold;
    }

    @Override
    protected Optional<Integer> evaluateWinner(State state) {
        Outcome outcome = state.isPlayerTurn(Constants.Player.WHITE)
                ? whiteNetwork.predict(state)
                : blackNetwork.predict(state);

        if (outcome.getProbability() >= threshold) {
            logger.fine("Outcome " + outcome.getWinner() + ", " + outcome.getProbability());
            return Optional.of(outcome.getWinner());
        }

        return super.evaluateWinner(state);
    }
}

