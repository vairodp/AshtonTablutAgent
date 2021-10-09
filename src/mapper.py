import game
import tablut
from protocol import Action, State
from const import Turn, Pawn
from game import Move


class Mapper:

    _protocol_turns = {tablut.Player.WHITE: Turn.WHITE,
                       tablut.Player.BLACK: Turn.BLACK}

    _game_turns = dict((v, k) for k, v in _protocol_turns.items())

    _protocol_pawns = {tablut.Pawn.WHITE: Pawn.WHITE,
                       tablut.Pawn.BLACK: Pawn.BLACK,
                       tablut.Pawn.EMPTY: Pawn.EMPTY,
                       tablut.Pawn.THRONE: Pawn.THRONE,
                       tablut.Pawn.KING: Pawn.KING}

    _game_pawns = dict((v, k) for k, v in _protocol_pawns.items())

    def map_to_protocol_action(self, state: game.State, move: Move) -> Action:
        return Action(Action.map_to_coord(*move.from_),
                      Action.map_to_coord(*move.to),
                      turn=self.map_to_protocol_turn(state.turn))
        # return Action(move.from_, move.to,
        #               turn=self.map_to_protocol_turn(state.turn))

    def map_to_protocol_turn(self, turn: int) -> Turn:
        return self._protocol_turns[turn]

    def map_to_game_state(self, state: State) -> game.State:
        board = self._map_board(state.board)
        turn = self._game_turns[state.turn]
        return tablut.TablutState(board, turn)

    def _map_board(self, board):
        return [[self._game_pawns[cell] for cell in row] for row in board]
