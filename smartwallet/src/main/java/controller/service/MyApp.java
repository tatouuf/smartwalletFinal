package controller.service;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyApp extends Application {

    // ✅ GLOBAL
    private static HostServices hostServices;

    public static HostServices getHostServicesInstance() {
        return hostServices;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            hostServices = getHostServices(); // ✅ STOCKAGE GLOBAL

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/mainALC/MainALC.fxml")
            );

            Parent root = loader.load();

            primaryStage.setTitle("Smart Wallet - Services");
            primaryStage.setScene(new Scene(root, 900, 500));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}