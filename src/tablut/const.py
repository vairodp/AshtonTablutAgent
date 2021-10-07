class __Player:
    BLACK = 0
    WHITE = 1

    def next(self, value):
        return (value + 1) % 2

    def previous(self, value):
        return (value - 1) % 2


Player = __Player()


class __Pawn:
    WHITE = Player.WHITE
    BLACK = Player.BLACK
    EMPTY = -1
    THRONE = 10
    KING = 100

    def is_white(self, pawn):
        return pawn == self.WHITE or pawn == self.KING


Pawn = __Pawn()
