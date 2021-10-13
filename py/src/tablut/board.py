import numpy as np
import copy
from . import Pawn, Coord


class Board(np.ndarray):
    _pawn_cells: dict[int, set[tuple]]

    def __new__(cls, board):
        return np.array(board).view(cls)

    def __init__(self, board):
        self._pawn_cells = {Pawn.BLACK: set(), Pawn.WHITE: set(), Pawn.KING: set()}
        self._index()

    def _index(self):
        for i in range(self.shape[0]):
            for j in range(self.shape[1]):
                pawn = self[i, j]
                if pawn in self._pawn_cells:
                    self._pawn_cells[pawn].add((i, j))

    @property
    def citadels(self) -> list[Coord]:
        return []

    def count(self, pawn: Pawn):
        return len(self._pawn_cells[pawn]) \
            if pawn in self._pawn_cells \
            else np.count_nonzero(self == pawn)

    def pawn_cells(self, pawn: Pawn) -> set[tuple]:
        return self._pawn_cells[pawn]

    def clone(self):
        return copy.deepcopy(self)

    def __setitem__(self, key, value):
        pawn = self[key]
        if pawn in self._pawn_cells:
            self._pawn_cells[pawn].remove(tuple(key))

        if value in self._pawn_cells:
            self._pawn_cells[value].add(tuple(key))

        super().__setitem__(key, value)

    def __deepcopy__(self, memo):
        cls = self.__class__
        result = cls.__new__(cls, self)
        memo[id(self)] = result
        for k, v in self.__dict__.items():
            setattr(result, k, copy.deepcopy(v, memo))
        return result
