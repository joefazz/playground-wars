package uk.ac.reading.ps026478.playgroundwars;

//Other parts of the android libraries that we use
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import uk.ac.reading.ps026478.playgroundwars.tiles.GrassTile;
import uk.ac.reading.ps026478.playgroundwars.tiles.LavaTile;
import uk.ac.reading.ps026478.playgroundwars.tiles.SandTile;
import uk.ac.reading.ps026478.playgroundwars.tiles.StoneTile;
import uk.ac.reading.ps026478.playgroundwars.tiles.Tile;
import uk.ac.reading.ps026478.playgroundwars.units.Lizard;
import uk.ac.reading.ps026478.playgroundwars.units.Paper;
import uk.ac.reading.ps026478.playgroundwars.units.Rock;
import uk.ac.reading.ps026478.playgroundwars.units.Scissors;
import uk.ac.reading.ps026478.playgroundwars.units.Spock;
import uk.ac.reading.ps026478.playgroundwars.units.Unit;
import uk.ac.reading.ps026478.playgroundwars.units.UnitTypes;

/**
 * TheGame class inherits from the GameThread class implementing the abstract declarations made there.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 19/4/17
 */
public class TheGame extends GameThread {



    // Store the images of the map
    private Bitmap mLavaTile;
    private Bitmap mGrassTile;
    private Bitmap mStoneTile;
    private Bitmap mSandTile;
    private Bitmap mHighlightTile;


    // Store the images of the units
    private Bitmap mSpockBlue;
    private Bitmap mSpockRed;
    private Bitmap mSpockSelected;
    private Bitmap mLizardBlue;
    private Bitmap mLizardRed;
    private Bitmap mLizardSelected;
    private Bitmap mPaperBlue;
    private Bitmap mPaperRed;
    private Bitmap mPaperSelected;
    private Bitmap mScissorsBlue;
    private Bitmap mScissorsRed;
    private Bitmap mScissorsSelected;
    private Bitmap mRockBlue;
    private Bitmap mRockRed;
    private Bitmap mRockSelected;

    // Store the score for each team
    private long mBlueScore;
    private long mRedScore;

    // Reference to the end turn button
    private Button mEndTurnButton;

    // Reference for the online database
    private DatabaseReference databaseReference;

    // Reference for the player name turn indicator
    private TextView mPlayerNameTextView;

    // A very simple trigger switch to make sure duplicates can't be written to the database
    private Boolean hasWrittenToDB = false;

    // Store the pixel height and width of the display in use
    int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    // Create map object
    MapGeneration map = new MapGeneration();

    // Define borders for UI outside the map
    int borderHeight = screenHeight - (175 * map.getMapHeight());
    int borderWidth = screenWidth - (175 * map.getMapWidth());

    // Create arraylist to store tiles that are highlighted (explain process)
    ArrayList<Tile> highlightedTiles = new ArrayList<>();
    ArrayList<Unit> blueUnits = new ArrayList<>();
    ArrayList<Unit> redUnits = new ArrayList<>();

    int turnCounter = 0;



    //This is run before anything else, so we can prepare things here

    /**
     * Constructor for TheGame class where the onClickListener is set for the end turn button, as well
     * as all the Bitmap objects being assigned theiry relevent PNGs. It also calls the setupBeginning function
     * so the game is ready to go.
     * @param gameView Reference to the gameView so the canvas can be changed
     * @param endTurnButton Reference from the UI thread to the button that is used to switch turns
     * @param playerName Reference from the UI thread to the textfield that displays the player's name
     */
    public TheGame(GameView gameView, Button endTurnButton, TextView playerName) {
        //House keeping
        super(gameView);

        this.mPlayerNameTextView = playerName;
        this.mEndTurnButton = endTurnButton;

        mEndTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEndTurnButton.getText() != "Game Over") {
                    if (turnCounter % 2 == 1) {
                        nextTurn(true);
                    } else {
                        nextTurn(false);
                    }
                }
            }
        });

        // ASSIGNMENT OF ALL BITMAP VARIABLES TO THE CORRECT RESOURCES
        mLavaTile = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.lava_tile);

        mGrassTile = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.grass_tile);

        mSandTile = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.sand_tile);

        mHighlightTile = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.highlight_tile);

        mStoneTile = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.stone_tile);

        mSpockBlue = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.spock_blue);

        mSpockRed = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.spock_red);

        mSpockSelected = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.spock_selected);

        mLizardBlue = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.lizard_blue);

        mLizardRed = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.lizard_red);

        mLizardSelected = BitmapFactory.decodeResource(
                gameView.getContext().getResources(),
                        R.drawable.lizard_selected);

        mRockBlue = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.rock_blue);

        mRockRed = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.rock_red);

        mRockSelected = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.rock_selected);

        mScissorsBlue = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.scissors_blue);

        mScissorsRed = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.scissors_red);

        mScissorsSelected = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.scissors_selected);

        mPaperBlue = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.paper_blue);

        mPaperRed = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.paper_red);

        mPaperSelected = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.paper_selected);

        // Required after it crashes due to null reference errors
        setupBeginning();
    }

    /**
     * setUpBeginning is run after the contructor finishes assigning bitmaps to the Bitmap objects so
     * no NullReferenceExceptions occur. It sets the location for all the Units and Tiles on the screen
     * so that the world is populated. It uses RNG because it makes life easier and more fun.
     */
    @Override
    public void setupBeginning() {
        int unitCounter = 0;

        // for every point of the map grid assign a random tile and have a 10% chance of generating an AI
        for (int y = 0; y < map.getMapHeight(); y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                switch ((int) (Math.random() * 6)) {
                    case 0:
                        map.tileMap[y][x] = new GrassTile(mGrassTile, x, y);
                        break;
                    case 1:
                        map.tileMap[y][x] = new StoneTile(mStoneTile, x, y);
                        break;
                    case 2:
                        map.tileMap[y][x] = new SandTile(mSandTile, x, y);
                        break;
                    case 3:
                        map.tileMap[y][x] = new LavaTile(mLavaTile, x, y);
                        break;
                    case 4:
                        map.tileMap[y][x] = new GrassTile(mGrassTile, x, y);
                        break;
                    case 5:
                        map.tileMap[y][x] = new StoneTile(mStoneTile, x, y);
                }
            }
        }

        // Random chance of a red unit appearing in the top half of the grid
        for (int y = 0; y < map.getMapHeight() / 2; y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                switch ((int)(Math.random() * 4)) {
                    case 0:
                        if (unitCounter == 0) {
                            map.unitMap[y][x] = new Rock(mRockRed, mRockSelected, true, x, y);
                            unitCounter++;
                        } else if (unitCounter == 1) {
                            map.unitMap[y][x] = new Paper(mPaperRed, mPaperSelected, true, x, y);
                            unitCounter++;
                        } else if (unitCounter == 2) {
                            map.unitMap[y][x] = new Scissors(mScissorsRed, mScissorsSelected, true, x, y);
                            unitCounter++;
                        } else if (unitCounter == 3) {
                            map.unitMap[y][x] = new Lizard(mLizardRed, mLizardSelected, true, x, y);
                            unitCounter++;
                        } else if (unitCounter == 4) {
                            map.unitMap[y][x] = new Spock(mSpockRed, mSpockSelected, true, x, y);
                            unitCounter++;
                        }
                }
            }
        }

        unitCounter = 0;

        // Random chance of a blue unit appearing in the top of the grid
        for (int y = map.getMapHeight() / 2; y < map.getMapHeight(); y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                switch ((int)(Math.random() * 4)) {
                    case 0:
                        if (unitCounter == 0) {
                            map.unitMap[y][x] = new Rock(mRockBlue, mRockSelected, false, x, y);
                            unitCounter++;
                        } else if (unitCounter == 1) {
                            map.unitMap[y][x] = new Paper(mPaperBlue, mPaperSelected, false, x, y);
                            unitCounter++;
                        } else if (unitCounter == 2) {
                            map.unitMap[y][x] = new Scissors(mScissorsBlue, mScissorsSelected, false, x, y);
                            unitCounter++;
                        } else if (unitCounter == 3) {
                            map.unitMap[y][x] = new Lizard(mLizardBlue, mLizardSelected, false, x, y);
                            unitCounter++;
                        } else if (unitCounter == 4) {
                            map.unitMap[y][x] = new Spock(mSpockBlue, mSpockSelected, false, x, y);
                            unitCounter++;
                        }
                }
            }
        }


        // Store a reference of all units inside an ArrayList
        for (Unit[] units : map.unitMap) {
            for (Unit unit : units) {
                if (unit != null && unit.isRedTeam) {
                    redUnits.add(unit);
                }
            }
        }

        mRedScore = redUnits.size() * 1000;
        setScore(mRedScore, true);

    }

    /**
     * doDraw function which will check every grid location to see what Tile needs to be drawn there
     * and if a unit needs to be drawn there too.
     * @param canvas Reference to the canvas that is drawn by this function
     */
    @Override
    protected void doDraw(Canvas canvas) {
        //If there isn't a canvas to do nothing
        //It is ok not understanding what is happening here
        if (canvas == null) return;

        //House keeping
        super.doDraw(canvas);

        // Store the 'cursor' location that will be scrolled through for the drawing
        int currentHeight = borderHeight / 2;
        int currentWidth = borderWidth / 2;

        // For as many spaces on the map there are draw all the tiles, if there are any highlighted
        // tiles draw those too and then draw all the units on top

        for (int y = 0; y < map.getMapHeight(); y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                map.tileMap[y][x].setScreenXPos(currentWidth);
                map.tileMap[y][x].setScreenYPos(currentHeight);
                canvas.drawBitmap(map.tileMap[y][x].getTileBitmap(), map.tileMap[y][x].getScreenXPos(), map.tileMap[y][x].getScreenYPos(), null);


                for (Tile tile : highlightedTiles) {
                    if (x == tile.getGridXPos() && y == tile.getGridYPos()) {
                        tile.setScreenXPos(currentWidth);
                        tile.setScreenYPos(currentHeight);
                        canvas.drawBitmap(mHighlightTile, tile.getScreenXPos(), tile.getScreenYPos(), null);
                    }
                }

                if (map.unitMap[y][x] != null) {
                    map.unitMap[y][x].setScreenXPos((currentWidth + 87) - map.unitMap[y][x].getUnitBitmap().getWidth() / 2);
                    map.unitMap[y][x].setScreenYPos((currentHeight + 87) - map.unitMap[y][x].getUnitBitmap().getHeight() / 2);
                    canvas.drawBitmap(map.unitMap[y][x].getUnitBitmap(), map.unitMap[y][x].getScreenXPos(), map.unitMap[y][x].getScreenYPos(), null);
                }
                currentWidth += 175;
            }
            currentWidth = borderWidth / 2;
            currentHeight += 175;
        }
    }


    /**
     * actionOnTouch function will check the position of the touch, if a unit is touched then that
     * unit will become selected and the movementHighlighting function will be called in order to show
     * which tiles the unit can move to.
     *
     * <p>If a tile is touched that is occupied by an enemy unit then the fight function will be run.</p>
     * @param x x coordinate of the touch event
     * @param y y coordinate of the touch event
     */
    @Override
    protected void actionOnTouch(float x, float y) {
        // Take the x and y, ignore all above and below the grid (for now),
        // Check every single unit to see if any of them have been touched
        if (y < (borderHeight / 2) + (175 * map.getMapHeight()) && y > borderHeight / 2) {
            for (Unit[] units : map.unitMap) {
                for (Unit unit : units) {
                    if (unit != null && unit.isUnitActive) {
                        if (x < unit.getScreenXPos() + 100 && x > unit.getScreenXPos() && y < unit.getScreenYPos() + 100 && y > unit.getScreenYPos()) {
                            // Switch the unit state to inactive if the unit is touched again while active
                            if (unit.isUnitSelected) {
                                unit.isUnitSelected = false;
                                highlightedTiles.clear();
                            } else {
                                // If the unit is inactive and is touched then it becomes active and the tiles it can move to are highlighted
                                unit.isUnitSelected = true;
                                movementHighlighting(map.tileMap[unit.getGridYPos()][unit.getGridXPos()], unit.getMovementRange());
                            }
                        } else {
                            // If a space is touched that the current unit does not occupy check to see if the unit is selected
                            if (unit.isUnitSelected) {
                                // If the unit is active then take all the highlighted tiles and check to see if any of them are pressed
                                for (Tile tile : highlightedTiles) {
                                    if (x < tile.getScreenXPos() + 175 && y < tile.getScreenYPos() + 175 &&
                                            x > tile.getScreenXPos() && y > tile.getScreenYPos()) {
                                        int heightDif = Math.abs(unit.getGridYPos() - tile.getGridYPos());
                                        int widthDif = Math.abs(unit.getGridXPos() - tile.getGridXPos());

                                        if (unit.getMovementRange() - tile.getMovementCost() == 0 || heightDif + widthDif == 2) {
                                            unit.isUnitActive = false;
                                        } else {
                                            unit.setMovementRange(unit.getMovementRange()-1);
                                        }


                                        // If the space is unoccupied
                                        if (map.unitMap[tile.getGridYPos()][tile.getGridXPos()] == null) {

                                            // If they are pressed then first the old grid position of the unit must be made null
                                            map.unitMap[unit.getGridYPos()][unit.getGridXPos()] = null;
                                            map.unitMap[tile.getGridYPos()][tile.getGridXPos()] = unit;
                                            unit.setGridXPos(tile.getGridXPos());
                                            unit.setGridYPos(tile.getGridYPos());

                                        } else {
                                            if ((!unit.isRedTeam && map.unitMap[tile.getGridYPos()][tile.getGridXPos()].isRedTeam) ||
                                                    (unit.isRedTeam && !map.unitMap[tile.getGridYPos()][tile.getGridXPos()].isRedTeam)) {
                                                Unit enemyUnit = map.unitMap[tile.getGridYPos()][tile.getGridXPos()];

                                                unit.fight(enemyUnit);

                                                if (!unit.isAlive()) {
                                                    map.unitMap[unit.getGridYPos()][unit.getGridXPos()] = null;
                                                }

                                                if (!enemyUnit.isAlive()) {
                                                    map.unitMap[unit.getGridYPos()][unit.getGridXPos()] = null;
                                                    map.unitMap[enemyUnit.getGridYPos()][enemyUnit.getGridXPos()] = unit;
                                                    unit.setGridXPos(tile.getGridXPos());
                                                    unit.setGridYPos(tile.getGridYPos());
                                                    unit.isUnitActive = false;
                                                }
                                            }
                                        }

                                        // No point going through elements when we've already found what we want
                                        break;
                                    }
                                }
                                // Deselect the unit and clear the highlighted tiles
                                unit.isUnitSelected = false;
                                highlightedTiles.clear();
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * updateGame function is called in order to always check if someone has won the game yet and if
     * they have then the gameOver function is called. It also has limited control of passing turns over by
     * always checking to see if any particular team has all their units inactive therefore meaning they
     * have no more turns to make at which point the nextTurn function is called.
     * @param secondsElapsed How the function knows that time is passing.
     */
    @Override
    protected void updateGame(float secondsElapsed) {
        // Always keep track of the exact amount of units left alive
        blueUnits.clear();
        redUnits.clear();
        for (Unit[] units : map.unitMap) {
            for (Unit unit : units) {
                if (unit != null) {
                    if (unit.isRedTeam) {
                        redUnits.add(unit);
                    } else {
                        blueUnits.add(unit);
                    }
                }
            }
        }

        // Set the score
        mRedScore = redUnits.size() * 1000;
        mBlueScore = blueUnits.size() * 1000;

        // If either team has no units left then the game is over
        if (mRedScore == 0 || mBlueScore == 0) {
            gameOver();
        }

        // Always checks to see how many units are active, if all of them are inactive then go to next turn
        if (turnCounter % 2 == 1) {
            for (int i = 0; i < blueUnits.size(); i++) {
                if (!blueUnits.get(i).isUnitActive) {
                    if (i == blueUnits.size() - 1) {
                        nextTurn(true);
                    }
                } else {
                    break;
                }
            }
        } else {
            for (int i = 0; i < redUnits.size(); i++) {
                if (!redUnits.get(i).isUnitActive) {
                    if (i == redUnits.size() - 1) {
                        nextTurn(false);
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * nextTurn function increments the global turn counter and then checks to see whose turn is next,
     * it will then make all of that teams units switch to the active state so that they are able to move.
     *
     * <p>It also changes the score via the handler defined in the GameThread.</p>
     * @param isRedTurn Boolean value that helps determine whose turn it is next
     */
    private void nextTurn(boolean isRedTurn) {
        turnCounter++;

        if (isRedTurn) {
            for (Unit[] units : map.unitMap) {
                for (Unit unit : units) {
                    if (unit != null && unit.isRedTeam) {
                        unit.isUnitActive = true;
                        if (unit.getUnitType() != UnitTypes.ROCK) {
                            unit.setMovementRange(2);
                        }
                        setScore(mRedScore, true);
                    }

                    if (unit != null && !unit.isRedTeam) {
                        unit.isUnitActive = false;
                    }
                }
            }
        } else {
            for (Unit[] units : map.unitMap) {
                for (Unit unit : units) {
                    if (unit != null && !unit.isRedTeam) {
                        unit.isUnitActive = true;
                        if (unit.getUnitType() != UnitTypes.ROCK) {
                            unit.setMovementRange(2);
                        }
                        setScore(mBlueScore, false);
                    }

                    if (unit != null && unit.isRedTeam) {
                        unit.isUnitActive = false;
                    }
                }
            }
        }
    }

    /**
     * writeNewScore is a function that is used expressly to write to the online high score table.
     * It creates a User object and then writes that object's parameters to the Firebase database as children
     * of a randomly generated user id.
     * @param name Name of user to be added to database
     * @param score Score of user to be added to database
     */
    private void writeNewScore(String name, long score) {
        User user = new User(name, score);

        if (!hasWrittenToDB) {
            databaseReference.child("users").child(Integer.toString((int) (Math.random() * 100))).setValue(user);
            hasWrittenToDB = true;
        }
    }

    /**
     * gameOver function is called when either one of the sides has no units remaining. It creates the
     * reference to the Firebase database and determines the name of the user and the score that should be written to it
     * before calling the writeNewScore function and passing the name and score as parameters. It also sets to state
     * to STATE_WIN otherwise the game will go on forever and call this function everytime the update function is
     * executed.
     */
    private void gameOver() {
        databaseReference = FirebaseDatabase.getInstance().getReference();



        if (redUnits.size() != 0) {
            if (turnCounter % 2 == 1) {
                nextTurn(true);
            } else {
                writeNewScore(mPlayerNameTextView.getText().toString(), mRedScore);
            }
        } else {
            if (turnCounter % 2 == 0) {
                nextTurn(false);
            } else {
                writeNewScore(mPlayerNameTextView.getText().toString(), mBlueScore);
            }
        }

        setState(STATE_WIN);

    }

    /**
     * movementHighlighting is an algorithm that I created which, using recurssion will highlight the
     * tiles around a certain unit showing where it can and cannot move to. This information is based on
     * details such as the units movement allowance and the movement cost of moving to particular tiles.
     *
     * <p>The function will first attempt to add all tiles adjacent to the currentTile to an ArrayList and will
     * catch any ArrayIndexOutOfBoundsExceptions in case we're looking near the border of the grid.</p>
     *
     * <p>After adding all the adjacent tiles to the array list the function will check if the unit has anymore
     * movement allowance remaining and if it does then it will call the function again and pass in each adjacent tile
     * highlighting all adjacent tiles as it goes. This results in all tiles the unit can move to being highlighted
     * making it clear to the user where they can and cannot move to.</p>
     * @param currentTile The tile that is currently being checked for adjacent tiles.
     * @param movementAllowance The allowance of the unit that occupies the original tile.
     */
    private void movementHighlighting(Tile currentTile, int movementAllowance) {
        int totalMovementAllowance = movementAllowance;
        int currentX = currentTile.getGridXPos();
        int currentY = currentTile.getGridYPos();
        ArrayList<Tile> adjacentTiles = new ArrayList<>();

        // Wrap all inserts to the arraylist with a try and catch to avoid errors when
        // at the border of the grid
        try {
            adjacentTiles.add(map.tileMap[currentY][currentX + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing but print the stacktrace because everyone else does it
            e.printStackTrace();
        }

        try {
            adjacentTiles.add(map.tileMap[currentY][currentX - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing but print the stacktrace because everyone else does it
            e.printStackTrace();
        }

        try{
            adjacentTiles.add(map.tileMap[currentY + 1][currentX]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing but print the stacktrace because everyone else does it
            e.printStackTrace();
        }

        try {
            adjacentTiles.add(map.tileMap[currentY - 1][currentX]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing but print the stacktrace because everyone else does it
            e.printStackTrace();
        }

        // For all of the adjacent tiles calculate what the new movement allowance is, if it's 0 then
        // we can add the current tile to the global array list, if it's not then add the current tile
        // to the global array list and then call the algorithm again passing the current adjacent tile being inspected
        for (Tile tile : adjacentTiles) {
            int newAllowance = totalMovementAllowance - tile.getMovementCost();
            if (newAllowance > 0) {
                highlightedTiles.add(tile);
                movementHighlighting(tile, newAllowance);
            } else if (newAllowance == 0) {
                highlightedTiles.add(tile);
            }
        }
    }
}