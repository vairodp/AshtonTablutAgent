from abc import ABCMeta, abstractmethod


class State(metaclass=ABCMeta):
    def __init__(self, move_history, player_turn: int):
        self.move_history = move_history
        self.turn = player_turn

    def is_player_turn(self, player_turn):
        return self.turn == player_turn

    def __hash__(self):
        return hash(str(self.move_history))
