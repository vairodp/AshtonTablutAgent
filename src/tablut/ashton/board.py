import numpy as np
from tablut.action import Coords
from tablut import Board, Coord, Pawn

__citadels__ = [Coords.A4, Coords.A5, Coords.A6, Coords.B5,
                Coords.D1, Coords.E1, Coords.F1, Coords.E2,
                Coords.I4, Coords.I5, Coords.I6, Coords.H5,
                Coords.D9, Coords.E9, Coords.F9, Coords.E8]


class AshtonBoard(Board):
    @property
    def citadels(self) -> list[Coord]:
        return __citadels__

    @staticmethod
    def initial_board():
        board = np.array([[Pawn.EMPTY] * 9] * 9)
        for coord in __citadels__:
            board[coord] = Pawn.BLACK
        board[2:7, Coords.E4.column] = Pawn.WHITE
        board[Coords.I5.row, 2:7] = Pawn.WHITE
        board[Coords.E5] = Pawn.KING

        return AshtonBoard(board)
