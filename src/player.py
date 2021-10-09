import logging
import random
from abc import ABCMeta, abstractmethod

from agent import Agent
from const import Turn
from game import Game, Action, State
from montecarlo import MonteCarlo, Ucb1ScoreStrategy, MaxChildStrategy

logger = logging.getLogger(__name__)


class Player(metaclass=ABCMeta):
    def __init__(self, name: str, team: Turn):
        self.name = name
        self.team = team

    @abstractmethod
    def get_action(self, state: State) -> Action:
        pass


class RandomAction(Action):
    def __init__(self):
        self.from_ = random.randint(0, 8), random.randint(0, 8)
        self.to = random.randint(0, 8), random.randint(0, 8)

    def __hash__(self):
        return hash(self.from_ + self.to)


class RandomPlayer(Player):
    def get_action(self, state: State) -> Action:
        return RandomAction()


class AgentPlayer(Player):

    def __init__(self, name, team: Turn, agent:Agent):
        super().__init__(name, team)
        self._agent = agent

    def get_action(self, state: State) -> Action:
        state = self._agent.update_state(state)
        return self._agent.get_action(state)
