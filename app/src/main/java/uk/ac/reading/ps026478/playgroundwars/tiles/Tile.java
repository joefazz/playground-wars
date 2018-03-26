package uk.ac.reading.ps026478.playgroundwars.tiles;

import android.graphics.Bitmap;

/**
 * Tile is the base abstract class that each tile inherits from. It contains information such as their
 * specific bitmap's, their x and y positions and their selected bitmaps.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */

public abstract class Tile {
    private Bitmap tileBitmap;
    private int gridXPos;
    private int gridYPos;
    private int screenXPos;
    private int screenYPos;
    protected int movementCost = 1;
    protected TileTypes tileType;

    public Tile(Bitmap mTileBitmap, int mTileX, int mTileY) {
        this.tileBitmap = mTileBitmap;
        this.gridXPos = mTileX;
        this.gridYPos = mTileY;
    }

    public Bitmap getTileBitmap() {
        return tileBitmap;
    }

    public void setTileBitmap(Bitmap tileBitmap) {
        this.tileBitmap = tileBitmap;
    }

    public int getGridXPos() {
        return gridXPos;
    }

    public int getGridYPos() {
        return gridYPos;
    }

    public int getScreenXPos() {
        return screenXPos;
    }

    public int getScreenYPos() {
        return screenYPos;
    }

    public int getMovementCost() {
        return movementCost;
    }

    public void setScreenXPos(int screenXPos) {
        this.screenXPos = screenXPos;
    }

    public void setScreenYPos(int screenYPos) {
        this.screenYPos = screenYPos;
    }

    public TileTypes getTileType() {
        return tileType;
    }

    abstract protected void tileBehavior();
}
