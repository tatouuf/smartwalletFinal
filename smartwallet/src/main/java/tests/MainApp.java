package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/wallet/mainLayout.fxml")
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Smart Wallet");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}