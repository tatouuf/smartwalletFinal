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

public class AjouterAssurance {

    @FXML private TextField nomAssuranceField;
    @FXML private ComboBox<TypeAssurance> typeAssuranceCombo;
    @FXML private TextField prixField;
    @FXML private TextField dureeField;
    @FXML private TextArea descriptionField;
    @FXML private TextArea conditionsField;
    @FXML private ComboBox<Statut> statutCombo;
    @FXML private Button retouritaf;
    @FXML private ImageView imgLogoAssurance;
    @FXML private Label typePreview;
    @FXML private Label statusPreview;

    private ServiceAssurances serviceAssurances = new ServiceAssurances();

    @FXML
    public void initialize() {
        typeAssuranceCombo.getItems().addAll(TypeAssurance.values());
        statutCombo.getItems().addAll(Statut.values());
        loadLogo();

        // Ajouter listeners pour les aperçus
        typeAssuranceCombo.valueProperty().addListener((obs, old, newVal) -> updateTypePreview());
        statutCombo.valueProperty().addListener((obs, old, newVal) -> updateStatusPreview());

        // Valeurs par défaut
        typeAssuranceCombo.setValue(TypeAssurance.VOITURE);
        statutCombo.setValue(Statut.ACTIVE);
    }

    @FXML
    private void updateTypePreview() {
        if (typePreview != null && typeAssuranceCombo.getValue() != null) {
            typePreview.setText(typeAssuranceCombo.getValue().name());
        }
    }

    @FXML
    private void updateStatusPreview() {
        if (statusPreview != null && statutCombo.getValue() != null) {
            statusPreview.setText(statutCombo.getValue().name());
        }
    }

    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assurance/AfficherAssurance.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retouritaf.getScene().getWindow();
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

    @FXML
    private void ajouterAssurance() {
        try {
            if (nomAssuranceField.getText().isEmpty() ||
                    typeAssuranceCombo.getValue() == null ||
                    prixField.getText().isEmpty() ||
                    dureeField.getText().isEmpty() ||
                    statutCombo.getValue() == null) {

                showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Remplir tous les champs !");
                return;
            }

            float prix = Float.parseFloat(prixField.getText());
            int duree = Integer.parseInt(dureeField.getText());

            Assurances a = new Assurances(
                    nomAssuranceField.getText(),
                    typeAssuranceCombo.getValue(),
                    descriptionField.getText(),
                    prix,
                    duree,
                    conditionsField.getText(),
                    statutCombo.getValue()
            );

            serviceAssurances.ajouterAssurance(a);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Assurance ajoutée avec succès !");

            // Retour à l'affichage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assurance/AfficherAssurance.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomAssuranceField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Assurances");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Prix ou durée invalide !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'assurance !");
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