package uk.ac.reading.ps026478.playgroundwars.tiles;

import android.graphics.Bitmap;

/**
 * LavaTile class inheriting from Tile class. Sets it's own movement cost as 2 as it is harder to get past.
 *
 * Contains a tileBehavior method for future reference and usage if desired.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class LavaTile extends Tile {
    public LavaTile(Bitmap mTileBitmap, int mTileX, int mTileY) {
        super(mTileBitmap, mTileX, mTileY);
        this.tileType = TileTypes.LAVA;
        this.movementCost = 2;
    }

    @Override
    protected void tileBehavior() {
        // Take 1 damage per turn while on a lava tile
    }
}
