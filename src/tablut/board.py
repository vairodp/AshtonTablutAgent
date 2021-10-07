from typing import Sequence
import numpy as np
from . import Pawn, Coord


class Board(np.ndarray):
    _pawn_coords: dict[Pawn, list[Coord]] = {Pawn.WHITE: [], Pawn.KING: [], Pawn.BLACK: [], Pawn.THRONE: []}

    def __new__(cls, board: list[list[Pawn]]):
        obj = np.asarray(board).view(cls)
        return obj

    def __init__(self, board: list[list[Pawn]]):
        self._index()

    def _index(self):
        for i in range(self.shape[0]):
            for j in range(self.shape[1]):
                if self[i, j] != Pawn.EMPTY:
                    self._pawn_coords[self[i, j]].append(Coord(i, j))

    def pawns(self, pawn: Pawn) -> Sequence[Coord]:
        """Get coords of given pawn type"""
        return self._pawn_coords[pawn].copy()