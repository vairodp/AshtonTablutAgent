from typing import Sequence, Optional

import numpy as np

from game import Game, Move
from . import TablutState, Player, Pawn, Coord
from .exception import *


class Coords:
    A4 = Coord.from_str('A4')
    A5 = Coord.from_str('A5')
    A6 = Coord.from_str('A6')
    B5 = Coord.from_str('B5')
    C5 = Coord.from_str('C5')
    D1 = Coord.from_str('D1')
    D4 = Coord.from_str('D4')
    D5 = Coord.from_str('D5')
    D6 = Coord.from_str('D6')
    D9 = Coord.from_str('D9')
    E1 = Coord.from_str('E1')
    E2 = Coord.from_str('E2')
    E3 = Coord.from_str('E3')
    E4 = Coord.from_str('E4')
    E5 = Coord.from_str('E5')
    E6 = Coord.from_str('E6')
    E7 = Coord.from_str('E7')
    E8 = Coord.from_str('E8')
    E9 = Coord.from_str('E9')
    F1 = Coord.from_str('F1')
    F4 = Coord.from_str('F4')
    F5 = Coord.from_str('F5')
    F6 = Coord.from_str('F6')
    F9 = Coord.from_str('F9')
    G5 = Coord.from_str('G5')
    H5 = Coord.from_str('H5')
    I4 = Coord.from_str('I4')
    I5 = Coord.from_str('I5')
    I6 = Coord.from_str('I6')


class AshtonTablutGame(Game):
    """Number of repeated states that can occur before a draw."""
    _repeated_moves_allowed: int

    """Counter for the moves without capturing that have occurred"""
    # _moves_without_capturing: int
    # _draw_conditions: RingBuffer

    _citadels: list[Coord]

    def __init__(self, repeated_moves_allowed: int, cache_size: int):
        super().__init__(players=2)

        self._repeated_moves_allowed = repeated_moves_allowed

        # self._draw_conditions = RingBuffer(cache_size)
        self._citadels = [Coords.A4, Coords.A5, Coords.A6, Coords.B5,
                          Coords.D1, Coords.E1, Coords.F1, Coords.E2,
                          Coords.I4, Coords.I5, Coords.I6, Coords.H5,
                          Coords.D9, Coords.E9, Coords.F9, Coords.E8]

    def start(self) -> TablutState:
        """Generate and return the initial game state."""
        board = np.array([[Pawn.EMPTY] * 9] * 9)
        for coord in self._citadels:
            board[coord] = Pawn.BLACK
        board[2:7, Coords.E4.column] = Pawn.WHITE
        board[Coords.I5.row, 2:7] = Pawn.WHITE
        board[Coords.E5] = Pawn.KING

        return TablutState(board, Player.WHITE)

    def get_move(self, from_state: TablutState, to_state: TablutState) -> Optional[Move]:
        """Return the move needed to go from one state to the following one"""
        print(from_state.turn, to_state.turn)
        if from_state.turn == to_state.turn:
            return None
        coords = list(zip(*np.where(from_state.board != to_state.board)))
        for from_coord in coords:
            if Pawn.player(from_state.board[from_coord]) == from_state.turn:
                for to_coord in coords:
                    move = Action(Coord(*from_coord), Coord(*to_coord))
                    if from_coord != to_coord and self.is_legal_move(from_state, move):
                        return move
        return None

    # TODO: indicizzare le PAWN != THRONE in modo da rendere più efficiente la ricerca
    # inoltre l'assenza di mosse disponibili costituisce vittoria / sconfitta
    def legal_moves(self, state: TablutState) -> Sequence[Move]:
        """Return all the possible moves from the given state."""
        turn = state.turn
        legal_moves: list[Action] = []

        for i in range(state.board.shape[0]):
            for j in range(state.board.shape[1]):
                from_ = Coord(i, j)

                # if pawn color is equal of turn color
                pawn = state.pawn(from_)
                if pawn == turn or (pawn == Pawn.KING and turn == Player.WHITE):

                    top_coords = (Coord(k, j) for k in range(i - 1, -1, -1))
                    bottom_coords = (Coord(k, j) for k in range(i + 1, state.board.shape[0]))
                    left_coords = (Coord(i, k) for k in range(j - 1, -1, -1))
                    right_coords = (Coord(i, k) for k in range(j + 1, state.board.shape[1]))

                    directions = [top_coords, bottom_coords, left_coords, right_coords]

                    for coords in directions:
                        for coord in coords:
                            action = Action(from_, coord)
                            if self.is_legal_move(state, action):
                                legal_moves.append(action)
                            else:
                                break
        return legal_moves

    def next_state(self, state: TablutState, move: Action) -> TablutState:
        """Advance the given state and return it. Ensure the move is legal before."""
        # new_history = list(state.move_history)
        # new_history.append(move)
        # new_state_history = state.previous_states.copy()
        # new_state_history.append(hash(state))
        #
        # new_board = np.copy(state.board)
        # state = TablutState(new_history, new_state_history, new_board, state.turn)
        new_state = state.clone()
        new_state.previous_states[hash(state)] += 1

        self._move(new_state, move)
        self._remove_captured(new_state, move)

        new_state.turn = Player.next(new_state.turn)

        return new_state

    def _move(self, state, action: Action):
        from_pawn = state.pawn(action.from_)
        to_pawn = Pawn.THRONE if action.from_ == Coords.E5 else Pawn.EMPTY

        state.set_pawn(action.from_, to_pawn)
        state.set_pawn(action.to, from_pawn)

    def _remove_captured(self, state: TablutState, action: Action):
        if state.turn == Player.BLACK:
            # self._remove_captured_whites_from_black(state, action)
            self._remove_captured_simple_pawns_from(state, action, self._remove_captured_white_from_black)
            self._remove_captured_king_from_black(state, action)
        elif state.turn == Player.WHITE:
            # self._remove_captured_blacks_from_white(state, action)
            self._remove_captured_simple_pawns_from(state, action, self._remove_captured_black_from_white)

        # state.moves_without_capturing += 1

    def _remove_captured_simple_pawns_from(self, state: TablutState, action: Action, remove_simple_pawn):
        # capture on the right
        if action.to.column < state.board.shape[1] - 2:
            captured = Coord(action.to.row, action.to.column + 1)
            second = Coord(action.to.row, action.to.column + 2)
            remove_simple_pawn(state, captured, second)

        # capture on the left
        if action.to.column > 1:
            captured = Coord(action.to.row, action.to.column - 1)
            second = Coord(action.to.row, action.to.column - 2)
            remove_simple_pawn(state, captured, second)

        # capture on the top
        if action.to.row > 1:
            captured = Coord(action.to.row - 1, action.to.column)
            second = Coord(action.to.row - 2, action.to.column)
            remove_simple_pawn(state, captured, second)

        # capture on the bottom
        if action.to.row < state.board.shape[0] - 2:
            captured = Coord(action.to.row + 1, action.to.column)
            second = Coord(action.to.row + 2, action.to.column)
            remove_simple_pawn(state, captured, second)

    # def _remove_captured_blacks_from_white(self, state: TablutState, action: Action):
    #     # controllo se mangio a destra
    #     if action.to.column < state.board.shape[1] - 2:
    #         captured = Coord(action.to.row, action.to.column + 1)
    #         second_white = Coord(action.to.row, action.to.column + 2)
    #         self._remove_captured_black_from_white(state, captured, second_white)
    #
    #     # controllo se mangio a sinistra
    #     if action.to.column > 1:
    #         captured = Coord(action.to.row, action.to.column - 1)
    #         second_white = Coord(action.to.row, action.to.column - 2)
    #         self._remove_captured_black_from_white(state, captured, second_white)
    #
    #     # controllo se mangio sopra
    #     if action.to.row > 1:
    #         captured = Coord(action.to.row - 1, action.to.column)
    #         second_white = Coord(action.to.row - 2, action.to.column)
    #         self._remove_captured_black_from_white(state, captured, second_white)
    #
    #     # controllo se mangio sotto
    #     if action.to.row < state.board.shape[0] - 2:
    #         captured = Coord(action.to.row + 1, action.to.column)
    #         second_white = Coord(action.to.row + 2, action.to.column)
    #         self._remove_captured_black_from_white(state, captured, second_white)

    def _remove_captured_black_from_white(self, state: TablutState, captured: Coord, second: Coord):
        """Remove black pawn between first and second white pawn"""
        captured_pawn = state.pawn(captured)
        second_pawn = state.pawn(second)

        if captured_pawn == Pawn.BLACK:
            if Pawn.is_white(second_pawn) or second_pawn == Pawn.THRONE \
                    or (second in self._citadels and second not in [Coords.I5, Coords.E1, Coords.A5, Coords.E9]):
                state.remove_pawn(captured)
                # state.moves_without_capturing = -1

    # def _remove_captured_whites_from_black(self, state: TablutState, action: Action):
    #     # capture on the right
    #     if action.to.column < state.board.shape[1] - 2:
    #         captured = Coord(action.to.row, action.to.column + 1)
    #         second_black = Coord(action.to.row, action.to.column + 2)
    #         self._remove_captured_white_from_black(state, captured, second_black)
    #
    #     # capture on the left
    #     if action.to.column > 1:
    #         captured = Coord(action.to.row, action.to.column - 1)
    #         second_black = Coord(action.to.row, action.to.column - 2)
    #         self._remove_captured_white_from_black(state, captured, second_black)
    #
    #     # capture on the top
    #     if action.to.row > 1:
    #         captured = Coord(action.to.row - 1, action.to.column)
    #         second_black = Coord(action.to.row - 2, action.to.column)
    #         self._remove_captured_white_from_black(state, captured, second_black)
    #
    #     # capture on the bottom
    #     if action.to.row < state.board.shape[0] - 2:
    #         captured = Coord(action.to.row + 1, action.to.column)
    #         second_black = Coord(action.to.row + 2, action.to.column)
    #         self._remove_captured_white_from_black(state, captured, second_black)

    def _remove_captured_white_from_black(self, state: TablutState, captured: Coord, second: Coord):
        """Remove white pawn between first and second black pawn"""
        captured_pawn = state.pawn(captured)
        second_pawn = state.pawn(second)

        if captured_pawn == Pawn.WHITE and \
                (second_pawn == Pawn.BLACK or second_pawn == Pawn.THRONE
                 or second in self._citadels or second == Coords.E5):
            state.remove_pawn(captured)
            # state.moves_without_capturing = -1

    def _remove_captured_king_from_black(self, state: TablutState, action: Action):
        right = Coord(action.to.row, action.to.column + 1)
        left = Coord(action.to.row, action.to.column - 1)
        bottom = Coord(action.to.row + 1, action.to.column)
        top = Coord(action.to.row - 1, action.to.column)

        # capture on the right
        if action.to.column < state.board.shape[1] - 2 and state.pawn(right) == Pawn.KING:
            # re sul trono
            if right == Coords.E5:
                if state.pawn(Coords.E4) == Pawn.BLACK and state.pawn(Coords.F5) == Pawn.BLACK and state.pawn(
                        Coords.E6) == Pawn.BLACK:
                    state.remove_pawn(right)

            # re adiacente al trono
            elif right == Coords.E4:
                if state.pawn(Coords.E3) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(right)

            elif right == Coords.E6:
                if state.pawn(Coords.F6) == Pawn.BLACK and state.pawn(Coords.E7) == Pawn.BLACK:
                    state.remove_pawn(right)

            elif right == Coords.D5:
                if state.pawn(Coords.D4) == Pawn.BLACK and state.pawn(Coords.D6) == Pawn.BLACK:
                    state.remove_pawn(right)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row, action.to.column + 2)) == Pawn.BLACK \
                        or Coord(action.to.row, action.to.column + 2) in self._citadels:
                    state.remove_pawn(right)

        # capture on the left
        elif action.to.column > 1 and state.pawn(left) == Pawn.KING:
            # re sul trono
            if left == Coords.E5:
                if state.pawn(Coords.E4) == Pawn.BLACK and state.pawn(Coords.D5) == Pawn.BLACK and state.pawn(
                        Coords.E6) == Pawn.BLACK:
                    state.remove_pawn(left)

            # re adiacente al trono
            elif left == Coords.E4:
                if state.pawn(Coords.E3) == Pawn.BLACK and state.pawn(Coords.D4) == Pawn.BLACK:
                    state.remove_pawn(left)

            elif left == Coords.F5:
                if state.pawn(Coords.F6) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(left)

            elif left == Coords.E6:
                if state.pawn(Coords.E7) == Pawn.BLACK and state.pawn(Coords.D6) == Pawn.BLACK:
                    state.remove_pawn(left)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row, action.to.column - 2)) == Pawn.BLACK \
                        or Coord(action.to.row, action.to.column - 2) in self._citadels:
                    state.remove_pawn(left)

        # capture on the bottom
        elif action.to.row < state.board.shape[0] - 2 and state.pawn(bottom) == Pawn.KING:
            # re sul trono
            if bottom == Coords.E5:
                if state.pawn(Coords.E6) == Pawn.BLACK and state.pawn(Coords.F5) == Pawn.BLACK and state.pawn(
                        Coords.D5) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            # re adiacente al trono
            elif bottom == Coords.E4:
                if state.pawn(Coords.D4) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            elif bottom == Coords.D5:
                if state.pawn(Coords.C5) == Pawn.BLACK and state.pawn(Coords.D6) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            elif bottom == Coords.F5:
                if state.pawn(Coords.G5) == Pawn.BLACK and state.pawn(Coords.F6) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row + 2, action.to.column)) == Pawn.BLACK \
                        or Coord(action.to.row + 2, action.to.column) in self._citadels:
                    state.remove_pawn(bottom)

        # capture on top
        if action.to.row > 1 and state.pawn(top) == Pawn.KING:
            # re sul trono
            if top == Coords.E5:
                if state.pawn(Coords.E4) == Pawn.BLACK and state.pawn(Coords.F5) == Pawn.BLACK and state.pawn(
                        Coords.D5) == Pawn.BLACK:
                    state.remove_pawn(top)

            # re adiacente al trono
            elif top == Coords.E6:
                if state.pawn(Coords.D6) == Pawn.BLACK and state.pawn(Coords.F6) == Pawn.BLACK:
                    state.remove_pawn(top)

            elif top == Coords.D5:
                if state.pawn(Coords.C5) == Pawn.BLACK and state.pawn(Coords.D4) == Pawn.BLACK:
                    state.remove_pawn(top)

            elif top == Coords.F5:
                if state.pawn(Coords.G5) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(top)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row - 2, action.to.column)) == Pawn.BLACK \
                        or Coord(action.to.row - 2, action.to.column) in self._citadels:
                    state.remove_pawn(top)

    def winner(self, state: TablutState) -> Optional[int]:
        """Return the winner of the game, None else."""
        turn = Player.previous(state.turn)

        if turn == Player.WHITE:
            if self._is_king_on_edge(state) or state.number_of(Pawn.BLACK) == 0:
                return Player.WHITE

        if turn == Player.BLACK:
            if state.number_of(Pawn.KING) == 0:
                return Player.BLACK

        if self._is_a_draw(state):
            return Game.DRAW

        return None

    def _is_king_on_edge(self, state: TablutState) -> bool:
        top = state.board[0, :]
        bottom = state.board[state.board.shape[0] - 1, :]
        left = state.board[:, 0]
        right = state.board[:, state.board.shape[1] - 1]

        return np.any(top == Pawn.KING) or np.any(bottom == Pawn.KING) \
               or np.any(left == Pawn.KING) or np.any(right == Pawn.KING)

    def _is_a_draw(self, state: TablutState) -> bool:
        # TODO: Capire come implementare il controllo senza avere dati nel game, ma solo nello state
        # potrebbe essere utile usare move_history come cache e salvare _moves_without_capturing dentro state
        # forse quest'ultima è anche inutile, dato che mangiare una pedina modificherebbe il tabellone e quindi lo stato
        # bisognerebbe prima capire come cavolo è implementata sta cosa. Scrivere al tutor magari
        # N.B. Prestare attenzione al comportamento di montecarlo.py, forse non è gestita la chiusura del nodo in caso di pareggio

        # return False

        # if something has been captured, clear cache for draws
        # if state.moves_without_capturing == 0:
        #     self._draw_conditions.clear()

        # subtract current move from the history
        repeated_moves = state.previous_states[hash(state)]
        # self._draw_conditions.append(state.clone())

        return repeated_moves > self._repeated_moves_allowed

    def is_legal_move(self, state: TablutState, action: Action) -> bool:
        """Check if given an action, it is allowed from the current state according to the rules of game."""

        # controllo se sono fuori dal tabellone
        if action.from_.column > state.board.shape[1] - 1 or action.from_.row > state.board.shape[0] - 1 \
                or action.to.row > state.board.shape[0] - 1 or action.to.column > state.board.shape[1] - 1 \
                or action.from_.column < 0 or action.from_.row < 0 or action.to.row < 0 or action.to.column < 0:
            return False

        # controllo la casella di arrivo
        if state.pawn(action.to) != Pawn.EMPTY:
            return False

        # Mossa che arriva sopra una citadel
        if action.to in self._citadels:
            if action.from_ in self._citadels:
                if action.from_.row == action.to.row:
                    if action.from_.column - action.to.column > 5 or action.from_.column - action.to.column < -5:
                        return False
                elif action.from_.row - action.to.row > 5 or action.from_.row - action.to.row < -5:
                    return False
            else:
                return False

        # controllo se cerco di stare fermo
        if action.from_ == action.to:
            return False

        # controllo se sto muovendo una pedina giusta
        if state.turn == Player.WHITE:
            if not Pawn.is_white(state.pawn(action.from_)):
                return False

        if state.turn == Player.BLACK:
            if not state.pawn(action.from_) == Pawn.BLACK:
                return False

        # controllo di non muovere in diagonale
        if action.from_.row != action.to.row and action.from_.column != action.to.column:
            return False

        # controllo di non scavalcare pedine
        if action.from_.row == action.to.row:
            interval = action.to.column, action.from_.column if action.from_.column > action.to.column else action.from_.column + 1, action.to.column + 1
            for j in range(*interval):
                if self._is_climbing(state, action.from_.row, j, action):
                    return False

        else:
            interval = action.to.row, action.from_.row if action.from_.row > action.to.row else action.from_.row + 1, action.to.row + 1
            for i in range(*interval):
                if self._is_climbing(state, i, action.from_.column, action):
                    return False

        return True

    def _is_climbing(self, state: TablutState, i: int, j: int, action: Action) -> bool:
        coord = Coord(i, j)
        return state.pawn(coord) != Pawn.EMPTY or \
               (coord in self._citadels and action.from_ not in self._citadels)
