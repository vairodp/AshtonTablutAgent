from abc import ABCMeta, abstractmethod
from dataclasses import dataclass
from typing import Sequence

from game import Move, State


class MonteCarloNode:
    pass


class MonteCarloScoreStrategy(metaclass=ABCMeta):

    @abstractmethod
    def score(self, node: MonteCarloNode) -> float:
        pass


@dataclass
class MonteCarloChild:
    move: Move
    node: MonteCarloNode = None

    def __str__(self):
        return f'{self.move}, {self.node}'


MonteCarloChildren = dict[Move, MonteCarloChild]


class MonteCarloNode:
    """ Class representing a node in the search tree. Stores tree search stats. """

    def __init__(self, parent: MonteCarloNode, move: Move, state: State, unexpanded_moves: Sequence[Move]):
        self.move = move
        self.state = state

        # MonteCarlo stuff
        self.n_simulations = 0
        self.n_wins = 0

        # Tree stuff
        self.parent = parent
        self.children: MonteCarloChildren = {}
        for move in unexpanded_moves:
            self.children[move] = MonteCarloChild(move)

    def child_node(self, move: Move) -> MonteCarloNode:
        """Get the MonteCarloNode corresponding to the given move."""
        child = self.children[move]

        if child is None:
            raise Exception('No such move!')
        elif child.node is None:
            raise Exception('Child is not expanded!')

        return child.node

    def expand(self, move: Move, child_state: State, child_unexpanded_moves: Sequence[Move]) -> MonteCarloNode:
        """Expand the specified child move and return the new child node.
          Add the node to the array of children nodes.
          Remove the move from the array of unexpanded plays."""

        if move not in self.children:
            raise Exception('No such move!')

        child_node = MonteCarloNode(
            self, move, child_state, child_unexpanded_moves)
        self.children[move] = MonteCarloChild(move, child_node)

        return child_node

    def all_moves(self) -> Sequence[Move]:
        """Get all legal moves from this node."""

        return [child.move for child in self.children.values()]

    def unexpanded_moves(self) -> Sequence[Move]:
        """Get all unexpanded legal moves from this node."""

        return [child.move for child in self.children.values() if child.node is None]

    def is_fully_expanded(self) -> bool:
        return all(child.node is not None for child in self.children.values())

    def is_leaf(self) -> bool:
        """Whether self node is terminal in the game tree, NOT INCLUSIVE of termination due to winning."""

        return len(self.children) == 0

    def score(self, score_strategy: MonteCarloScoreStrategy) -> float:
        return score_strategy.score(self)
