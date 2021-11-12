package it.ai;

import it.ai.agents.Agent;
import it.ai.agents.MctsAgent;
import it.ai.client.TablutClient;
import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.tablut.ashton.AshtonTablutGame;
import it.ai.montecarlo.MonteCarlo;
import it.ai.montecarlo.strategies.bestaction.RobustChildStrategy;
import it.ai.montecarlo.strategies.score.Ucb1SelectionScoreStrategy;
import it.ai.montecarlo.strategies.winscore.DefaultWinScoreStrategy;
import it.ai.players.AgentPlayer;
import it.ai.protocol.State;
import it.ai.protocol.Turn;
import it.ai.util.AshtonMapper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger(Main.class.getName());
        String playerName = "AI";
        String playerTeam = Turn.BLACK;

        configureLogger();

        try {
            Game game = new AshtonTablutGame(0);
            MonteCarlo mcts = new MonteCarlo(game,
                    new Ucb1SelectionScoreStrategy(2),
                    new RobustChildStrategy(),
                    new DefaultWinScoreStrategy());
            Agent agent = new MctsAgent(game, mcts, 50);
            Player player = new AgentPlayer(playerName, playerTeam, agent);
//            Action bestAction = player.getAction(game.start());
//            Logger.getLogger(Main.class.getName()).info(bestAction.toString());
//            Player player = new RandomPlayer(playerTeam);

            Mapper mapper = new AshtonMapper();
            TablutClient client = new TablutClient(player, mapper, "127.0.0.1");
            client.run();

            State state = client.getState();
            it.ai.game.State gameState = mapper.mapToGameState(state);
            agent.updateState(gameState);

            Iterable<Action> actions = agent.getActions();
            logger.info("Action history:\n" + actions);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception was thrown", e);
        }
    }

    private static void configureLogger() throws IOException {
        String logFolder = "logs";
        ensureDirectoryExists(logFolder);

        Logger logger = Logger.getLogger("");
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.setLevel(Level.ALL);

        String filename = logFolder + File.separator + new Date().getTime() + ".log";
        FileHandler fileHandler = new FileHandler(filename);
        fileHandler.setLevel(Level.FINE);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        logger.addHandler(consoleHandler);
    }

    private static void ensureDirectoryExists(String dir) {
        File directory = new File(dir);
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();
    }
}
