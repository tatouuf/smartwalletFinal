package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.service.ServiceServices;

import java.sql.SQLException;

public class ModifierService {

    @FXML
    private TextField txtPrix;

    @FXML
    private TextField txtType;

    @FXML
    private TextField txtStatut;

    @FXML
    private TextField txtLocalisation;

    @FXML
    private TextField txtAdresse;

    @FXML
    private TextField txtTypeService;

    @FXML
    private Button btnEnregistrer;

    private Services service; // le service à modifier
    private ServiceServices serviceServices = new ServiceServices();

    // Préremplir les champs avec le service sélectionné
    public void setService(Services s) {
        this.service = s;

        txtPrix.setText(String.valueOf(s.getPrix()));
        txtType.setText(s.getType());
        txtStatut.setText(s.getStatut() != null ? s.getStatut().name() : "");
        txtLocalisation.setText(s.getLocalisation());
        txtAdresse.setText(s.getAdresse());
        txtTypeService.setText(s.getTypeService() != null ? s.getTypeService().name() : "");
    }

    // Action du bouton Enregistrer
    @FXML
    public void enregistrerModifications() {
        try {
            service.setPrix((float) Double.parseDouble(txtPrix.getText()));
            service.setType(txtType.getText());
            service.setLocalisation(txtLocalisation.getText());
            service.setAdresse(txtAdresse.getText());

            // Conversion en enum
            if (!txtStatut.getText().isEmpty()) {
                service.setStatut(Statut.valueOf(txtStatut.getText()));
            } else {
                service.setStatut(null);
            }

            if (!txtTypeService.getText().isEmpty()) {
                service.setTypeService(TypeService.valueOf(txtTypeService.getText()));
            } else {
                service.setTypeService(null);
            }

            serviceServices.modifierServices(service);

            // Fermer la fenêtre après modification
            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.close();

            System.out.println("Service modifié avec succès !");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Le prix doit être un nombre !");
        } catch (IllegalArgumentException e) {
            System.out.println("Le statut ou le type de service est invalide !");
        }
    }
}
