package it.ai.montecarlo.heuristics.black;

import it.ai.game.tablut.Coords;
import it.ai.game.tablut.Pawn;
import it.ai.montecarlo.heuristics.PawnsWellPositioned;

public class BlackOnRhombus extends PawnsWellPositioned {
    //Array of favourite black positions in initial stages and to block the escape ways
    private static final Coords[] rhombus = {
            Coords.fromString("C2"), Coords.fromString("G2"),
            Coords.fromString("B3"), Coords.fromString("H3"),

            Coords.fromString("B7"), Coords.fromString("H7"),
            Coords.fromString("C8"), Coords.fromString("G8")
    };

    /***
     *
     * @param threshold used to decide whether to use rhombus configuration. Default: 10
     */
    public BlackOnRhombus(int threshold) {
        super(threshold, rhombus, Pawn.BLACK);
    }

    public BlackOnRhombus() {
        this(10);
    }
}
