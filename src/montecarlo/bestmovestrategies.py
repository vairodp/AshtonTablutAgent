from abc import ABCMeta, abstractmethod
import math


class MonteCarloBestMoveStrategy(metaclass=ABCMeta):

    @abstractmethod
    def score(self, n_wins: int, n_simulations: int) -> float:
        pass


class MaxChildStrategy(MonteCarloBestMoveStrategy):
    def score(self, n_wins: int, n_simulations: int) -> float:
        return n_wins / n_simulations


class RobustChildStrategy(MonteCarloBestMoveStrategy):
    def score(self, n_wins: int, n_simulations: int) -> float:
        return n_simulations

class PLogNStrategy(MonteCarloBestMoveStrategy):
    def score(self, n_wins: int, n_simulations: int) -> float:
        return (n_wins / n_simulations) * math.log(n_simulations)