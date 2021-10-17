from abc import ABCMeta, abstractmethod
import math
from . import MonteCarloNode


class MonteCarloBestMoveStrategy(metaclass=ABCMeta):

    @abstractmethod
    def score(self, node: MonteCarloNode) -> float:
        pass


class MaxChildStrategy(MonteCarloBestMoveStrategy):
    def score(self, node: MonteCarloNode) -> float:
        return node.win_score / node.n_simulations


class RobustChildStrategy(MonteCarloBestMoveStrategy):
    def score(self, node: MonteCarloNode) -> float:
        return node.n_simulations


class PLogNStrategy(MonteCarloBestMoveStrategy):
    def score(self, node: MonteCarloNode) -> float:
        return (node.win_score / node.n_simulations) * math.log(node.n_simulations)
