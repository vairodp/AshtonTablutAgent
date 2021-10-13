from dataclasses import dataclass


@dataclass
class Action:
    from_: str
    to: str
    turn: str

    @staticmethod
    def map_to_coord(row: int, column: int):
        return chr(column + 97).upper() + str(row + 1)

    @staticmethod
    def from_coord(coord: str):
        return Action.row(coord), Action.column(coord)

    @staticmethod
    def row(coord: str):
        return int(coord[1]) - 1

    @staticmethod
    def column(coord: str):
        return ord(coord[0].lower()) - 97


@dataclass
class State:
    board = None
    turn = None

    def __init__(self, d: dict):
        self.__dict__ = d
