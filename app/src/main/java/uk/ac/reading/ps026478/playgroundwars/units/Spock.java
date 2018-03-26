package uk.ac.reading.ps026478.playgroundwars.units;

import android.graphics.Bitmap;

/**
 * Spock unit class inheriting from Unit class. This class specifies the types of units that Spock is
 * weak against and strong against.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class Spock extends Unit {

    public Spock(Bitmap unitBitmap, Bitmap selectedBitmap, boolean isAIControlled, int xPosition, int yPosition) {
        super(unitBitmap, selectedBitmap, isAIControlled, xPosition, yPosition);
        this.unitType = UnitTypes.SPOCK;

        weakAgainst[0] = UnitTypes.PAPER.name();
        weakAgainst[1] = UnitTypes.LIZARD.name();
        strongAgainst[0] = UnitTypes.ROCK.name();
        strongAgainst[1] = UnitTypes.SCISSORS.name();
    }
}
