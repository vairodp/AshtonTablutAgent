class Pawn(str):
    EMPTY = 'EMPTY'
    WHITE = 'WHITE'
    BLACK = 'BLACK'
    THRONE = 'THRONE'
    KING = 'KING'


class Turn(str):
    WHITE = 'WHITE'
    BLACK = 'BLACK'
    WHITE_WIN = 'WHITEWIN'
    BLACK_WIN = 'BLACKWIN'
    DRAW = 'DRAW'


class Settings:
    WHITE_PORT = 5800
    BLACK_PORT = 5801
