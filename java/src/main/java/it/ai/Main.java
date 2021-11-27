package it.ai;

import it.ai.agents.Agent;
import it.ai.agents.MctsAgent;
import it.ai.client.TablutClient;
import it.ai.constants.Constants;
import it.ai.game.Action;
import it.ai.game.Game;
import it.ai.game.tablut.ashton.AshtonTablutGame;
import it.ai.montecarlo.IMCTS;
import it.ai.montecarlo.MCTS;
import it.ai.montecarlo.heuristics.AggregateHeuristic;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.heuristics.black.BlackAlive;
import it.ai.montecarlo.heuristics.black.BlackSurroundKing;
import it.ai.montecarlo.heuristics.black.BlockedKingEscapes;
import it.ai.montecarlo.heuristics.black.WhiteEaten;
import it.ai.montecarlo.heuristics.white.*;
import it.ai.montecarlo.phases.Simulation;
import it.ai.montecarlo.phases.*;
import it.ai.montecarlo.strategies.bestaction.MonteCarloBestActionStrategy;
import it.ai.montecarlo.strategies.bestaction.RobustChildStrategy;
import it.ai.montecarlo.strategies.qvalue.DynamicAlpha;
import it.ai.montecarlo.strategies.qvalue.HeuristicQValue;
import it.ai.montecarlo.strategies.qvalue.QEvaluation;
import it.ai.montecarlo.strategies.reward.DefaultRewardStrategy;
import it.ai.montecarlo.strategies.reward.RewardStrategy;
import it.ai.montecarlo.strategies.selection.MonteCarloSelectionScoreStrategy;
import it.ai.montecarlo.strategies.selection.Ucb1SelectionScoreStrategy;
import it.ai.montecarlo.termination.TimeoutTerminationCondition;
import it.ai.players.AgentPlayer;
import it.ai.protocol.State;
import it.ai.protocol.Turn;
import it.ai.util.AshtonMapper;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Main {
    private static Level loggerLevel = Level.ALL;

    private static int playerTeam = -1;
    private static String name = "taboletta";
    private static int timeout_s = 55;
    private static String serverIp = "localhost";
    private static boolean debug = false;


    private static void argParse(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("taboletta.sh").build()
                .defaultHelp(true);
        parser.addArgument("player")
                .choices("WHITE", "BLACK")
                .nargs(1);
        parser.addArgument("-t", "--timeout")
                .type(Integer.class)
                .help("Timeout in seconds")
                .setDefault(timeout_s);
        parser.addArgument("-s", "--server-ip")
                .setDefault(serverIp);
        parser.addArgument("-n", "--name")
                .help("Team name")
                .setDefault(name);
        parser.addArgument("--debug")
                .action(Arguments.storeTrue());
        Namespace res;
        try {
            res = parser.parseArgs(args);
            playerTeam = res.getString("player").equals("WHITE") ? Constants.Player.WHITE : Constants.Player.BLACK;
            name = res.getString("name");
            timeout_s = res.getInt("timeout");
            serverIp = res.getString("server_ip");
            debug = res.getBoolean("debug");
            loggerLevel = debug ? Level.ALL : Level.INFO;
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

    private static void printInfo() {
        System.out.println("Player: " + (playerTeam == Constants.Player.BLACK ? "BLACK" : "WHITE"));
        System.out.println("Timeout: " + timeout_s + " s");
        System.out.println("Server: " + serverIp);
        System.out.println("Debug mode: " + debug + "\n");
    }

    public static void main(String[] args) throws Exception {
        argParse(args);
        printInfo();
        configureLogger(loggerLevel);
        Logger logger = Logger.getLogger(Main.class.getName());

        String team = playerTeam == Constants.Player.BLACK ? Turn.BLACK : Turn.WHITE;

//        DynamicAlpha alpha = new IncreasingAlpha();
        DynamicAlpha alpha = new HeuristicQValue.ConstAlpha(0.6);
        double exploration = 1.4;

        Game game = new AshtonTablutGame(0);
//        MonteCarloBestActionStrategy bestActionStrategy = new MaxChildStrategy();
        MonteCarloBestActionStrategy bestActionStrategy = new RobustChildStrategy();
        RewardStrategy rewardStrategy = new DefaultRewardStrategy();

//        QEvaluation qEvaluation = new WinProbabilityQValue();
        QEvaluation qEvaluation = new HeuristicQValue(alpha);
        MonteCarloSelectionScoreStrategy selectionScoreStrategy = new Ucb1SelectionScoreStrategy(exploration, qEvaluation);

        HeuristicEvaluation blackHeuristic = getBlackHeuristic();
        HeuristicEvaluation whiteHeuristic = getWhiteHeuristic();
        HeuristicEvaluation heuristic = chooseHeuristic(blackHeuristic, whiteHeuristic, playerTeam);
//        HeuristicEvaluation heuristic = new BlackAndWhiteHeuristic(blackHeuristic, whiteHeuristic);

        MCTSMinMax minMax = new MCTSMinMax(game, rewardStrategy, heuristic, playerTeam);
        Selection selection = new Selection(selectionScoreStrategy);
//        Expansion expansion = new Expansion(game);
        Expansion expansion = minMax.getExpansion();
//        Simulation simulation = new it.ai.montecarlo.phases.Simulation(game);
//        Simulation baseSimulation =
//                new HeuristicSimulation(game, blackAndWhiteHeuristic, rewardStrategy, actionsToEvaluate);
        Simulation simulation = new ParallelSimulation(game);
//        Backpropagation backpropagation = new Backpropagation(game, rewardStrategy);
        Backpropagation backpropagation = minMax.getBackPropagation();


        IMCTS mcts = new MCTS(game, bestActionStrategy, selection, expansion, simulation, backpropagation);

//        MCTS mcts = new NeuralNetworkMonteCarlo(mctsImpl,
//                new ValueNeuralNetwork(blackNN), new ValueNeuralNetwork(whiteNN), networkThreshold);


        Agent agent = new MctsAgent(game, mcts, () -> new TimeoutTerminationCondition(timeout_s - 2));
        Player player = new AgentPlayer(name, team, agent);

        Mapper mapper = new AshtonMapper();
        TablutClient client = new TablutClient(player, mapper, serverIp);

        try {
            client.run();

            State state = client.getState();
            String winner = state.getTurn();
            logger.info("Winner: " + winner);
            state.setTurn(team);
            it.ai.game.State gameState = mapper.mapToGameState(state);
            agent.updateStateWithOpponentAction(gameState);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception was thrown", e);
        } finally {
            Iterable<Action> actions = agent.getActions();
            logger.info("Action history:\n" + actions);
        }
    }

    private static HeuristicEvaluation chooseHeuristic(
            HeuristicEvaluation blackHeuristic, HeuristicEvaluation whiteHeuristic, int player) {

        if (player == Constants.Player.WHITE)
            return whiteHeuristic;
        else return blackHeuristic;

//        return new BlackAndWhiteHeuristic(blackHeuristic, whiteHeuristic);
    }

    private static AggregateHeuristic getBlackHeuristic() {
        return new AggregateHeuristic(new AggregateHeuristic.WeightedHeuristic[]{
                new AggregateHeuristic.WeightedHeuristic(44, new BlackAlive()),
                new AggregateHeuristic.WeightedHeuristic(40, new WhiteEaten()),
                new AggregateHeuristic.WeightedHeuristic(10, new BlockedKingEscapes()),
                new AggregateHeuristic.WeightedHeuristic(6, new BlackSurroundKing()),
//                new AggregateHeuristic.WeightedHeuristic(2, new BlackOnRhombus()),

//                new AggregateHeuristic.WeightedHeuristic(35, new BlackAlive()),
//                new AggregateHeuristic.WeightedHeuristic(48, new WhiteEaten()),
//                new AggregateHeuristic.WeightedHeuristic(15, new BlackSurroundKing()),
//                new AggregateHeuristic.WeightedHeuristic(2, new BlackOnRhombus())
        });
    }

    private static HeuristicEvaluation getWhiteHeuristic() {
        return new AggregateHeuristic(new AggregateHeuristic.WeightedHeuristic[]{
                new AggregateHeuristic.WeightedHeuristic(5, new WhiteWellPositioned()),
                new AggregateHeuristic.WeightedHeuristic(27, new BlackEaten()),
                new AggregateHeuristic.WeightedHeuristic(35, new WhiteAlive()),
                new AggregateHeuristic.WeightedHeuristic(26, new KingEscapes()),
                new AggregateHeuristic.WeightedHeuristic(7, new RemainingToSurroundKing()),
//                new AggregateHeuristic.WeightedHeuristic(18, new KingProtection()),

//                new AggregateHeuristic.WeightedHeuristic(2, new WhiteWellPositioned()),
//                new AggregateHeuristic.WeightedHeuristic(20, new BlackEaten()),
//                new AggregateHeuristic.WeightedHeuristic(35, new WhiteAlive()),
//                new AggregateHeuristic.WeightedHeuristic(18, new KingEscapes()),
//                new AggregateHeuristic.WeightedHeuristic(7, new RemainingToSurroundKing()),
//                new AggregateHeuristic.WeightedHeuristic(18, new KingProtection())
        });
    }

    private static void configureLogger(Level minLevel) throws IOException {
        String logFolder = "logs";
        ensureDirectoryExists(logFolder);

        Logger logger = Logger.getLogger("");
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.setLevel(minLevel);

        String filename = logFolder + File.separator + new Date().getTime() + ".log";
        FileHandler fileHandler = new FileHandler(filename);
        fileHandler.setLevel(Level.FINE);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
//        consoleHandler.setFormatter(new SimpleFormatter() {
//            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
//
//            @Override
//            public synchronized String format(LogRecord lr) {
//                return String.format(format,
//                        new Date(lr.getMillis()),
//                        lr.getLevel().getLocalizedName(),
//                        lr.getMessage()
//                );
//            }
//        });
        logger.addHandler(consoleHandler);
    }

    private static void ensureDirectoryExists(String dir) {
        File directory = new File(dir);
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();
    }
}
