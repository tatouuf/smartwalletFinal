package controller.assurance;

import entities.assurances.Assurances;
import entities.assurances.Statut;
import entities.assurances.TypeAssurance;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;

import java.util.Objects;

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
    private Button cancelassurance;
    @FXML
    private Button btnEnregistrer;

    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assurance/AfficherAssurance.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cancelassurance.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu principal !");
        }
    }
    @FXML
    private ImageView imgLogoAssurance; // 🔥 LOGO

    private Assurances assurance;
    private ServiceAssurances serviceAssurances = new ServiceAssurances();

    @FXML
    public void initialize() {
        txtTypeAssurance.getItems().addAll(TypeAssurance.values());
        txtStatut.getItems().addAll(Statut.values());
        loadLogo(); // ✅ Charger le logo
    }

    // ================== LOGO ==================
    private void loadLogo() {
        try {
            Image logo = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream("/icons/logoservices.png")
                    )
            );
            imgLogoAssurance.setImage(logo);
        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    // ================== MODIFIER ==================
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
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
