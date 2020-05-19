package com.client.login;

import com.client.App;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainLauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        App.setStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/LoginView.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Secure Socket Chat");
        String applicationIconPath = getClass().getClassLoader().getResource("images/plug.png").toString();
        primaryStage.getIcons().add(new Image(applicationIconPath));
        Scene mainScene = new Scene(root, 350, 420);
        mainScene.setRoot(root);
        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }
}
