#!/usr/bin/env python3

import sys
import socket
import json

import os

from agent import MctsAgent
from client import Client
from const import Settings, Turn, Pawn
from utils import write_string, read_string
from player import RandomPlayer, AgentPlayer
from ashtonmapper import AshtonMapper
from tablut import AshtonTablutGame
from utils import random_string
import logging


# import argparse

# parser = argparse.ArgumentParser(description='Process some integers.')
# parser.add_argument('integers', metavar='N', type=int, nargs='+',
#                     help='an integer for the accumulator')
# parser.add_argument('--sum', dest='accumulate', action='store_const',
#                     const=sum, default=max,
#                     help='sum the integers (default: find the max)')

# args = parser.parse_args()
# print(args.accumulate(args.integers))
def configure_logger():
    filename = random_string(10)
    log_folder = 'logs'
    if not os.path.exists(log_folder):
        os.makedirs(log_folder)
    file_handler = logging.FileHandler(f'{log_folder}/{filename}.log')
    file_handler.setLevel(logging.DEBUG)
    log_format = "%(asctime)s [%(threadName)-12.12s] [%(levelname)-5.5s]  %(message)s"
    file_handler.setFormatter(logging.Formatter(log_format))

    console_handler = logging.StreamHandler()
    console_handler.setLevel(logging.INFO)

    logging.basicConfig(level=logging.DEBUG,
                        format='%(message)s', encoding='utf-8',
                        handlers=[console_handler, file_handler])


if __name__ == '__main__':
    player_name = 'AI'
    player_team = Turn.BLACK

    configure_logger()

    game = AshtonTablutGame(repeated_moves_allowed=0)
    agent = MctsAgent(game, timeout=50)
    player = AgentPlayer(player_name, player_team, agent)
    # player = RandomPlayer(player_name, player_team)
    client = Client(player, AshtonMapper())
    client.run()
