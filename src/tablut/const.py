class Player(int):
    BLACK = 0
    WHITE = 1

    @staticmethod
    def next(value):
        return (value + 1) % 2

    @staticmethod
    def previous(value):
        return (value - 1) % 2


class Pawn(int):
    WHITE = Player.WHITE
    BLACK = Player.BLACK
    EMPTY = -1
    THRONE = 10
    KING = 100

    @staticmethod
    def is_white(pawn):
        return pawn == Pawn.WHITE or pawn == Pawn.KING

    @staticmethod
    def player(pawn) -> Player:
        if pawn == Pawn.BLACK:
            return Player.BLACK

        if Pawn.is_white(pawn):
            return Player.WHITE

        return None
