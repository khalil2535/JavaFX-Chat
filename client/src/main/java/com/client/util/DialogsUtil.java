package com.client.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class DialogsUtil {
    /**
     * This displays an alert message to the user
     *
     * @param message to show in error dialog
     */
    public static void showErrorDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText(message);
            alert.setContentText("Please check for firewall issues and check if the server is running.");
            alert.showAndWait();
        });
    }
}
