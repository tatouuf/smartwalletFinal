package controller.mainalc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tests.services.MainFXML;

public class MainALC {

    @FXML
    private void userseract() {
        openAccueilServiceclient();
    }

    @FXML
    private void buttonadminser() {
        openAccueilService();
    }
    @FXML
    private ImageView imgLogo;

    @FXML
    public void initialize() {
        try {
            // Charger le logo depuis les ressources
            imgLogo.setImage(new Image(getClass().getResourceAsStream("/icons/logoservices.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAccueilService() {
        try {
            // Charger AccueilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilService.fxml"));
            Parent root = loader.load();

            // Remplacer la scène principale
            Stage stage = MainFXML.getPrimaryStage();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Accueil Services");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur : Impossible d'ouvrir AccueilService.fxml");
        }
    }
    private void openAccueilServiceclient() {
        try {
            // Charger AccueilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml"));
            Parent root = loader.load();

            // Remplacer la scène principale
            Stage stage = MainFXML.getPrimaryStage();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Accueil Services");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur : Impossible d'ouvrir AccueilService.fxml");
        }
    }
}
