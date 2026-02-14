package controller.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.credit.ServiceCredit;

import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterCredit {

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
    public void initialize() {
        // Remplir la ComboBox avec les statuts disponibles
        comboStatut.getItems().addAll(StatutCredit.values());
    }

    // Méthode liée au bouton Ajouter Crédit
    @FXML
    private void ajouterCredit() {
        try {
            // Vérification des champs obligatoires
            if (txtNomClient.getText().isEmpty() ||
                    txtMontant.getText().isEmpty() ||
                    dateCredit.getValue() == null ||
                    comboStatut.getValue() == null) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Veuillez remplir tous les champs obligatoires !");
                alert.show();
                return;
            }

            // Conversion du montant
            float montant = Float.parseFloat(txtMontant.getText());
            LocalDate date = dateCredit.getValue();
            StatutCredit statut = comboStatut.getValue();

            // Création du crédit
            Credit c = new Credit(txtNomClient.getText(), montant, date, txtDescription.getText(), statut);

            // Ajouter le crédit dans la base
            ServiceCredit sc = new ServiceCredit();
            sc.ajouterCredit(c);

            // Message de succès
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setContentText("Crédit ajouté avec succès !");
            success.showAndWait();

            // 🔹 Redirection vers AfficherCredit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/credit/AfficherCredit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtNomClient.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Crédits");
            stage.show();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Le montant doit être un nombre valide !");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erreur lors de l'ajout du crédit !");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
