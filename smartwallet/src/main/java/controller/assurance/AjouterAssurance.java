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

public class AjouterAssurance {

    @FXML
    private TextField nomAssuranceField;

    @FXML
    private ComboBox<TypeAssurance> typeAssuranceCombo;

    @FXML
    private TextField prixField;

    @FXML
    private TextField dureeField;

    @FXML
    private TextArea descriptionField;
    @FXML private Button retouritaf;
    @FXML
    private TextArea conditionsField;

    @FXML
    private ComboBox<Statut> statutCombo;

    @FXML
    private ImageView imgLogoAssurance; // 🔥 LOGO

    private ServiceAssurances serviceAssurances = new ServiceAssurances();

    @FXML
    public void initialize() {
        typeAssuranceCombo.getItems().addAll(TypeAssurance.values());
        statutCombo.getItems().addAll(Statut.values());
        loadLogo(); // ✅ Charger le logo
    }
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainalc/MainALC.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) retouritaf.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu principal !");
        }
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

    // ================== AJOUT ==================
    @FXML
    private void ajouterAssurance() {
        try {
            if (nomAssuranceField.getText().isEmpty()
                    || typeAssuranceCombo.getValue() == null
                    || prixField.getText().isEmpty()
                    || dureeField.getText().isEmpty()
                    || statutCombo.getValue() == null) {

                new Alert(Alert.AlertType.WARNING, "Remplir tous les champs !").show();
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

            new Alert(Alert.AlertType.INFORMATION, "Assurance ajoutée !").showAndWait();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/assurance/AfficherAssurance.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) nomAssuranceField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Assurances");

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Prix ou durée invalide !").show();
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
