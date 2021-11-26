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
import it.ai.montecarlo.heuristics.black.BlackOnRhombus;
import it.ai.montecarlo.heuristics.black.BlackSurroundKing;
import it.ai.montecarlo.heuristics.black.WhiteEaten;
import it.ai.montecarlo.heuristics.white.*;
import it.ai.montecarlo.phases.Simulation;
import it.ai.montecarlo.phases.*;
import it.ai.montecarlo.strategies.bestaction.MonteCarloBestActionStrategy;
import it.ai.montecarlo.strategies.bestaction.RobustChildStrategy;
import it.ai.montecarlo.strategies.qvalue.DynamicAlpha;
import it.ai.montecarlo.strategies.qvalue.HeuristicQValue;
import it.ai.montecarlo.strategies.qvalue.IncreasingAlpha;
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Main {
//    private static boolean check(State state, Action action, int pawn){
//        Board board = ((TablutState)state).getBoard();
//        return checkBoard(board) && checkMovedPawn(board, action,pawn);
//    }
//    private static boolean checkMovedPawn(Board board, Action action, int pawn) {
//        int from = board.get(action.getFrom());
//        return (from == Pawn.EMPTY || from == Pawn.THRONE) &&  Pawn.getOwner(board.get(action.getTo())) == pawn;
//    }
//
//    private static boolean checkBoard(Board board) {
//        if (isDifferent(board, Pawn.BLACK)) return false;
//        if (isDifferent(board, Pawn.WHITE)) return false;
//        if (isDifferent(board, Pawn.KING)) return false;
//        return true;
//    }
//
//    private static boolean isDifferent(Board board, int pawn) {
//        for (Coords coords : board.getPawnCoords(pawn)) {
//            if (board.get(coords) != pawn)
//                return true;
//        }
//        return false;
//    }

    public static void main(String[] args) throws Exception {
        configureLogger();
        Logger logger = Logger.getLogger(Main.class.getName());

        int playerTeam = Constants.Player.BLACK;
        String playerName = "AI";
        String team = playerTeam == Constants.Player.BLACK ? Turn.BLACK : Turn.WHITE;
        int timeout_s = 55;

//        double alpha = 0.4;
        DynamicAlpha alpha = new IncreasingAlpha();
//        DynamicAlpha alpha = new HeuristicQValue.ConstAlpha(0.4);
        double exploration = 1.4;
//        double networkThreshold = 0.6;

//        String blackNN = "value_model_b_1.h5";
//        String whiteNN = "value_model_b_1.h5";

        Game game = new AshtonTablutGame(0);
//        MonteCarloBestActionStrategy bestActionStrategy = new MaxChildStrategy();
        MonteCarloBestActionStrategy bestActionStrategy = new RobustChildStrategy();
        RewardStrategy rewardStrategy = new DefaultRewardStrategy();

//        QEvaluation qEvaluation = new WinProbabilityQValue();
        QEvaluation qEvaluation = new HeuristicQValue(alpha);
        MonteCarloSelectionScoreStrategy selectionScoreStrategy = new Ucb1SelectionScoreStrategy(exploration, qEvaluation);

        HeuristicEvaluation heuristicEvaluation = buildHeuristic(playerTeam);

        MCTSMinMax minMax = new MCTSMinMax(game, rewardStrategy, heuristicEvaluation, playerTeam);
        Selection selection = new Selection(selectionScoreStrategy);
//        Expansion expansion = new Expansion(game);
        Expansion expansion = minMax.getExpansion();
//        Simulation simulation = new Simulation(game);
        Simulation simulation = new ParallelSimulation(game);
//        Backpropagation backpropagation = new Backpropagation(game, rewardStrategy);
        Backpropagation backpropagation = minMax.getBackPropagation();


        IMCTS mcts = new MCTS(game, bestActionStrategy, selection, expansion, simulation, backpropagation);

//        MCTS mcts = new NeuralNetworkMonteCarlo(mctsImpl,
//                new ValueNeuralNetwork(blackNN), new ValueNeuralNetwork(whiteNN), networkThreshold);


        Agent agent = new MctsAgent(game, mcts, () -> new TimeoutTerminationCondition(timeout_s));
        Player player = new AgentPlayer(playerName, team, agent);

        Mapper mapper = new AshtonMapper();
        TablutClient client = new TablutClient(player, mapper, "127.0.0.1");

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

    private static HeuristicEvaluation buildHeuristic(int player) {
        AggregateHeuristic whiteHeuristic = new AggregateHeuristic(new AggregateHeuristic.WeightedHeuristic[]{
                new AggregateHeuristic.WeightedHeuristic(2, new WhiteWellPositioned()),
                new AggregateHeuristic.WeightedHeuristic(20, new BlackEaten()),
                new AggregateHeuristic.WeightedHeuristic(35, new WhiteAlive()),
                new AggregateHeuristic.WeightedHeuristic(18, new KingEscapes()),
                new AggregateHeuristic.WeightedHeuristic(7, new RemainingToSurroundKing()),
                new AggregateHeuristic.WeightedHeuristic(18, new KingProtection())
        });

        AggregateHeuristic blackHeuristic = new AggregateHeuristic(new AggregateHeuristic.WeightedHeuristic[]{
                new AggregateHeuristic.WeightedHeuristic(35, new BlackAlive()),
                new AggregateHeuristic.WeightedHeuristic(48, new WhiteEaten()),
                new AggregateHeuristic.WeightedHeuristic(15, new BlackSurroundKing()),
                new AggregateHeuristic.WeightedHeuristic(2, new BlackOnRhombus()),

//                new AggregateHeuristic.WeightedHeuristic(35, new BlackAlive()),
//                new AggregateHeuristic.WeightedHeuristic(48, new WhiteEaten()),
//                new AggregateHeuristic.WeightedHeuristic(15, new BlackSurroundKing()),
//                new AggregateHeuristic.WeightedHeuristic(2, new BlackOnRhombus())
        });

        if (player == Constants.Player.WHITE)
            return whiteHeuristic;
        else return blackHeuristic;

//        return new BlackAndWhiteHeuristic(blackHeuristic, whiteHeuristic);
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
