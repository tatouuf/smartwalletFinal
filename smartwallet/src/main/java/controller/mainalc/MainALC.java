package controller.mainalc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MainALC {

    @FXML
    private ImageView imgLogo;

    // ===============================
    // INITIALIZE
    // ===============================
    @FXML
    public void initialize() {
        try {
            imgLogo.setImage(
                    new Image(getClass().getResourceAsStream("/icons/logoservices.png"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // USER BUTTON
    // ===============================
    @FXML
    private void userseract(ActionEvent event) {
        openAccueilServiceclient(event);
    }

    // ===============================
    // ADMIN BUTTON
    // ===============================
    @FXML
    private void buttonadminser(ActionEvent event) {
        openAccueilService(event);
    }

    // ===============================
    // OUVRIR ACCUEIL ADMIN
    // ===============================
    private void openAccueilService(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/acceuilservices/AcceuilService.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Accueil Services");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur : Impossible d'ouvrir AcceuilService.fxml");
        }
    }

    // ===============================
    // OUVRIR ACCUEIL CLIENT
    // ===============================
    private void openAccueilServiceclient(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Accueil Services Client");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur : Impossible d'ouvrir AcceuilServiceClient.fxml");
        }
    }
}