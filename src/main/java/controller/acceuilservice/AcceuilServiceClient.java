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

import java.io.InputStream;
import java.net.URL;

public class AcceuilServiceClient {

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

    @FXML
    public void initialize() {
        try {
            if (imgLogo != null) {
                // Vérifier si l'image existe
                InputStream imgStream = getClass().getResourceAsStream("/images/logo.png");
                if (imgStream != null) {
                    Image image = new Image(imgStream);
                    imgLogo.setImage(image);
                } else {
                    System.out.println("[AcceuilServiceClient] Logo non trouvé, masquage de l'image");
                    imgLogo.setVisible(false);
                }
            } else {
                System.out.println("[AcceuilServiceClient] imgLogo est null - vérifier le fichier FXML");
            }
        } catch (Exception e) {
            System.out.println("[AcceuilServiceClient] Erreur chargement logo: " + e.getMessage());
        }
    }

    // ================== BUTTON ACTIONS ==================
    @FXML
    private void showAfficherService() {
        openFXMLInSameStage("/services/AfficherServiceClient.fxml", "Afficher Tous les Services");
    }

    @FXML
    private void showAfficherAssurance() {
        openFXMLInSameStage("/assurance/AfficherAssuranceClient.fxml", "Afficher Toutes les Assurances");
    }

    @FXML
    private void showAfficherCredit() {
        openFXMLInSameStage("/credit/AfficherCreditClient.fxml", "Afficher Tous les Crédits");
    }

    @FXML
    private void retourMain() {
        try {
            // Utiliser LandingPage.fxml au lieu de MainALC.fxml
            String fxmlPath = "/LandingPage.fxml";
            URL location = getClass().getResource(fxmlPath);

            if (location == null) {
                System.err.println("[AcceuilServiceClient] Fichier non trouvé: " + fxmlPath);
                // Alternative: fermer la fenêtre actuelle
                if (btnRetourMain.getScene() != null) {
                    Stage stage = (Stage) btnRetourMain.getScene().getWindow();
                    stage.close();
                }
                return;
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();

            // Récupérer le Stage actuel
            Stage stage = null;
            if (btnRetourMain.getScene() != null) {
                stage = (Stage) btnRetourMain.getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("SmartWallet");
                stage.centerOnScreen();
            } else {
                // Si aucun stage existant, ouvre une nouvelle fenêtre
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("SmartWallet");
                newStage.centerOnScreen();
                newStage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible d'ouvrir la page principale: " + e.getMessage());
        }
    }

    // ================== UTILS ==================
    private void openFXMLInSameStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupère le Stage actuel via un Node sûr
            Stage stage = (Stage) btnRetourMain.getScene().getWindow();
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