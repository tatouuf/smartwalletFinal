package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import entities.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import services.service.ServiceServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AjouterService {

    // ================= IMAGE LOGO =================

    @FXML
    private ImageView imgLogoService;

    // ================= FORM FIELDS =================

    @FXML
    private TextField prixservice;

    @FXML
    private TextField typeservice;

    @FXML
    private ComboBox<TypeService> typeserviceservice;

    @FXML
    private ComboBox<Statut> statutservice;

    @FXML
    private TextField localisationservice;

    @FXML
    private TextField adresseservice;

    @FXML
    private TextField descriptionservice;
    @FXML private Button btnAjouterserrr;
    @FXML
    private TextField imagajt;

    private File selectedFile;

    // ================= INITIALIZE =================
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainalc/MainALC.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAjouterserrr.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu principal !");
        }
    }
    @FXML
    public void initialize() {

        // 🔹 Initialiser les ComboBox
        typeserviceservice.getItems().addAll(TypeService.values());
        statutservice.getItems().addAll(Statut.values());

        // 🔹 Charger le logo
        imgLogoService.setImage(
                new Image(getClass().getResourceAsStream("/icons/logoservices.png"))
        );

        // 🔹 Rendre le logo circulaire
        double radius = 150; // moitié de fitWidth (300)
        Circle clip = new Circle(150, 150, radius);
        imgLogoService.setClip(clip);
    }

    // ================= CHOISIR IMAGE =================

    @FXML
    private void choisirImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) imagajt.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {

                File destDir = new File("D:\\imageprojet");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                File dest = new File(destDir, selectedFile.getName());

                Files.copy(
                        selectedFile.toPath(),
                        dest.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                imagajt.setText(dest.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Erreur lors de la copie de l'image !");
                alert.show();
            }
        }
    }

    // ================= AJOUT SERVICE =================

    @FXML
    private void onButtonClicked() {

        try {

            if (prixservice.getText().isEmpty()
                    || typeserviceservice.getValue() == null
                    || statutservice.getValue() == null
                    || localisationservice.getText().isEmpty()
                    || adresseservice.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Veuillez remplir tous les champs obligatoires !");
                alert.show();
                return;
            }

            float prix = Float.parseFloat(prixservice.getText());

            // ⚠ À remplacer par ton vrai utilisateur connecté
            User currentUser = new User();

            String cheminImage = imagajt.getText();

            Services s = new Services(
                    prix,
                    localisationservice.getText(),
                    adresseservice.getText(),
                    descriptionservice.getText(),
                    typeservice.getText(),
                    statutservice.getValue(),
                    typeserviceservice.getValue(),
                    currentUser,
                    cheminImage
            );

            ServiceServices ss = new ServiceServices();
            ss.ajouterServices(s);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setContentText("Service ajouté avec succès !");
            success.showAndWait();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/services/AfficherService.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) prixservice.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Services");
            stage.show();

        } catch (NumberFormatException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Le prix doit être un nombre valide !");
            alert.show();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erreur lors de l'ajout du service !");
            alert.show();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
