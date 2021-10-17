package it.ai.game.tablut.ashton;

import com.google.common.collect.Sets;
import it.ai.collections.Iterables;
import it.ai.collections.Streams;
import it.ai.game.Game;
import it.ai.game.State;
import it.ai.game.tablut.*;
import lombok.Getter;

import java.util.*;
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
        return new TablutState(new AshtonBoard(), Player.WHITE);
    }

    /***
     * Return all the possible actions from the given state.
     */
    @Override
    public List<it.ai.game.Action> getValidActions(State _state) {
        TablutState state = (TablutState) _state;
        Board board = state.getBoard();
        List<it.ai.game.Action> validActions = new ArrayList<>();

        Set<Coords> currentPlayerPawnCells = new HashSet<>(board.getPawnCoords(state.getTurn()));
        if (state.getTurn() == Player.WHITE)
            currentPlayerPawnCells.addAll(board.getPawnCoords(Pawn.KING));

        for (Coords pawnCoords : currentPlayerPawnCells) {
            Iterable<Coords> topCoords = () -> Streams.rangeInclusive(pawnCoords.getRow() - 1, 0, -1)
                    .map(k -> new Coords(k, pawnCoords.getColumn())).iterator();

            Iterable<Coords> bottomCoords = () -> Streams.range(pawnCoords.getRow() + 1, board.numberOfRows())
                    .map(k -> new Coords(k, pawnCoords.getColumn())).iterator();

            Iterable<Coords> leftCoords = () -> Streams.rangeInclusive(pawnCoords.getColumn() - 1, 0, -1)
                    .map(k -> new Coords(pawnCoords.getRow(), k)).iterator();

            Iterable<Coords> rightCoords = () -> Streams.range(pawnCoords.getColumn() + 1, board.numberOfColumns())
                    .map(k -> new Coords(pawnCoords.getRow(), k)).iterator();

            List<Iterable<Coords>> directions = List.of(topCoords, bottomCoords, leftCoords, rightCoords);

            for (Iterable<Coords> direction : directions) {
                for (Coords coords : direction) {
                    Action action = new Action(pawnCoords, coords);
                    if (!isValidAction(state, action))
                        break;

                    validActions.add(action);
                }
            }
        }

        return validActions;
    }

    /***
     * Check if given an action, it is allowed from the current state according to the rules of game.
     */
    private boolean isValidAction(TablutState state, Action action) {
        Board board = state.getBoard();
        //controllo se sono fuori dal tabellone
        //TODO: remove this check
        if (action.getFrom().getColumn() > board.numberOfColumns() - 1 || action.getFrom().getRow() > board.numberOfRows() - 1
                || action.getTo().getRow() > board.numberOfRows() - 1 || action.getTo().getColumn() > board.numberOfColumns() - 1
                || action.getFrom().getColumn() < 0 || action.getFrom().getRow() < 0 || action.getTo().getRow() < 0 || action.getTo().getColumn() < 0) {
            return false;
        }

        //controllo la casella di arrivo
        if (board.get(action.getTo()) != Pawn.EMPTY)
            return false;

        //Mossa che arriva sopra una citadel
        if (board.inCitadels(action.getTo())) {
            if (board.inCitadels(action.getFrom())) {
                if (action.getFrom().getRow() == action.getTo().getRow()) {
                    if (action.getFrom().getColumn() - action.getTo().getColumn() > 5
                            || action.getFrom().getColumn() - action.getTo().getColumn() < -5)
                        return false;
                } else if (action.getFrom().getRow() - action.getTo().getRow() > 5
                        || action.getFrom().getRow() - action.getTo().getRow() < -5)
                    return false;
            } else return false;
        }

        // TODO: remove
        //controllo se cerco di stare fermo
        if (action.getFrom().equals(action.getTo()))
            return false;

        // TODO: remove
        //controllo se sto muovendo una pedina giusta
        if (state.getTurn() == Player.WHITE && !Pawn.isWhite(board.get(action.getFrom())))
            return false;

        if (state.getTurn() == Player.BLACK && board.get(action.getFrom()) != Pawn.BLACK)
            return false;

        // TODO: remove
        //controllo di non muovere in diagonale
        if (action.getFrom().getRow() != action.getTo().getRow()
                && action.getFrom().getColumn() != action.getTo().getColumn())
            return false;

        //controllo di non scavalcare pedine
        if (action.getFrom().getRow() == action.getTo().getRow()) {
            Iterable<Integer> direction;
            if (action.getFrom().getColumn() > action.getTo().getColumn()) {
                direction = Iterables.range(action.getTo().getColumn(), action.getFrom().getColumn());
            } else direction = Iterables.rangeInclusive(action.getFrom().getColumn() + 1, action.getTo().getColumn());


            for (int j : direction) {
                if (isClimbing(state, action.getFrom().getRow(), j, action))
                    return false;
            }
        } else {
            Iterable<Integer> direction;
            if (action.getFrom().getRow() > action.getTo().getRow()) {
                direction = Iterables.range(action.getTo().getRow(), action.getFrom().getRow());
            } else direction = Iterables.rangeInclusive(action.getFrom().getRow() + 1, action.getTo().getRow());


            for (int i : direction) {
                if (isClimbing(state, i, action.getFrom().getColumn(), action))
                    return false;
            }
        }
        return true;
    }

    private boolean isClimbing(TablutState state, int i, int j, Action action) {
        Board board = state.getBoard();
        return board.get(i, j) != Pawn.EMPTY || (board.inCitadels(i, j) && !board.inCitadels(action.getFrom()));
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

        if (turn == Player.WHITE) {
            if (isKingOnEdge(state) || state.getBoard().count(Pawn.BLACK) == 0)
                return Optional.of(Player.WHITE);
        }

        if (turn == Player.BLACK) {
            if (state.getBoard().count(Pawn.KING) == 0)
                return Optional.of(Player.BLACK);
        }

        if (isADraw(state))
            return Optional.of(Game.DRAW);

        return Optional.empty();
    }

    private boolean isKingOnEdge(TablutState state) {
        Board board = state.getBoard();
        Coords kingCoords = board.getPawnCoords(Pawn.KING).iterator().next();
        int row = kingCoords.getRow();
        int column = kingCoords.getColumn();

        return row == 0 || row == board.numberOfRows() - 1 || column == 0 || column == board.numberOfColumns() - 1;
    }

    private boolean isADraw(TablutState state) {
        int repeatedActions = state.getPreviousStates().get(state.hashCode());

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

        if (fromState.getTurn() == Player.WHITE) {
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
        if (player == Player.WHITE) {
            cells = Stream.concat(cells, state.getBoard().getPawnCoords(Pawn.KING).stream());
        }
        return cells.map(Function.identity());
    }
}
