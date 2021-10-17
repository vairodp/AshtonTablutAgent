package it.ai.game.tablut;

public final class Pawn {
    public static final int WHITE = Player.WHITE;
    public static final int BLACK = Player.BLACK;
    public static final int EMPTY = -1;
    public static final int THRONE = 10;
    public static final int KING = 100;

    public static boolean isWhite(int pawn) {
        return pawn == Pawn.WHITE || pawn == Pawn.KING;
    }

    public static Integer getOwner(int pawn) {
        if (pawn == Pawn.BLACK)
            return Player.BLACK;

        if (Pawn.isWhite(pawn))
            return Player.WHITE;

        return null;
    }
}
