package it.ai.game.tablut.ashton;

import it.ai.game.tablut.Board;
import it.ai.game.tablut.Coords;
import it.ai.game.tablut.Pawn;

import java.util.Collection;
import java.util.Set;

public class AshtonBoard extends Board {
    private static final Set<Coords> citadels = Set.of(
            Coords.A4, Coords.A5, Coords.A6, Coords.B5, Coords.D1, Coords.E1, Coords.F1, Coords.E2, Coords.I4,
            Coords.I5, Coords.I6, Coords.H5, Coords.D9, Coords.E9, Coords.F9, Coords.E8);

    public final static int NUM_BLACK = 16;
    public final static int NUM_WHITE = 8;
    public final static int NUM_CITADELS = citadels.size();
    public final static int NUM_ESCAPES = 16;

    public AshtonBoard() {
        this(initialBoard());
    }

    public AshtonBoard(int[][] board) {
        super(board);
    }

    private static int[][] initialBoard() {
        int size = 9;
        int[][] board = new int[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board[i][j] = Pawn.EMPTY;

        for (Coords coords : citadels)
            board[coords.getRow()][coords.getColumn()] = Pawn.BLACK;

        for (int i = Coords.E3.getRow(); i <= Coords.E7.getRow(); i++)
            board[i][Coords.E3.getColumn()] = Pawn.WHITE;
        for (int j = Coords.C5.getColumn(); j <= Coords.G5.getColumn(); j++)
            board[Coords.C5.getRow()][j] = Pawn.WHITE;

        board[Coords.E5.getRow()][Coords.E5.getColumn()] = Pawn.KING;

        return board;
    }

    @Override
    public void move(Coords from, Coords to) {
        super.move(from, to);

        if (from.equals(Coords.E5))
            set(from, Pawn.THRONE);
    }

    @Override
    public Collection<Coords> getCitadels() {
        return citadels;
    }
}
