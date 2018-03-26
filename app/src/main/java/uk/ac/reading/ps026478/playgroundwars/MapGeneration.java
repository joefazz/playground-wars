package uk.ac.reading.ps026478.playgroundwars;

import uk.ac.reading.ps026478.playgroundwars.tiles.Tile;
import uk.ac.reading.ps026478.playgroundwars.units.Unit;
import uk.ac.reading.ps026478.playgroundwars.units.UnitTypes;


    // Class which creates the map for objects to live on, seperate 2D arrays are used with the public
    // security so they can be accessed very easily by TheGame.java

/**
 * MapGeneration class which creates the 2D arrays for the tileMap and the unitMap. It also defines the
 * height and width of the grid overall which is used to define the contrains for both 2D arrays.
 *
 * In the future it may be a better idea to just use ArrayLists as null objects then wouldn't be a problem.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */
public class MapGeneration {
    public Tile[][] tileMap;
    public Unit[][] unitMap;
    private final int MAP_HEIGHT = 8;
    private final int MAP_WIDTH = 6;

    public MapGeneration() {
        tileMap = new Tile[MAP_HEIGHT][MAP_WIDTH];
        unitMap = new Unit[MAP_HEIGHT][MAP_WIDTH];
    }

    public int getMapHeight() {
        return MAP_HEIGHT;
    }

    public int getMapWidth() {
        return MAP_WIDTH;
    }
}
