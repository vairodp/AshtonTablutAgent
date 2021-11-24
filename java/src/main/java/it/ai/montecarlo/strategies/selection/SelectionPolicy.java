package it.ai.montecarlo.strategies.selection;

import it.ai.game.Action;
import it.ai.montecarlo.MonteCarloNode;

public interface SelectionPolicy {
    Action selectAction(MonteCarloNode node);
}
