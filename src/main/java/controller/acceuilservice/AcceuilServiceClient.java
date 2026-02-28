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
import tests.MainFxml;

import java.util.Objects;

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

    // ================== INITIALIZE ==================
    @FXML
    public void initialize() {
        try {
            System.out.println("✅ AcceuilServiceClient.initialize() appelé");

            // Vérifier que les éléments FXML ne sont pas null
            if (imgLogo != null) {
                imgLogo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logoservices.png"))));
                System.out.println("✅ Logo chargé");
            } else {
                System.out.println("⚠️ imgLogo est null dans initialize()");
            }

            if (imgService != null) {
                imgService.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/location.png"))));
            }

            if (imgAssurance != null) {
                imgAssurance.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/assurance.png"))));
            }

            if (imgCredit != null) {
                imgCredit.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/credit.png"))));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible de charger les images: " + e.getMessage());
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
            System.out.println("🏠 Retour à la page principale");

            // Essayer de retourner à la page d'accueil
            MainFxml.getInstance().showWalletHome();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible d'ouvrir la page principale.");
        }
    }

    // ================== UTILS ==================
    private void openFXMLInSameStage(String fxmlPath, String title) {
        try {
            System.out.println("📂 Ouverture de: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupérer le Stage actuel de manière sécurisée
            Stage stage = getCurrentStage();

            if (stage != null) {
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle(title);
                stage.centerOnScreen();
                System.out.println("✅ Navigation vers: " + title);
            } else {
                // Fallback: créer une nouvelle fenêtre
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root, 900, 500));
                newStage.setTitle(title);
                newStage.show();
                System.out.println("✅ Nouvelle fenêtre créée pour: " + title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible d'ouvrir " + fxmlPath + "\n" + e.getMessage());
        }
    }

    /**
     * Récupère le Stage actuel de manière sécurisée
     */
    private Stage getCurrentStage() {
        // Essayer avec btnRetourMain
        if (btnRetourMain != null && btnRetourMain.getScene() != null) {
            return (Stage) btnRetourMain.getScene().getWindow();
        }

        // Essayer avec imgLogo
        if (imgLogo != null && imgLogo.getScene() != null) {
            return (Stage) imgLogo.getScene().getWindow();
        }

        // Essayer avec imgService
        if (imgService != null && imgService.getScene() != null) {
            return (Stage) imgService.getScene().getWindow();
        }

        // Fallback: utiliser la stage principale
        try {
            return MainFxml.getInstance().getPrimaryStage();
        } catch (Exception e) {
            System.err.println("❌ Impossible de trouver une stage");
            return null;
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