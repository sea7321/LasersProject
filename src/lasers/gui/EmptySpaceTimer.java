package lasers.gui;

// Import & Set Up Workspace
import javafx.application.Platform;
import javafx.scene.image.ImageView;

/**
 * Sets the button graphic back to white after
 * the thread timer runs out.
 * @author Jamieson Dube (2851)
 * @author Savannah Alfaro (2985)
 */
public class EmptySpaceTimer implements Runnable{

    // Initialize Variables
    private final int seconds;
    private final SafeButton button;

    // Constructor
    /**
     * Creates an EmptySpaceTimer to reset the button graphic.
     * @param button (Button) the button
     * @param seconds (int) number of seconds to reset
     */
    public EmptySpaceTimer(SafeButton button, int seconds) {
        this.seconds = seconds;
        this.button = button;
    }


    // Methods
    /**
     * Overrides the run method to sleep the timer for a second.
     * Then sets the new image view of the button.
     */
    @Override
    public void run() {
        try {
            // set to red
            ImageView redView = new ImageView(button.getRedImage());
            redView.setFitHeight(50);
            redView.setFitWidth(50);
            Platform.runLater(() -> button.setGraphic(redView));

            // sleep thread
            Thread.sleep(seconds * 1000);

            // set to white
            ImageView imageView = new ImageView(button.getBlackImage());
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            Platform.runLater(() -> button.setGraphic(imageView));
        } catch (InterruptedException ie) {
            // squash
        }
    }
}
