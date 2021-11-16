import random
from tablut import AshtonTablutGame
import pandas as pd
from time import time
import numpy as np

# CONST
# EMPTY = -1
# BLACK = 0
# WHITE = 1
# THRONE = 10
# KING = 100

MATCHES = 100

game = AshtonTablutGame(repeated_moves_allowed=0)

boards = []
turns = []
winners = []
counts=[]

start = time()
nactions=[]
for i in range(MATCHES):
    state = game.start()
    winner = game.winner(state)

    boards.append(state.board)
    turns.append(state.turn)

    count = 0
    while winner is None:
        
        action = random.choice(game.legal_actions(state))
        nactions.append(len(game.legal_actions(state)))
        state = game.next_state(state, action)
        winner = game.winner(state)
        
        boards.append(state.board)
        turns.append(state.turn)

        count += 1

    winners.extend([winner] * (count + 1))
    counts.append(count)

print('running time', time() - start)
print('avg states:', np.mean(np.array(counts)))
print('avg actions:', sum(nactions)/len(nactions))

df = pd.DataFrame({"board": boards, "turns": turns, "winner": winners})
df.to_csv("../../datasets/simulation.csv", index=False)