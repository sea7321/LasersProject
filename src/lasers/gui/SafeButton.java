package lasers.gui;

// Import & Set Up Workspace
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lasers.model.components.Asterisk;
import lasers.model.components.Laser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Class that creates the GUI buttons based on the tile from LasersModel
 *
 * @author Savannah Alfaro (sea2985), Jamieson Dube (jmd2851)
 */
public class SafeButton extends Button {

    // Initialize Variables
    private final int row;
    private final int col;
    private String value;

    // Images
    private static Image beam;
    private static Image laser;
    private static Image pillar0;
    private static Image pillar1;
    private static Image pillar2;
    private static Image pillar3;
    private static Image pillar4;
    private static Image pillarX;
    private static Image black;
    private static Image red;

    // creates a new image for the corresponding tile
    static {
        try {
            beam = new Image(new FileInputStream("src/lasers/gui/resources/beam.jpg"));
            laser = new Image(new FileInputStream("src/lasers/gui/resources/laser.jpg"));
            pillar0 = new Image(new FileInputStream("src/lasers/gui/resources/pillar0.png"));
            pillar1 = new Image(new FileInputStream("src/lasers/gui/resources/pillar1.png"));
            pillar2 = new Image(new FileInputStream("src/lasers/gui/resources/pillar2.png"));
            pillar3 = new Image(new FileInputStream("src/lasers/gui/resources/pillar3.png"));
            pillar4 = new Image(new FileInputStream("src/lasers/gui/resources/pillar4.png"));
            pillarX = new Image(new FileInputStream("src/lasers/gui/resources/pillarX.png"));
            black = new Image(new FileInputStream("src/lasers/gui/resources/black.png"));
            red = new Image(new FileInputStream("src/lasers/gui/resources/red.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Constructor
    /**
     * Constructor that initializes a safe button based on the value of the tile
     */
    public SafeButton (String value, int row, int col) {
        updateImage(value);
        this.value = value;
        this.row = row;
        this.col = col;
    }


    // Methods
    /**
     * Updates the image based on the value of the tile
     * @param value (String) the value of the tile
     */
    public void updateImage(String value) {
        Image image = null;
        setButtonBackground("black.png");
        switch (value) {
            case "*":
                image = beam;
                break;
            case ".":
                image = black;
                break;
            case "L":
                image = laser;
                break;
            case "0":
                image = pillar0;
                break;
            case "1":
                image = pillar1;
                break;
            case "2":
                image = pillar2;
                break;
            case "3":
                image = pillar3;
                break;
            case "4":
                image = pillar4;
                break;
            case "X":
                image = pillarX;
                break;
            case "R":
                image = red;
                break;
        }

        // set the graphic on the button
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        this.setGraphic(imageView);
        this.setPadding(Insets.EMPTY);
        this.setStyle("-fx-border-style: solid");
        this.setStyle("-fx-border-color: #4f4f4f");
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param bgImgName the name of the image file
     */
    public void setButtonBackground(String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        this.setBackground(background);
    }

    /**
     * Getter for the row of the button.
     * @return (int) row of the button
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for the column of the button.
     * @return (int) column of the button
     */
    public int getCol() {
        return col;
    }

    /**
     * Getter for the white image.
     * @return (Image) the white image
     */
    public Image getBlackImage() {
        return black;
    }

    /**
     * Getter for the red image.
     * @return (Image) the red image
     */
    public Image getRedImage() {
        return red;
    }

    /**
     * Getter for the SafeButton's value
     * @return (String) the value
     */
    public String getValue() {
        return value;
    }
}
