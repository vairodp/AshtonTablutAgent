from abc import ABCMeta, abstractmethod
from . import State, Move


class Game(metaclass=ABCMeta):
    def __init__(self, players):
        self.players = players

    DRAW = -1
    # @abstractmethod
    # def start(self) -> State:
    #     """Generate and return the initial game state."""
    #     pass

    @abstractmethod
    def legal_moves(self, state: State) -> list[Move]:
        """"Return all the possible moves from the given state."""
        pass

    @abstractmethod
    def next_state(self, state: State, move: Move) -> State:
        """Advance the given state and return it."""
        pass

    @abstractmethod
    def winner(self, state: State) -> int:
        """Return the winner of the game, None else."""
        pass

    def previous_player(self, current_player: int) -> int:
        """Return the previous turn player"""
        return (current_player - 1) % self.players
