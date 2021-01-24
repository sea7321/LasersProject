package lasers.backtracking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class used to build a Path on a separate Thread.
 * @author Jamieson Dube (jmd2851)
 * @author Savannah Alfaro (sea2985)
 */
public class PathBuilder implements Runnable {

    private final String filename;
    /**
     * A container for a path. The key is true if the path
     * has finished building and false if it is still building.
     */
    private final Map<Boolean, List<Configuration>> pathData;

    /**
     * Creates a new PathBuilderInstance.
     * pathData's key is initialized to false to
     * indicate that a path has not yet been built.
     * @param filename the file to generate the path from
     */
    public PathBuilder(String filename) {
        this.filename = filename;
        pathData = new HashMap<>();
        pathData.put(false, null);
    }

    /**
     * Creates a new BackTracker and solves it
     * on a separate thread.
     * Replaces the false key with a true key
     * to indicate that a path has been built (even if
     * there is no solution).
     */
    @Override
    public void run() {
        Backtracker backtracker = new Backtracker(false);
        pathData.remove(false);
        pathData.put(true, backtracker.solveWithPath(new SafeConfig(filename)));
    }

    /**
     * Get the PathData contained in a map.
     * @return the path data
     */
    public Map<Boolean, List<Configuration>> getPathData() {
        return pathData;
    }
}
