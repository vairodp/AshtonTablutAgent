package it.ai.game.tablut;

import java.io.Serializable;

public final class Coords implements it.ai.game.Coords, Serializable {
    public static final Coords A4 = Coords.fromString("A4");
    public static final Coords A5 = Coords.fromString("A5");
    public static final Coords A6 = Coords.fromString("A6");
    public static final Coords B5 = Coords.fromString("B5");
    public static final Coords C5 = Coords.fromString("C5");
    public static final Coords D1 = Coords.fromString("D1");
    public static final Coords D4 = Coords.fromString("D4");
    public static final Coords D5 = Coords.fromString("D5");
    public static final Coords D6 = Coords.fromString("D6");
    public static final Coords D9 = Coords.fromString("D9");
    public static final Coords E1 = Coords.fromString("E1");
    public static final Coords E2 = Coords.fromString("E2");
    public static final Coords E3 = Coords.fromString("E3");
    public static final Coords E4 = Coords.fromString("E4");
    public static final Coords E5 = Coords.fromString("E5");
    public static final Coords E6 = Coords.fromString("E6");
    public static final Coords E7 = Coords.fromString("E7");
    public static final Coords E8 = Coords.fromString("E8");
    public static final Coords E9 = Coords.fromString("E9");
    public static final Coords F1 = Coords.fromString("F1");
    public static final Coords F4 = Coords.fromString("F4");
    public static final Coords F5 = Coords.fromString("F5");
    public static final Coords F6 = Coords.fromString("F6");
    public static final Coords F9 = Coords.fromString("F9");
    public static final Coords G5 = Coords.fromString("G5");
    public static final Coords H5 = Coords.fromString("H5");
    public static final Coords I4 = Coords.fromString("I4");
    public static final Coords I5 = Coords.fromString("I5");
    public static final Coords I6 = Coords.fromString("I6");

    private final int row;
    private final int column;

    public Coords(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coords)) return false;

        Coords coords = (Coords) o;

        return row == coords.row && column == coords.column;
    }

    @Override
    public int hashCode() {
        int value = 629;
        return (value + row) * value + column;
    }

    @Override
    public String toString() {
        return String.valueOf(Character.toUpperCase((char) (column + 97))).concat(String.valueOf(row + 1));
    }


    public static Coords fromString(String coord) {
        return new Coords(Coords.getRowFromString(coord), Coords.getColumnFromString(coord));
    }

    private static int getRowFromString(String coord) {
        String row = coord.substring(1);
        return Integer.parseInt(row) - 1;
    }

    private static int getColumnFromString(String coord) {
        char column = Character.toLowerCase(coord.charAt(0));
        return ((int) column) - 97;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public Coords top() {
        return new Coords(row - 1, column);
    }

    public Coords bottom() {
        return new Coords(row + 1, column);
    }

    public Coords left() {
        return new Coords(row, column - 1);
    }

    public Coords right() {
        return new Coords(row, column + 1);
    }

    public static Coords[] surroundingPositions(Coords position) {
        return new Coords[]{
                position.top(),
                position.bottom(),
                position.left(),
                position.right()
        };
    }
}
