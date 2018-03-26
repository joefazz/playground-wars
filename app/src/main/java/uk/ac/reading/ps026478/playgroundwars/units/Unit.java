package uk.ac.reading.ps026478.playgroundwars.units;

import android.graphics.Bitmap;

/**
 * Base abstract Unit class that all unit varieties inherit from. Contains information such as their
 * current health value, their x and y position on the grid, their x and y position on the screen in pixels,
 * what team they belong to, their movement range, if they are selected, if they are active, if they are alive,
 * the type of unit that they are, what they are weakAgainst, what they are strongAgainst and their respective
 * bitmaps.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 20/4/2017
 */
public abstract class Unit {
    protected final int MAX_HEALTH = 10;
    protected int currentHealth;
    private int gridXPos;
    private int gridYPos;
    private int screenXPos;
    private int screenYPos;
    public boolean isRedTeam;
    protected int movementRange = 2;
    public boolean isUnitSelected = false;
    public boolean isUnitActive;
    private boolean isAlive = true;
    protected UnitTypes unitType;
    protected String[] weakAgainst = new String[2];
    protected String[] strongAgainst = new String[2];
    protected Bitmap unitBitmap;
    protected Bitmap selectedBitmap;

    /**
     * Constructor for Unit object, assigns the units their properties and if they are acitve or not.
     *
     * @param unitBitmap Bitmap reference so the unit has a visual representation
     * @param selectedBitmap Bitmap reference for when the unit has been selected
     * @param isRedTeam Boolean determining which team the unit belongs to
     * @param gridXPos The x position on the grid the unit belongs to
     * @param gridYPos The y position on the grid the unit belongs to
     */
    public Unit(Bitmap unitBitmap, Bitmap selectedBitmap, boolean isRedTeam, int gridXPos, int gridYPos) {
        this.unitBitmap = unitBitmap;
        this.selectedBitmap = selectedBitmap;
        this.isRedTeam = isRedTeam;
        this.gridXPos = gridXPos;
        this.gridYPos = gridYPos;
        currentHealth = MAX_HEALTH;

        if (!isRedTeam) {
            this.isUnitActive = false;
        } else {
            this.isUnitActive = true;
        }
    }

    /**
     * Function carried out when the fight function is performed, determines the amount of health lost
     * by the fighting units.
     *
     * @param damageTaken The amount of damage to reduce the unit health by.
     */
    protected void damageTaken(int damageTaken) {
        currentHealth -= damageTaken;

        if (currentHealth <= 0) {
            this.isAlive = false;
        }
    }

    /**
     * Function not in use currently but could be used for any units that can provide healing.
     *
     * @param healingAmount The amount to increse the unit's health by.
     */
    public void healthRestored(int healingAmount) {
        currentHealth += healingAmount;
    }

    public Bitmap getUnitBitmap() {
        if (isUnitSelected) {
            return selectedBitmap;
        } else {
            return unitBitmap;
        }
    }

    public void setGridXPos(int gridXPos) {
        this.gridXPos = gridXPos;
    }

    public void setGridYPos(int gridYPos) {
        this.gridYPos = gridYPos;
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

    public boolean isAlive() {
        return isAlive;
    }

    public int getScreenYPos() {
        return screenYPos;
    }

    public void setScreenXPos(int screenXPos) {
        this.screenXPos = screenXPos;
    }

    public void setScreenYPos(int screenYPos) {
        this.screenYPos = screenYPos;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public void setMovementRange(int movementRange) {
        this.movementRange = movementRange;
    }

    public UnitTypes getUnitType() {
        return unitType;
    }

    public void setUnitBitmap(Bitmap unitBitmap) {
        this.unitBitmap = unitBitmap;
    }

    /**
     * Function that is run whenever the user selects a unit and touches an in range opponent unit. Function
     * first checks to see if it is weak to the enemy unit, then will take and deal damage based on that.
     *
     * <p>It then checks to see if it is strong against the enemy unit and will do the same thing.</p>
     *
     * <p>If both units are of the same type then they will deal the same amount of damage to each other.</p>
     * @param enemyUnit
     */
    public void fight(Unit enemyUnit) {
        for (String bad: weakAgainst) {
            if (enemyUnit.getUnitType().name() == bad) {
                enemyUnit.damageTaken(3);
                this.damageTaken(4);
            }
        }

        for (String good: strongAgainst) {
            if (enemyUnit.getUnitType().name() == good) {
                enemyUnit.damageTaken(7);
                this.damageTaken(3);
            }
        }

        if (enemyUnit.getUnitType().name() == this.getUnitType().name()) {
            enemyUnit.damageTaken(5);
            this.damageTaken(5);
        }
    }
}
