package controller.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.credit.ServiceCredit;

import java.sql.SQLException;

public class ModifierCredit {

    @FXML
    private TextField txtNomClient;

    @FXML
    private TextField txtMontant;

    @FXML
    private DatePicker dateCredit;

    @FXML
    private TextField txtDescription;

    @FXML
    private ComboBox<StatutCredit> comboStatut;
    @FXML
    private Button cancel;
    @FXML
    private Button btnEnregistrer;

    @FXML
    private ImageView imgLogoCredit; // ✅ compatible avec le nouveau FXML

    private Credit credit;
    private final ServiceCredit serviceCredit = new ServiceCredit();
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/credit/AfficherCredit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu principal !");
        }
    }
    @FXML
    public void initialize() {

        // Remplir ComboBox une seule fois
        comboStatut.getItems().setAll(StatutCredit.values());

        // Charger le logo (si موجود dans resources/images/)
        try {
            Image image = new Image(getClass().getResourceAsStream("/icons/logoservices.png"));
            imgLogoCredit.setImage(image);
        } catch (Exception e) {
            System.out.println("Logo non trouvé.");
        }
    }

    // Méthode appelée depuis AfficherCredit
    public void setCredit(Credit c) {
        this.credit = c;

        txtNomClient.setText(c.getNomClient());
        txtMontant.setText(String.valueOf(c.getMontant()));
        dateCredit.setValue(c.getDateCredit());
        txtDescription.setText(c.getDescription());
        comboStatut.setValue(c.getStatut());
    }

    @FXML
    public void enregistrerModifications() {

        try {

            if (txtNomClient.getText().trim().isEmpty()
                    || txtMontant.getText().trim().isEmpty()
                    || dateCredit.getValue() == null
                    || comboStatut.getValue() == null) {

                showAlert(Alert.AlertType.WARNING,
                        "Champs manquants",
                        "Veuillez remplir tous les champs obligatoires !");
                return;
            }

            float montant;
            try {
                montant = Float.parseFloat(txtMontant.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR,
                        "Erreur",
                        "Le montant doit être un nombre valide !");
                return;
            }

            // Mise à jour des données
            credit.setNomClient(txtNomClient.getText());
            credit.setMontant(montant);
            credit.setDateCredit(dateCredit.getValue());
            credit.setDescription(txtDescription.getText());
            credit.setStatut(comboStatut.getValue());

            serviceCredit.modifierCredit(credit);

            showAlert(Alert.AlertType.INFORMATION,
                    "Succès",
                    "Crédit modifié avec succès !");

            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur Base de Données",
                    "Erreur lors de la modification !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
