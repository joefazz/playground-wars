package uk.ac.reading.ps026478.playgroundwars.units;

import android.graphics.Bitmap;

/**
 * Rock unit class inheriting from Unit class. This class specifies the types of units that Rock is
 * weak against and strong against.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class Rock extends Unit {

    public Rock(Bitmap unitBitmap, Bitmap selectedBitmap, boolean isAIControlled, int xPosition, int yPosition) {
        super(unitBitmap, selectedBitmap, isAIControlled, xPosition, yPosition);
        this.movementRange = 1;
        this.unitType = UnitTypes.ROCK;
        this.currentHealth = 15;

        weakAgainst[0] = UnitTypes.SPOCK.name();
        weakAgainst[1] = UnitTypes.PAPER.name();
        strongAgainst[0] = UnitTypes.SCISSORS.name();
        strongAgainst[1] = UnitTypes.LIZARD.name();
    }
}
