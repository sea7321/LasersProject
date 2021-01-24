package lasers.model;

// Import & Set Up Workspace
import lasers.model.components.SafeNode;

/**
 * Use this class to customize the data you wish to send from the model
 * to the view when the model changes state.
 *
 * @author RIT CS
 * @author Savannah Alfaro (sea2985), Jamieson Dube (jmd2851)
 */
public class ModelData {

    // Initialize Variables
    private final String message;
    private final SafeNode[][] safe;
    private final int errorRow;
    private final int errorCol;


    // Constructors
    /**
     * Constructs the model data to be sent to the GUI/PTUI
     * @param message (String) to be sent to the GUI/PTUI
     * @param safe (SafeNode[][]) 2D array of the safe
     */
    public ModelData(String message, SafeNode[][] safe){
        this.message = message;
        this.safe = safe;
        this.errorRow = -1;
        this.errorCol = -1;
    }

    /**
     * Constructs the model data to be sent to the GUI/PTUI
     * @param message (String) to be sent to the GUI/PTUI
     * @param safe (SafeNode[][]) 2D array of the safe
     * @param errorRow (int) the row in which there is an error
     * @param errorCol (int) the column in which there is an error
     */
    public ModelData(String message, SafeNode[][] safe, int errorRow, int errorCol){
        this.message = message;
        this.safe = safe;
        this.errorRow = errorRow;
        this.errorCol = errorCol;
    }


    // Methods
    /**
     * Getter for errorRow.
     * @return (int) errorRow
     */
    public int getErrorRow() {
        return errorRow;
    }

    /**
     * Getter for errorCol.
     * @return (int) errorCol
     */
    public int getErrorCol() {
        return errorCol;
    }

    /**
     * Getter for message.
     * @return (String) message
     */
    public String getMessage(){
        return message;
    }

    /**
     * Getter for safe.
     * @return (SafeNode[][]) safe
     */
    public SafeNode[][] safe() {
        return safe;
    }

}
