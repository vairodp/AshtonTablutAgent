package it.ai;

import it.ai.agents.Agent;
import it.ai.agents.MctsAgent;
import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.tablut.ashton.AshtonTablutGame;
import it.ai.montecarlo.MCTSMinMax;
import it.ai.montecarlo.heuristics.AggregateHeuristic;
import it.ai.montecarlo.heuristics.BlackAndWhiteHeuristic;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.heuristics.black.BlackAlive;
import it.ai.montecarlo.heuristics.black.BlackOnRhombus;
import it.ai.montecarlo.heuristics.black.BlackSurroundKing;
import it.ai.montecarlo.heuristics.black.WhiteEaten;
import it.ai.montecarlo.heuristics.white.*;
import it.ai.montecarlo.strategies.bestaction.MaxChildStrategy;
import it.ai.montecarlo.strategies.qvalue.HeuristicQValue;
import it.ai.montecarlo.strategies.qvalue.QEvaluation;
import it.ai.montecarlo.strategies.reward.DefaultRewardStrategy;
import it.ai.montecarlo.strategies.selection.Ucb1SelectionScoreStrategy;
import it.ai.montecarlo.termination.TimeoutTerminationCondition;
import it.ai.players.AgentPlayer;
import it.ai.protocol.Turn;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Profile {
    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger(Profile.class.getName());
        String playerName = "AI";
        String playerTeam = Turn.BLACK;
        int timeout_s = 10;

        double alpha = 0.4;
        double exploration = 1.4;
//        double networkThreshold = 0.6;

//        String blackNN = "value_model_b_1.h5";
//        String whiteNN = "value_model_b_1.h5";
        configureLogger();

        Game game = new AshtonTablutGame(0);

//        QEvaluation qEvaluation = new WinProbabilityQValue();
        QEvaluation qEvaluation = new HeuristicQValue(alpha);
        HeuristicEvaluation heuristicEvaluation = buildHeuristic();
//        AbstractMCTS mctsImpl = new MCTSImpl(game,
//                new Ucb1SelectionScoreStrategy(exploration, qEvaluation),
//                new MaxChildStrategy(),
//                new DefaultRewardStrategy());

        MCTSMinMax mcts = new MCTSMinMax(game,
                new Ucb1SelectionScoreStrategy(exploration, qEvaluation),
                new MaxChildStrategy(),
                new DefaultRewardStrategy(),
                heuristicEvaluation);

//        MCTS mcts = new NeuralNetworkMonteCarlo(mctsImpl,
//                new ValueNeuralNetwork(blackNN), new ValueNeuralNetwork(whiteNN), networkThreshold);

//            MCTS mcts = new MCTSRootParallelization(() -> new MCTSImpl(game,
//                    new Ucb1SelectionScoreStrategy(2),
//                    new RobustChildStrategy(),
//                    new DefaultWinScoreStrategy()), 4);

        Agent agent = new MctsAgent(game, mcts, () -> new TimeoutTerminationCondition(timeout_s));
        Player player = new AgentPlayer(playerName, playerTeam, agent);


        try {
            Action bestAction = player.getAction(game.start());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception was thrown", e);
        } finally {
            Iterable<Action> actions = agent.getActions();
            logger.info("Action history:\n" + actions);
        }
    }

    private static HeuristicEvaluation buildHeuristic() {
        AggregateHeuristic whiteHeuristic = new AggregateHeuristic(new AggregateHeuristic.WeightedHeuristic[]{
                new AggregateHeuristic.WeightedHeuristic(2, new WhiteWellPositioned()),
                new AggregateHeuristic.WeightedHeuristic(20, new BlackEaten()),
                new AggregateHeuristic.WeightedHeuristic(35, new WhiteAlive()),
                new AggregateHeuristic.WeightedHeuristic(18, new KingEscapes()),
                new AggregateHeuristic.WeightedHeuristic(7, new RemainingToSurroundKing()),
                new AggregateHeuristic.WeightedHeuristic(18, new KingProtection())
        });

        AggregateHeuristic blackHeuristic = new AggregateHeuristic(new AggregateHeuristic.WeightedHeuristic[]{
                new AggregateHeuristic.WeightedHeuristic(30, new BlackAlive()),
                new AggregateHeuristic.WeightedHeuristic(45, new WhiteEaten()),
                new AggregateHeuristic.WeightedHeuristic(20, new BlackSurroundKing()),
                new AggregateHeuristic.WeightedHeuristic(5, new BlackOnRhombus()),

        });

        return new BlackAndWhiteHeuristic(blackHeuristic, whiteHeuristic);
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
