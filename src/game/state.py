from abc import ABCMeta, abstractmethod


class State(metaclass=ABCMeta):
    def __init__(self, action_history, player_turn: int):
        self.action_history = action_history
        self.turn = player_turn

    def is_player_turn(self, player_turn):
        return self.turn == player_turn

    def __hash__(self):
        return hash(str(self.action_history))

    def history_hash(self):
        return hash(tuple(self.action_history))
