//package it.ai.montecarlo.phases;
//
//import it.ai.constants.Constants;
//import it.ai.game.Action;
//import it.ai.game.State;
//import it.ai.game.tablut.Coords;
//import it.ai.game.tablut.TablutState;
//import it.ai.game.tablut.ashton.AshtonTablutGame;
//import it.ai.montecarlo.heuristics.HeuristicEvaluation;
//import it.ai.montecarlo.heuristics.HeuristicUtils;
//import it.ai.montecarlo.strategies.reward.RewardStrategy;
//import it.ai.util.MathUtils;
//import it.ai.util.RandomUtils;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//
//public class HeuristicSimulation extends Simulation {
//    private final AshtonTablutGame ashtonGame;
//    private final HeuristicEvaluation heuristic;
//    private final RewardStrategy rewardStrategy;
//    private final int actionsToEvaluate;
//
//    public HeuristicSimulation(AshtonTablutGame game, HeuristicEvaluation heuristic, RewardStrategy rewardStrategy,
//                               int actionsToEvaluate) {
//        super(game);
//        this.ashtonGame = game;
//        this.heuristic = heuristic;
//        this.rewardStrategy = rewardStrategy;
//        this.actionsToEvaluate = actionsToEvaluate;
//    }
//
//    @Override
//    protected Action chooseAction(State state, List<Action> validActions) {
//        int n = Math.min(actionsToEvaluate, validActions.size());
//        validActions = RandomUtils.choice(validActions, n);
//
//        return MathUtils.argmax(validActions, action -> {
//            State newState = game.nextState(state, action);
//            return StateEvaluationHelper.evaluateState(game, rewardStrategy, heuristic, newState);
//        });
//    }
//
//    private List<Coords> getProtectionCoordinates(State state) {
//        if (state.getTurn() == Constants.Player.WHITE) {
//            return getWhiteProtection();
//        }
//        return getBlackProtection();
//    }
//
//    private List<Coords> getBlackProtection(TablutState state) {
//        Coords kingPosition = ashtonGame.getKingPosition(state);
//        List<Action> kingsActions = ashtonGame.getPawnActions(state, kingPosition);
//        List<Coords> result = new ArrayList<>();
//        Coords winningPos = null;
//        Directions winningDir = null;
//        for (Action kingAction : kingsActions) {
//            if(HeuristicUtils.countKingEscapes(state, kingPosition) >=2){
//                winningPos = (Coords) kingAction.getTo();
//                break;
//            }
//        }
//
//        if (winningPos != null) {
//            boolean row = winningDir == Directions.UP || winningDir == Directions.DOWN;
//            for (int i = 0; i < BOARD_SIZE; i++)
//                result.add(new Coordinates(row ? i : winningPos.row, row ? winningPos.column : i));
//        }
//        return result;
//    }
//
//    private LinkedList<Coordinates> getWhiteProtection() {
//        int r = kingPosition.row;
//        int c = kingPosition.column;
//        LinkedList<Coordinates> result = new LinkedList<>();
//        Function<Coordinates, Boolean> condition = (Coordinates coord) -> getValue(pawns, coord) == BLACK;
//        Coordinates[] empties = new Coordinates[Directions.values().length];
//        int blockers = 1;
//        if (isEarlyGame() || isKingNearThrone())
//            blockers = 3;
//
//        if (isBlocker(r + 1, c, BLACK))
//            empties[getDirection(kingPosition, new Coordinates(r + 1, c)).value] = new Coordinates(r - 1, c);
//        if (isBlocker(r - 1, c, BLACK))
//            empties[getDirection(kingPosition, new Coordinates(r - 1, c)).value] = new Coordinates(r + 1, c);
//        if (isBlocker(r, c + 1, BLACK))
//            empties[getDirection(kingPosition, new Coordinates(r, c + 1)).value] = new Coordinates(r, c - 1);
//        if (isBlocker(r, c - 1, BLACK))
//            empties[getDirection(kingPosition, new Coordinates(r, c - 1)).value] = new Coordinates(r, c + 1);
//        int i = 0;
//        for (Coordinates coord : empties)
//            if (coord != null)
//                i++;
//        if (i < blockers)
//            return result;
//        for (Directions dir : Directions.values()) {
//            if (empties[dir.value] != null && getValue(pawns, empties[dir.value]) == EMPTY) {
//                for (Directions d : getOtherDirections(dir)) {
//                    Coordinates enemyPos = searchByDirection(empties[dir.value], d, condition);
//                    if (enemyPos != null) {
//                        if (result.isEmpty())
//                            result = insideCoordinates(empties[dir.value], enemyPos);
//                        else {
//                            result = new LinkedList<>();
//                            result.add(empties[dir.value]);
//                            return result;
//                        }
//                    }
//                }
//                if (!result.isEmpty())
//                    return result;
//            }
//        }
//        return result;
//    }
//    private enum Directions {
//        UP(0), RIGHT(1), DOWN(2), LEFT(3);
//
//        private int value;
//
//        private Directions(int value) {
//            this.value = value;
//        }
//    }
//
//    private Directions getDirection(Coords from, Coords to) {
//        if (from.getRow() == to.getRow()) {
//            if (from.getColumn() > to.getColumn())
//                return Directions.LEFT;
//            else
//                return Directions.RIGHT;
//        } else {
//            if (from.getRow() > to.getRow())
//                return Directions.UP;
//            else
//                return Directions.DOWN;
//        }
//    }
//
//    private Directions[] getOtherAxisDirections(Directions dir) {
//        if (dir == Directions.DOWN || dir == Directions.UP)
//            return new Directions[] { Directions.RIGHT, Directions.LEFT };
//        else
//            return new Directions[] { Directions.UP, Directions.DOWN };
//    }
//}