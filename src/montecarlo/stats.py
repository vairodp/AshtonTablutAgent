from dataclasses import dataclass, field
from game import Action
import json


@dataclass
class MonteCarloNodeStats:
    move: Action
    n_simulations: int = 0
    win_score: int = 0


@dataclass
class MonteCarloStats:
    n_simulations: int
    win_score: int
    children: list[MonteCarloNodeStats] = field(default_factory=lambda: [])

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
