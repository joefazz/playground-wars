package uk.ac.reading.ps026478.playgroundwars.units;

import android.graphics.Bitmap;

/**
 * Scissors unit class inheriting from Unit class. This class specifies the types of units that Scissors is
 * weak against and strong against.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class Scissors extends Unit {

    public Scissors(Bitmap unitBitmap, Bitmap selectedBitmap, boolean isAIControlled, int xPosition, int yPosition) {
        super(unitBitmap, selectedBitmap, isAIControlled, xPosition, yPosition);
        this.unitType = UnitTypes.SCISSORS;

        weakAgainst[0] = UnitTypes.SPOCK.name();
        weakAgainst[1] = UnitTypes.ROCK.name();
        strongAgainst[0] = UnitTypes.PAPER.name();
        strongAgainst[1] = UnitTypes.LIZARD.name();
    }
}
