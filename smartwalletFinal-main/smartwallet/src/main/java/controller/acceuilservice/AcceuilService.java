package controller.acceuilservice;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utils.Session;

import java.util.Objects;

public class AcceuilService {

    // ================== FXML ELEMENTS ==================
    @FXML private ImageView imgService;
    @FXML private ImageView imgAssurance;
    @FXML private ImageView imgCredit;
    @FXML private ImageView imgLogo;
    @FXML private Button btnRetourMain;

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

    @FXML
    private void retourMain() {
        try {
            // Vérifier le rôle de l'utilisateur connecté
            User currentUser = Session.getCurrentUser();

            if (currentUser != null && "ADMIN".equals(currentUser.getRole().name())) {
                // Si c'est un admin, retourner au DashboardAdmin
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnRetourMain.getScene().getWindow();
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle("Admin Dashboard");
            } else {
                // Si c'est un utilisateur normal, retourner à la page d'accueil des services
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainALC/MainALC.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnRetourMain.getScene().getWindow();
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle("SmartWallet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAfficherService() {
        ouvrirPage("/services/AfficherService.fxml", "Services");
    }

    @FXML
    private void showAfficherAssurance() {
        ouvrirPage("/assurance/AfficherAssurance.fxml", "Assurances");
    }

    @FXML
    private void showAfficherCredit() {
        ouvrirPage("/credit/AfficherCredit.fxml", "Crédits");
    }

    private void ouvrirPage(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) imgLogo.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle(titre);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir " + titre);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}