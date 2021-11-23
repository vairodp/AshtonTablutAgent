package it.ai.neuralnetworks;

import it.ai.constants.Constants;
import it.ai.game.State;
import it.ai.game.tablut.TablutState;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ValueNeuralNetwork {
    private final MultiLayerNetwork model;
    private static final int[] winnerMap = {
            Constants.Outcome.BLACK_WIN,
            Constants.Outcome.WHITE_WIN,
            Constants.Outcome.DRAW
    };
    private static final Map<Integer, Double> pawnsMap = new HashMap<>() {{
        put(-1, 0d);
        put(10, 0d);
        put(0, 1 / 3d);
        put(1, 2 / 3d);
        put(100, 1d);
    }};

    public ValueNeuralNetwork(String filename) throws IOException, UnsupportedKerasConfigurationException, InvalidKerasConfigurationException {
        model = KerasModelImport.importKerasSequentialModelAndWeights(filename);
    }

    public Outcome predict(State state) {
        INDArray input = makeInput(getBoard(state));
        INDArray output = model.output(input);

        return mapToOutcome(output);
    }

    private int[][] getBoard(State s) {
        TablutState state = (TablutState) s;
        return state.getBoard().toArray();
    }

    private INDArray makeInput(int[][] board) {
        int boardSize = board.length * board[0].length;
        double[] input = new double[boardSize];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                double value = pawnsMap.get(board[i][j]);
                input[i * board[0].length + j] = value;
            }
        }

        return Nd4j.create(input, 1, boardSize);
    }

    private Outcome mapToOutcome(INDArray output) {
        int i = output.argMax().getInt(0);
        double probability = output.getDouble(i);
        int winner = winnerMap[i];
        return new Outcome(winner, probability);
    }
}
