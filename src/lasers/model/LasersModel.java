package lasers.model;

// Import & Set Up Workspace
import javafx.application.Platform;
import lasers.Lasers;
import lasers.backtracking.Backtracker;
import lasers.backtracking.Configuration;
import lasers.backtracking.PathBuilder;
import lasers.backtracking.SafeConfig;
import lasers.model.components.*;
import java.io.*;
import java.util.*;

/**
 * The model of the lasers safe.  You are free to change this class however
 * you wish, but it should still follow the MVC architecture.
 *
 * @author RIT CS
 * @author Savannah Alfaro (sea2985), Jamieson Dube (jmd2851)
 */
public class LasersModel {

    // Initialize Variables
    private SafeNode[][] safe;
    private final SafeNode[][] initialSafe;
    private int rows;
    private int cols;
    public final Period PERIOD = new Period(".");
    private final Asterisk ASTERISK = new Asterisk("*");
    private final Laser LASER = new Laser();
    private List<Configuration> path;
    private int hintCounter;
    private final PathBuilder pathBuilder;
    private boolean pathBuilt;

    /** the observers who are registered with this model */
    private final List<Observer<LasersModel, ModelData>> observers;

    // Constructor
    /**
     * Constructs a new safe (1 parameter).
     * @param filename The safe text file to parse.
     */
    public LasersModel(String filename) {
        this.observers = new LinkedList<>();
        this.safe = safeFile(filename);
        this.initialSafe = safeFile(filename);
        pathBuilder = new PathBuilder(filename);
        pathBuilt = false;
        Thread thread = new Thread(pathBuilder);
        thread.setDaemon(true);
        thread.start();
        updatePathAndData();
        hintCounter = 1;
    }

    /**
     * Constructs a new Model without a backtracker.
     * @param filename The safe text file to parse.
     * @param createdByBacktracker should neither be true nor false unless
     *                             this model was created by a backtracker.
     */
    public LasersModel(String filename, boolean createdByBacktracker) {
        this.observers = new LinkedList<>();
        this.safe = safeFile(filename);
        this.initialSafe = safeFile(filename);
        pathBuilder = null;
        path = null;
        hintCounter = 1;
    }


    // Methods
    /**
     * Resets the safe.
     */
    public void resetSafe(){
        hintCounter = 1;
        setSafe(initialSafe, "Restarted!");
    }

    /**
     * Try to set the board to the solution Configuration.
     */
    public void showSolution() {
        updatePathAndData();
        if (pathBuilt) {
            if (path == null) {
                setSafe(getSafe(), "No solution.");
            } else {
                solve();
            }
        } else {
            setSafe(getSafe(), "Working on it...");
        }
    }

    /**
     * Checks to see if a path has been built by the PathBuilder.
     * If it has, the path state is updated.
     */
    private void updatePathAndData() {
        if (pathBuilder.getPathData().containsKey(true)) {
            pathBuilt = true;
            path = pathBuilder.getPathData().get(true);
        }
    }

    /**
     * Solve the current board.
     * Only to be called once a path has been built.
     */
    private void solve() {
        SafeConfig solutionConfig = (SafeConfig) path.get(path.size() - 1);
        setSafe(charArraytoSafeNodeArray(solutionConfig.getSafe()), "Solved!");
        hintCounter = path.size();
    }

    /**
     * Try to get the next hint, if it exists.
     */
    public void showNextHint() {
        updatePathAndData();
        if (!pathBuilt) {
            setSafe(getSafe(), "Working on it...");
        } else if (path == null || hintCounter >= path.size() - 1) {
            setSafe(getSafe(), "No hints to give.");
        } else {
            SafeConfig nextHintConfig = (SafeConfig) path.get(++hintCounter);
            setSafe(charArraytoSafeNodeArray(nextHintConfig.getSafe()), "Hope this helps!");
        }
    }

    /**
     * Convert a 2D charArray to a more usable
     * 2D SafeNode array.
     * @param charArray the array to convert
     * @return The SafeNode[][]
     */
    public SafeNode[][] charArraytoSafeNodeArray(char[][] charArray) {
        SafeNode[][] nodeArray = new SafeNode[charArray.length][charArray[0].length];
        for (int i = 0; i < charArray.length; i++) {
            for (int j = 0; j < charArray[i].length; j++) {
                switch (charArray[i][j]) {
                    case '.':
                        nodeArray[i][j] = new Period(".");
                        break;
                    case '*':
                        nodeArray[i][j] = new Asterisk("*");
                        break;
                    case 'L':
                        nodeArray[i][j] = new Laser();
                        break;
                    case 'X':
                        nodeArray[i][j] = new Pillar(-1);
                        break;
                    default:
                        nodeArray[i][j] = new Pillar(Character.getNumericValue(charArray[i][j]));
                        break;
                }
            }
        }
        return nodeArray;
    }

    /**
     * Sets the safe using a copy.
     * @param safe (SafeNode[][]) the safe to copy
     */
    public void setSafe(SafeNode[][] safe, String message) {
        this.safe = copy2DArray(safe);
        notifyObservers(new ModelData(message, safe));
    }

    public SafeNode[][] getInitialSafe() {
        return initialSafe;
    }

    /**
     * Getter for safe.
     * @return (SafeNode[][]) safe
     */
    public SafeNode[][] getSafe(){
        return safe;
    }

    /**
     * Tries to add a laser to the board at a point (r,c).
     * Increments each "*" (asteriskCount) if already "*".
     * If instead a ".", creates a new Asterisk.
     * If given valid coordinates, check to see if it is already a laser.
     * If it is already a laser, print an error.
     * Otherwise, throw an error due to invalid coordinates.
     * @param row (int) row of the board
     * @param col (int) column of the board
     * @param suppressOutput whether or not to print the status of the addition
     */
    public void add(int row, int col, boolean suppressOutput){

        // if valid coordinates check if safe[row][col] equals "*" (is not already a laser)
        if (checkCoordinates(row, col)) {
            if (safe[row][col].equals(PERIOD) || safe[row][col].equals(ASTERISK)) {

                safe[row][col] = new Laser();

                // iterate over positive rows & columns (assign i & j)
                int i = row;
                int j = col;

                // iterate over rows (positive)
                while( i < rows  && i + 1 < rows && continueLaser(i + 1, col)) {
                    if (safe[i + 1][col].equals(PERIOD)) {
                        Asterisk asterisk = new Asterisk("*");
                        safe[i + 1][col] = asterisk;
                    }
                    i++;
                }

                // iterate over columns (positive)
                while(j < cols && j + 1 < cols && continueLaser(row, j + 1)) {
                    if (safe[row][j + 1].equals(PERIOD)) {
                        Asterisk asterisk = new Asterisk("*");
                        safe[row][j + 1] = asterisk;
                    }
                    j++;
                }

                // iterate over negative rows & columns (assign i & j)
                i = row;
                j = col;

                // iterate over rows (negative)
                while(i >= 0 && i - 1 >= 0 && continueLaser(i - 1, col)) {
                    if (safe[i - 1][col].equals(PERIOD)) {
                        Asterisk asterisk = new Asterisk("*");
                        safe[i - 1][col] = asterisk;
                    }
                    i--;
                }

                // iterate over columns (negative)
                while(j >= 0 && j - 1 >= 0 && continueLaser(row, j - 1)) {
                    if (safe[row][j - 1].equals(PERIOD)) {
                        Asterisk asterisk = new Asterisk("*");
                        safe[row][j - 1] = asterisk;
                    }
                    j--;
                }

                if (!suppressOutput){
                    notifyObservers(new ModelData("Laser added at: (" + row + ", " + col + ")", getSafe()));
                } else {
                    notifyObservers(new ModelData("", getSafe()));
                }
            } else {
                if (!suppressOutput){
                    notifyObservers(new ModelData("Error adding laser at: (" + row + ", " + col + ")", getSafe(), row, col));
                }
            }
        } else {
            if (!suppressOutput) {
                System.out.println("Error adding laser at: (" + row + ", " + col + ")");
                notifyObservers(new ModelData("Error adding laser at: (" + row + ", " + col + ")", getSafe(), row, col));
            }
        }
    }

    /**
     * Displays the board.
     */
    public void display() {

        // print the main board (upper half)
        System.out.print(" ");
        for (int i = 0; i < cols; i++) {
            System.out.print(" " + i);
        }
        System.out.println();

        // print horizontal line
        int counter = cols;
        System.out.print("  ");
        while(counter > 0) {
            System.out.print("--");
            counter--;
        }
        System.out.println();

        int i = 0;
        for (SafeNode[] sArray : safe) {
            System.out.print(i+++ "|");
            for (SafeNode s : sArray) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    /**
     * Verifies the validity of the safe.
     */
    public void verify(){

        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols; col++) {

                // check each coordinate if it is a period and throw an error
                if (safe[row][col].equals(PERIOD)){
                    notifyObservers(new ModelData("Error verifying at: (" + row + ", " + col + ")", getSafe(), row, col));
                    return;
                } else if (safe[row][col] instanceof Laser) {
                    int[] lv;
                    lv = laserVerification(row, col);
                    if (lv[0] != -1 && lv[1] != -1) {
                        notifyObservers(new ModelData("Error verifying at: (" + row + ", " + col + ")", getSafe(), row, col));
                        return;
                    }

                } else if (safe[row][col] instanceof Pillar) {

                    Pillar currentPillar = (Pillar) safe[row][col];
                    if (currentPillar.getLasers() == -1) {
                        continue;
                    }

                    int lasersSurrounding = 0;
                    if (row - 1 >= 0 && (safe[row - 1][col] instanceof Laser)) {
                        lasersSurrounding++;
                    }

                    if (row + 1 < rows && safe[row + 1][col] instanceof Laser) {
                        lasersSurrounding++;
                    }

                    if (col - 1 >= 0 && safe[row][col - 1] instanceof Laser) {
                        lasersSurrounding++;
                    }

                    if (col + 1 < cols && safe[row][col + 1] instanceof Laser) {
                        lasersSurrounding++;
                    }

                    if (lasersSurrounding != currentPillar.getLasers()){
                        notifyObservers(new ModelData("Error verifying at: (" + row + ", " + col + ")", getSafe(), row, col));
                        return;
                    }
                }
            }
        }
        notifyObservers(new ModelData("Safe is fully verified!", getSafe()));
    }

    /**
     * Reads the safe text file and creates the safe board.
     * @param file (File) safe file to initialize the board
     * @return (String[][]) the 2D safe board
     */
    public SafeNode[][] safeFile(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            // read first line and split (dimensions of the board)
            String line = reader.readLine();
            String[] lineSplit = line.split(" ");
            this.rows = Integer.parseInt(lineSplit[0]);
            this.cols = Integer.parseInt(lineSplit[1]);

            // build the safe based off of rows and cols
            SafeNode[][] safeBuilder = new SafeNode[rows][cols];

            for (int i = 1; i < rows + 1; i++) {
                line = reader.readLine();
                lineSplit = line.split(" ");

                for (int j = 0; j < lineSplit.length; j++) {
                    switch (lineSplit[j]) {
                        case ".":
                            safeBuilder[i - 1][j] = PERIOD;
                            break;
                        case "X":
                            safeBuilder[i - 1][j] = new Pillar(-1);
                            break;
                        case "0":
                            safeBuilder[i - 1][j] = new Pillar(0);
                            break;
                        case "1":
                            safeBuilder[i - 1][j] = new Pillar(1);
                            break;
                        case "2":
                            safeBuilder[i - 1][j] = new Pillar(2);
                            break;
                        case "3":
                            safeBuilder[i - 1][j] = new Pillar(3);
                            break;
                        case "4":
                            safeBuilder[i - 1][j] = new Pillar(4);
                            break;
                    }
                }
            }
            reader.close();
            return safeBuilder;
        } catch (FileNotFoundException e) {
            System.err.println("File cannot be found");
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses an Input File and runs the desired commands.
     * @param inputFile the file to parse
     */
    public void inputFile(File inputFile){
        try {
            Scanner reader = new Scanner(inputFile);
            while (reader.hasNextLine()) {
                String[] lineSplit = reader.nextLine().split(" ");
                if (lineSplit[0].equals("a")){
                    this.add(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2]), false);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File cannot be found");
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
        if (row < getRows() && row >= 0 && col < getCols() && col >=0) {
            return true;
        } else {
            System.out.println("Invalid coordinates (" + row + ", " + col + ")");
            return false;
        }
    }

    /**
     * Checks to make sure an asterisk should be placed
     * in the current location on the safe.
     *
     * @return true if the * should be added or false if it should not.
     */
    public boolean continueLaser(int row, int col){
        return safe[row][col].equals(PERIOD) || safe[row][col].equals(ASTERISK);
    }

    /**
     * Verifies the lasers on the board by checking positive and negative directions
     * of the specified laser (r, c).
     * @param row (int) row of the board
     * @param col (int) column of the board
     * @return (int[]) row and column where the error occurred -- [-1, -1] if no error
     */
    public int[] laserVerification(int row, int col) {
        boolean hitPillar = false;

        // From laser, to far left (or pillar) (negative)
        for (int g = col - 1; g >= 0; g--) {
            if (safe[row][g] instanceof Laser && g != col && !hitPillar) {
                return new int[]{row, col};
            } else if (safe[row][g] instanceof Pillar) {
                hitPillar = true;
            }
        }

        hitPillar = false;
        // From laser, to far right (or pillar) (positive)
        for (int g = col + 1; g < cols; g++) {
            if (safe[row][g] instanceof Laser && g != col &&!hitPillar) {
                return new int[]{row, col};
            } else if (safe[row][g] instanceof Pillar) {
                hitPillar = true;
            }
        }

        hitPillar = false;
        // From laser, to top (or pillar) (negative)
        for (int g = row - 1; g >= 0; g--) {
            if (safe[g][col] instanceof Laser && g != row && !hitPillar) {
                return new int[]{row, col};
            } else if (safe[g][col] instanceof Pillar) {
                hitPillar = true;
            }
        }

        hitPillar = false;
        // From laser, to bottom (positive)
        for (int g = row + 1; g < rows; g++) {
            if (safe[g][col] instanceof Laser && g != row && !hitPillar) {
                return new int[]{row, col};
            } else if (safe[g][col] instanceof Pillar) {
                hitPillar = true;
            }
        }
        //SUCCESS
        return new int[]{-1,-1};
    }

    /**
     * Tries to remove a laser at a point (r,c).
     * If given valid coordinates, check to see if it is already a laser.
     * If it is not a laser, print an error.
     * Otherwise, throw an error due to invalid coordinates.
     * @param row (int) row of the board
     * @param col (int) column of the board
     */
    public void remove(int row, int col) {

        if (checkCoordinates(row, col)) {
            if (safe[row][col].equals(LASER)) {
                safe[row][col] = new Period(".");

                int i = 0;
                int j;
                for (SafeNode[] sArray : safe) {
                    j = 0;
                    for (SafeNode s : sArray) {
                        if (s instanceof Asterisk) {
                            safe[i][j] = new Period(".");
                        }
                        j++;
                    }
                    i++;
                }

                i = 0;
                for (SafeNode[] sArray : safe) {
                    j = 0;
                    for (SafeNode s : sArray) {
                        if (s.equals(LASER)) {
                            safe[i][j] = PERIOD;
                            add(i, j, true);
                        }
                        j++;
                    }
                    i++;
                }
                display();
                notifyObservers(new ModelData("Laser removed at: (" + row + ", " + col + ")", getSafe()));

            } else {
                notifyObservers(new ModelData("Error removing laser at: (" + row + ", " + col + ")", getSafe(), row, col));
            }
        } else {
            notifyObservers(new ModelData("Error removing laser at: (" + row + ", " + col + ")", getSafe(), row, col));
        }
    }

    /**
     * Add a new observer.
     * @param observer the new observer
     */
    public void addObserver(Observer<LasersModel, ModelData > observer) {
        this.observers.add(observer);
    }

    /**
     * Notify observers the model has changed.
     * @param data optional data the model can send to the view
     */
    private void notifyObservers(ModelData data){
        for (Observer<LasersModel, ModelData> observer: observers) {
            observer.update(this, data);
        }
    }

    /**
     * Efficiently makes and returns a copy of the given array.
     * @param array The array to copy.
     * @return The copy of the original array.
     */
    public static SafeNode[][] copy2DArray(SafeNode[][] array) {
        SafeNode[][] copy = new SafeNode[array.length][];
        for(int i=0; i<array.length; i++) {
            copy[i] = Arrays.copyOf(array[i], array[i].length);
        }
        return copy;
    }

    /**
     * Getter for rows.
     * @return (int) rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Getter for cols.
     * @return (int) cols
     */
    public int getCols() {
        return cols;
    }

    /**
     * Getter for the value at a specific row, column on the safe.
     * @param row (int) row of the safe
     * @param col (int) column of the safe
     * @return (String) value of the tile at a certain row, column on the safe
     */
    public String getValue(int row, int col) {
        return safe[row][col].toString();
    }

    /**
     * Returns true if there is a laser at a specified row, column
     * @param row (int) row of the safe
     * @param col (int) column of the safe
     * @return (boolean) true if there is already a laser
     */
    public boolean isLaser(int row, int col) {
        return safe[row][col].equals(LASER);
    }
}
