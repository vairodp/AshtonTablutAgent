package it.ai.montecarlo.heuristics.white;

import it.ai.game.State;
import it.ai.game.tablut.Board;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.Pawn;
import it.ai.game.tablut.TablutState;
import it.ai.montecarlo.heuristics.HeuristicEvaluation;
import it.ai.montecarlo.heuristics.HeuristicUtils;

import java.util.List;

public class KingProtection implements HeuristicEvaluation {
    //Values whether there is only a white pawn near to the king
    private final double whiteNearKingValue;
    private final double maxValue;

    public KingProtection(double whiteNearKingValue, double maxValue) {
        this.whiteNearKingValue = whiteNearKingValue;
        this.maxValue = maxValue;
    }

    public KingProtection() {
        this(0.6, 1);
    }

    @Override
    public double evaluate(State s, int player) {
        TablutState state = (TablutState) s;
        return calculateProtectionValue(state);
    }

    private double calculateProtectionValue(TablutState state) {
        double result = 0.0;

        Board board = state.getBoard();
        Coords kingPosition = HeuristicUtils.kingPosition(state);
        int numberOfPawnsToEatKing = HeuristicUtils.numberOfPawnsToEatKing(kingPosition);
        List<Coords> blackPawnsNearKing = HeuristicUtils.positionOccupiedNearPawn(state, kingPosition, Pawn.BLACK);

        //There is a black pawn that threatens the king and 2 pawns are enough to eat the king
        if (blackPawnsNearKing.size() == 1 && numberOfPawnsToEatKing == 2) {
            Coords enemyPos = blackPawnsNearKing.get(0);
            //Used to store other position from where king could be eaten
            Coords targetPosition;
            //Enemy right to the king
            if (enemyPos.equals(kingPosition.right())) {
                //Left to the king there is a white pawn and king is protected
                targetPosition = kingPosition.left();
                if (board.get(targetPosition) == Pawn.WHITE) {
                    result += whiteNearKingValue;
                }
                //Enemy left to the king
            } else if (enemyPos.equals(kingPosition.left())) {
                //Right to the king there is a white pawn and king is protected
                targetPosition = kingPosition.right();
                if (board.get(targetPosition) == Pawn.WHITE) {
                    result += whiteNearKingValue;
                }
                //Enemy up to the king
            } else if (enemyPos.equals(kingPosition.top())) {
                //Down to the king there is a white pawn and king is protected
                targetPosition = kingPosition.bottom();
                if (board.get(targetPosition) == Pawn.WHITE) {
                    result += whiteNearKingValue;
                }
                //Enemy down to the king
            } else {
                //Up there is a white pawn and king is protected
                targetPosition = kingPosition.top();
                if (board.get(targetPosition) == Pawn.WHITE) {
                    result += whiteNearKingValue;
                }
            }

            //Considering whites to use as barriers for the target pawn
            double otherPoints = maxValue - whiteNearKingValue;
            double contributionPerN;

            //Whether it is better to keep free the position
            if (board.isOnEdges(targetPosition)) {
                result = board.get(targetPosition) == Pawn.EMPTY ? 1 : 0;
            } else {
                int targetRow = targetPosition.getRow();
                int targetColumn = targetPosition.getColumn();
                //Considering a reduced number of neighbours whether target is near to citadels or throne
                if (targetRow == 4 && targetColumn == 2 || targetRow == 4 && targetColumn == 6
                        || targetRow == 2 && targetColumn == 4 || targetRow == 6 && targetColumn == 4
                        || targetRow == 3 && targetColumn == 4 || targetRow == 5 && targetColumn == 4
                        || targetRow == 4 && targetColumn == 3 || targetRow == 4 && targetColumn == 5) {
                    contributionPerN = otherPoints / 2;
                } else {
                    contributionPerN = otherPoints / 3;
                }

                int whitesSurroundingTarget =
                        HeuristicUtils.countPawnsSurroundingPosition(state, targetPosition, Pawn.WHITE);
                result += contributionPerN * whitesSurroundingTarget;
            }

        }
        return result;
    }
}
