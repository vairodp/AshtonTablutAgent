package it.ai.montecarlo.phases;

import it.ai.constants.Constants;
import it.ai.game.Game;
import it.ai.montecarlo.MonteCarloNode;

import java.util.List;

public class FakeSimulation extends Simulation {
    public FakeSimulation(Game game) {
        super(game);
    }

    @Override
    public Iterable<Integer> run(MonteCarloNode node) {
        return List.of(Constants.Outcome.DRAW);
    }

    @Override
    public Integer runSingle(MonteCarloNode node) {
        return Constants.Outcome.DRAW;
    }
}
