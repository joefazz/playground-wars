package uk.ac.reading.ps026478.playgroundwars.tiles;

import android.graphics.Bitmap;

/**
 * GrassTile class inheriting from Tile class.
 *
 * Contains a tileBehavior method for future reference and usage if desired.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public class GrassTile extends Tile {
    public GrassTile(Bitmap mTileBitmap, int mTileX, int mTileY) {
        super(mTileBitmap, mTileX, mTileY);
        this.tileType = TileTypes.GRASS;
    }

    @Override
    protected void tileBehavior() {
        // Lizard gets a buff
    }
}
