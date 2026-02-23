package esprit.tn.chayma;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/wallet/deplayoutLayout.fxml"));

        // Crée la scène
        Scene scene = new Scene(root, 1200, 900);

        // Charge le fichier CSS pour le style du sidebar
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());

        primaryStage.setTitle("Smart Wallet");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}