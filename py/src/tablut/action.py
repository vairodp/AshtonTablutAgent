import copy

from game import Action
from dataclasses import dataclass


@dataclass
class Coord(tuple):
    row: int
    column: int

    def __new__(cls, row: int, column: int):
        return tuple.__new__(Coord, (row, column))

    def __eq__(self, other):
        if not isinstance(other, Coord):
            return False

        return self is other or (self.row == other.row and self.column == other.column)

    def __hash__(self):
        return hash((self.row, self.column))

    def __str__(self):
        return chr(self.column + 97).upper() + str(self.row + 1)

    def __deepcopy__(self, memo):
        cls = self.__class__
        result = cls.__new__(cls, *self)
        memo[id(self)] = result
        for k, v in self.__dict__.items():
            setattr(result, k, copy.deepcopy(v, memo))
        return result

    @staticmethod
    def from_str(coord: str):
        return Coord(Coord.row_from_str(coord), Coord.column_from_str(coord))

    @staticmethod
    def row_from_str(coord: str) -> int:
        return int(coord[1:]) - 1

    @staticmethod
    def column_from_str(coord: str) -> int:
        return ord(coord[0].lower()) - 97


class Action(Action):
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
