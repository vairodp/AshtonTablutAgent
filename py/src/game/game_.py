from abc import ABCMeta, abstractmethod
from typing import Optional, Sequence
from . import State, Action


class Game(metaclass=ABCMeta):
    def __init__(self, players):
        self.n_players = players

    DRAW = -1

    @abstractmethod
    def start(self) -> State:
        """Generate and return the initial game state."""
        pass

    @abstractmethod
    def legal_actions(self, state: State) -> list[Action]:
        """"Return all the possible actions from the given state."""
        pass

    @abstractmethod
    def next_state(self, state: State, move: Action) -> State:
        """Advance the given state and return it."""
        pass

    @abstractmethod
    def winner(self, state: State) -> int:
        """Return the winner of the game, None else."""
        pass

    def previous_player(self, current_player: int) -> int:
        """Return the previous turn player"""
        return (self.n_players + current_player - 1) % self.n_players

    def nex_player(self, current_player: int) -> int:
        return (current_player + 1) % self.n_players

    def players(self):
        return range(self.n_players)

    @abstractmethod
    def get_action(self, from_state: State, to_state: State) -> Optional[Action]:
        """Return the action needed to go from one state to the following one"""
        pass

    @abstractmethod
    def cells_of(self, state: State, player: int) -> Sequence:
        """Return cells occupied by items of the given player"""
        pass
