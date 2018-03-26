package uk.ac.reading.ps026478.playgroundwars.units;

import android.graphics.Bitmap;

/**
 * Paper unit class inheriting from Unit class. This class specifies the types of units that Paper is
 * weak against and strong against.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */
public class Paper extends Unit {

    public Paper(Bitmap unitBitmap, Bitmap selectedBitmap, boolean isAIControlled, int xPosition, int yPosition) {
        super(unitBitmap, selectedBitmap, isAIControlled, xPosition, yPosition);
        this.unitType = UnitTypes.PAPER;
        weakAgainst[0] = UnitTypes.LIZARD.name();
        weakAgainst[1] = UnitTypes.SCISSORS.name();
        strongAgainst[0] = UnitTypes.ROCK.name();
        strongAgainst[1] = UnitTypes.SPOCK.name();
    }
}
