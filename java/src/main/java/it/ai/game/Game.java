package it.ai.game;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Game {

    /***
     * Generate and return the initial game state.
     * @return the initial game state
     */
    State start();

    /***
     * Return all the possible actions from the given state.
     */
    List<Action> getValidActions(State state);

    /***
     * Advance the given state and return it.
     */
    State nextState(State state, Action action);

    /***
     * Return the winner of the game, None else.
     */
    Optional<Integer> getWinner(State state);

    /***
     * Return the previous turn player
     */
    int previousPlayer(int player);

    /***
     * Return the next turn player
     */
    int nextPlayer(int player);

    Stream<Integer> getPlayers();

    /***
     * Return the action needed to go from one state to the following one.
     */
    Optional<Action> getAction(State fromState, State toState);


    /***
     * Return cells occupied by items of the given player
     */
    Stream<Coords> cellsOccupiedBy(State state, int player);
}
