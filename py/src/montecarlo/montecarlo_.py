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
        stats = MonteCarloStats(node.n_simulations, node.win_score, node.n_rave, node.rave_score)

        for child in node.children.values():
            if child.node is None:
                stats.children.append(
                    MonteCarloNodeStats(child.action))
            else:
                stats.children.append(
                    MonteCarloNodeStats(child.action,
                                        child.node.n_simulations, child.node.win_score,
                                        child.node.n_rave, child.node.rave_score))
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
                final_state, winner = self._simulate(node)

            if winner is None:
                raise Exception('No actions available')

            self._backpropagate(node, final_state, winner)

        node = self._nodes[state.history_hash()]
        unexpanded = len(node.unexpanded_actions())
        total = len(node.all_actions())
        logger.debug(f'Unexpanded = {unexpanded} / {total}')
        logger.debug(f'n_simulations {node.n_simulations}, win_score {node.win_score}')

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

        # self._nodes[child_state.history_hash()] = child_node

        # if child_state.turn == 0:
        #     logger.debug(f'Expanding action {action}, parent = {self._nodes[node.state.history_hash()].action}, turn = {child_state.turn}')

        return child_node

    def _simulate(self, node: MonteCarloNode, default_policy=random.choice): #-> (State, Optional[int])
        """Phase 3, Simulation: Play game to terminal state using random actions, return winner."""
        state = node.state
        winner = self._game.winner(state)

        while winner is None:
            action = default_policy(self._game.legal_actions(state))
            state = self._game.next_state(state, action)
            winner = self._game.winner(state)

        return state, winner

    def _backpropagate(self, node: MonteCarloNode, final_state: State, winner: int):
        """Phase 4, Backpropagation: Update ancestor statistics."""
        player_cells = self._player_cells(final_state)
        previous_player = self._game.previous_player(winner)
        while node is not None:
            node.n_simulations += 1

            if winner == Game.DRAW:
                reward = self._draw_score

            # score of child node is used by the parent.
            # Therefore we increment the child node score if the parent player has won
            elif node.state.is_player_turn(previous_player):
                reward = self._win_score
            else:
                reward = -self._win_score

            node.win_score += reward

            reward = -reward
            for child_node in node.children_nodes():
                # If the pawn moved is still in the final state and the player has won (lost), the action was good (bad)
                # Therefore, we increase the child score
                if tuple(child_node.action.to) in player_cells[node.state.turn]:
                    child_node.n_rave += 1
                    child_node.rave_score += reward

            node = node.parent

    def _player_cells(self, state: State) -> dict[int, set]:
        player_cells = {}
        for player in self._game.players():
            player_cells[player] = set(self._game.cells_of(state, player))

        return player_cells

# Commentare tutta sta porcheria RAVE che tanto non funziona (per ora)
# TODO: add move priority in order to choice move in simulation phase
# They can be summarised by applying four prioritised rules after any opponent move a:
# 1. If a put some of our stones into atari, play a saving move at random.
# 2. Otherwise, if one of the 8 intersections surrounding a matches a simple pattern
# for cutting or hane, randomly play one.
# 3. Otherwise, if any opponent stone can be captured, play a capturing move at random.
# 4. Otherwise play a random move.

# Successivamente è possibile usare supervised learning per stimare i pesi della default_policy
