package controller.acceuilservice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class AcceuilService {

    // ================== FXML ELEMENTS ==================
    @FXML
    private ImageView imgService;
    @FXML
    private ImageView imgAssurance;
    @FXML
    private ImageView imgCredit;
    @FXML
    private ImageView imgLogo;
    @FXML
    private Button btnRetourMain;

    // ================== INITIALIZE ==================
    @FXML
    public void initialize() {
        try {
            imgLogo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logoservices.png"))));
            imgService.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/location.png"))));
            imgAssurance.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/assurance.png"))));
            imgCredit.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/credit.png"))));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible de charger les images.");
        }
    }

    // ================== BUTTON ACTIONS ==================
    @FXML
    private void showAfficherService() {
        openFXMLInSameStage("/services/AfficherService.fxml", "Afficher Tous les Services");
    }

    @FXML
    private void showAfficherAssurance() {
        openFXMLInSameStage("/assurance/AfficherAssurance.fxml", "Afficher Toutes les Assurances");
    }

    @FXML
    private void showAfficherCredit() {
        openFXMLInSameStage("/credit/AfficherCredit.fxml", "Afficher Tous les Crédits");
    }

    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainALC/MainALC.fxml"));
            Parent root = loader.load();

            // Essaie de récupérer le Stage actuel
            Stage stage = null;
            if (btnRetourMain.getScene() != null) {
                stage = (Stage) btnRetourMain.getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle("Main ALC");
            } else {
                // Si aucun stage existant, ouvre une nouvelle fenêtre
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root, 900, 500));
                newStage.setTitle("Main ALC");
                newStage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible d'ouvrir la page principale.");
        }
    }


    // ================== UTILS ==================
    private void openFXMLInSameStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupère le Stage actuel depuis un Node sûr
            Stage stage = (Stage) imgLogo.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle(title);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible d'ouvrir " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
