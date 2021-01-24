package lasers.gui;

import javafx.application.Platform;
import lasers.model.components.Laser;

/**
 * Class that implements Runnable to have a thread sleep for a
 * a certain amount of time to then change the background.
 *
 * @author Savannah Alfaro (sea2985), Jamieson Dube (jmd2851)
 */
public class ColorTimer implements Runnable {

    // Initialize Variables
    private final SafeButton safeButton;
    private final int seconds;

    // Constructor
    /**
     * Initializes a ColorTimer to wait an amount of time (in seconds) to
     * change the background of a tile.
     * @param safeButton (SafeButton) button in which the background is changed
     * @param seconds (int) number of seconds to sleep
     */
    ColorTimer(SafeButton safeButton, int seconds){
        this.safeButton = safeButton;
        this.seconds = seconds;
    }


    // Methods
    /**
     * Overrides the run method to reset the background after a second.
     */
    @Override
    public void run() {
        if (safeButton.getValue().equals(".")) {
            try {
                Platform.runLater(() -> safeButton.updateImage("R"));
                Thread.sleep(seconds * 1000);
                Platform.runLater(() -> safeButton.updateImage("L"));
            } catch (InterruptedException ie) {
                //squash
            }
        } else {
            try {
                safeButton.setButtonBackground("red.png");
                Thread.sleep(seconds * 1000);
                safeButton.setButtonBackground("black.png");
            } catch (InterruptedException ie) {
                //squash
            }
        }
    }
}
