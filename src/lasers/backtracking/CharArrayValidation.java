package lasers.backtracking;

/**
 * A class that validates a 2D character array safe
 * @author Jamieson Dube (jmd2851)
 * @author Savannah Alfaro (sea2985)
 */
public class CharArrayValidation {

    /**
     * Returns true if the safe is valid.
     * @param safe (char[][]) 2D character array safe
     * @return (boolean) true if a valid safe
     */
    public static boolean charArrayVerify(char[][] safe) {

        for (int i = 0; i < safe.length; i++) {
            for (int j = 0; j < safe[i].length; j++) {
                char node = safe[i][j];
                boolean canCheckNorth = i > 0;
                boolean canCheckEast = j < safe[i].length - 1;
                boolean canCheckSouth = i < safe.length - 1;
                boolean canCheckWest = j > 0;
                switch (node) {
                    case '.':
                        return false;
                    case '*':
                    case 'X':
                        break;
                    case 'L':
                        // North
                        if (canCheckNorth) {
                            for (int laserRow = i - 1; laserRow >= 0; laserRow--) {
                                if (safe[laserRow][j] == 'L') {
                                    return false;
                                } else if (safe[laserRow][j] != '.' && safe[laserRow][j] != '*') {
                                    break;
                                }
                            }
                        }

                        // East
                        if (canCheckEast) {
                            for (int laserCol = j + 1; laserCol < safe[i].length; laserCol++) {
                                if (safe[i][laserCol] == 'L') {
                                    return false;
                                } else if (safe[i][laserCol] != '.' && safe[i][laserCol] != '*') {
                                    break;
                                }
                            }
                        }

                        // South
                        if (canCheckSouth) {
                            for (int laserRow = i + 1; laserRow < safe.length; laserRow++) {
                                if (safe[laserRow][j] == 'L') {
                                    return false;
                                } else if (safe[laserRow][j] != '.' && safe[laserRow][j] != '*') {
                                    break;
                                }
                            }
                        }

                        // West
                        if (canCheckWest) {
                            for (int laserCol = j - 1; laserCol >= 0; laserCol--) {
                                if (safe[i][laserCol] == 'L') {
                                    return false;
                                } else if (safe[i][laserCol] != '.' && safe[i][laserCol] != '*') {
                                    break;
                                }
                            }
                        }
                        break;

                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                        int surroundingLasers = 0;
                        if (canCheckNorth && safe[i - 1][j] == 'L') {
                            surroundingLasers++;
                        }
                        if (canCheckEast && safe[i][j + 1] == 'L') {
                            surroundingLasers++;
                        }
                        if (canCheckSouth && safe[i + 1][j] == 'L') {
                            surroundingLasers++;
                        }
                        if (canCheckWest && safe[i][j - 1] == 'L') {
                            surroundingLasers++;
                        }
                        if (surroundingLasers != Character.getNumericValue(safe[i][j])) {
                            return false;
                        }
                }
            }
        }
        return true;
    }
}
