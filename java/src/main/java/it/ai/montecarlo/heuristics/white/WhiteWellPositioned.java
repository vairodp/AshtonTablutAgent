package it.ai.montecarlo.heuristics.white;

import it.ai.game.tablut.Coords;
import it.ai.game.tablut.Pawn;
import it.ai.montecarlo.heuristics.PawnsWellPositioned;

public class WhiteWellPositioned extends PawnsWellPositioned {
    private static final Coords[] bestPositions = {
            Coords.fromString("D3"), Coords.fromString("F4"),
            Coords.fromString("D6"), Coords.fromString("F7"),
    };

    /***
     * @param threshold used to decide whether to use the configuration. Default: 6
     */
    public WhiteWellPositioned(int threshold) {
        super(threshold, bestPositions, Pawn.WHITE);
    }

    public WhiteWellPositioned() {
        this(6);
    }
}
