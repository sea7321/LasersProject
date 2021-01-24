package lasers.gui;

// Import & Set Up Workspace
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

import lasers.model.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the lasers.lasers.model
 * and receives updates from it.
 *
 * @author RIT CS
 * @author Savannah Alfaro (sea2985)
 * @author Jamieson Dube (jmd2851)
 */
public class LasersGUI extends Application implements Observer<LasersModel, ModelData> {

    private Label message;
    private SafeButton[][] safeButtons;
    private HBox buttons;
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;
    /** The UI's connection to the Controller */
    private ControllerGUI controller;
    private BorderPane borderPane;
    private int backgroundCounter = 1;

    // Methods
    /**
     * Creates a new model and adds itself as an observer.
     */
    @Override
    public void init() {
    // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        this.model = new LasersModel(getParameters().getRaw().get(0));
        this.model.addObserver(this);
        this.controller = new ControllerGUI(model);
    }

    /**
     * The initialization of all GUI component happens here.
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {


        // create message label
        message = new Label();
        message.setMaxWidth(Double.MAX_VALUE);
        message.setAlignment(Pos.CENTER);
        message.setFont(Font.font("Consolas", FontWeight.BOLD, 25));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.MISTYROSE);
        dropShadow.setRadius(20.0);
        message.setEffect(dropShadow);
        message.setTextFill(Color.WHITE);

        // create grid pane and safeButtons
        MakeGridPane grid = new MakeGridPane(model);
        GridPane safeView = grid.makeGridPane(model, controller);
        safeView.setMaxWidth(Double.MAX_VALUE);
        safeView.setAlignment(Pos.CENTER);
        safeView.setHgap(10);
        safeView.setVgap(10);
        safeButtons = grid.getSafeButtons();

        // create border pane and H box
        buttons = new HBox();
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(30, 30, 30, 30));
        changeBorderBackground(borderPane);

        // create bottom buttons
        // toggle button
        Button toggle = new Button("Toggle");
        toggle.setOnAction(e -> changeBorderBackground(borderPane));
        toggle.setPadding(new Insets(15, 12, 15, 12));
        toggle.setStyle("-fx-background-color: slateblue");
        toggle.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        toggle.setTextFill(Color.WHITE);

        // check button
        Button check = new Button("Check");
        check.setOnAction(e -> controller.verify());
        check.setPadding(new Insets(15, 12, 15, 12));
        check.setStyle("-fx-background-color: Lime");
        check.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        check.setTextFill(Color.WHITE);

        // hint button
        Button hint = new Button("Hint");
        hint.setPadding(new Insets(15, 12, 15, 12));
        hint.setStyle("-fx-background-color: DeepPink");
        hint.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        hint.setTextFill(Color.WHITE);
        hint.setOnAction(e -> controller.getHint());

        // solve button
        Button solve = new Button("Solve");
        solve.setPadding(new Insets(15, 12, 15, 12));
        solve.setStyle("-fx-background-color: MediumPurple");
        solve.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        solve.setTextFill(Color.WHITE);

        solve.setOnAction(e -> controller.solve());

        // restart button
        Button restart = new Button("Restart");
        restart.setPadding(new Insets(15, 12, 15, 12));
        restart.setOnAction(e -> controller.resetSafe());
        restart.setStyle("-fx-background-color: Indigo");
        restart.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        restart.setTextFill(Color.WHITE);

        // load button
        Button load = new Button("Load");
        load.setPadding(new Insets(15, 12, 15, 12));
        load.setStyle("-fx-background-color: Cyan");
        load.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        load.setTextFill(Color.WHITE);

        // file chooser window
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("tests"));
        fc.setTitle("Open safe file");
        load.setOnAction(e -> {
            File newFile = fc.showOpenDialog(null);
            String newFileName = newFile.toString();
            String shortName = newFile.getName();
            boolean error = false;

            // Test the file before updating anything important
            try {
                new LasersModel(newFileName);
            } catch (Exception ex) {
                message.setText("Error loading file: " + shortName);
                error = true;
            }
            if (!error) {
                // create a new LasersModel and controller
                model = new LasersModel(newFileName);
                model.addObserver(this);
                controller = new ControllerGUI(model);

                // make a new grid pane and make grid pane
                MakeGridPane newMaker = new MakeGridPane(model);
                GridPane newButtonGrid = newMaker.makeGridPane(model, controller);
                newButtonGrid.setMaxWidth(Double.MAX_VALUE);
                newButtonGrid.setAlignment(Pos.CENTER);
                newButtonGrid.setHgap(10);
                newButtonGrid.setVgap(10);
                safeButtons = newMaker.getSafeButtons();
                message.setText("Loaded file: " + shortName);

                // set border pane
                borderPane = new BorderPane();
                borderPane.setTop(message);
                borderPane.setCenter(newButtonGrid);
                borderPane.setBottom(buttons);
                borderPane.setPadding(new Insets(30, 30, 30, 30));
                changeBorderBackground(borderPane);

                // set the stage and create a new scene
                stage.setScene(new Scene(borderPane));
            }
            });

        // set buttons
        buttons.getChildren().addAll(toggle, check, hint, solve, restart, load);
        buttons.setMaxWidth(Double.MAX_VALUE);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(15, 12, 15, 12));

        // set border pane
        borderPane.setTop(message);
        borderPane.setCenter(safeView);
        borderPane.setBottom(buttons);

        // create & set scene
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
    }

    /**
     * Starts the stage, sets the title, and shows the stage.
     * @param stage (Stage) the stage of the GUI
     */
    @Override
    public void start(Stage stage) {

        // initialization
        init(stage);

        // set title and show stage
        stage.setTitle("Lasers GUI");
        message.setText(getParameters().getRaw().get(0));
        stage.show();
    }

    /**
     * Updates the tiles on the grid pane.
     * @param model (LasersModel) the model in MVC
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(LasersModel model, ModelData data) {

        // set message label to data message
        message.setText(data.getMessage());
        for (SafeButton[] buttonArray : safeButtons) {
            for (SafeButton button : buttonArray) {
                if (button.getRow() == data.getErrorRow() && button.getCol() == data.getErrorCol()) {
                    if (model.getValue(button.getRow(), button.getCol()).equals(".")) {
                        button.setGraphic(new ImageView(button.getRedImage()));
                        Runnable emptyTimer = new EmptySpaceTimer(button, 2);
                        Thread thread = new Thread(emptyTimer);
                        thread.start();
                    } else {
                        button.setButtonBackground("red.png");
                        Runnable colorTimer = new ColorTimer(button, 2);
                        Thread thread = new Thread(colorTimer);
                        thread.start();
                    }

                }
                button.updateImage(model.getValue(button.getRow(), button.getCol()));
            }
        }
    }

    /**
     * Changes the background of the border pane
     * @param borderPane (BorderPane) the border pane of the GUI
     */
    public void changeBorderBackground(BorderPane borderPane) {

        // initialize background size and source file
        BackgroundSize bSize = new BackgroundSize(1.0, 1.0, true, true, false, false);
        String source = null;

        // switch between backgrounds using backgroundCounter
        switch(backgroundCounter) {
            case 1:
                source = "resources/background1.jpg";
                break;
            case 2:
                source = "resources/background2.jpg";
                break;
            case 3:
                source = "resources/background3.jpg";
                break;
            case 4:
                source = "resources/background4.jpg";
                break;
            case 5:
                source = "resources/background5.jpg";
                break;
            case 6:
                source = "resources/background6.jpg";
                break;
        }

        // changing the background image
        assert source != null;
        BackgroundImage backgroundImage = new BackgroundImage(

                new Image(getClass().getResource(source).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bSize);
        Background background = new Background(backgroundImage);
        borderPane.setBackground(background);

        // update backgroundCounter
        if (backgroundCounter < 6) {
            backgroundCounter++;
        } else {
            backgroundCounter = 1;
        }
    }
}
