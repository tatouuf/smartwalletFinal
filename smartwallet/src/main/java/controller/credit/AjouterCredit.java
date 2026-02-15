package controller.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.credit.ServiceCredit;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

public class AjouterCredit {

    @FXML private TextField txtNomClient;
    @FXML private TextField txtMontant;
    @FXML private DatePicker dateCredit;
    @FXML private TextField txtDescription;
    @FXML private ComboBox<StatutCredit> comboStatut;
    @FXML private Button btnRetour;
    @FXML private ImageView imgLogoCredit;
    @FXML private Button btnAjouter;

    @FXML
    public void initialize() {
        // Charger les statuts dans la ComboBox
        comboStatut.getItems().setAll(StatutCredit.values());
        comboStatut.getSelectionModel().selectFirst();

        // Charger le logo
        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/icons/logoservices.png")
            ));
            imgLogoCredit.setImage(image);
        } catch (Exception e) {
            System.out.println("Logo non trouvé !");
        }
    }

    // ================= BUTTON RETOUR =================
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainalc/MainALC.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu principal !");
        }
    }

    // ================= BUTTON AJOUTER =================
    @FXML
    private void ajouterCredit() {

        // Vérification des champs obligatoires
        if (txtNomClient.getText().trim().isEmpty()
                || txtMontant.getText().trim().isEmpty()
                || dateCredit.getValue() == null
                || comboStatut.getValue() == null) {

            showAlert(Alert.AlertType.WARNING,
                    "Champs obligatoires",
                    "Veuillez remplir tous les champs obligatoires !");
            return;
        }

        // Vérification que le montant est un nombre
        float montant;
        try {
            montant = Float.parseFloat(txtMontant.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur de saisie",
                    "Le montant doit être un nombre valide !");
            return;
        }

        LocalDate date = dateCredit.getValue();
        StatutCredit statut = comboStatut.getValue();

        Credit credit = new Credit(
                txtNomClient.getText(),
                montant,
                date,
                txtDescription.getText(),
                statut
        );

        ServiceCredit service = new ServiceCredit();

        try {
            service.ajouterCredit(credit);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Crédit ajouté avec succès !");

            // Redirection vers AfficherCredit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/credit/AfficherCredit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAjouter.getScene().getWindow(); // on utilise btnAjouter pour récupérer la scène
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Crédits");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Erreur lors de l'ajout du crédit !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page d'affichage des crédits !");
        }
    }

    // ================= UTILITAIRE ALERT =================
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
