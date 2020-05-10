package com.client.login;

import com.client.App;
import com.client.Client;
import com.client.chatwindow.ChatController;
import com.client.util.DialogsUtil;
import com.client.util.ResizeHelper;
import com.model.messages.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by Dominic on 12-Nov-15.
 */
public class LoginController implements Initializable {
    public static ChatController chatController;
    @FXML
    public TextField hostnameTextfield;
    @FXML
    private ImageView Defaultview;
    @FXML
    private ImageView Sarahview;
    @FXML
    private ImageView Dominicview;
    @FXML
    private TextField portTextfield;
    @FXML
    private TextField usernameTextfield;
    @FXML
    private ChoiceBox<String> imagePicker;
    @FXML
    private Label selectedPicture;
    @FXML
    private BorderPane borderPane;
    private double xOffset;
    private double yOffset;
    private Scene scene;


    public void loginButtonAction() throws IOException {
        String hostname = hostnameTextfield.getText();
        int port = Integer.parseInt(portTextfield.getText());
        String username = usernameTextfield.getText();
        String picture = selectedPicture.getText();

        Client client = new Client(username, picture, Status.ONLINE, port, hostname);
        try {
            client.connect();

            App.setClient(client);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ChatView.fxml"));
            Parent window = (Pane) fxmlLoader.load();
            chatController = fxmlLoader.getController();
            this.showScene();
            this.scene = new Scene(window);

        } catch (IOException ex) {
            DialogsUtil.showErrorDialog("Could not connect to server\n please check hostname and port");
        }


        // TODO change this here
//        Listener listener = new Listener(hostname, port, username, picture, chatController);
//        new Thread(listener).start();
    }

    public void showScene() { // TODO change this here
        Platform.runLater(() -> {
            Stage stage = (Stage) hostnameTextfield.getScene().getWindow();
            stage.setResizable(false);

            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setScene(this.scene);
            stage.setMinWidth(800);
            stage.setMinHeight(300);
            ResizeHelper.addResizeListener(stage);
            stage.centerOnScreen();
            chatController.setUsernameLabel(usernameTextfield.getText());
            chatController.setImageLabel(selectedPicture.getText());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imagePicker.getSelectionModel().selectFirst();
        selectedPicture.textProperty().bind(imagePicker.getSelectionModel().selectedItemProperty());
        selectedPicture.setVisible(false);

        /* Drag and Drop */
        imagePicker.getSelectionModel().selectedItemProperty().addListener(
                (selected, oldPicture, newPicture) -> {
                    if (oldPicture != null) {
                        switch (oldPicture) {
                            case "Default":
                                Defaultview.setVisible(false);
                                break;
                            case "Dominic":
                                Dominicview.setVisible(false);
                                break;
                            case "Sarah":
                                Sarahview.setVisible(false);
                                break;
                        }
                    }
                    if (newPicture != null) {
                        switch (newPicture) {
                            case "Default":
                                Defaultview.setVisible(true);
                                break;
                            case "Dominic":
                                Dominicview.setVisible(true);
                                break;
                            case "Sarah":
                                Sarahview.setVisible(true);
                                break;
                        }
                    }
                });


        int numberOfSquares = 30;
        while (numberOfSquares > 0) {
            generateAnimation();
            numberOfSquares--;
        }
    }

    /* This method is used to generate the animation on the login window, It will generate random ints to determine
     * the size, speed, starting points and direction of each square.
     */
    public void generateAnimation() {
        Random rand = new Random();
        int sizeOfSquare = rand.nextInt(50) + 1;
        int speedOfSquare = rand.nextInt(10) + 5;
        int startXPoint = rand.nextInt(420);
        int startYPoint = rand.nextInt(350);
        int direction = rand.nextInt(5) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Rectangle r1 = null;

        switch (direction) {
            case 1:
                // MOVE LEFT TO RIGHT
                r1 = new Rectangle(0, startYPoint, sizeOfSquare, sizeOfSquare);
                moveXAxis = new KeyValue(r1.xProperty(), 350 - sizeOfSquare);
                break;
            case 2:
                // MOVE TOP TO BOTTOM
                r1 = new Rectangle(startXPoint, 0, sizeOfSquare, sizeOfSquare);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSquare);
                break;
            case 3:
            case 6:
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                r1 = new Rectangle(startXPoint, 0, sizeOfSquare, sizeOfSquare);
                moveXAxis = new KeyValue(r1.xProperty(), 350 - sizeOfSquare);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSquare);
                break;
            case 4:
                // MOVE BOTTOM TO TOP
                r1 = new Rectangle(startXPoint, 420 - sizeOfSquare, sizeOfSquare, sizeOfSquare);
                moveYAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 5:
                // MOVE RIGHT TO LEFT
                r1 = new Rectangle(420 - sizeOfSquare, startYPoint, sizeOfSquare, sizeOfSquare);
                moveXAxis = new KeyValue(r1.xProperty(), 0);
                break;
            default:
                System.out.println("default");
        }

        r1.setFill(Color.web("#F89406"));
        r1.setOpacity(0.1);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(speedOfSquare * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        borderPane.getChildren().add(borderPane.getChildren().size() - 1, r1);
    }

    /* Terminates Application */
    @FXML
    public void closeSystem() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void minimizeWindow() {
        App.getStage().setIconified(true);
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        xOffset = App.getStage().getX() - event.getScreenX();
        yOffset = App.getStage().getY() - event.getScreenY();
        borderPane.setCursor(Cursor.CLOSED_HAND);
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        App.getStage().setX(event.getScreenX() + xOffset);
        App.getStage().setY(event.getScreenY() + yOffset);
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        borderPane.setCursor(Cursor.DEFAULT);
    }
}
