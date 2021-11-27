package it.ai.game.tablut.ashton;

import com.google.common.collect.Sets;
import it.ai.collections.Iterables;
import it.ai.collections.Streams;
import it.ai.constants.Constants;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.game.tablut.*;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class AshtonTablutGame implements Game {
    @Getter
    private static final int numberOfPlayers = 2;

    private final int repeatedActionsAllowed;

    public AshtonTablutGame(int repeatedActionsAllowed) {
        this.repeatedActionsAllowed = repeatedActionsAllowed;
    }

    /***
     * Generate and return the initial game state.
     * @return the initial game state
     */
    @Override
    public State start() {
        return new TablutState(new AshtonBoard(), Constants.Player.WHITE);
    }


    /***
     * Return all the possible actions from the given state.
     */
    @Override
    public List<it.ai.game.Action> getValidActions(State _state) {
        TablutState state = (TablutState) _state;
        Board board = state.getBoard();
        List<it.ai.game.Action> validActions = new ArrayList<>();

        List<Coords> currentPlayerPawnCells = getPlayerPawnCells(board, state.getTurn());

        for (Coords pawnCoords : currentPlayerPawnCells) {
            addPawnActions(validActions, state, pawnCoords);
        }

        return validActions;
    }

    public List<it.ai.game.Action> getPawnActions(TablutState state, Coords pawnCoords) {
        List<it.ai.game.Action> actions = new ArrayList<>();
        addPawnActions(actions, state, pawnCoords);
        return actions;
    }

    private void addPawnActions(List<it.ai.game.Action> actions, TablutState state, Coords pawnCoords) {
        Iterable<Iterable<Coords>> directions = getDirectionalCoords(state.getBoard(), pawnCoords);

        for (Iterable<Coords> direction : directions) {
            for (Coords coords : direction) {
                Action action = new Action(pawnCoords, coords);
                if (!isValidAction(state, action))
                    break;

                actions.add(action);
            }
        }
    }

    private boolean noValidActions(TablutState state) {
        Board board = state.getBoard();

        List<Coords> currentPlayerPawnCells = getPlayerPawnCells(board, state.getTurn());

        for (Coords pawnCoords : currentPlayerPawnCells) {
            Iterable<Iterable<Coords>> directions = getDirectionalCoords(board, pawnCoords);

            for (Iterable<Coords> direction : directions) {
                for (Coords coords : direction) {
                    Action action = new Action(pawnCoords, coords);
                    if (!isValidAction(state, action))
                        break;

                    return false;
                }
            }
        }

        return true;
    }

    private List<Coords> getPlayerPawnCells(Board board, int player) {
        List<Coords> currentPlayerPawnCells = new ArrayList<>(board.getPawnCoords(player));
        if (player == Constants.Player.WHITE)
            currentPlayerPawnCells.addAll(board.getPawnCoords(Pawn.KING));
        return currentPlayerPawnCells;
    }

    private Iterable<Iterable<Coords>> getDirectionalCoords(Board board, Coords pawnCoords) {
        Iterable<Coords> topCoords = com.google.common.collect.Iterables.transform(
                Iterables.rangeInclusive(pawnCoords.getRow() - 1, 0, -1),
                k -> new Coords(k, pawnCoords.getColumn()));

        Iterable<Coords> bottomCoords = com.google.common.collect.Iterables.transform(
                Iterables.range(pawnCoords.getRow() + 1, board.numberOfRows()),
                k -> new Coords(k, pawnCoords.getColumn()));

        Iterable<Coords> leftCoords = com.google.common.collect.Iterables.transform(
                Iterables.rangeInclusive(pawnCoords.getColumn() - 1, 0, -1),
                k -> new Coords(pawnCoords.getRow(), k));

        Iterable<Coords> rightCoords = com.google.common.collect.Iterables.transform(
                Iterables.range(pawnCoords.getColumn() + 1, board.numberOfColumns()),
                k -> new Coords(pawnCoords.getRow(), k));

        return List.of(topCoords, bottomCoords, leftCoords, rightCoords);
    }


    /***
     * Check if given an action, it is allowed from the current state according to the rules of game.
     */
    private boolean isValidAction(TablutState state, Action action) {
        Board board = state.getBoard();
        int fromRow = action.getFrom().getRow();
        int fromColumn = action.getFrom().getColumn();
        int toRow = action.getTo().getRow();
        int toColumn = action.getTo().getColumn();

        if (board.get(action.getTo()) != Pawn.EMPTY)
            return false;

        if (board.inCitadels(action.getTo())) {
            if (!board.inCitadels(action.getFrom())) return false;

            if (fromRow == toRow && Math.abs(fromColumn - toColumn) > 5) return false;
            if (fromColumn == toColumn && Math.abs(fromRow - toRow) > 5) return false;
        }

        return true;
    }


    /***
     * Advance the given state and return it.
     */
    @Override
    public State nextState(State _state, it.ai.game.Action _action) {
        TablutState state = (TablutState) _state;
        Action action = (Action) _action;
        TablutState newState = state.nextState(action);

        movePawn(newState, action);

        newState.setTurn(nextPlayer(newState.getTurn()));

        return newState;
    }

    private void movePawn(TablutState state, Action action) {
        state.getBoard().move(action.getFrom(), action.getTo());
        CaptureRules.removeCapturedPawns(state, action);
    }

    /***
     * Return the winner of the game for the given state.
     */
    @Override
    public Optional<Integer> getWinner(State _state) {
        TablutState state = (TablutState) _state;
        int turn = previousPlayer(state.getTurn());

        if (turn == Constants.Player.WHITE) {
            if (isKingOnEdge(state) || state.getBoard().count(Pawn.BLACK) == 0)
                return Optional.of(Constants.Outcome.WHITE_WIN);
        }

        if (turn == Constants.Player.BLACK) {
            if (state.getBoard().count(Pawn.KING) == 0)
                return Optional.of(Constants.Outcome.BLACK_WIN);
        }

        if (isADraw(state))
            return Optional.of(Constants.Outcome.DRAW);

        if (noValidActions(state))
            return Optional.of(nextPlayer(turn));

        return Optional.empty();
    }

    public boolean isKingNearThrone(TablutState state) {
        Coords kingPosition = getKingPosition(state);
        Coords[] surroundingPositions = Coords.surroundingPositions(kingPosition);
        return ArrayUtils.contains(surroundingPositions, AshtonBoard.THRONE);
    }

    public boolean isKingOnThrone(TablutState state) {
        return getKingPosition(state).equals(AshtonBoard.THRONE);
    }

    public boolean isKingOnEdge(TablutState state) {
        Board board = state.getBoard();
        Coords kingPosition = getKingPosition(state);
        return board.isOnEdges(kingPosition);
    }

    public Coords getKingPosition(TablutState state) {
        return state.getBoard().getPawnCoords(Pawn.KING).iterator().next();
    }

    private boolean isADraw(TablutState state) {
        int repeatedActions = state.getBoardHistory().get(state.getBoard().hashCode());

        return repeatedActions > repeatedActionsAllowed;
    }

    /***
     * Return the previous turn player
     */
    @Override
    public int previousPlayer(int player) {
        return (numberOfPlayers + player - 1) % numberOfPlayers;
    }

    /***
     * Return the next turn player
     */
    @Override
    public int nextPlayer(int player) {
        return (player + 1) % numberOfPlayers;
    }

    @Override
    public Stream<Integer> getPlayers() {
        return Streams.range(numberOfPlayers);
    }

    /***
     * Return the action needed to go from one state to the following one.
     */
    @Override
    public Optional<it.ai.game.Action> getAction(State _fromState, State _toState) {
        TablutState fromState = (TablutState) _fromState;
        TablutState toState = (TablutState) _toState;

        if (fromState.getTurn() == toState.getTurn())
            return Optional.empty();

        if (fromState.getTurn() == Constants.Player.WHITE) {
            Optional<it.ai.game.Action> action = getActionForPawn(fromState, toState, Pawn.KING);
            if (action.isPresent()) return action;
        }

        return getActionForPawn(fromState, toState, fromState.getTurn());
    }

    private Optional<it.ai.game.Action> getActionForPawn(TablutState fromState, TablutState toState, int pawn) {
        Set<Coords> pawnsBefore = fromState.getBoard().getPawnCoords(pawn);
        Set<Coords> pawnsAfter = toState.getBoard().getPawnCoords(pawn);

        Set<Coords> movedPawnPositions = Sets.difference(pawnsBefore, pawnsAfter);
        Set<Coords> reachedPawnPositions = Sets.difference(pawnsAfter, pawnsBefore);

        for (Coords startingPosition : movedPawnPositions) {
            for (Coords reachedPosition : reachedPawnPositions) {
                Action action = new Action(startingPosition, reachedPosition);
                if (isValidAction(fromState, action)) return Optional.of(action);
            }
        }
        return Optional.empty();
    }

    /***
     * Return cells occupied by items of the given player
     */
    @Override
    public Stream<it.ai.game.Coords> cellsOccupiedBy(State _state, int player) {
        TablutState state = (TablutState) _state;
        Stream<Coords> cells = state.getBoard().getPawnCoords(player).stream();
        if (player == Constants.Player.WHITE) {
            cells = Stream.concat(cells, state.getBoard().getPawnCoords(Pawn.KING).stream());
        }
        return cells.map(Function.identity());
    }
}
