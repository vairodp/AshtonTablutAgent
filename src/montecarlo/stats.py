from dataclasses import dataclass, field
from game import Move
import json


@dataclass
class MonteCarloNodeStats:
    move: Move
    n_simulations: int = 0
    n_wins: int = 0


@dataclass
class MonteCarloStats:
    n_simulations: int
    n_wins: int
    children: list[MonteCarloNodeStats] = field(default_factory=lambda: [])

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
