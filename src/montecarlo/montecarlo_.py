import random
from time import time
from game import Game, State, Move
from . import MonteCarloScoreStrategy, MonteCarloStats, MonteCarloNodeStats, MonteCarloNode, MonteCarloBestMoveStrategy
import logging

logger = logging.getLogger(__name__)
# https://medium.com/@quasimik/implementing-monte-carlo-tree-search-in-node-js-5f07595104df


class MonteCarlo:

    def __init__(self, game: Game, score_strategy: MonteCarloScoreStrategy,
                 best_move_strategy: MonteCarloBestMoveStrategy, win_score=1, draw_score=0.5):
        self._game = game
        self._score_strategy = score_strategy
        self._best_move_strategy = best_move_strategy
        self._win_score = win_score
        self._draw_score = draw_score
        self._nodes: dict[int, MonteCarloNode] = {}

    def stats(self, state: State) -> MonteCarloStats:
        """Return MCTS statistics for this node and children nodes."""
        node: MonteCarloNode = self._nodes[hash(state)]
        stats = MonteCarloStats(node.n_simulations, node.n_wins)

        for child in node.children.values():
            if child.node is None:
                stats.children.append(
                    MonteCarloNodeStats(child.move, 0, 0))
            else:
                stats.children.append(MonteCarloNodeStats(
                    child.move, child.node.n_simulations, child.node.n_wins))
        return stats

    def make_node(self, state: State):
        """If state does not exist, create dangling node.
        \n@param {State} state - The state to make a dangling node for; its parent is set to null."""

        if hash(state) not in self._nodes:
            unexpanded_moves = self._game.legal_moves(state).copy()
            node = MonteCarloNode(None, None, state, unexpanded_moves)
            self._nodes[hash(state)] = node

    def best_move(self, state: State) -> Move:
        """Get the best move from available statistics."""
        self.make_node(state)

        # If not all children are expanded, not enough information
        if not self._nodes[hash(state)].is_fully_expanded():
            raise Exception("Not enough information!")

        best_move = None
        best_score = float('-inf')

        node = self._nodes[hash(state)]
        for move in node.all_moves():
            child_node = node.child_node(move)
            score = self._best_move_strategy.score(
                child_node.n_wins, child_node.n_simulations)

            if score > best_score:
                best_move = move
                best_score = score
        
        logger.debug(f'Best score = {best_score}')
        return best_move

    def run_search(self, state: State, timeout_s: int):
        """From given state, run as many simulations as possible until the time limit (in seconds), building statistics."""

        self.make_node(state)

        end = time() + timeout_s
        while time() < end:
            node = self._select(state)
            winner = self._game.winner(node.state)

            # if the match is not closed and there are possible moves from the selected node
            if winner is None and not node.is_leaf():
                node = self._expand(node)
                winner = self._simulate(node)

            if winner is None:
                raise Exception('Non ci sono più mosse disponibili')

            self._backpropagate(node, winner)

        node = self._nodes[hash(state)]
        unexpanded = len(node.unexpanded_moves())
        total = len(node.all_moves())
        logger.debug(f'Unexpanded = {unexpanded} / {total}')

    def _select(self, state: State):
        """Phase 1, Selection: Select until not fully expanded OR leaf."""
        node = self._nodes[hash(state)]

        while node.is_fully_expanded() and not node.is_leaf():
            best_score = float('-inf')
            best_move = None

            # Get best move from current node
            for move in node.all_moves():
                child_node = node.child_node(move)
                child_score = child_node.score(self._score_strategy)

                if child_score > best_score:
                    best_score = child_score
                    best_move = move

            node = node.child_node(best_move)

        return node

    def _expand(self, node: MonteCarloNode):
        """Phase 2, Expansion: Expand a random unexpanded child node."""
        unexpanded_moves = node.unexpanded_moves()
        if len(unexpanded_moves) == 0:
            logger.critical(f'Len = 0, state = {node.state}')
        move = random.choice(unexpanded_moves)

        child_state = self._game.next_state(node.state, move)
        child_unexpanded_moves = self._game.legal_moves(child_state)
        child_node = node.expand(move, child_state, child_unexpanded_moves)

        return child_node

    def _simulate(self, node: MonteCarloNode):
        """Phase 3, Simulation: Play game to terminal state using random moves, return winner."""
        state = node.state
        winner = self._game.winner(state)

        while winner is None:
            move = random.choice(self._game.legal_moves(state))
            state = self._game.next_state(state, move)
            winner = self._game.winner(state)

        return winner

    def _backpropagate(self, node: MonteCarloNode, winner: int):
        """Phase 4, Backpropagation: Update ancestor statistics."""
        while node is not None:
            node.n_simulations += 1

            if winner == Game.DRAW:
                node.n_wins += self._draw_score

            # if black wins, the win counter of each visited white node is increased, as white statistics are used for black's choice (and viceversa)
            elif node.state.is_player_turn(self._game.previous_player(winner)):
                node.n_wins += self._win_score

            node = node.parent
