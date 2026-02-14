package controller.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.credit.ServiceCredit;

import java.sql.SQLException;
import java.time.LocalDate;

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
    private Button btnEnregistrer;

    private Credit credit;
    private ServiceCredit serviceCredit = new ServiceCredit();

    public void setCredit(Credit c) {
        this.credit = c;

        txtNomClient.setText(c.getNomClient());
        txtMontant.setText(String.valueOf(c.getMontant()));
        dateCredit.setValue(c.getDateCredit());
        txtDescription.setText(c.getDescription());
        comboStatut.getItems().addAll(StatutCredit.values());
        comboStatut.setValue(c.getStatut());
    }

    @FXML
    public void enregistrerModifications() {
        try {
            credit.setNomClient(txtNomClient.getText());
            credit.setMontant(Float.parseFloat(txtMontant.getText()));
            credit.setDateCredit(dateCredit.getValue());
            credit.setDescription(txtDescription.getText());
            credit.setStatut(comboStatut.getValue());

            serviceCredit.modifierCredit(credit);

            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.close();

            System.out.println("Crédit modifié avec succès !");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Le montant doit être un nombre !");
        }
    }
}

