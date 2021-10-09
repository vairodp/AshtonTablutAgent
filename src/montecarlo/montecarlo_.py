import random
from time import time
from typing import Optional

from game import Game, State, Action
from utils import argmax
from . import MonteCarloScoreStrategy, MonteCarloStats, MonteCarloNodeStats, MonteCarloNode, MonteCarloBestMoveStrategy
import logging

logger = logging.getLogger(__name__)


# https://medium.com/@quasimik/implementing-monte-carlo-tree-search-in-node-js-5f07595104df


class MonteCarlo:

    def __init__(self, game: Game, score_strategy: MonteCarloScoreStrategy,
                 best_action_strategy: MonteCarloBestMoveStrategy, win_score=1, draw_score=0.5):
        self._game = game
        self._score_strategy = score_strategy
        self._best_action_strategy = best_action_strategy
        self._win_score = win_score
        self._draw_score = draw_score
        self._nodes: dict[int, MonteCarloNode] = {}
        self._root_node: MonteCarloNode = None

    def stats(self, state: State) -> MonteCarloStats:
        """Return MCTS statistics for this node and children nodes."""
        node: MonteCarloNode = self._nodes[state.history_hash()]
        stats = MonteCarloStats(node.n_simulations, node.win_score)

        for child in node.children.values():
            if child.node is None:
                stats.children.append(
                    MonteCarloNodeStats(child.action, 0, 0))
            else:
                stats.children.append(MonteCarloNodeStats(
                    child.action, child.node.n_simulations, child.node.win_score))
        return stats

    def make_node(self, state: State):
        """If state does not exist, create dangling node.
        \n@param {State} state - The state to make a dangling node for; its parent is set to null."""

        action_history = state.history_hash()
        if action_history not in self._nodes:
            unexpanded_actions = self._game.legal_actions(state).copy()
            node = MonteCarloNode(None, None, state, unexpanded_actions)
            self._nodes[action_history] = node

    def update_root_node(self, action: Action, state: State):
        action_history = state.history_hash()
        node = self._nodes.get(action_history)

        if node is None and self._root_node is not None and action in self._root_node.children:
            logger.debug(f'Node found for action {action}')
            child_node = self._root_node.children[action]
            action = child_node.action
            node = child_node.node

        if node is None:
            unexpanded_actions = self._game.legal_actions(state).copy()
            node = MonteCarloNode(self._root_node, action, state, unexpanded_actions)

        self._nodes[action_history] = node
        self._root_node = node

    def best_action(self, state: State) -> Action:
        """Get the best action from available statistics."""
        node = self._nodes.get(state.history_hash())
        if node is None:
            raise Exception('Run search before getting best move')

        # If not all children are expanded, not enough information
        if not node.is_fully_expanded():
            raise Exception("Not enough information!")

        best_action = argmax(
            lambda action: self._best_action_strategy.score(node.child_node(action)),
            node.all_actions())

        logger.debug(f'Best action = {best_action}')
        return best_action

    def run_search(self, state: State, timeout_s: int):
        """From given state, run as many simulations as possible until the time limit (in seconds), building statistics."""
        self.make_node(state)

        node = self._nodes[state.history_hash()]
        unexpanded = len(node.unexpanded_actions())
        total = len(node.all_actions())
        logger.debug(f'Unexpanded = {unexpanded} / {total}')

        end = time() + timeout_s
        while time() < end:
            node = self._select(state)
            winner = self._game.winner(node.state)

            # if the match is not closed and there are possible actions from the selected node
            if winner is None and not node.is_leaf():
                node = self._expand(node)
                winner = self._simulate(node)

            if winner is None:
                raise Exception('No actions available')

            self._backpropagate(node, winner)

        node = self._nodes[state.history_hash()]
        unexpanded = len(node.unexpanded_actions())
        total = len(node.all_actions())
        logger.debug(f'Unexpanded = {unexpanded} / {total}')

    def _select(self, state: State):
        """Phase 1, Selection: Select until not fully expanded OR leaf."""
        node = self._nodes[state.history_hash()]

        while node.is_fully_expanded() and not node.is_leaf():
            best_action = argmax(
                lambda action: node.child_node(action).score(self._score_strategy),
                node.all_actions())

            node = node.child_node(best_action)

        return node

    def _expand(self, node: MonteCarloNode):
        """Phase 2, Expansion: Expand a random unexpanded child node."""
        unexpanded_actions = node.unexpanded_actions()
        if len(unexpanded_actions) == 0:
            logger.critical(f'Len = 0, state = {node.state}')
        action = random.choice(unexpanded_actions)

        child_state = self._game.next_state(node.state, action)
        child_unexpanded_actions = self._game.legal_actions(child_state)
        child_node = node.expand(action, child_state, child_unexpanded_actions)

        logger.debug(f'Expanding action {action}, turn = {child_state.turn}')

        return child_node

    def _simulate(self, node: MonteCarloNode, choice=random.choice) -> Optional[int]:
        """Phase 3, Simulation: Play game to terminal state using random actions, return winner."""
        state = node.state
        winner = self._game.winner(state)

        while winner is None:
            action = choice(self._game.legal_actions(state))
            state = self._game.next_state(state, action)
            winner = self._game.winner(state)

        return winner

    def _backpropagate(self, node: MonteCarloNode, winner: int):
        """Phase 4, Backpropagation: Update ancestor statistics."""
        while node is not None:
            node.n_simulations += 1

            if winner == Game.DRAW:
                node.win_score += self._draw_score

            # if black wins, the win counter of each visited white node is increased, as white statistics are used for black's choice (and viceversa)
            elif node.state.is_player_turn(self._game.previous_player(winner)):
                node.win_score += self._win_score
            else:
                node.win_score -= self._win_score

            node = node.parent
