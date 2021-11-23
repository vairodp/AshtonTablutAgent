package it.ai;

import it.ai.game.Game;
import it.ai.game.State;
import it.ai.game.tablut.Action;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.ashton.AshtonTablutGame;
import it.ai.neuralnetworks.Outcome;
import it.ai.neuralnetworks.ValueNeuralNetwork;

public class Test {
    public static void main(String[] args) throws Exception {
//        String playerName = "AI";
//        String playerTeam = Turn.BLACK;
//
        Game game = new AshtonTablutGame(0);
//
//
//        AbstractMCTS mcts = new MCTSImpl(game,
//                new Ucb1SelectionScoreStrategy(2),
//                new RobustChildStrategy(),
//                new DefaultWinScoreStrategy()
////                new InverseLogDistanceWinScoreStrategy(1, 0.5, 2)
//
//        );
//        Agent agent = new MctsAgent(game, mcts, () -> new TimeoutTerminationCondition(2));
//        Player player = new AgentPlayer(playerName, playerTeam, agent);
//        Action bestAction = player.getAction(game.start());
//        var actions = agent.getActions();
//        System.out.println(actions);
//

//        ValueNeuralNetwork nn = new ValueNeuralNetwork("value_model_b_1.h5");
//        ValueNeuralNetwork nn = new ValueNeuralNetwork("value_model_b_1");
        State state = game.nextState(game.start(), new Action(Coords.F5, Coords.F6));
        state = game.nextState(state, new Action(Coords.F1, Coords.F4));
        state = game.nextState(state, new Action(Coords.E5, Coords.F5));
        state = game.nextState(state, new Action(Coords.F4, Coords.F1));
        state = game.nextState(state, new Action(Coords.F5, Coords.E5));

//        Outcome outcome = nn.predict(state);
//        System.out.println(outcome);
    }
}
