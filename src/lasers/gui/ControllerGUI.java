package lasers.gui;

// Import & Set Up Workspace
import javafx.scene.media.AudioClip;
import lasers.model.LasersModel;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Controller for LasersGUI in the MVC set up.
 * @author Savannah Alfaro (sea2985)
 * @author Jamieson Dube (jmd2851)
 */
public class ControllerGUI {

    // Initialize Variables
    private final LasersModel model;
    private AudioClip addAudioClip;
    private AudioClip removeAudioClip;

    // Constructor
    /**
     * Initializes the ControllerGUI to interact with LasersModel
     * and LasersGUI.
     * @param model (LasersModel) the model.
     */
    public ControllerGUI(LasersModel model) {
        this.model = model;
        init();
    }


    // Methods
    /**
     * Verifies the model's safe
     */
    public void verify() {
        model.verify();
    }

    /**
     * Adds or removes a laser depending if a laser is already there.
     * @param row (int) row of the tile
     * @param col (int) column of the tile
     */
    public void addRemoveLaser(int row, int col) {

        if (model.isLaser(row, col)) {
            model.remove(row, col);
            removeAudioClip.play();
        } else {
            model.add(row, col, false);
            addAudioClip.play();
        }
    }

    /**
     * Resets the model's safe.
     */
    public void resetSafe() {
        model.resetSafe();
    }

    /**
     * Initializes the interactive sounds of adding/removing lasers.
     */
    public void init() {
        URL addSound = null;
        URL removeSound = null;
        try {
            addSound = new URL(getClass().getResource("resources/addLaser.mp3").toExternalForm());
            removeSound = new URL(getClass().getResource("resources/removeLaser.mp3").toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.addAudioClip = new AudioClip(addSound.toString());
        addAudioClip.setVolume(0.9);
        this.removeAudioClip = new AudioClip(removeSound.toString());
        removeAudioClip.setVolume(0.9);
    }

    /**
     * Tell the model to update the board
     * to the solution.
     */
    public void solve() {
        model.showSolution();
    }

    /**
     * Tell the model to update the board
     * to the next hint/
     */
    public void getHint() {
        model.showNextHint();
    }
}
