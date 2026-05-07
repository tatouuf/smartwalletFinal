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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;

import java.util.Objects;

public class ModifierAssurance {

    @FXML private TextField txtNomAssurance;
    @FXML private ComboBox<TypeAssurance> txtTypeAssurance;
    @FXML private TextField txtPrix;
    @FXML private TextField txtDuree;
    @FXML private TextArea txtDescription;
    @FXML private TextArea txtConditions;
    @FXML private ComboBox<Statut> txtStatut;
    @FXML private Button cancelassurance;
    @FXML private Button btnEnregistrer;
    @FXML private ImageView imgLogoAssurance;
    @FXML private Label insuranceIdLabel;
    @FXML private Label typePreview;
    @FXML private Label statusPreview;

    private Assurances assurance;
    private ServiceAssurances serviceAssurances = new ServiceAssurances();

    @FXML
    public void initialize() {
        txtTypeAssurance.getItems().addAll(TypeAssurance.values());
        txtStatut.getItems().addAll(Statut.values());
        loadLogo();

        // Listeners pour les aperçus
        txtTypeAssurance.valueProperty().addListener((obs, old, newVal) -> updateTypePreview());
        txtStatut.valueProperty().addListener((obs, old, newVal) -> updateStatusPreview());
    }

    @FXML
    private void updateTypePreview() {
        if (typePreview != null && txtTypeAssurance.getValue() != null) {
            typePreview.setText(txtTypeAssurance.getValue().name());
        }
    }

    @FXML
    private void updateStatusPreview() {
        if (statusPreview != null && txtStatut.getValue() != null) {
            statusPreview.setText(txtStatut.getValue().name());
        }
    }

    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assurance/AfficherAssurance.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cancelassurance.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Assurances");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner !");
        }
    }

    private void loadLogo() {
        try {
            Image logo = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/icons/logoservices.png")));
            imgLogoAssurance.setImage(logo);
            Circle clip = new Circle(90, 90, 90);
            imgLogoAssurance.setClip(clip);
        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    public void setAssurance(Assurances a) {
        this.assurance = a;

        if (insuranceIdLabel != null) {
            insuranceIdLabel.setText(String.valueOf(a.getId()));
        }

        txtNomAssurance.setText(a.getNomAssurance());
        txtTypeAssurance.setValue(a.getTypeAssurance());
        txtPrix.setText(String.valueOf(a.getPrix()));
        txtDuree.setText(String.valueOf(a.getDureeMois()));
        txtDescription.setText(a.getDescription());
        txtConditions.setText(a.getConditions());
        txtStatut.setValue(a.getStatut());

        updateTypePreview();
        updateStatusPreview();
    }

    @FXML
    public void enregistrerModifications() {
        try {
            if (txtNomAssurance.getText().isEmpty() ||
                    txtTypeAssurance.getValue() == null ||
                    txtPrix.getText().isEmpty() ||
                    txtDuree.getText().isEmpty() ||
                    txtStatut.getValue() == null) {

                showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Remplir tous les champs !");
                return;
            }

            assurance.setNomAssurance(txtNomAssurance.getText());
            assurance.setTypeAssurance(txtTypeAssurance.getValue());
            assurance.setPrix(Float.parseFloat(txtPrix.getText()));
            assurance.setDureeMois(Integer.parseInt(txtDuree.getText()));
            assurance.setDescription(txtDescription.getText());
            assurance.setConditions(txtConditions.getText());
            assurance.setStatut(txtStatut.getValue());

            serviceAssurances.modifierAssurance(assurance);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Assurance modifiée avec succès !");

            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Prix ou durée invalide !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier l'assurance !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}