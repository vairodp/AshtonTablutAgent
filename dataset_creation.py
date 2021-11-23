import pandas as pd
import numpy as np
import glob
import os


dirname = 'datasets'
files = glob.glob(os.path.join(dirname, 'old_games', '*.txt'))

games = {'id': [], 'winner': []}
states = {'game_id': [], 'board': [], 'turn': []}
id = 1
for filename in files:
    game = {'board': [], 'turn': []}
    with open(filename) as f:
        lines = f.readlines()
        indices = [i for i, line in enumerate(
            lines) if line == 'FINE: Stato:\n']

        for i in indices:
            board = []
            for line in lines[i + 1: i + 10]:
                board.append(list(line.strip()))

            states['game_id'].append(id)
            states['board'].append(np.array(board))
            states['turn'].append(lines[i + 11].strip())

    games['id'].append(id)
    games['winner'].append(states['turn'][-1])

    id += 1

games = pd.DataFrame(games)
games.to_csv(os.path.join(dirname, 'games.csv'), index=False)

states = pd.DataFrame(states)
states.to_csv(os.path.join(dirname, 'states.csv'), index=False)

for i, game in games.iterrows():
    states_game = states.loc[states['game_id'] == game['id']]
    states_game['turn'] = game['winner']

states.to_csv(os.path.join(dirname, 'states_results.csv'), index=False)
