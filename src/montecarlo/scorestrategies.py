import math
from abc import ABCMeta, abstractmethod
import montecarlo.node


class MonteCarloScoreStrategy(metaclass=ABCMeta):

    @abstractmethod
    def score(self, node: montecarlo.node.MonteCarloNode) -> float:
        pass


class Ucb1ScoreStrategy(MonteCarloScoreStrategy):
    def __init__(self, bias=2):
        """@param {bias} The square of the bias parameter in the UCB1 algorithm, defaults to 2."""
        self.bias = bias

    def score(self, node: montecarlo.node.MonteCarloNode) -> float:
        return (node.n_wins / node.n_simulations) \
               + math.sqrt(self.bias * math.log(node.parent.n_simulations) / node.n_simulations)
