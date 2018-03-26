package uk.ac.reading.ps026478.playgroundwars.units;

import android.graphics.Bitmap;

/**
 * Lizard unit class inheriting from Unit class. This class specifies the types of units that Lizard is
 * weak against and strong against.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class Lizard extends Unit {

    public Lizard(Bitmap unitBitmap, Bitmap selectedBitmap, boolean isAIControlled, int xPosition, int yPosition) {
        super(unitBitmap, selectedBitmap, isAIControlled, xPosition, yPosition);
        this.unitType = UnitTypes.LIZARD;

        this.weakAgainst[0] = UnitTypes.SCISSORS.name();
        this.weakAgainst[1] = UnitTypes.ROCK.name();
        strongAgainst[0] = UnitTypes.SPOCK.name();
        strongAgainst[1] = UnitTypes.PAPER.name();
    }
}
