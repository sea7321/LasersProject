package lasers.backtracking;

// Import & Set Up Workspace
import java.util.*;

/**
 * This class represents the classic recursive backtracking algorithm.
 * It has a solver that can take a valid configuration and return a
 * solution, if one exists.
 *
 * This file comes from the backtracking lecture. It should be useful
 * in this project. A second method has been added that you should
 * implement.
 *
 * @author RIT CS
 * @author Jamieson Dube (jmd2851)
 * @author Savannah Alfaro (sea2985)
 */
public class Backtracker {

    // Initialize Variables
    private final boolean debug;
    private final List<Configuration> path = new ArrayList<>();

    // Constructor
    /**
     * Initialize a new backtracker.
     *
     * @param debug Is debugging output enabled?
     */
    public Backtracker(boolean debug) {
        this.debug = debug;
        if (this.debug) {
            System.out.println("Backtracker debugging enabled...");
        }
    }


    // Methods
    /**
     * A utility routine for printing out various debug messages.
     *
     * @param msg    The type of config being looked at (current, goal,
     *               successor, e.g.)
     * @param config The config to display
     */
    private void debugPrint(String msg, Configuration config) {
        if (this.debug) {
            System.out.println(msg + ":\n" + config);
        }
    }

    /**
     * Try find a solution, if one exists, for a given configuration.
     *
     * @param config A valid configuration
     * @return A solution config, or Optional.empty() if no solution
     */
    public Optional<Configuration> solve(Configuration config) {
        debugPrint("Current config", config);
        if (config.isGoal()) {
            debugPrint("\tGoal config", config);
            return Optional.of(config);
        } else {
            for (Configuration child : config.getSuccessors()) {
                if (child.isValid()) {
                    debugPrint("\tValid successor", child);
                    Optional<Configuration> sol = solve(child);
                    if (sol.isPresent()) {
                        return sol;
                    }
                } else {
                    debugPrint("\tInvalid successor", child);
                }

            }
            // implicit backtracking happens here
        }
        return Optional.empty();
    }

    /**
     * Find a goal configuration if it exists, and how to get there.
     *
     * @param current the starting configuration
     * @return a list of configurations to get to a goal configuration.
     * If there are none, return null.
     */
    public List<Configuration> solveWithPath(Configuration current) {

        // find valid successors and find solution configuration
        for (Configuration child : current.getSuccessors()) {
            if (child.isValid()) {
                Optional<Configuration> sol = solve(child);
                if (sol.isPresent()) {
                    SafeConfig sc = (SafeConfig) sol.get();
                    path.add(sc);
                    findPath(sc);
                    
                    // find the original path
                    List<Configuration> newPath = new ArrayList<>();
                    for (int i = path.size() - 1; i >=0; i--) {
                        newPath.add(path.get(i));
                    }

                    return newPath;
                }
            }
        }
        return null; // if no path is found
    }        

    /**
     * Efficiently makes and returns a copy of the given array.
     * @param array The array to copy.
     * @return The copy of the original array.
     */
    public static char[][] copy2DCharArray(char[][] array) {
        char[][] copy = new char[array.length][];
        for(int i=0; i<array.length; i++) {
            copy[i] = Arrays.copyOf(array[i], array[i].length);
        }
        return copy;
    }

    /**
     * Find the path from the current solution up to null (predecessor)
     * @param solution (Configuration) solution of the safe
     */
    public void findPath(Configuration solution) {
        if (solution != null) {
            path.add(solution.getPredecessor());
            findPath(solution.getPredecessor());
        }
    }
}
