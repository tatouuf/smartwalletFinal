package tests.services;

import controller.service.AfficherService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFXML extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showAjouterService();
    }

    public static void showAjouterService() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/services/AjouterService.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Ajouter Service");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAfficherService() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/services/AfficherService.fxml"));
            Parent root = loader.load();

            AfficherService controller = loader.getController();
            controller.loadServices();

            primaryStage.setTitle("Afficher Tous les Services");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
