package lasers.gui;

// Import & Set Up Workspace
import javafx.scene.layout.*;
import lasers.model.LasersModel;

/**
 * Initializes a grid will all the button's values from LasersModel
 *
 * @author Savannah Alfaro (sea2985), Jamieson Dube(jmd2851)
 */
public class MakeGridPane {

    // Initialize Variables
    private final SafeButton[][] safeButtons;

    // Constructor
    /**
     * Initializes the grid pane for the GUI
     * @param model (LasersModel) model in MVC
     */
    public MakeGridPane(LasersModel model) {
        this.safeButtons = new SafeButton[model.getRows()][model.getCols()];
    }


    // Methods
    /**
     * Creates the grid pane for the GUI.
     * Creates all buttons and if pressed, adds/removes a laser.
     * @param model (LasersModel) model in MVC
     * @return (GridPane) the grid pane for the GUI
     */
    public GridPane makeGridPane(LasersModel model, ControllerGUI controller) {
        GridPane gp = new GridPane();
        for (int row = 0; row < model.getRows(); row++) {
            for (int col = 0; col < model.getCols(); col++) {
                // get the next type of safe value and create a button for it
                SafeButton button = new SafeButton(model.getValue(row, col), row, col);
                button.setOnAction(e -> {
                    controller.addRemoveLaser(button.getRow(), button.getCol());
                    model.display();
                });

                // add button to grid pane
                gp.add(button, col, row);

                // add the button to list of buttons
                safeButtons[row][col] = button;
            }
        }
        return gp;
    }

    /**
     * Returns the 2D array of SafeButtons
     * @return (SafeButton[][]) 2D array of SafeButtons
     */
    public SafeButton[][] getSafeButtons() {
        return safeButtons;
    }
}
