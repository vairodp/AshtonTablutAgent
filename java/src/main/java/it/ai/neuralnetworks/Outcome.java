package it.ai.neuralnetworks;

import lombok.Getter;

@Getter
public class Outcome {
    private final Integer winner;
    private final double probability;

    public Outcome(Integer winner, double probability) {
        this.winner = winner;
        this.probability = probability;
    }
}
