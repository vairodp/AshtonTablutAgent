import socket
import json

from const import Settings, Turn, Pawn
from protocol import *
from utils import write_string, read_string
from player import Player
from ashtonmapper import AshtonMapper
import logging

logger = logging.getLogger(__name__)


class Client:
    _state: State = None
    _socket: socket.socket = None

    def __init__(self, player: Player, mapper: AshtonMapper, ip='localhost'):
        self.player = player
        self.mapper = mapper

        port = Settings.WHITE_PORT if player.team == Turn.WHITE else Settings.BLACK_PORT
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._socket.connect((ip, port))

    def run(self):
        self._send(self.player.name)

        while True:
            self._update_state()

            if self._state.turn == self.player.team:
                game_state = self.mapper.map_to_game_state(self._state)
                move = self.player.get_action(game_state)
                action = self.mapper.map_to_protocol_action(game_state, move)
                logger.info(f'Moving from {action.from_} to {action.to}')
                self._send(action)

            elif self._state.turn == Turn.DRAW:
                logger.info('DRAW!')
                return
            elif self.player.team == Turn.WHITE:
                if self._state.turn == Turn.WHITE_WIN:
                    logger.info('YOU WIN!')
                    return
                elif self._state.turn == Turn.BLACK_WIN:
                    logger.info('YOU LOSE!')
                    return
            else:
                if self._state.turn == Turn.WHITE_WIN:
                    logger.info('YOU LOSE!')
                    return
                elif self._state.turn == Turn.BLACK_WIN:
                    logger.info('YOU WIN!')
                    return
                logger.info('Waiting for your opponent action ... ')

    def _send(self, obj):
        gson = json.dumps(
            obj, default=lambda o: o.__dict__)

        write_string(self._socket, gson.replace('_', ''))

    def _update_state(self):
        state = json.loads(read_string(self._socket))
        self._state = State(state)
        logger.debug(state)
