package uk.ac.reading.ps026478.playgroundwars.tiles;

import android.graphics.Bitmap;

/**
 * SandTile class inheriting from Tile class. Sets it's own movement cost as 2 as it is harder to get past.
 *
 * Contains a tileBehavior method for future reference and usage if desired.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class SandTile extends Tile {
    public SandTile(Bitmap mTileBitmap, int mTileX, int mTileY) {
        super(mTileBitmap, mTileX, mTileY);
        this.tileType = TileTypes.SAND;
        this.movementCost = 2;
    }

    @Override
    protected void tileBehavior() {
        // Less movement distance
    }
}
