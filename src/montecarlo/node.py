from abc import ABCMeta, abstractmethod
from dataclasses import dataclass
from typing import Sequence

from game import Action, State


class MonteCarloNode:
    pass


class MonteCarloScoreStrategy(metaclass=ABCMeta):

    @abstractmethod
    def score(self, node: MonteCarloNode) -> float:
        pass


@dataclass
class MonteCarloChild:
    action: Action
    node: MonteCarloNode = None

    def __str__(self):
        return f'{self.action}, {self.node}'


MonteCarloChildren = dict[Action, MonteCarloChild]


class MonteCarloNode:
    """ Class representing a node in the search tree. Stores tree search stats. """

    def __init__(self, parent: MonteCarloNode, action: Action, state: State, unexpanded_moves: Sequence[Action]):
        self.action = action
        self.state = state

        # MonteCarlo stuff
        self.n_simulations = 0
        self.win_score = 0

        #Rave stuff
        self.n_rave = 0
        self.rave_score = 0

        # Tree stuff
        self.parent = parent
        self.children: MonteCarloChildren = {}
        for action in unexpanded_moves:
            self.children[action] = MonteCarloChild(action)

    def child_node(self, action: Action) -> MonteCarloNode:
        """Get the MonteCarloNode corresponding to the given action."""
        child = self.children[action]

        if child is None:
            raise Exception('No such action!')
        elif child.node is None:
            raise Exception('Child is not expanded!')

        return child.node

    def children_nodes(self):
        return [child.node for child in self.children.values() if child.node is not None]

    def expand(self, action: Action, child_state: State, child_unexpanded_moves: Sequence[Action]) -> MonteCarloNode:
        """Expand the specified child action and return the new child node.
          Add the node to the array of children nodes.
          Remove the action from the array of unexpanded plays."""

        if action not in self.children:
            raise Exception('No such action!')

        child_node = MonteCarloNode(
            self, action, child_state, child_unexpanded_moves)
        self.children[action] = MonteCarloChild(action, child_node)

        return child_node

    def all_actions(self) -> Sequence[Action]:
        """Get all legal moves from this node."""

        return [child.action for child in self.children.values()]

    def unexpanded_actions(self) -> Sequence[Action]:
        """Get all unexpanded legal actions from this node."""

        return [child.action for child in self.children.values() if child.node is None]

    def is_fully_expanded(self) -> bool:
        return all(child.node is not None for child in self.children.values())

    def is_leaf(self) -> bool:
        """Whether self node is terminal in the game tree, NOT INCLUSIVE of termination due to winning."""

        return len(self.children) == 0

    def score(self, score_strategy: MonteCarloScoreStrategy) -> float:
        return score_strategy.score(self)
