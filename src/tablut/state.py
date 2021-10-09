import copy
import numpy as np
from collections import Counter
from game import State
from . import Pawn, Coord, Board


class TablutState(State):

    def __init__(self, board: Board, player_turn: int):
        super(TablutState, self).__init__([], player_turn)
        self.previous_states = Counter()
        self.board: Board = board

    def pawn(self, coord: Coord) -> Pawn:
        """Get the pawn inside a specific box on the board."""
        return self.board[coord]

    def set_pawn(self, coord: Coord, pawn: Pawn):
        self.board[coord] = pawn

    def remove_pawn(self, coord: Coord):
        self.set_pawn(coord, Pawn.EMPTY)

    def clone(self):
        return copy.deepcopy(self)

    def number_of(self, pawn: Pawn) -> int:
        """Counts the number of checkers of a specific color on the board.
        \nNote: the king is not taken into account for white, it must be checked separately."""

        return self.board.count(pawn)

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
