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
import services.service.ServiceServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AjouterService {

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

    @FXML
    private TextField imagajt;

    private File selectedFile;

    @FXML
    public void initialize() {
        // Initialiser les ComboBox
        typeserviceservice.getItems().addAll(TypeService.values());
        statutservice.getItems().addAll(Statut.values());
    }

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
                // Copier l'image vers ton dossier D:\imageprojet
                File destDir = new File("D:\\imageprojet");
                if (!destDir.exists()) destDir.mkdirs(); // créer le dossier si inexistant

                File dest = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imagajt.setText(dest.getAbsolutePath()); // chemin complet à stocker
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Erreur lors de la copie de l'image !");
                alert.show();
            }
        }
    }

    @FXML
    private void onButtonClicked() {
        try {
            // Vérification des champs obligatoires
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

            // Convertir le prix
            float prix = Float.parseFloat(prixservice.getText());

            // 🔹 Utilisateur connecté (à adapter selon ta session)
            User currentUser = new User(); // Remplace par ton SessionManager si nécessaire

            // 🔹 Chemin complet de l'image
            String cheminImage = imagajt.getText(); // contient le chemin D:\imageprojet\image.jpg

            // Créer le service
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

            // Ajouter le service à la base
            ServiceServices ss = new ServiceServices();
            ss.ajouterServices(s);

            // Message succès
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setContentText("Service ajouté avec succès !");
            success.showAndWait();

            // Redirection vers AfficherService
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/services/AfficherService.fxml"));
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
}
