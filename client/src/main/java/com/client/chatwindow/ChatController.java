package com.client.chatwindow;

import com.client.App;
import com.client.bubble.BubbleSpec;
import com.client.bubble.BubbledLabel;
import com.client.traynotifications.animations.AnimationType;
import com.client.traynotifications.notification.TrayNotification;
import com.client.util.DialogsUtil;
import com.model.messages.Message;
import com.model.messages.Status;
import com.model.messages.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ChatController implements Initializable {

    @FXML
    public Button buttonSend;
    @FXML
    public HBox onlineUsersHbox;
    @FXML
    ListView<HBox> chatPane;
    @FXML
    ListView statusList;
    @FXML
    BorderPane borderPane;
    @FXML
    ComboBox<String> statusComboBox;
    Logger logger = LoggerFactory.getLogger(ChatController.class);
    @FXML
    private TextArea messageBox;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label onlineCountLabel;
    @FXML
    private ListView<User> userList;
    @FXML
    private ImageView userImageView;
    private double xOffset;
    private double yOffset;

    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()) {
            try {
                App.getClient().sendMessage(msg);
            } catch (Exception e) {
                DialogsUtil.showErrorDialog("error in encryption the message");
                e.printStackTrace();
            }
            messageBox.clear();
        }
    }


    public synchronized void addToChat(Message msg) {
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                String imagePath = "images/" + msg.getPicture().toLowerCase() + ".png";
                Image image = new Image(getClass().getClassLoader().getResource(imagePath).toString());

                String userName = msg.getName();
                String msgText = msg.getTextDecrypted(App.getClient().key);

                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(userName + ": " + msgText);
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);

                HBox x = new HBox();
                x.getChildren().addAll(profileImage, bl6);
                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };

        othersMessages.setOnSucceeded(event -> {
            chatPane.getItems().add(othersMessages.getValue());
        });

        Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                Image image = userImageView.getImage();
                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(msg.getTextDecrypted(App.getClient().key));
                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                        null, null)));
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6, profileImage);

                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));

        if (msg.getName().equals(usernameLabel.getText())) {
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
        }
    }

    public void setUsernameLabel(String username) {
        this.usernameLabel.setText(username);
    }

    public void setImageLabel() {
        this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/dominic.png").toString()));
    }

    public void setOnlineLabel(String userCount) {
        Platform.runLater(() -> onlineCountLabel.setText(userCount));
    }

    public void setUserList(Message msg) {
        logger.info("setUserList() method Enter");
        Platform.runLater(() -> {
            ObservableList<User> users = FXCollections.observableList(msg.getUsers());
            userList.setItems(users);
            userList.setCellFactory(new CellRenderer());
            setOnlineLabel(String.valueOf(msg.getUsers().size()));
        });
        logger.info("setUserList() method Exit");
    }

    /* Displays Notification when a user joins */
    public void newUserNotification(Message msg) {
        Platform.runLater(() -> {
            Image profileImg = new Image(getClass().getClassLoader().getResource("images/" + msg.getPicture().toLowerCase() + ".png").toString(), 50, 50, false, false);
            TrayNotification tray = new TrayNotification();
            tray.setTitle("A new user has joined!");
            tray.setMessage(msg.getName() + " has joined the JavaFX Chatroom!");
            tray.setRectangleFill(Paint.valueOf("#2C3E50"));
            tray.setAnimationType(AnimationType.POPUP);
            tray.setImage(profileImg);
            tray.showAndDismiss(Duration.seconds(5));
            try {
                Media hit = new Media(getClass().getClassLoader().getResource("sounds/notification.wav").toString());
                MediaPlayer mediaPlayer = new MediaPlayer(hit);
                mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    @FXML
    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    /* Method to display server messages */
    public synchronized void addAsServer(Message msg) {

        logger.info("add as server");

        Task<HBox> task = new Task<HBox>() {
            @Override
            public HBox call() {
                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(msg.getText());
                bl6.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,
                        null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_BOTTOM);
                x.setAlignment(Pos.CENTER);
                x.getChildren().addAll(bl6);
                return x;
            }
        };
        task.setOnSucceeded(event -> chatPane.getItems().add(task.getValue()));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            int errors = 0;
            while (App.getClient().isConnected() && errors < 5) {
                Message message = null;
                try {
                    message = App.getClient().getNewMessage();
                    errors = 0;
                } catch (IOException | ClassNotFoundException e) {
                    DialogsUtil.showErrorDialog("error in receiving message");
                    e.printStackTrace();
                    errors++;
                }

                if (message != null) {
                    switch (message.getType()) {
                        case USER:
                            addToChat(message);
                            break;
                        case NOTIFICATION:
                            newUserNotification(message);
                            break;
                        case SERVER:
                            addAsServer(message);
                            break;
                        case CONNECTED:
                        case DISCONNECTED:
                        case STATUS:
                            setUserList(message);
                            break;
                    }
                } else {
                    logger.info("message is null !");
                }
            }
        }).start();

        setImageLabel();

        /* Drag and Drop */
        borderPane.setOnMousePressed(event -> {
            xOffset = App.getStage().getX() - event.getScreenX();
            yOffset = App.getStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            App.getStage().setX(event.getScreenX() + xOffset);
            App.getStage().setY(event.getScreenY() + yOffset);

        });

        borderPane.setOnMouseReleased(event -> borderPane.setCursor(Cursor.DEFAULT));

        statusComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                App.getClient().sendStatusUpdate(Status.valueOf(newValue.toUpperCase()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /* Added to prevent the enter from adding a new line to inputMessageBox */
        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    sendButtonAction();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ke.consume();
            }
        });

    }

    public void setImageLabel(String selectedPicture) {
        switch (selectedPicture) {
            case "Dominic":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/dominic.png").toString()));
                break;
            case "Sarah":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/sarah.png").toString()));
                break;
            case "Default":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/default.png").toString()));
                break;
        }
    }

    @FXML
    public void logoutScene() {
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent window = null;
            try {
                window = (Pane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (window != null) {
                Stage stage = App.getStage();
                Scene scene = new Scene(window);
                stage.setScene(scene);
                stage.centerOnScreen();
            } else {
                logger.error("Log out error, windows is null");
            }
        });
    }
}