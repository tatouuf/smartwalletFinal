package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.service.ServiceServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ModifierService {

    @FXML
    private TextField txtPrix;

    @FXML
    private ComboBox<TypeService> txtTypeServiceCategory;

    @FXML
    private TextField txtType;

    @FXML
    private ComboBox<Statut> txtStatut;

    @FXML
    private TextField txtLocalisation;

    @FXML
    private TextField txtAdresse;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtImage;

    @FXML
    private Button btnBrowseImage;

    @FXML
    private Button btnSave;

    @FXML
    private ImageView imgLogoModifier;

    private Services currentService;
    private File selectedFile;

    @FXML
    public void initialize() {
        // Charger le logo
        if (imgLogoModifier != null) {
            imgLogoModifier.setImage(
                    new Image(getClass().getResourceAsStream("/icons/logoservices.png"))
            );
            Circle clip = new Circle(150, 150, 150);
            imgLogoModifier.setClip(clip);
        }

        // Initialiser les ComboBox
        txtTypeServiceCategory.getItems().addAll(TypeService.values());
        txtStatut.getItems().addAll(Statut.values());
    }

    // Méthode pour remplir les champs avec un service existant
    public void setService(Services s) {
        this.currentService = s;

        txtPrix.setText(String.valueOf(s.getPrix()));
        txtType.setText(s.getType());
        txtLocalisation.setText(s.getLocalisation());
        txtAdresse.setText(s.getAdresse());
        txtDescription.setText(s.getDescription());
        txtImage.setText(s.getImage());

        txtTypeServiceCategory.setValue(s.getTypeService());
        txtStatut.setValue(s.getStatut());
    }

    // Bouton Browse pour choisir une image
    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) txtImage.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                File destDir = new File("D:\\imageprojet");
                if (!destDir.exists()) destDir.mkdirs();

                File dest = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                txtImage.setText(dest.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error copying image!");
                alert.show();
            }
        }
    }

    // Bouton Save pour enregistrer les modifications
    @FXML
    private void enregistrerModifications() {
        try {
            if (txtPrix.getText().isEmpty() || txtType.getText().isEmpty() ||
                    txtLocalisation.getText().isEmpty() || txtAdresse.getText().isEmpty() ||
                    txtTypeServiceCategory.getValue() == null || txtStatut.getValue() == null) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Please fill all mandatory fields!");
                alert.show();
                return;
            }

            float prix = Float.parseFloat(txtPrix.getText());

            String cheminImage = txtImage.getText();

            currentService.setPrix(prix);
            currentService.setType(txtType.getText());
            currentService.setLocalisation(txtLocalisation.getText());
            currentService.setAdresse(txtAdresse.getText());
            currentService.setDescription(txtDescription.getText());
            currentService.setImage(cheminImage);
            currentService.setTypeService(txtTypeServiceCategory.getValue());
            currentService.setStatut(txtStatut.getValue());

            ServiceServices ss = new ServiceServices();
            ss.modifierServices(currentService);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setContentText("Service updated successfully!");
            success.showAndWait();

            // Fermer la fenêtre après sauvegarde
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Price must be a valid number!");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error updating the service!");
            alert.show();
        }
    }
}
