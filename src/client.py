import socket
import json

from const import settings, Turn, Pawn
from protocol import *
from utils import write_string, read_string
from player import Player
from mapper import Mapper
import logging

logger = logging.getLogger(__name__)


class Client:
    _state: State = None
    _socket: socket.socket = None

    def __init__(self, player: Player, mapper: Mapper, ip='localhost'):
        self.player = player
        self.mapper = mapper

        port = settings.WHITE_PORT if player.team == Turn.WHITE else settings.BLACK_PORT
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._socket.connect((ip, port))

    def run(self):
        self._send(self.player.name)

        while True:
            self._update_state()

            if self._state.turn == self.player.team:
                logger.info('Computing best move ...')
                game_state = self.mapper.map_to_game_state(self._state)
                move = self.player.get_movement(game_state)
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
                logger.info('Waiting for your opponent move ... ')

    def _send(self, obj):
        gson = json.dumps(
            obj, default=lambda o: o.__dict__)

        write_string(self._socket, gson.replace('_', ''))

    def _update_state(self):
        state = json.loads(read_string(self._socket))
        self._state = State(state)
        logger.debug(state)
