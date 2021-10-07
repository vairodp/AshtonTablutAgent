import logging
import random
from abc import ABCMeta, abstractmethod

from const import Turn
from game import Game, Move, State
from montecarlo import MonteCarlo, Ucb1ScoreStrategy, MaxChildStrategy

logger = logging.getLogger(__name__)


class Player(metaclass=ABCMeta):
    def __init__(self, name: str, team: Turn):
        self.name = name
        self.team = team

    @abstractmethod
    def get_movement(self, state: State) -> Move:
        pass


class RandomMove(Move):
    def __init__(self):
        self.from_ = random.randint(0, 8), random.randint(0, 8)
        self.to = random.randint(0, 8), random.randint(0, 8)

    def __hash__(self):
        return hash(self.from_ + self.to)


class RandomPlayer(Player):
    def get_movement(self, state: State) -> Move:
        return RandomMove()


class MonteCarloPlayer(Player):

    def __init__(self, name, team: Turn, game: Game, timeout=50):
        super().__init__(name, team)
        self._timeout = timeout
        self._game = game
        self._mcts = MonteCarlo(
            game, Ucb1ScoreStrategy(), MaxChildStrategy())

    def get_movement(self, state: State) -> Move:
        self._mcts.run_search(state, self._timeout)
        move = self._mcts.best_move(state)
        stats = self._mcts.stats(state)
        logger.debug(stats.to_json())
        # state = game.nextState(state, play)
        # winner = game.winner(state)

        return move
