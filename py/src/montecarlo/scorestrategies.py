import math
from abc import ABCMeta, abstractmethod
import montecarlo.node


class MonteCarloScoreStrategy(metaclass=ABCMeta):

    @abstractmethod
    def score(self, node: montecarlo.node.MonteCarloNode) -> float:
        pass


class Ucb1ScoreStrategy(MonteCarloScoreStrategy):
    def __init__(self, exploration=2):
        """@param {exploration} The square of the exploration parameter in the UCB1 algorithm."""
        self.exploration = exploration

    def score(self, node: montecarlo.node.MonteCarloNode) -> float:
        return (node.win_score / node.n_simulations) \
               + math.sqrt(self.exploration * math.log(node.parent.n_simulations) / node.n_simulations)


class RaveScoreStrategy(MonteCarloScoreStrategy):
    def __init__(self, exploration=2, rave=300):
        self.exploration = exploration
        self.rave = rave

    def score(self, node: montecarlo.node.MonteCarloNode) -> float:
        alpha = max(0, (self.rave - node.n_simulations) / self.rave)
        uct = Ucb1ScoreStrategy(self.exploration).score(node)
        amaf = node.rave_score / node.n_rave if node.n_rave != 0 else 0

        return (1 - alpha) * uct + alpha * amaf
