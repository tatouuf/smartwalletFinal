package controller.assurance;

import entities.assurances.Assurances;
import entities.assurances.Statut;
import entities.assurances.TypeAssurance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;

public class ModifierAssurance {

    @FXML
    private TextField txtNomAssurance;

    @FXML
    private ComboBox<TypeAssurance> txtTypeAssurance;

    @FXML
    private TextField txtPrix;

    @FXML
    private TextField txtDuree;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextArea txtConditions;

    @FXML
    private ComboBox<Statut> txtStatut;

    @FXML
    private Button btnEnregistrer;

    private Assurances assurance;
    private ServiceAssurances serviceAssurances = new ServiceAssurances();

    @FXML
    public void initialize() {
        txtTypeAssurance.getItems().addAll(TypeAssurance.values());
        txtStatut.getItems().addAll(Statut.values());
    }

    public void setAssurance(Assurances a) {
        this.assurance = a;

        txtNomAssurance.setText(a.getNomAssurance());
        txtTypeAssurance.setValue(a.getTypeAssurance());
        txtPrix.setText(String.valueOf(a.getPrix()));
        txtDuree.setText(String.valueOf(a.getDureeMois()));
        txtDescription.setText(a.getDescription());
        txtConditions.setText(a.getConditions());
        txtStatut.setValue(a.getStatut());
    }

    @FXML
    public void enregistrerModifications() {

        try {
            assurance.setNomAssurance(txtNomAssurance.getText());
            assurance.setTypeAssurance(txtTypeAssurance.getValue());
            assurance.setPrix(Float.parseFloat(txtPrix.getText()));
            assurance.setDureeMois(Integer.parseInt(txtDuree.getText()));
            assurance.setDescription(txtDescription.getText());
            assurance.setConditions(txtConditions.getText());
            assurance.setStatut(txtStatut.getValue());

            serviceAssurances.modifierAssurance(assurance);

            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
