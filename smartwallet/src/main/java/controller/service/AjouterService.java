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
            // Copier l'image vers ton dossier D:\imageprojet
            File dest = new File("D:\\imageprojet\\" + selectedFile.getName());
            selectedFile.renameTo(dest); // Ou Files.copy pour éviter les pertes
            imagajt.setText(dest.getAbsolutePath()); // chemin complet à stocker
        }
    }

    @FXML
    private void onButtonClicked() {
        try {
            if (prixservice.getText().isEmpty()
                    || typeserviceservice.getValue() == null
                    || statutservice.getValue() == null) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Veuillez remplir tous les champs obligatoires !");
                alert.show();
                return;
            }

            float prix = Float.parseFloat(prixservice.getText());

            // 🔹 Utilisateur connecté
            User currentUser = new User(); // Remplace par ton SessionManager

            // 🔹 Chemin complet de l'image
            String cheminImage = imagajt.getText(); // doit contenir D:\imageprojet\image.jpg

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
            success.show();

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
        }
    }
}
