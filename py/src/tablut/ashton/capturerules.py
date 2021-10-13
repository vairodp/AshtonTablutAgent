from tablut import TablutState, Action, Player, Pawn, Coord
from tablut.action import Coords


class AshtonCaptureRules:
    @staticmethod
    def remove_captured(state: TablutState, action: Action):
        pawn_capture_function = None

        if state.turn == Player.BLACK:
            AshtonCaptureRules._remove_captured_king_from_black(state, action)
            pawn_capture_function = AshtonCaptureRules._remove_captured_white_from_black

        elif state.turn == Player.WHITE:
            pawn_capture_function = AshtonCaptureRules._remove_captured_black_from_white

        AshtonCaptureRules._remove_captured_simple_pawns_from(state, action, pawn_capture_function)

        # state.moves_without_capturing += 1

    @staticmethod
    def _remove_captured_simple_pawns_from(state: TablutState, action: Action, remove_simple_pawn):
        # capture on the right
        if action.to.column < state.board.shape[1] - 2:
            captured = Coord(action.to.row, action.to.column + 1)
            second = Coord(action.to.row, action.to.column + 2)
            remove_simple_pawn(state, captured, second)

        # capture on the left
        if action.to.column > 1:
            captured = Coord(action.to.row, action.to.column - 1)
            second = Coord(action.to.row, action.to.column - 2)
            remove_simple_pawn(state, captured, second)

        # capture on the top
        if action.to.row > 1:
            captured = Coord(action.to.row - 1, action.to.column)
            second = Coord(action.to.row - 2, action.to.column)
            remove_simple_pawn(state, captured, second)

        # capture on the bottom
        if action.to.row < state.board.shape[0] - 2:
            captured = Coord(action.to.row + 1, action.to.column)
            second = Coord(action.to.row + 2, action.to.column)
            remove_simple_pawn(state, captured, second)

    @staticmethod
    def _remove_captured_black_from_white(state: TablutState, captured: Coord, second: Coord):
        """Remove black pawn between first and second white pawn"""
        captured_pawn = state.pawn(captured)
        second_pawn = state.pawn(second)

        if captured_pawn == Pawn.BLACK:
            if Pawn.is_white(second_pawn) or second_pawn == Pawn.THRONE \
                    or (second in state.board.citadels and second not in [Coords.I5, Coords.E1, Coords.A5, Coords.E9]):
                state.remove_pawn(captured)
                # state.moves_without_capturing = -1

    @staticmethod
    def _remove_captured_white_from_black(state: TablutState, captured: Coord, second: Coord):
        """Remove white pawn between first and second black pawn"""
        captured_pawn = state.pawn(captured)
        second_pawn = state.pawn(second)

        if captured_pawn == Pawn.WHITE and \
                (second_pawn == Pawn.BLACK or second_pawn == Pawn.THRONE
                 or second in state.board.citadels or second == Coords.E5):
            state.remove_pawn(captured)
            # state.moves_without_capturing = -1

    @staticmethod
    def _remove_captured_king_from_black(state: TablutState, action: Action):
        right = Coord(action.to.row, action.to.column + 1)
        left = Coord(action.to.row, action.to.column - 1)
        bottom = Coord(action.to.row + 1, action.to.column)
        top = Coord(action.to.row - 1, action.to.column)

        # capture on the right
        if action.to.column < state.board.shape[1] - 2 and state.pawn(right) == Pawn.KING:
            # re sul trono
            if right == Coords.E5:
                if state.pawn(Coords.E4) == Pawn.BLACK and state.pawn(Coords.F5) == Pawn.BLACK and state.pawn(
                        Coords.E6) == Pawn.BLACK:
                    state.remove_pawn(right)

            # re adiacente al trono
            elif right == Coords.E4:
                if state.pawn(Coords.E3) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(right)

            elif right == Coords.E6:
                if state.pawn(Coords.F6) == Pawn.BLACK and state.pawn(Coords.E7) == Pawn.BLACK:
                    state.remove_pawn(right)

            elif right == Coords.D5:
                if state.pawn(Coords.D4) == Pawn.BLACK and state.pawn(Coords.D6) == Pawn.BLACK:
                    state.remove_pawn(right)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row, action.to.column + 2)) == Pawn.BLACK \
                        or Coord(action.to.row, action.to.column + 2) in state.board.citadels:
                    state.remove_pawn(right)

        # capture on the left
        elif action.to.column > 1 and state.pawn(left) == Pawn.KING:
            # re sul trono
            if left == Coords.E5:
                if state.pawn(Coords.E4) == Pawn.BLACK and state.pawn(Coords.D5) == Pawn.BLACK and state.pawn(
                        Coords.E6) == Pawn.BLACK:
                    state.remove_pawn(left)

            # re adiacente al trono
            elif left == Coords.E4:
                if state.pawn(Coords.E3) == Pawn.BLACK and state.pawn(Coords.D4) == Pawn.BLACK:
                    state.remove_pawn(left)

            elif left == Coords.F5:
                if state.pawn(Coords.F6) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(left)

            elif left == Coords.E6:
                if state.pawn(Coords.E7) == Pawn.BLACK and state.pawn(Coords.D6) == Pawn.BLACK:
                    state.remove_pawn(left)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row, action.to.column - 2)) == Pawn.BLACK \
                        or Coord(action.to.row, action.to.column - 2) in state.board.citadels:
                    state.remove_pawn(left)

        # capture on the bottom
        elif action.to.row < state.board.shape[0] - 2 and state.pawn(bottom) == Pawn.KING:
            # re sul trono
            if bottom == Coords.E5:
                if state.pawn(Coords.E6) == Pawn.BLACK and state.pawn(Coords.F5) == Pawn.BLACK and state.pawn(
                        Coords.D5) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            # re adiacente al trono
            elif bottom == Coords.E4:
                if state.pawn(Coords.D4) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            elif bottom == Coords.D5:
                if state.pawn(Coords.C5) == Pawn.BLACK and state.pawn(Coords.D6) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            elif bottom == Coords.F5:
                if state.pawn(Coords.G5) == Pawn.BLACK and state.pawn(Coords.F6) == Pawn.BLACK:
                    state.remove_pawn(bottom)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row + 2, action.to.column)) == Pawn.BLACK \
                        or Coord(action.to.row + 2, action.to.column) in state.board.citadels:
                    state.remove_pawn(bottom)

        # capture on top
        if action.to.row > 1 and state.pawn(top) == Pawn.KING:
            # re sul trono
            if top == Coords.E5:
                if state.pawn(Coords.E4) == Pawn.BLACK and state.pawn(Coords.F5) == Pawn.BLACK and state.pawn(
                        Coords.D5) == Pawn.BLACK:
                    state.remove_pawn(top)

            # re adiacente al trono
            elif top == Coords.E6:
                if state.pawn(Coords.D6) == Pawn.BLACK and state.pawn(Coords.F6) == Pawn.BLACK:
                    state.remove_pawn(top)

            elif top == Coords.D5:
                if state.pawn(Coords.C5) == Pawn.BLACK and state.pawn(Coords.D4) == Pawn.BLACK:
                    state.remove_pawn(top)

            elif top == Coords.F5:
                if state.pawn(Coords.G5) == Pawn.BLACK and state.pawn(Coords.F4) == Pawn.BLACK:
                    state.remove_pawn(top)

            # sono fuori dalle zone del trono
            else:
                if state.pawn(Coord(action.to.row - 2, action.to.column)) == Pawn.BLACK \
                        or Coord(action.to.row - 2, action.to.column) in state.board.citadels:
                    state.remove_pawn(top)
