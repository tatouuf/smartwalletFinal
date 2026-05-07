package com.example.smartwallet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/smartwallet/dashboard-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 700, 600);

        // ✅ Charger le CSS (fix case-sensitive path)
        scene.getStylesheets().add(
                getClass().getResource("/com/example/smartwallet/Styles.css").toExternalForm()
        );

        stage.setTitle("SmartWallet - Recurring Payments");
        stage.setScene(scene);
        stage.show();
    }
}
