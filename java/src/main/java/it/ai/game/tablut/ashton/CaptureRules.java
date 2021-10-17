package it.ai.game.tablut.ashton;

import com.google.common.collect.Sets;
import it.ai.game.tablut.*;

import java.util.Set;

final class CaptureRules {
    @FunctionalInterface
    interface TriConsumer<A, B, C> {
        void apply(A a, B b, C c);
    }

    public static void removeCapturedPawns(TablutState state, Action action) {
        TriConsumer<TablutState, Coords, Coords> simplePawnCapturer = null;

        if (state.getTurn() == Player.BLACK) {
            removeKingIfCaptured(state, action);
            simplePawnCapturer = CaptureRules::removeWhitePawnIfCaptured;
        } else if (state.getTurn() == Player.WHITE) {
            simplePawnCapturer = CaptureRules::removeBlackPawnIfCaptured;
        }

        removeCapturedNormalPawns(state, action, simplePawnCapturer);
    }

    private static void removeCapturedNormalPawns(TablutState state, Action action,
                                                  TriConsumer<TablutState, Coords, Coords> removeSimplePawn) {

        int toRow = action.getTo().getRow();
        int toColumn = action.getTo().getColumn();

        // capture on the right
        if (toColumn < state.getBoard().numberOfColumns() - 2) {
            Coords captured = new Coords(toRow, toColumn + 1);
            Coords second = new Coords(toRow, toColumn + 2);
            removeSimplePawn.apply(state, captured, second);
        }

        //capture on the left
        if (toColumn > 1) {
            Coords captured = new Coords(toRow, toColumn - 1);
            Coords second = new Coords(toRow, toColumn - 2);
            removeSimplePawn.apply(state, captured, second);
        }

        //capture on the top
        if (toRow > 1) {
            Coords captured = new Coords(toRow - 1, toColumn);
            Coords second = new Coords(toRow - 2, toColumn);
            removeSimplePawn.apply(state, captured, second);
        }

        //capture on the bottom
        if (toRow < state.getBoard().numberOfRows() - 2) {
            Coords captured = new Coords(toRow + 1, toColumn);
            Coords second = new Coords(toRow + 2, toColumn);
            removeSimplePawn.apply(state, captured, second);
        }
    }

    private static final Set<Coords> whiteUnusableCitadels = Sets.newHashSet(
            Coords.I5, Coords.E1, Coords.A5, Coords.E9);

    /***
     * Remove black pawn between first and second white pawn.
     */
    private static void removeBlackPawnIfCaptured(TablutState state, Coords captured, Coords second) {
        Board board = state.getBoard();
        int captured_pawn = board.get(captured);
        int secondPawn = board.get(second);

        if (captured_pawn == Pawn.BLACK) {
            boolean pawnIsWhiteOrThrone = Pawn.isWhite(secondPawn) || second.equals(Coords.E5);

            // if second pawn is white or throne or is a valid citadel
            if (pawnIsWhiteOrThrone || (board.inCitadels(second) && !whiteUnusableCitadels.contains(second)))
                board.remove(captured);
        }
    }

    /***
     * Remove white pawn between first and second black pawn
     */
    private static void removeWhitePawnIfCaptured(TablutState state, Coords captured, Coords second) {
        Board board = state.getBoard();
        int captured_pawn = board.get(captured);
        int secondPawn = board.get(second);

        if (captured_pawn == Pawn.WHITE) {
            if (secondPawn == Pawn.BLACK || board.inCitadels(second) || second.equals(Coords.E5)) {
                board.remove(captured);
            }
        }
    }

    private static void removeKingIfCaptured(TablutState state, Action action) {
        int toRow = action.getTo().getRow();
        int toColumn = action.getTo().getColumn();
        Board board = state.getBoard();

        Coords right = new Coords(toRow, toColumn + 1);
        Coords left = new Coords(toRow, toColumn - 1);
        Coords bottom = new Coords(toRow + 1, toColumn);
        Coords top = new Coords(toRow - 1, toColumn);

        //capture on the right
        if (toColumn < board.numberOfColumns() - 2 && board.get(right) == Pawn.KING) {
            //re sul trono
            if (right.equals(Coords.E5)) {
                if (board.get(Coords.E4) == Pawn.BLACK
                        && board.get(Coords.F5) == Pawn.BLACK
                        && board.get(Coords.E6) == Pawn.BLACK) {
                    board.remove(right);
                }
            }

            //re adiacente al trono
            else if (right.equals(Coords.E4)) {
                if (board.get(Coords.E3) == Pawn.BLACK && board.get(Coords.F4) == Pawn.BLACK)
                    board.remove(right);
            } else if (right.equals(Coords.E6)) {
                if (board.get(Coords.F6) == Pawn.BLACK && board.get(Coords.E7) == Pawn.BLACK)
                    board.remove(right);
            } else if (right.equals(Coords.D5)) {
                if (board.get(Coords.D4) == Pawn.BLACK && board.get(Coords.D6) == Pawn.BLACK)
                    board.remove(right);
            }

            //sono fuori dalle zone del trono
            else {
                Coords secondPawn = new Coords(toRow, toColumn + 2);
                if (board.get(secondPawn) == Pawn.BLACK || board.inCitadels(secondPawn))
                    board.remove(right);
            }
        }

        //capture on the left
        else if (toColumn > 1 && board.get(left) == Pawn.KING) {
            //re sul trono
            if (left.equals(Coords.E5)) {
                if (board.get(Coords.E4) == Pawn.BLACK
                        && board.get(Coords.D5) == Pawn.BLACK
                        && board.get(Coords.E6) == Pawn.BLACK)
                    board.remove(left);
            }
            //re adiacente al trono
            else if (left.equals(Coords.E4)) {
                if (board.get(Coords.E3) == Pawn.BLACK && board.get(Coords.D4) == Pawn.BLACK)
                    board.remove(left);
            } else if (left.equals(Coords.F5)) {
                if (board.get(Coords.F6) == Pawn.BLACK && board.get(Coords.F4) == Pawn.BLACK)
                    board.remove(left);
            } else if (left.equals(Coords.E6)) {
                if (board.get(Coords.E7) == Pawn.BLACK && board.get(Coords.D6) == Pawn.BLACK)
                    board.remove(left);
            }
            //sono fuori dalle zone del trono
            else {
                Coords secondPawn = new Coords(toRow, toColumn - 2);
                if (board.get(secondPawn) == Pawn.BLACK || board.inCitadels(secondPawn))
                    board.remove(left);
            }
        }

        //capture on the bottom
        else if (toRow < board.numberOfRows() - 2 && board.get(bottom) == Pawn.KING) {
            //re sul trono
            if (bottom.equals(Coords.E5)) {
                if (board.get(Coords.E6) == Pawn.BLACK
                        && board.get(Coords.F5) == Pawn.BLACK
                        && board.get(Coords.D5) == Pawn.BLACK)
                    board.remove(bottom);
            }

            //re adiacente al trono
            else if (bottom.equals(Coords.E4)) {
                if (board.get(Coords.D4) == Pawn.BLACK && board.get(Coords.F4) == Pawn.BLACK)
                    board.remove(bottom);
            } else if (bottom.equals(Coords.D5)) {
                if (board.get(Coords.C5) == Pawn.BLACK && board.get(Coords.D6) == Pawn.BLACK)
                    board.remove(bottom);
            } else if (bottom.equals(Coords.F5)) {
                if (board.get(Coords.G5) == Pawn.BLACK && board.get(Coords.F6) == Pawn.BLACK)
                    board.remove(bottom);
            }

            //sono fuori dalle zone del trono
            else {
                Coords secondPawn = new Coords(toRow + 2, toColumn);
                if (board.get(secondPawn) == Pawn.BLACK || board.inCitadels(secondPawn))
                    board.remove(bottom);
            }
        }

        //capture on top
        else if (toRow > 1 && board.get(top) == Pawn.KING) {
            //re sul trono
            if (top.equals(Coords.E5)) {
                if (board.get(Coords.E4) == Pawn.BLACK
                        && board.get(Coords.F5) == Pawn.BLACK
                        && board.get(Coords.D5) == Pawn.BLACK)
                    board.remove(top);
            }

            //re adiacente al trono
            else if (top.equals(Coords.E6)) {
                if (board.get(Coords.D6) == Pawn.BLACK && board.get(Coords.F6) == Pawn.BLACK)
                    board.remove(top);
            } else if (top.equals(Coords.D5)) {
                if (board.get(Coords.C5) == Pawn.BLACK && board.get(Coords.D4) == Pawn.BLACK)
                    board.remove(top);
            } else if (top.equals(Coords.F5)) {
                if (board.get(Coords.G5) == Pawn.BLACK && board.get(Coords.F4) == Pawn.BLACK)
                    board.remove(top);
            }

            //sono fuori dalle zone del trono
            else {
                Coords secondPawn = new Coords(toRow - 2, toColumn);
                if (board.get(secondPawn) == Pawn.BLACK || board.inCitadels(secondPawn))
                    board.remove(top);
            }
        }
    }
}
