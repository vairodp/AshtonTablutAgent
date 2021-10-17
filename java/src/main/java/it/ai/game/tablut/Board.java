package it.ai.game.tablut;

import it.ai.util.CopyUtils;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.*;

public class Board implements Cloneable, Serializable {
    private int[][] board;
    private HashMap<Integer, Set<Coords>> pawnCoords;

    public Board(int[][] board) {
        this.board = board;
        this.pawnCoords = new HashMap<>() {{
            put(Pawn.BLACK, new HashSet<>());
            put(Pawn.WHITE, new HashSet<>());
            put(Pawn.KING, new HashSet<>());
        }};
        index();
    }

    private void index() {
        for (int i = 0; i < numberOfRows(); i++) {
            for (int j = 0; j < numberOfColumns(); j++) {
                int pawn = board[i][j];
                Set<Coords> pawns = pawnCoords.get(pawn);
                if (pawns != null) pawns.add(new Coords(i, j));
            }
        }
    }

    public int numberOfRows() {
        return board.length;
    }

    public int numberOfColumns() {
        return numberOfRows() > 0 ? board[0].length : 0;
    }

    public void remove(Coords coords) {
        set(coords, Pawn.EMPTY);
    }

    public void remove(int row, int column) {
        remove(new Coords(row, column));
    }

    public void set(Coords coords, int pawn) {
        int currentPawn = this.board[coords.getRow()][coords.getColumn()];

        if (pawnCoords.containsKey(currentPawn))
            pawnCoords.get(currentPawn).remove(coords);

        if (pawnCoords.containsKey(pawn))
            pawnCoords.get(pawn).add(coords);

        this.board[coords.getRow()][coords.getColumn()] = pawn;
    }

    public void set(int row, int column, int pawn) {
        set(new Coords(row, column), pawn);
    }

    public int get(Coords coords) {
        return get(coords.getRow(), coords.getColumn());
    }

    public int get(int row, int column) {
        return this.board[row][column];
    }

    public void move(Coords from, Coords to) {
        int pawn = get(from);

        remove(from);
        set(to, pawn);
    }

    public Collection<Coords> getCitadels() {
        return Collections.emptyList();
    }

    public boolean inCitadels(Coords coords) {
        return getCitadels().contains(coords);
    }

    public boolean inCitadels(int row, int column) {
        return getCitadels().contains(new Coords(row, column));
    }

    public int count(int pawn) {
        Set<Coords> cells = pawnCoords.get(pawn);
        if (cells != null)
            return cells.size();

        return (int) Arrays.stream(board).mapToLong(row ->
                Arrays.stream(row).filter(cell -> cell == pawn).count()).sum();
    }

    public Set<Coords> getPawnCoords(int pawn) {
        return pawnCoords.get(pawn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board)) return false;
        Board board1 = (Board) o;
        return Arrays.deepEquals(board, board1.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @SneakyThrows
    @Override
    public Board clone() {
        Board newBoard = (Board) super.clone();


        newBoard.board = CopyUtils.clone(board);
        newBoard.pawnCoords = CopyUtils.clone(pawnCoords);

        return newBoard;
    }

    @Override
    public String toString() {
        return "Board{" +
                "board=" + Arrays.deepToString(board) +
                '}';
    }
}
