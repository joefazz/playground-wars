package uk.ac.reading.ps026478.playgroundwars.tiles;

import android.graphics.Bitmap;

/**
 * StoneTile class inheriting from Tile class.
 *
 * Contains a tileBehavior method for future reference and usage if desired.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class StoneTile extends Tile {
    public StoneTile(Bitmap mTileBitmap, int mTileX, int mTileY) {
        super(mTileBitmap, mTileX, mTileY);
        this.tileType = TileTypes.STONE;
    }

    @Override
    protected void tileBehavior() {
        // Rock gets a buff
    }
}
