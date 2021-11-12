package it.ai;

import it.ai.agents.Agent;
import it.ai.agents.MctsAgent;
import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.tablut.ashton.AshtonTablutGame;
import it.ai.montecarlo.MonteCarlo;
import it.ai.montecarlo.strategies.bestaction.RobustChildStrategy;
import it.ai.montecarlo.strategies.score.Ucb1SelectionScoreStrategy;
import it.ai.montecarlo.strategies.winscore.DefaultWinScoreStrategy;
import it.ai.players.AgentPlayer;
import it.ai.protocol.Turn;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String playerName = "AI";
        String playerTeam = Turn.BLACK;

        Game game = new AshtonTablutGame(0);


        MonteCarlo mcts = new MonteCarlo(game,
                new Ucb1SelectionScoreStrategy(2),
                new RobustChildStrategy(),
                new DefaultWinScoreStrategy()
//                new InverseLogDistanceWinScoreStrategy(1, 0.5, 2)

        );
        Agent agent = new MctsAgent(game, mcts, 50);
        Player player = new AgentPlayer(playerName, playerTeam, agent);
        Action bestAction = player.getAction(game.start());
//            Logger.getLogger(Main.class.getName()).info(bestAction.toString());
//            Player player = new RandomPlayer(playerTeam);

//        TablutClient client = new TablutClient(player, new AshtonMapper(), "127.0.0.1");
//        client.run();
    }
}