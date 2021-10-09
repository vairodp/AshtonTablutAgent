from abc import ABCMeta, abstractmethod
from typing import Optional
from . import State, Action


class Game(metaclass=ABCMeta):
    def __init__(self, players):
        self.players = players

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
        return (current_player - 1) % self.players

    @abstractmethod
    def get_action(self, from_state: State, to_state: State) -> Optional[Action]:
        """Return the action needed to go from one state to the following one"""
        pass
