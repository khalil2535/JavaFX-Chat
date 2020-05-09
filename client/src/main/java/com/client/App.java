package com.client;

import javafx.stage.Stage;

public class App {
    private static Client client;
    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        App.stage = stage;
    }

    public static Client getClient() {
        return client;
    }

    public static void setClient(Client client) {
        App.client = client;
    }
}
