#!/usr/bin/env python3

import sys
import socket
import json

from client import Client
from const import settings, Turn, Pawn
from utils import write_string, read_string
from player import RandomPlayer, MonteCarloPlayer
from mapper import Mapper
from tablut import AshtonTablutGame

# import argparse

# parser = argparse.ArgumentParser(description='Process some integers.')
# parser.add_argument('integers', metavar='N', type=int, nargs='+',
#                     help='an integer for the accumulator')
# parser.add_argument('--sum', dest='accumulate', action='store_const',
#                     const=sum, default=max,
#                     help='sum the integers (default: find the max)')

# args = parser.parse_args()
# print(args.accumulate(args.integers))
import logging

if __name__ == '__main__':

    logging.basicConfig(filename='debug.log', format='%(asctime)s %(message)s',
                        encoding='utf-8', level=logging.DEBUG)

    player_name = 'AI'
    player_team = Turn.WHITE
    game = AshtonTablutGame(0, 0)
    player = MonteCarloPlayer(player_name, player_team, game, timeout=50)
    # player = RandomPlayer(player_name, player_team)
    client = Client(player, Mapper())
    client.run()
