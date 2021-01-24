package lasers.backtracking;

// Import & Set Up Workspace
import lasers.model.LasersModel;
import lasers.model.components.Pillar;
import lasers.model.components.SafeNode;

import java.util.*;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the lasers.model
 * package and/or incorporate it into another class.
 *
 * @author RIT CS
 * @author Savannah Alfaro (sea2985)
 * @author Jamieson Dube (jmd2851)
 */
public class SafeConfig implements Configuration {

    // Initialize Variables
    private final int ROWS;
    private final int COLS;
    private int startRow;
    private int startCol;
    private char[][] safe;
    private final SafeNode[][] initialSafe;
    private int lastPlacedLaserRow;
    private int lastPlacedLaserCol;
    private SafeConfig predecessor;

    // Constructors
    /**
     * Initial configuration of the SafeConfig with the filename
     * @param filename (String) name of the file
     */
    public SafeConfig(String filename) {
        LasersModel m = new LasersModel(filename, true);
        this.startRow = 0;
        this.startCol = -1;
        this.safe = convert(m.getSafe());
        this.ROWS = m.getRows();
        this.COLS = m.getCols();
        this.safe = convert(m.getSafe());
        this.initialSafe = m.getInitialSafe();
        this.predecessor = null;
    }

    /**
     * Copy configuration of the SafeConfig
     * @param other (SafeConfig) current configuration to copy
     */
    public SafeConfig(SafeConfig other) {
        this.safe = Backtracker.copy2DCharArray(other.safe);
        this.initialSafe = other.initialSafe;
        this.ROWS = other.ROWS;
        this.COLS = other.COLS;
        this.startRow = other.startRow;
        this.startCol = other.startCol;
        this.lastPlacedLaserRow = other.lastPlacedLaserRow;
        this.lastPlacedLaserCol = other.lastPlacedLaserCol;
    }


    // Methods
    /**
     * Returns the collection of successors (2),
     * one of which is the period configuration
     * and one which is the laser configuration.
     * @return (Collection<Configuration>) the collection of successors
     */
    @Override
    public Collection<Configuration> getSuccessors() {

        startCol++;
        if (startCol == COLS) {
            startCol = 0;
            startRow++;
        }

        if (startRow == ROWS) {
            return new ArrayList<>();
        }

        List<Configuration> successors = new ArrayList<>();

        if (safe[startRow][startCol] != '*' && safe[startRow][startCol] != 'L'
        && safe[startRow][startCol] != '.') {
            SafeConfig unchangedConfig = new SafeConfig(this);
            unchangedConfig.predecessor = this.predecessor;
            successors.add(unchangedConfig);
        } else {

            // add period configuration
            SafeConfig newUnchangedConfig = new SafeConfig(this);
            newUnchangedConfig.predecessor = this.predecessor;
            successors.add(newUnchangedConfig);

            // add laser configuration
            SafeConfig addedLaserConfig = new SafeConfig(this);
            addedLaserConfig.add(startRow, startCol);
            addedLaserConfig.predecessor = this;
            addedLaserConfig.setLastPlacedLaserRow(startRow);
            addedLaserConfig.setLastPlacedLaserCol(startCol);
            successors.add(addedLaserConfig);
        }
        return successors;
    }

    /**
     * Checks to see if the configuration is valid.
     * Checks cardinal directions for a pillar,
     * which then checks to see if the last placed laser
     * created an invalid safe.
     * @return (boolean) true if the current configuration is valid
     */
    @Override
    public boolean isValid() {

        // north
        for (int i = lastPlacedLaserRow - 1; i >= 0; i--) {
            if (safe[i][lastPlacedLaserCol] != '.' && safe[i][lastPlacedLaserCol] != '*') {
                if (safe[i][lastPlacedLaserCol] == 'L') {
                    return false;
                }
                break;
            }
        }

        // east
        for (int i = lastPlacedLaserCol + 1; i < COLS; i++) {
            if (safe[lastPlacedLaserRow][i] != '.' && safe[lastPlacedLaserRow][i] != '*') {
                if (safe[lastPlacedLaserRow][i] == 'L') {
                    return false;
                }
                break;
            }
        }

        // south
        for (int i = lastPlacedLaserRow + 1; i < ROWS; i++) {
            if (safe[i][lastPlacedLaserCol] != '.' && safe[i][lastPlacedLaserCol] != '*') {
                if (safe[i][lastPlacedLaserCol] == 'L') {
                    return false;
                }
                break;
            }
        }

        // west
        for (int i = lastPlacedLaserCol - 1; i >= 0; i--) {
            if (safe[lastPlacedLaserRow][i] != '.' && safe[lastPlacedLaserRow][i] != '*') {
                if (safe[lastPlacedLaserRow][i] == 'L') {
                    return false;
                }
                break;
            }
        }

//        check north for pillar
        if (checkCoordinates(lastPlacedLaserRow - 1, lastPlacedLaserCol) &&
                (safe[lastPlacedLaserRow - 1][lastPlacedLaserCol] != 'L' &&
                        safe[lastPlacedLaserRow - 1][lastPlacedLaserCol] != '.' &&
                        safe[lastPlacedLaserRow - 1][lastPlacedLaserCol] != '*' )) {
            Pillar currentPillar;
            if (safe[lastPlacedLaserRow - 1][lastPlacedLaserCol] == 'X') {
                currentPillar = new Pillar(-1);
            } else {
                currentPillar = new Pillar(Character.getNumericValue(safe[lastPlacedLaserRow - 1][lastPlacedLaserCol]));
            }
            if (!checkPillar(currentPillar, lastPlacedLaserRow - 1, lastPlacedLaserCol)) {
                return false;
            }
        }

        //check east for pillar
        if (checkCoordinates(lastPlacedLaserRow, lastPlacedLaserCol + 1) &&
                (safe[lastPlacedLaserRow][lastPlacedLaserCol + 1] != 'L' &&
                        safe[lastPlacedLaserRow][lastPlacedLaserCol + 1] != '.' &&
                        safe[lastPlacedLaserRow][lastPlacedLaserCol + 1] != '*' )) {
            Pillar currentPillar;
            if (safe[lastPlacedLaserRow][lastPlacedLaserCol + 1] == 'X') {
                currentPillar = new Pillar(-1);
            } else {
                currentPillar = new Pillar(Character.getNumericValue(safe[lastPlacedLaserRow][lastPlacedLaserCol + 1]));
            }
            if (!checkPillar(currentPillar, lastPlacedLaserRow, lastPlacedLaserCol + 1)) {
                return false;
            }
        }

        //check south for pillar
        if (checkCoordinates(lastPlacedLaserRow + 1, lastPlacedLaserCol) &&
                (safe[lastPlacedLaserRow + 1][lastPlacedLaserCol] != 'L' &&
                        safe[lastPlacedLaserRow + 1][lastPlacedLaserCol] != '.' &&
                        safe[lastPlacedLaserRow + 1][lastPlacedLaserCol] != '*' )) {
            Pillar currentPillar;
            if (safe[lastPlacedLaserRow + 1][lastPlacedLaserCol] == 'X') {
                currentPillar = new Pillar(-1);
            } else {
                currentPillar = new Pillar(Character.getNumericValue(safe[lastPlacedLaserRow + 1][lastPlacedLaserCol]));
            }
            if (!checkPillar(currentPillar, lastPlacedLaserRow + 1, lastPlacedLaserCol)) {
                return false;
            }
        }

        //check west for pillar
        if (checkCoordinates(lastPlacedLaserRow, lastPlacedLaserCol - 1) &&
                (safe[lastPlacedLaserRow][lastPlacedLaserCol - 1] != 'L' &&
                        safe[lastPlacedLaserRow][lastPlacedLaserCol - 1] != '.' &&
                        safe[lastPlacedLaserRow][lastPlacedLaserCol - 1] != '*' )) {
            Pillar currentPillar;
            if (safe[lastPlacedLaserRow][lastPlacedLaserCol - 1] == 'X') {
                currentPillar = new Pillar(-1);
            } else {
                currentPillar = new Pillar(Character.getNumericValue(safe[lastPlacedLaserRow][lastPlacedLaserCol - 1]));
            }
            return checkPillar(currentPillar, lastPlacedLaserRow, lastPlacedLaserCol - 1);
        }
        return true;
    }

    /**
     * Returns true if the current configuration is the goal
     * (a valid safe configuration).
     * @return (boolean) true if the configuration is the goal
     */
    @Override
    public boolean isGoal() {
        return CharArrayValidation.charArrayVerify(safe);
    }

    /**
     * Sets the lastPlacedLaserRow at a specific (int) row.
     * @param lastPlacedLaserRow (int) row to place laser
     */
    public void setLastPlacedLaserRow(int lastPlacedLaserRow) {
        this.lastPlacedLaserRow = lastPlacedLaserRow;
    }

    /**
     * Sets the lastPlacedLaserCol at a specific (int) col.
     * @param lastPlacedLaserCol (int) column to place
     */
    public void setLastPlacedLaserCol(int lastPlacedLaserCol) {
        this.lastPlacedLaserCol = lastPlacedLaserCol;
    }

    /**
     * Overrides toString to return the current safeConfig
     * @return (String) current safeConfig display
     */
    @Override
    public String toString() {
        String toReturn = "";
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                toReturn += safe[i][j] + " ";
            }
            toReturn += "\n";
        }
        return toReturn;
    }

    /**
     * Checks to see if a pillar has not been overloaded.
     * @param currentPillar The pillar to check
     * @param pillarRow The pillar's row
     * @param pillarCol The pillar's column
     * @return true if the pillar has not been overloaded, false if it has
     */
    public boolean checkPillar(Pillar currentPillar, int pillarRow, int pillarCol) {
        int surroundingLasers = 0;
        if (currentPillar.getLasers() == 0) {
            return false;
        }
        if (currentPillar.getLasers() != -1) {
            if (checkCoordinates(pillarRow - 1, pillarCol) && safe[pillarRow - 1][pillarCol] == 'L') {
                surroundingLasers++;
            }
            if (checkCoordinates(pillarRow, pillarCol + 1) && safe[pillarRow][pillarCol + 1] == 'L') {
                surroundingLasers++;
            }
            if (checkCoordinates(pillarRow + 1, pillarCol) && safe[pillarRow + 1][pillarCol] == 'L') {
                surroundingLasers++;
            }
            if (checkCoordinates(pillarRow, pillarCol - 1) && safe[pillarRow][pillarCol - 1] == 'L') {
                surroundingLasers++;
            }
            return surroundingLasers <= currentPillar.getLasers();
        } else {
            return true;
        }
    }

    /**
     * Checks to see if the coordinates are in an acceptable range for the
     * board's dimensions (rows, cols)
     * @param row (int) row of the board
     * @param col (int) column of the board
     * @return (boolean) true if valid coordinates
     */
    public boolean checkCoordinates(int row, int col) {
        return row < ROWS && row >= 0 && col < COLS && col >= 0;
    }

    /**
     * Converts the original safe (SafeNode[][]) into a 2D char array
     * @param originalSafe (SafeNode[][]) safe
     * @return (char[][]) 2D char array
     */
    public char[][] convert(SafeNode[][] originalSafe) {
        char[][] toBuild = new char[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                toBuild[i][j] = originalSafe[i][j].toString().charAt(0);
            }
        }
        return toBuild;
    }

    /**
     * Adds a laser at the specified row, column
     * @param row (int) row of the board
     * @param col (int) column of the board
     */
    public void add(int row, int col) {
        if (safe[row][col] != '.' && safe[row][col] != '*') {

        } else {
            safe[row][col] = 'L';

            // north
            for (int i = row - 1; i >= 0; i--) {
                if (safe[i][col] != '.' && safe[i][col] != '*') {
                    break;
                } else {
                    safe[i][col] = '*';
                }
            }

            // east
            for (int i = col + 1; i < COLS; i++) {
                if (safe[row][i] != '.' && safe[row][i] != '*') {
                    break;
                } else {
                    safe[row][i] = '*';
                }
            }

            // south
            for (int i = row + 1; i < ROWS; i++) {
                if (safe[i][col] != '.' && safe[i][col] != '*') {
                    break;
                } else {
                    safe[i][col] = '*';
                }
            }

            // west
            for (int i = col - 1; i >= 0; i--) {
                if (safe[row][i] != '.' && safe[row][i] != '*') {
                    break;
                } else {
                    safe[row][i] = '*';
                }
            }
        }
    }

    /**
     * Returns the predecessor of a configuration.
     * @return (SafeConfig) predecessor of the current configuration
     */
    @Override
    public SafeConfig getPredecessor() {
        return this.predecessor;
    }

    /**
     * Get the safe.
     * @return the safe in 2D char array form
     */
    public char[][] getSafe() {
        return safe;
    }

    public void setSafe(char[][] chars) {
        this.safe = Backtracker.copy2DCharArray(chars);
    }
}
