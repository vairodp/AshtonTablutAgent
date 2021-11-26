package it.ai.montecarlo;

import it.ai.constants.Constants;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.montecarlo.phases.Simulation;
import it.ai.neuralnetworks.Outcome;
import it.ai.neuralnetworks.ValueNeuralNetwork;

import java.util.Optional;
import java.util.logging.Logger;

public class NeuralNetworkSimulation extends Simulation {
    Logger logger = Logger.getLogger(NeuralNetworkSimulation.class.getName());

    private final ValueNeuralNetwork blackNetwork;
    private final ValueNeuralNetwork whiteNetwork;
    private final double threshold;

    public NeuralNetworkSimulation(Game game, ValueNeuralNetwork blackNetwork, ValueNeuralNetwork whiteNetwork, double threshold) {
        super(game);
        this.blackNetwork = blackNetwork;
        this.whiteNetwork = whiteNetwork;
        this.threshold = threshold;
    }

    @Override
    protected Optional<Integer> estimateWinner(State state) {
        Outcome outcome = state.isPlayerTurn(Constants.Player.WHITE)
                ? whiteNetwork.predict(state)
                : blackNetwork.predict(state);

        if (outcome.getProbability() >= threshold) {
            logger.fine("Outcome " + outcome.getWinner() + ", " + outcome.getProbability());
            return Optional.of(outcome.getWinner());
        }

        return super.estimateWinner(state);
    }
}

