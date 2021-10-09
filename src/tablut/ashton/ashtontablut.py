from typing import Sequence, Optional

import numpy as np

from game import Game
from tablut import TablutState, Player, Pawn, Coord, Action
from tablut.action import Coords
from .board import AshtonBoard
from .capturerules import AshtonCaptureRules


class AshtonTablutGame(Game):
    """Number of repeated states that can occur before a draw."""
    _repeated_moves_allowed: int

    def __init__(self, repeated_moves_allowed: int):
        super().__init__(players=2)

        self._repeated_moves_allowed = repeated_moves_allowed

    def start(self) -> TablutState:
        """Generate and return the initial game state."""

        return TablutState(AshtonBoard.initial_board(), Player.WHITE)

    def get_action(self, from_state: TablutState, to_state: TablutState) -> Optional[Action]:
        """Return the action needed to go from one state to the following one"""
        if from_state.turn == to_state.turn:
            return None
        coords = list(zip(*np.where(from_state.board != to_state.board)))
        for from_coord in coords:
            if Pawn.player(from_state.board[from_coord]) == from_state.turn:
                for to_coord in coords:
                    action = Action(Coord(*from_coord), Coord(*to_coord))
                    if from_coord != to_coord and self.is_legal_action(from_state, action):
                        return action
        return None

    # TODO: indicizzare le PAWN != THRONE in modo da rendere più efficiente la ricerca, Inoltre l'assenza di mosse disponibili costituisce vittoria / sconfitta
    def legal_actions(self, state: TablutState) -> Sequence[Action]:
        """Return all the possible actions from the given state."""
        legal_moves: list[Action] = []

        current_player_pawn_cells = list(state.board.pawn_cells(state.turn))
        if state.turn == Player.WHITE:
            current_player_pawn_cells.extend(state.board.pawn_cells(Pawn.KING))

        for i, j in current_player_pawn_cells:

            top_coords = (Coord(k, j) for k in range(i - 1, -1, -1))
            bottom_coords = (Coord(k, j) for k in range(i + 1, state.board.shape[0]))
            left_coords = (Coord(i, k) for k in range(j - 1, -1, -1))
            right_coords = (Coord(i, k) for k in range(j + 1, state.board.shape[1]))

            directions = [top_coords, bottom_coords, left_coords, right_coords]

            from_ = Coord(i, j)
            for coords in directions:
                for coord in coords:
                    action = Action(from_, coord)
                    if self.is_legal_action(state, action):
                        legal_moves.append(action)
                    else:
                        break

        return legal_moves

    def next_state(self, state: TablutState, action: Action) -> TablutState:
        """Advance the given state and return it. Ensure the action is legal before."""
        new_state = state.clone()
        new_state.previous_states[hash(state)] += 1

        self._move_pawn(new_state, action)
        AshtonCaptureRules.remove_captured(new_state, action)

        new_state.action_history.append(action)
        new_state.turn = Player.next(new_state.turn)

        return new_state

    def _move_pawn(self, state, action: Action):
        from_pawn = state.pawn(action.from_)
        to_pawn = Pawn.THRONE if action.from_ == Coords.E5 else Pawn.EMPTY

        state.set_pawn(action.from_, to_pawn)
        state.set_pawn(action.to, from_pawn)

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
        ((row, col),) = state.board.pawn_cells(Pawn.KING)
        return row == 0 or row == state.board.shape[0] - 1 \
               or col == 0 or col == state.board.shape[1] - 1

    def _is_a_draw(self, state: TablutState) -> bool:
        repeated_moves = state.previous_states[hash(state)]

        return repeated_moves > self._repeated_moves_allowed

    def is_legal_action(self, state: TablutState, action: Action) -> bool:
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
        if action.to in state.board.citadels:
            if action.from_ in state.board.citadels:
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
               (coord in state.board.citadels and action.from_ not in state.board.citadels)
