from game import Move
from dataclasses import dataclass


@dataclass
class Coord(tuple):
    row: int
    column: int

    def __new__(cls, row:int, column: int):
        return tuple.__new__(Coord, (row, column))

    def __eq__(self, other):
        if not isinstance(other, Coord):
            return False

        return self is other or (self.row == other.row and self.column == other.column)

    def __hash__(self):
        return hash((self.row, self.column))

    def __str__(self):
        return str((self.row, self.column))

    @staticmethod
    def from_str(coord: str):
        return Coord(Coord.row_from_str(coord), Coord.column_from_str(coord))

    @staticmethod
    def row_from_str(coord: str) -> int:
        return int(coord[1:]) - 1

    @staticmethod
    def column_from_str(coord: str) -> int:
        return ord(coord[0].lower()) - 97


class Action(Move):
    def __init__(self, from_: Coord, to: Coord):
        self.from_ = from_
        self.to = to

    @staticmethod
    def from_str_coords(from_: str, to: str):
        return Action(Coord.from_str(from_), Coord.from_str(to))

    def __hash__(self):
        return hash((self.from_, self.to))

    def __str__(self):
        return f'{self.from_}, {self.to}'
