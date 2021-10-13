from . import Action


class ActionException(Exception):
    def __init__(self, action: Action):
        self.message = f'The format of the action is not correct: {action}'


class BoardException(Exception):
    def __init__(self, action: Action):
        self.message = f'The move is out of the board: {action}'


class CitadelException(Exception):
    def __init__(self, action: Action):
        self.message = f'Move into a citadel: {action}'


class ClimbingCitadelException(Exception):
    def __init__(self, action: Action):
        self.message = f'A pawn is tryng to climb over a citadel: {action}'


class ClimbingException(Exception):
    def __init__(self, action: Action):
        self.message = f'A pawn is tryng to climb over another pawn: {action}'


class DiagonalException(Exception):
    def __init__(self, action: Action):
        self.message = f'Diagonal move is not allowed: {action}'


class OccupitedException(Exception):
    def __init__(self, action: Action):
        self.message = f'Move into a box occupited form another pawn: {action}'


class PawnException(Exception):
    def __init__(self, action: Action):
        self.message = f'The player is tryng to move a wrong pawn: {action}'


class StopException(Exception):
    def __init__(self, action: Action):
        self.message = f'Action not allowed, a pawn need to move: {action}'


class ThroneException(Exception):
    def __init__(self, action: Action):
        self.message = f'Player {action.turn} is tryng to go into the castle: {action}'
