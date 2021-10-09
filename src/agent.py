from abc import ABCMeta, abstractmethod
import logging

from const import Turn
from game import Game, State, Action
from montecarlo import MonteCarlo, Ucb1ScoreStrategy, MaxChildStrategy

logger = logging.getLogger(__name__)


class Agent(metaclass=ABCMeta):
    @abstractmethod
    def update_state(self, state: State) -> State:
        """Update state after an opponent move"""
        pass

    @abstractmethod
    def get_action(self, state: State) -> Action:
        """Get action from given state"""
        pass


class MctsAgent(Agent):
    def __init__(self, game: Game, timeout):
        self._timeout = timeout
        self._game = game
        self._mcts = MonteCarlo(game, Ucb1ScoreStrategy(), MaxChildStrategy())
        self.root_state: State = self._game.start()

    def update_state(self, state: State) -> State:
        action = self._game.get_action(self.root_state, state)
        if action is not None:
            self.add_opponent_action(action)
            logger.info(f'Opponent action: from {action.from_} to {action.to}')
        return self.root_state

    def add_opponent_action(self, action: Action):
        self.root_state = self._game.next_state(self.root_state, action)

    def get_action(self, state: State) -> Action:
        logger.info('Computing best action ...')

        self._mcts.run_search(state, self._timeout)
        move = self._mcts.best_action(state)
        stats = self._mcts.stats(state)
        logger.debug(stats.to_json())

        self.root_state = self._game.next_state(state, move)

        return move