import copy
import numpy as np
import game
from . import Pawn, Coord

Board = np.ndarray


class TablutState(game.State):

    def __init__(self, move_history: list, board: list[list] or np.ndarray, player_turn: int):
        self.board = np.array(board)
        super(TablutState, self).__init__(move_history, player_turn)

    def pawn(self, coord: Coord) -> Pawn:
        """Get the pawn inside a specific box on the board."""
        return self.board[coord.row, coord.column]

    def set_pawn(self, coord: Coord, pawn: Pawn):
        self.board[coord.row, coord.column] = pawn

    def remove_pawn(self, coord: Coord):
        self.set_pawn(coord, Pawn.EMPTY)

    def clone(self):
        return copy.deepcopy(self)

    def number_of(self, color: Pawn) -> int:
        """Counts the number of checkers of a specific color on the board.
        \nNote: the king is not taken into account for white, it must be checked separately."""

        return np.count_nonzero(self.board == color)

    def __str__(self):
        return f'turn={self.turn}, board={self.board}'

    def __hash__(self):
        return hash(str(self.board) + str(self.turn))

    def __eq__(self, other):
        if not isinstance(other, TablutState):
            return False
        if self is other:
            return True

        return self.turn == other.turn and np.array_equal(self.board, other.board)
