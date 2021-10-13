# import copy
# import numpy as np
# from game import Game, State
# from const import GameResult
# from collections import RingBuffer
# from . import TablutState, Action, Pawn
# from .exception import *


# class __Turn:
#     BLACK = 0
#     WHITE = 1

#     def next(self, value) -> Turn:
#         return (value + 1) % 2

#     def previous(self, value) -> Turn:
#         return (value - 1) % 2


# Turn = __Turn()

# # class StateAshtonTablut(TablutState):
# #     def __init__(self, player: Turn):
# #         self.board = np.full((9, 9), Pawn.EMPTY)
# #         self.turn = Turn.BLACK

# #         self.board[4, 4] = Pawn.THRONE

# #         self.board[4][4] = Pawn.KING

# #         self.board[2][4] = Pawn.WHITE
# #         self.board[3][4] = Pawn.WHITE
# #         self.board[5][4] = Pawn.WHITE
# #         self.board[6][4] = Pawn.WHITE
# #         self.board[4][2] = Pawn.WHITE
# #         self.board[4][3] = Pawn.WHITE
# #         self.board[4][5] = Pawn.WHITE
# #         self.board[4][6] = Pawn.WHITE

# #         self.board[0][3] = Pawn.BLACK
# #         self.board[0][4] = Pawn.BLACK
# #         self.board[0][5] = Pawn.BLACK
# #         self.board[1][4] = Pawn.BLACK
# #         self.board[8][3] = Pawn.BLACK
# #         self.board[8][4] = Pawn.BLACK
# #         self.board[8][5] = Pawn.BLACK
# #         self.board[7][4] = Pawn.BLACK
# #         self.board[3][0] = Pawn.BLACK
# #         self.board[4][0] = Pawn.BLACK
# #         self.board[5][0] = Pawn.BLACK
# #         self.board[4][1] = Pawn.BLACK
# #         self.board[3][8] = Pawn.BLACK
# #         self.board[4][8] = Pawn.BLACK
# #         self.board[5][8] = Pawn.BLACK
# #         self.board[4][7] = Pawn.BLACK


# class GameAshtonTablut(Game):
#     """Number of repeated states that can occur before a draw."""
#     repeated_moves_allowed: int

#     """Counter for the moves without capturing that have occurred"""
#     moves_without_capturing: int
#     citadels: list[str]
#     draw_conditions: RingBuffer[str]

#     def __init__(self, repeated_moves_allowed: int, cache_size: int):
#         self._repeated_moves_allowed = repeated_moves_allowed
#         self.moves_without_capturing = 0

#         self.draw_conditions = RingBuffer(cache_size)
#         self.citadels = ['A4', 'A5', 'A6', 'B5', 'D1', 'E1', 'F1',
#                          'E2', 'I4', 'I5', 'I6', 'H5', 'D9', 'E9', 'F9', 'E8']

#     def legal_moves(self, state: State) -> Sequence[Move]:
#         """Return all the possible moves from the given state."""
#         turn = state.turn
#         legal_moves: list[Action] = []

#         for i in range(len(state.board)):
#             for j in range(len(state.board[0])):

#                 # if pawn color is equal of turn color
#                 if state.get_pawn(i, j) == turn or (state.get_pawn(i, j) == Pawn.KING and turn == Turn.WHITE):

#                     from_ = Action.map_to_coord(i, j)
#                     # search on top of pawn
#                     for k in range(i-1, -1, -1):
#                         action = Action(from_, Action.map_to_coord(k, j), turn)
#                         if self._is_legal_move(state, action):
#                             legal_moves.append(action)
#                         else:
#                             break

#                     # search on bottom of pawn
#                     for k in range(i+1, len(state.board)):
#                         action = Action(from_, Action.map_to_coord(k, j), turn)
#                         if self._is_legal_move(state, action):
#                             legal_moves.append(action)
#                         else:
#                             break

#                     # search on left of pawn
#                     for k in range(j-1, -1, -1):
#                         action = Action(from_, Action.map_to_coord(i, k), turn)
#                         if self._is_legal_move(state, action):
#                             legal_moves.append(action)
#                         else:
#                             break

#                     # search on right of pawn
#                     for k in range(j+1, len(state.board[0])):
#                         action = Action(from_, Action.map_to_coord(i, k), turn)
#                         if self._is_legal_move(state, action):
#                             legal_moves.append(action)
#                         else:
#                             break

#         return legal_moves

#     def next_state(self, state: State, move: Move) -> TablutState:
#         """Advance the given state and return it."""
#         new_history = list(state.move_history)
#         new_history.append(move)

#         new_board = np.copy(state.board)
#         state = TablutState(new_history, new_board, state.turn)

#         self._move(state, move)
#         self._remove_captured(state, move)

#         state.turn = Turn.next(state.turn)

#         return state

#     def _move(self, state, move: Action):
#         row_from, column_from = Action.from_coord(move.from_)
#         pawn = state.get_pawn(row_from, column_from)
#         # libero il trono o una casella qualunque
#         if row_from == 4 and column_from == 4:
#             board[row_from, column_from] = Pawn.THRONE
#         else:
#             board[row_from, column_from] = Pawn.EMPTY
#         # metto nel nuovo tabellone la pedina mossa
#         board[*Action.from_coord(move.to)] = pawn

#     def _remove_captured(self, state: TablutState, action: Action):
#         if state.turn == Turn.BLACK:
#             self._remove_captured_from_black(state, action)
#         elif state.turn == Turn.WHITE:
#             self._remove_captured_from_white(state, action)

#         self.moves_without_capturing += 1

#     def _remove_captured_from_white(self, state: TablutState, action: Action) -> void:
#         # controllo se mangio a destra
#         if action.column_to < len(state.board) - 2\
#                 and state.get_pawn(action.row_to, action.column_to + 1) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to, action.column_to + 2) == Pawn.WHITE
#                      or state.get_pawn(action.row_to, action.column_to + 2) == Pawn.THRONE
#                      or state.get_pawn(action.row_to, action.column_to + 2) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to, action.column_to + 2) in self.citadels
#                          and not (action.column_to + 2 == 8 and action.row_to == 4)
#                          and not (action.column_to + 2 == 4 and action.row_to == 0)
#                          and not (action.column_to + 2 == 4 and action.row_to == 8)
#                          and not (action.column_to + 2 == 0 and action.row_to == 4))):
#             state.remove_pawn(action.row_to, action.column_to + 1)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#         # controllo se mangio a sinistra
#         if action.column_to > 1 and state.get_pawn(action.row_to, action.column_to - 1) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to, action.column_to - 2) == Pawn.WHITE
#                      or state.get_pawn(action.row_to, action.column_to - 2) == Pawn.THRONE
#                      or state.get_pawn(action.row_to, action.column_to - 2) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to, action.column_to - 2) in self.citadels
#                          and not (action.column_to - 2 == 8 and action.row_to == 4)
#                          and not (action.column_to - 2 == 4 and action.row_to == 0)
#                          and not (action.column_to - 2 == 4 and action.row_to == 8)
#                          and not (action.column_to - 2 == 0 and action.row_to == 4))):
#             state.remove_pawn(action.row_to, action.column_to - 1)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#         # controllo se mangio sopra
#         if action.row_to > 1 and state.get_pawn(action.row_to - 1, action.column_to) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to - 2, action.column_to) == Pawn.WHITE
#                      or state.get_pawn(action.row_to - 2, action.column_to) == Pawn.THRONE
#                      or state.get_pawn(action.row_to - 2, action.column_to) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to - 2, action.column_to) in self.citadels
#                          and not (action.column_to == 8 and action.row_to - 2 == 4)
#                          and not (action.column_to == 4 and action.row_to - 2 == 0)
#                          and not (action.column_to == 4 and action.row_to - 2 == 8)
#                          and not (action.column_to == 0 and action.row_to - 2 == 4))):
#             state.remove_pawn(action.row_to - 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#         # controllo se mangio sotto
#         if action.row_to < len(state.board) - 2\
#                 and state.get_pawn(action.row_to + 1, action.column_to) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to + 2, action.column_to) == Pawn.WHITE
#                      or state.get_pawn(action.row_to + 2, action.column_to) == Pawn.THRONE
#                      or state.get_pawn(action.row_to + 2, action.column_to) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to + 2, action.column_to) in self.citadels.contains
#                          and not (action.column_to == 8 and action.row_to + 2 == 4)
#                          and not (action.column_to == 4 and action.row_to + 2 == 0)
#                          and not (action.column_to == 4 and action.row_to + 2 == 8)
#                          and not (action.column_to == 0 and action.row_to + 2 == 4))):
#             state.remove_pawn(action.row_to + 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#     def _remove_captured_from_black(self, state: TablutState, action: Action) -> void:
#         self._remove_captured_whites_from_black(state, action)
#         self._remove_captured_king_from_black(state, action)

#     def _remove_captured_whites_from_black(self, state: TablutState, action: Action) -> void:
#         # mangio a destra
#         if action.column_to < len(state.board) - 2 and state.get_pawn(action.row_to, action.column_to + 1) == Pawn.WHITE:
#             if state.get_pawn(action.row_to, action.column_to + 2) == Pawn.BLACK:
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#             if state.get_pawn(action.row_to, action.column_to + 2) == Pawn.THRONE:
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#             if Action.map_to_coord(action.row_to, action.column_to + 2) in self.citadels:
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#             if Action.map_to_coord(action.row_to, action.column_to + 2) == "E5":
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#         # mangio a sinistra
#         if action.column_to > 1 and state.get_pawn(action.row_to, action.column_to - 1) == Pawn.WHITE\
#                 and (state.get_pawn(action.row_to, action.column_to - 2) == Pawn.BLACK
#                      or state.get_pawn(action.row_to, action.column_to - 2) == Pawn.THRONE
#                      or Action.map_to_coord(action.row_to, action.column_to - 2) in self.citadels
#                      or Action.map_to_coord(action.row_to, action.column_to - 2 == "E5")):
#             state.remove_pawn(action.row_to, action.column_to - 1)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#         # mangio sopra
#         if action.row_to > 1 and state.get_pawn(action.row_to - 1, action.column_to) == Pawn.WHITE\
#                 and (state.get_pawn(action.row_to - 2, action.column_to) == Pawn.BLACK
#                      or state.get_pawn(action.row_to - 2, action.column_to) == Pawn.THRONE
#                      or Action.map_to_coord(action.row_to - 2, action.column_to) in self.citadels
#                      or Action.map_to_coord(action.row_to - 2, action.column_to) == "E5"):
#             state.remove_pawn(action.row_to - 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#         # mangio sotto
#         if action.row_to < len(state.board) - 2\
#                 and state.get_pawn(action.row_to + 1, action.column_to) == Pawn.WHITE\
#                 and (state.get_pawn(action.row_to + 2, action.column_to) == Pawn.BLACK
#                      or state.get_pawn(action.row_to + 2, action.column_to) == Pawn.THRONE
#                      or Action.map_to_coord(action.row_to + 2, action.column_to) in self.citadels
#                      or Action.map_to_coord(action.row_to + 2, action.column_to) == "E5"):
#             state.remove_pawn(action.row_to + 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#     def _remove_captured_king_from_black(self, state: TablutState, action: Action) -> void:
#         # capture on the right
#         if action.column_to < len(state.board) - 2 and state.get_pawn(action.row_to, action.column_to + 1) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to, action.column_to + 1) == "E5":
#                 if state.get_pawn(3, 4) == Pawn.BLACK and state.get_pawn(4, 5) == Pawn.BLACK and state.get_pawn(5, 4) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#             # re adiacente al trono
#             elif Action.map_to_coord(action.row_to, action.column_to + 1) == "E4":
#                 if state.get_pawn(2, 4) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#             elif Action.map_to_coord(action.row_to, action.column_to + 1) == "E6":
#                 if state.get_pawn(5, 5) == Pawn.BLACK and state.get_pawn(6, 4) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#             elif Action.map_to_coord(action.row_to, action.column_to + 1) == "D5":
#                 if state.get_pawn(3, 3) == Pawn.BLACK and state.get_pawn(5, 3) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#             # sono fuori dalle zone del trono
#             else:
#                 if state.get_pawn(action.row_to, action.column_to + 2) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to, action.column_to + 2) in self.citadels:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#         # capture on the left
#         elif action.column_to > 1 and state.get_pawn(action.row_to, action.column_to - 1) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to, action.column_to - 1) == "E5":
#                 if state.get_pawn(3, 4) == Pawn.BLACK and state.get_pawn(4, 3) == Pawn.BLACK and state.get_pawn(5, 4) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to - 1)

#             # re adiacente al trono
#             elif Action.map_to_coord(action.row_to, action.column_to - 1) == "E4":
#                 if state.get_pawn(2, 4) == Pawn.BLACK and state.get_pawn(3, 3) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#             elif Action.map_to_coord(action.row_to, action.column_to - 1) == "F5":
#                 if state.get_pawn(5, 5) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#             elif Action.map_to_coord(action.row_to, action.column_to - 1) == "E6":
#                 if state.get_pawn(6, 4) == Pawn.BLACK and state.get_pawn(5, 3) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#             # sono fuori dalle zone del trono
#             else:
#                 if state.get_pawn(action.row_to, action.column_to - 2) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to, action.column_to - 2) in self.citadels:
#                     state.remove_pawn(action.row_to, action.column_to + 1)

#         # capture on the bottom
#         elif action.row_to < len(state.board) - 2 and state.get_pawn(action.row_to + 1, action.column_to) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to + 1, action.column_to) == "E5":
#                 if state.get_pawn(5, 4) == Pawn.BLACK and state.get_pawn(4, 5) == Pawn.BLACK and state.get_pawn(4, 3) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to + 1, action.column_to)

#             # re adiacente al trono
#             elif Action.map_to_coord(action.row_to + 1, action.column_to) == "E4":
#                 if state.get_pawn(3, 3) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to + 1, action.column_to)

#             elif Action.map_to_coord(action.row_to + 1, action.column_to) == "D5":
#                 if state.get_pawn(4, 2) == Pawn.BLACK and state.get_pawn(5, 3) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to + 1, action.column_to)

#             elif Action.map_to_coord(action.row_to + 1, action.column_to) == "F5":
#                 if state.get_pawn(4, 6) == Pawn.BLACK and state.get_pawn(5, 5) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to + 1, action.column_to)

#             # sono fuori dalle zone del trono
#             else:
#                 if state.get_pawn(action.row_to + 2, action.column_to) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to + 2, action.column_to) in self.citadels:
#                     state.remove_pawn(action.row_to + 1, action.column_to)

#         # capture on top
#         if action.row_to > 1 and state.get_pawn(action.row_to - 1, action.column_to) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to - 1, action.column_to) == "E5":
#                 if state.get_pawn(3, 4) == Pawn.BLACK and state.get_pawn(4, 5) == Pawn.BLACK and state.get_pawn(4, 3) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to - 1, action.column_to)

#             # re adiacente al trono
#             elif Action.map_to_coord(action.row_to - 1, action.column_to) == "E6":
#                 if state.get_pawn(5, 3) == Pawn.BLACK and state.get_pawn(5, 5) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to - 1, action.column_to)

#             elif Action.map_to_coord(action.row_to - 1, action.column_to) == "D5":
#                 if state.get_pawn(4, 2) == Pawn.BLACK and state.get_pawn(3, 3) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to - 1, action.column_to)

#             elif Action.map_to_coord(action.row_to - 1, action.column_to) == "F5":
#                 if state.get_pawn(4, 6) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.remove_pawn(action.row_to - 1, action.column_to)

#             # sono fuori dalle zone del trono
#             else:
#                 if state.get_pawn(action.row_to - 2, action.column_to) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to - 2, action.column_to) in self.citadels:
#                     state.remove_pawn(action.row_to - 1, action.column_to)

#     def winner(self, state: TablutState) -> int:
#         """Return the winner of the game, None else."""

#         turn = Turn.previous(state.turn)

#         if turn == Turn.WHITE:
#             if self._is_king_on_edge(state) or state.number_of(Pawn.BLACK) == 0:
#                 return GameResult.WHITE

#         if turn == Turn.BLACK:
#             if state.number_of(Pawn.KING) == 0:
#                 return GameResult.BLACK

#         if self._is_a_draw(state):
#             return GameResult.DRAW

#         return None

#     def _is_king_on_edge(self, state: TablutState) -> bool:
#         top = state.board[0, :]
#         bottom = state.board[len(state.board) - 1, :]
#         left = state.board[:, 0]
#         right = state.board[:, len(state.board[0]) - 1]

#         return np.any(top == Pawn.KING) or np.any(bottom == Pawn.KING)\
#             or np.any(left == Pawn.KING) or np.any(right == Pawn.KING)

#     def _is_a_draw(self, state) -> bool:
#         # if something has been captured, clear cache for draws
#         if self.moves_without_capturing == 0:
#             self.draw_conditions.clear()

#         repeated_moves = self.draw_conditions.count(state)
#         self.draw_conditions.append(state.clone())

#         return repeated_moves > self.repeated_moves_allowed

#     def checkMove(self, state: TablutState, action: Action) -> TablutState:
#         # self.loggGame.fine(a
#         # controllo la mossa
#         if len(action.to) != 2 or len(action.from_) != 2:
#             # self.loggGame.warning("Formato mossa errato")
#             raise ActionException(action)

#         row_from, column_from = Action.from_coord(action.from_)
#         row_to, column_to = Action.from_coord(action.to)

#         if not is_legal_move(state, action):
#             return None

#         # se sono arrivato qui, muovo la pedina
#         state = self.movePawn(state, action)

#         # a questo punto controllo lo stato per eventuali catture
#         if state.turn == Turn.WHITE:
#             state = self.checkCaptureBlack(state, action)
#         elif state.turn == Turn.BLACK:
#             state = self.checkCaptureWhite(state, action)

#         # if something has been captured, clear cache for draws
#         if self.moves_without_capturing == 0:
#             self.draw_conditions.clear()
#             # self.loggGame.fine("Capturenot  Draw cache clearednot ")

#         # controllo pareggio
#         trovati = 0
#         for s in draw_conditions:

#             if s == state:
#                 trovati += 1
#                 if trovati > self.repeated_moves_allowed:
#                     state.turn = Turn.DRAW
#                     # self.loggGame.fine("Partita terminata in pareggio per numero di stati ripetuti")
#                     break

#         if trovati > 0:
#             # self.loggGame.fine("Equal states found: " + trovati)
#             pass

#         if self.cache_size >= 0 and len(self.draw_conditions) > self.cache_size:
#             self.draw_conditions.remove(0)
#         self.draw_conditions.append(state.clone())

#         # self.loggGame.fine("Current draw cache size: " +self.drawConditions.size())

#         # self.loggGame.fine("Stato:\n" + state)
#         # System.out.println("Stato:\n" + state

#         return state

#     def checkCaptureWhite(self, state: TablutState, action: Action) -> TablutState:
#         # controllo se mangio a destra
#         if action.column_to < len(state.board) - 2\
#                 and state.get_pawn(action.row_to, action.column_to + 1) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to, action.column_to + 2) == Pawn.WHITE
#                      or state.get_pawn(action.row_to, action.column_to + 2) == Pawn.THRONE
#                      or state.get_pawn(action.row_to, action.column_to + 2) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to, action.column_to + 2) in self.citadels
#                          and not (action.column_to + 2 == 8 and action.row_to == 4)
#                          and not (action.column_to + 2 == 4 and action.row_to == 0)
#                          and not (action.column_to + 2 == 4 and action.row_to == 8)
#                          and not (action.column_to + 2 == 0 and action.row_to == 4))):
#             state.remove_pawn(action.row_to, action.column_to + 1)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#         # controllo se mangio a sinistra
#         if action.column_to > 1 and state.get_pawn(action.row_to, action.column_to - 1) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to, action.column_to - 2) == Pawn.WHITE
#                      or state.get_pawn(action.row_to, action.column_to - 2) == Pawn.THRONE
#                      or state.get_pawn(action.row_to, action.column_to - 2) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to, action.column_to - 2) in self.citadels
#                          and not (action.column_to - 2 == 8 and action.row_to == 4)
#                          and not (action.column_to - 2 == 4 and action.row_to == 0)
#                          and not (action.column_to - 2 == 4 and action.row_to == 8)
#                          and not (action.column_to - 2 == 0 and action.row_to == 4))):
#             state.remove_pawn(action.row_to, action.column_to - 1)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#         # controllo se mangio sopra
#         if action.row_to > 1 and state.get_pawn(action.row_to - 1, action.column_to) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to - 2, action.column_to) == Pawn.WHITE
#                      or state.get_pawn(action.row_to - 2, action.column_to) == Pawn.THRONE
#                      or state.get_pawn(action.row_to - 2, action.column_to) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to - 2, action.column_to) in self.citadels
#                          and not (action.column_to == 8 and action.row_to - 2 == 4)
#                          and not (action.column_to == 4 and action.row_to - 2 == 0)
#                          and not (action.column_to == 4 and action.row_to - 2 == 8)
#                          and not (action.column_to == 0 and action.row_to - 2 == 4))):
#             state.remove_pawn(action.row_to - 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#         # controllo se mangio sotto
#         if action.row_to < len(state.board) - 2\
#                 and state.get_pawn(action.row_to + 1, action.column_to) == Pawn.BLACK\
#                 and (state.get_pawn(action.row_to + 2, action.column_to) == Pawn.WHITE
#                      or state.get_pawn(action.row_to + 2, action.column_to) == Pawn.THRONE
#                      or state.get_pawn(action.row_to + 2, action.column_to) == Pawn.KING
#                      or (Action.map_to_coord(action.row_to + 2, action.column_to) in self.citadels.contains
#                          and not (action.column_to == 8 and action.row_to + 2 == 4)
#                          and not (action.column_to == 4 and action.row_to + 2 == 0)
#                          and not (action.column_to == 4 and action.row_to + 2 == 8)
#                          and not (action.column_to == 0 and action.row_to + 2 == 4))):
#             state.remove_pawn(action.row_to + 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina nera rimossa in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#         # controllo se ho vinto
#         if action.row_to == 0 or action.row_to == len(state.board) - 1 or action.column_to == 0\
#                 or action.column_to == len(state.board) - 1:
#             if state.get_pawn(action.row_to, action.column_to) == Pawn.KING:
#                 state.turn = Turn.WHITEWIN
#                 # self.loggGame.fine("Bianco vince con re in " + action.to)

#         # TODO: implement the winning condition of the capture of the last
#         # black checker

#         self.moves_without_capturing += 1
#         return state

#     def checkCaptureBlack(self, state: TablutState, action: Action) -> TablutState:
#         self.checkCaptureBlackPawnRight(state, action)
#         self.checkCaptureBlackPawnLeft(state, action)
#         self.checkCaptureBlackPawnUp(state, action)
#         self.checkCaptureBlackPawnDown(state, action)

#         self.checkCaptureBlackKingRight(state, action)
#         self.checkCaptureBlackKingLeft(state, action)
#         self.checkCaptureBlackKingDown(state, action)
#         self.checkCaptureBlackKingUp(state, action)

#         self.moves_without_capturing += 1
#         return state

#     def checkCaptureBlackPawnRight(self, state: TablutState, action: Action) -> TablutState:
#         # mangio a destra
#         if action.column_to < len(state.board) - 2 and state.get_pawn(action.row_to, action.column_to + 1) == Pawn.WHITE:
#             if state.get_pawn(action.row_to, action.column_to + 2) == Pawn.BLACK:
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))
#             if state.get_pawn(action.row_to, action.column_to + 2) == Pawn.THRONE:
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))
#             if Action.map_to_coord(action.row_to, action.column_to + 2) in self.citadels:
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))
#             if Action.map_to_coord(action.row_to, action.column_to + 2) == "E5":
#                 state.remove_pawn(action.row_to, action.column_to + 1)
#                 self.moves_without_capturing = -1
#                 # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#         return state

#     def checkCaptureBlackPawnLeft(self, state: TablutState, action: Action) -> TablutState:
#         # mangio a sinistra
#         if action.column_to > 1 and state.get_pawn(action.row_to, action.column_to - 1) == Pawn.WHITE\
#                 and (state.get_pawn(action.row_to, action.column_to - 2) == Pawn.BLACK
#                      or state.get_pawn(action.row_to, action.column_to - 2) == Pawn.THRONE
#                      or Action.map_to_coord(action.row_to, action.column_to - 2) in self.citadels
#                      or Action.map_to_coord(action.row_to, action.column_to - 2 == "E5")):
#             state.remove_pawn(action.row_to, action.column_to - 1)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to, action.column_to - 1))
#         return state

#     def checkCaptureBlackPawnUp(self, state: TablutState, action: Action) -> TablutState:
#         # controllo se mangio sopra
#         if action.row_to > 1 and state.get_pawn(action.row_to - 1, action.column_to) == Pawn.WHITE\
#                 and (state.get_pawn(action.row_to - 2, action.column_to) == Pawn.BLACK
#                      or state.get_pawn(action.row_to - 2, action.column_to) == Pawn.THRONE
#                      or Action.map_to_coord(action.row_to - 2, action.column_to) in self.citadels
#                      or Action.map_to_coord(action.row_to - 2, action.column_to) == "E5"):
#             state.remove_pawn(action.row_to - 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to - 1, action.column_to))
#         return state

#     def checkCaptureBlackPawnDown(self, state: TablutState, action: Action) -> TablutState:
#         # controllo se mangio sotto
#         if action.row_to < len(state.board) - 2\
#                 and state.get_pawn(action.row_to + 1, action.column_to) == Pawn.WHITE\
#                 and (state.get_pawn(action.row_to + 2, action.column_to) == Pawn.BLACK
#                      or state.get_pawn(action.row_to + 2, action.column_to) == Pawn.THRONE
#                      or Action.map_to_coord(action.row_to + 2, action.column_to) in self.citadels
#                      or Action.map_to_coord(action.row_to + 2, action.column_to) == "E5"):
#             state.remove_pawn(action.row_to + 1, action.column_to)
#             self.moves_without_capturing = -1
#             # self.loggGame.fine("Pedina bianca rimossa in: " + Action.map_to_coord(action.row_to + 1, action.column_to))
#         return state

#     def checkCaptureBlackKingLeft(self, state: TablutState, action: Action) -> TablutState:
#         # ho il re sulla sinistra
#         if action.column_to > 1 and state.get_pawn(action.row_to, action.column_to - 1) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to, action.column_to - 1) == "E5":
#                 if state.get_pawn(3, 4) == Pawn.BLACK and state.get_pawn(4, 3) == Pawn.BLACK and state.get_pawn(5, 4) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#             # re adiacente al trono
#             if Action.map_to_coord(action.row_to, action.column_to - 1) == "E4":
#                 if state.get_pawn(2, 4) == Pawn.BLACK and state.get_pawn(3, 3) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#             if Action.map_to_coord(action.row_to, action.column_to - 1) == "F5":
#                 if state.get_pawn(5, 5) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#             if Action.map_to_coord(action.row_to, action.column_to - 1) == ("E6"):
#                 if state.get_pawn(6, 4) == Pawn.BLACK and state.get_pawn(5, 3) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#             # sono fuori dalle zone del trono
#             if not Action.map_to_coord(action.row_to, action.column_to - 1) == "E5"\
#                     and not Action.map_to_coord(action.row_to, action.column_to - 1) == "E6"\
#                     and not Action.map_to_coord(action.row_to, action.column_to - 1) == "E4"\
#                     and not Action.map_to_coord(action.row_to, action.column_to - 1) == "F5":
#                 if state.get_pawn(action.row_to, action.column_to - 2) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to, action.column_to - 2) in self.citadels:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to - 1))

#         return state

#     def checkCaptureBlackKingRight(self, state: TablutState, action: Action) -> TablutState:
#         # ho il re sulla destra
#         if action.column_to < len(state.board) - 2 and state.get_pawn(action.row_to, action.column_to + 1) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to, action.column_to + 1) == "E5":
#                 if state.get_pawn(3, 4) == Pawn.BLACK and state.get_pawn(4, 5) == Pawn.BLACK and state.get_pawn(5, 4) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#             # re adiacente al trono
#             if Action.map_to_coord(action.row_to, action.column_to + 1) == "E4":
#                 if state.get_pawn(2, 4) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#             if Action.map_to_coord(action.row_to, action.column_to + 1) == "E6":
#                 if state.get_pawn(5, 5) == Pawn.BLACK and state.get_pawn(6, 4) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#             if Action.map_to_coord(action.row_to, action.column_to + 1) == "D5":
#                 if state.get_pawn(3, 3) == Pawn.BLACK and state.get_pawn(5, 3) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#             # sono fuori dalle zone del trono
#             if not Action.map_to_coord(action.row_to, action.column_to + 1) == "D5"\
#                     and not Action.map_to_coord(action.row_to, action.column_to + 1) == "E6"\
#                     and not Action.map_to_coord(action.row_to, action.column_to + 1) == "E4"\
#                     and not Action.map_to_coord(action.row_to, action.column_to + 1) == "E5":
#                 if state.get_pawn(action.row_to, action.column_to + 2) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to, action.column_to + 2) in self.citadels:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to, action.column_to + 1))

#         return state

#     def checkCaptureBlackKingDown(self, state: TablutState, action: Action) -> TablutState:
#         # ho il re sotto
#         if action.row_to < len(state.board) - 2 and state.get_pawn(action.row_to + 1, action.column_to) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to + 1, action.column_to) == "E5":
#                 if state.get_pawn(5, 4) == Pawn.BLACK and state.get_pawn(4, 5) == Pawn.BLACK and state.get_pawn(4, 3) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#             # re adiacente al trono
#             if Action.map_to_coord(action.row_to + 1, action.column_to) == "E4":
#                 if state.get_pawn(3, 3) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#             if Action.map_to_coord(action.row_to + 1, action.column_to) == "D5":
#                 if state.get_pawn(4, 2) == Pawn.BLACK and state.get_pawn(5, 3) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#             if Action.map_to_coord(action.row_to + 1, action.column_to) == "F5":
#                 if state.get_pawn(4, 6) == Pawn.BLACK and state.get_pawn(5, 5) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#             # sono fuori dalle zone del trono
#             if not Action.map_to_coord(action.row_to + 1, action.column_to) == "D5"\
#                     and not Action.map_to_coord(action.row_to + 1, action.column_to) == "E4"\
#                     and not Action.map_to_coord(action.row_to + 1, action.column_to) == "F5"\
#                     and not Action.map_to_coord(action.row_to + 1, action.column_to) == "E5":
#                 if state.get_pawn(action.row_to + 2, action.column_to) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to + 2, action.column_to) in self.citadels:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to + 1, action.column_to))

#         return state

#     def checkCaptureBlackKingUp(self, state: TablutState, action: Action) -> TablutState:
#         # ho il re sopra
#         if action.row_to > 1 and state.get_pawn(action.row_to - 1, action.column_to) == Pawn.KING:
#             # re sul trono
#             if Action.map_to_coord(action.row_to - 1, action.column_to) == "E5":
#                 if state.get_pawn(3, 4) == Pawn.BLACK and state.get_pawn(4, 5) == Pawn.BLACK and state.get_pawn(4, 3) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#             # re adiacente al trono
#             if Action.map_to_coord(action.row_to - 1, action.column_to) == "E6":
#                 if state.get_pawn(5, 3) == Pawn.BLACK and state.get_pawn(5, 5) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#             if Action.map_to_coord(action.row_to - 1, action.column_to) == "D5":
#                 if state.get_pawn(4, 2) == Pawn.BLACK and state.get_pawn(3, 3) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#             if Action.map_to_coord(action.row_to - 1, action.column_to) == "F5":
#                 if state.get_pawn(4, 6) == Pawn.BLACK and state.get_pawn(3, 5) == Pawn.BLACK:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#             # sono fuori dalle zone del trono
#             if not Action.map_to_coord(action.row_to - 1, action.column_to) == "D5"\
#                     and not Action.map_to_coord(action.row_to - 1, action.column_to) == "E6"\
#                     and not Action.map_to_coord(action.row_to - 1, action.column_to) == "F5"\
#                     and not Action.map_to_coord(action.row_to - 1, action.column_to) == "E5":
#                 if state.get_pawn(action.row_to - 2, action.column_to) == Pawn.BLACK\
#                         or Action.map_to_coord(action.row_to - 2, action.column_to) in self.citadels:
#                     state.turn = Turn.BLACKWIN
#                     # self.loggGame.fine("Nero vince con re catturato in: " + Action.map_to_coord(action.row_to - 1, action.column_to))

#         return state

#     def movePawn(self, state: TablutState, action: Action) -> TablutState:
#         pawn = state.get_pawn(action.row_from, action.column_from)
#         newBoard = state.board
#         # TablutState newState = new TablutState()
#         # self.loggGame.fine("Movimento pedina")
#         # libero il trono o una casella qualunque
#         if action.column_from == 4 and action.row_from == 4:
#             newBoard[action.row_from, action.column_from] = Pawn.THRONE
#         else:
#             newBoard[action.row_from, action.column_from] = Pawn.EMPTY

#         # metto nel nuovo tabellone la pedina mossa
#         newBoard[action.row_to, action.column_to] = pawn
#         # aggiorno il tabellone
#         state.board = newBoard
#         # cambio il turno
#         if state.turn == Turn.WHITE:
#             state.turn = Turn.BLACK
#         else:
#             state.turn = Turn.WHITE

#         return state

#     def endGame(self, state: TablutState) -> void:
#         # self.loggGame.fine("Stato:\n"+state)
#         pass

#         # #
#         #  # Method that perform an action in a given state and return resulting state
#         #  *
#         #  # @param state Current state
#         #  # @param action Action admissible on the given state
#         #  # @return TablutState obtained after performing the action

#         # @Override
#         # public TablutState getResult(self, state: TablutState, action: Action) {

#         # 	# move pawn
#         # 	state = self.movePawn(state.clone(), action)

#         # 	# check the state for any capture
#         # 	if (state.turn.equalsTurn(Pawn.WHITE)) {
#         # 		state = self.checkCaptureBlack(state, action) 		} elif (state.turn.equalsTurn(Pawn.BLACK)) {
#         # 		state = self.checkCaptureWhite(state, action) 		}

#         # 	# TODO This version of code doesn't check draws

#         # 	/ *
#         # 	# if something has been captured, clear cache for draws
#         # 	if (self.moves_without_capturing == 0) {
#         # 		self.drawConditions.clear()
#         # 		self.loggGame.fine("Capture! Draw cache cleared!") 		}
#         # 	# controllo pareggio
#         # 	int trovati = 0
#         # 	for (TablutState s: drawConditions) {
#         # 		# System.out.println(s.toString())
#         # 		if (s.equals(state)) {
#         # 			trovati++
#         # 			if (trovati > repeated_moves_allowed) {
#         # 				state.turn = (Turn.DRAW)
#         # 				self.loggGame.fine(
#         # 				    "Partita terminata in pareggio per numero di stati ripetuti")
#         # 				break}
#         # 		} else {
#         # 			# DEBUG: #
#         # 			# System.out.println("DIVERSI:")
#         # 			# System.out.println("STATO VECCHIO:\t" + s.toLinearString())
#         # 			# System.out.println("STATO NUOVO:\t" +
#         # 			# state.toLinearString()) 			}
#         # 	}
#         # 	if (trovati > 0) {
#         # 		self.loggGame.fine("Equal states found: " + trovati) 		}
#         # 	if (cache_size >= 0 and self.drawConditions.size() > cache_size) {
#         # 		self.drawConditions.remove(0) 		}
#         # 	self.drawConditions.add(state.clone())
#         # 	self.loggGame.fine("Current draw cache size: " + \
#         # 	                   self.drawConditions.size())
#         # 	self.loggGame.fine("Stato:\n" + state.toString())
#         # 	# System.out.println("Stato:\n" + state.toString())

#         # 	return state 	}

#         # #
#         #  # Check if a state is terminal, it means that one of player wins or draw.
#         #  *
#         #  # @param state Current state
#         #  # @return Return true if teh current state is terminal, otherwise false

#         # @Override
#         # public boolean isTerminal(self, state: TablutState) {
#         # 	if (state.turn.equals(Turn.WHITEWIN) or state.turn.equalsTurn.BLACKWIN or state.turn.equals(Turn.DRAW)) {
#         # 		return true 		}
#         # 	return false 	}

#     def _is_legal_move(self, state: TablutState, action: Action) -> bool:
#         # TODO togliere sto schifo di eccezioni e gestire in maniera umana. Il metodo senza underscore serve per la funzione check_move
#         """Check if given an action, it is allowed from the current state according to the rules of game."""
#         try:
#             return self.is_possible_move(state, action)
#         except:
#             return False

#     def is_legal_move(self, state: TablutState, action: Action) -> bool:
#         row_from, column_from = Action.from_coord(action.from_)
#         row_to, column_to = Action.from_coord(action.to)

#         # controllo se sono fuori dal tabellone
#         if column_from > len(state.board) - 1 or row_from > len(state.board) - 1\
#                 or row_to > len(state.board) - 1 or column_to > len(state.board) - 1 or column_from < 0\
#                 or row_from < 0 or row_to < 0 or column_to < 0:
#             # self.loggGame.warning("Mossa fuori tabellone")
#             raise BoardException(action)

#         # controllo che non vada sul trono
#         if state.get_pawn(row_to, column_to) == Pawn.THRONE:
#             # self.loggGame.warning("Mossa sul trono")
#             raise ThroneException(action)

#         # controllo la casella di arrivo
#         if not state.get_pawn(row_to, column_to) == Pawn.EMPTY:
#             # self.loggGame.warning("Mossa sopra una casella occupata")
#             raise OccupitedException(action)
#         if Action.map_to_coord(row_to, column_to) in self.citadels and not Action.map_to_coord(row_from, column_from) in self.citadels:
#             # self.loggGame.warning("Mossa che arriva sopra una citadel")
#             raise CitadelException(action)
#         if Action.map_to_coord(row_to, column_to) in self.citadels and Action.map_to_coord(row_from, column_from) in self.citadels:
#             if row_from == row_to:
#                 if column_from - column_to > 5 or column_from - column_to < -5:
#                     # self.loggGame.warning("Mossa che arriva sopra una citadel")
#                     raise CitadelException(action)
#             elif row_from - row_to > 5 or row_from - row_to < -5:
#                 # self.loggGame.warning("Mossa che arriva sopra una citadel")
#                 raise CitadelException(action)

#         # controllo se cerco di stare fermo
#         if row_from == row_to and column_from == column_to:
#             # self.loggGame.warning("Nessuna mossa")
#             raise StopException(action)

#         # controllo se sto muovendo una pedina giusta
#         if state.turn == Turn.WHITE:
#             if not state.get_pawn(row_from, column_from) == Pawn.WHITE and not state.get_pawn(row_from, column_from) == Pawn.KING:
#                 # self.loggGame.warning("Giocatore " + action.turn +
#                 #                         " cerca di muovere una pedina avversaria")
#                 raise PawnException(action)

#         if state.turn == Turn.BLACK:
#             if not state.get_pawn(row_from, column_from) == Pawn.BLACK:
#                 # self.loggGame.warning("Giocatore " + action.turn +
#                 #                         " cerca di muovere una pedina avversaria")
#                 raise PawnException(action)

#         # controllo di non muovere in diagonale
#         if row_from != row_to and column_from != column_to:
#             # self.loggGame.warning("Mossa in diagonale")
#             raise DiagonalException(action)

#         # controllo di non scavalcare pedine
#         if row_from == row_to:
#             interval = column_to, column_from if column_from > column_to else column_from + 1, column_to + 1
#             for j in range(*interval):
#                 self._check_climbing(row_from, j, action)

#         else:
#             interval = row_to, row_from if row_from > row_to else row_from + 1, row_to + 1
#             for i in range(*interval):
#                 self._check_climbing(i, column_from, action)

#         return True

#     def _check_climbing(self, i: int, j: int, action: Action):
#         if not state.get_pawn(i, j) == Pawn.EMPTY:
#             if state.get_pawn(i, j) == Pawn.THRONE:
#                 # self.loggGame.warning("Mossa che scavalca il trono")
#                 raise ClimbingException(action)
#             else:
#                 # self.loggGame.warning("Mossa che scavalca una pedina")
#                 raise ClimbingException(action)
#         if Action.map_to_coord(i, j) in self.citadels and not Action.map_to_coord(*Action.from_coord(action.from_)) in self.citadels:
#             # self.loggGame.warning("Mossa che scavalca una citadel")
#             raise ClimbingCitadelException(action)

#         # #
#         #  # Method to evaluate a state using heuristics
#         #  *
#         #  # @param state Current state
#         #  # @param turn Player that want find the best moves in the search space
#         #  # @return Evaluation of the state

#         # @Override
#         # public double getUtility(self, state: TablutState, Turn turn) {

#         # 	# if it is a terminal state
#         # 	if ((turn.equals(Turn.BLACK) and state.turn.equalsTurn.BLACKWIN)
#         # 			or (turn.equals(Turn.WHITE) and state.turn.equals(Turn.WHITEWIN)))
#         # 		return Double.POSITIVE_INFINITY
#         # 	elif ((turn.equals(Turn.BLACK) and state.turn.equals(Turn.WHITEWIN))
#         # 			or (turn.equals(Turn.WHITE) and state.turn.equalsTurn.BLACKWIN))
#         # 		return Double.NEGATIVE_INFINITY

#         # 	# if it isn't a terminal state
#         # 	Heuristics heuristics=null
#         # 	if (turn.equals(Turn.WHITE)) {
#         # 		heuristics=new WhiteHeuristics(state) 		} else {
#         # 		heuristics=new BlackHeuristics(state) 		}
#         # 	return heuristics.evaluateState() 	}
