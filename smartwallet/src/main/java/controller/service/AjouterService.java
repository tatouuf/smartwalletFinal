package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import entities.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import services.service.ServiceServices;
import tests.services.MainFXML;

import java.sql.SQLException;

public class AjouterService {

    @FXML
    private TextField prixservice;
    @FXML
    private TextField localisationservice;
    @FXML
    private TextField adresseservice;
    @FXML
    private TextField descriptionservice;
    @FXML
    private TextField typeservice; // texte libre
    @FXML
    private ComboBox<String> typeserviceservice; // enum
    @FXML
    private ComboBox<String> statutservice;

    @FXML
    public void initialize() {
        // Remplir ComboBox TypeService (enum)
        typeserviceservice.getItems().clear();
        for (TypeService ts : TypeService.values()) {
            typeserviceservice.getItems().add(ts.name());
        }
        typeserviceservice.getSelectionModel().selectFirst();

        // Remplir ComboBox Statut
        statutservice.getItems().clear();
        for (Statut st : Statut.values()) {
            statutservice.getItems().add(st.name());
        }
        statutservice.getSelectionModel().selectFirst();
    }

    // Méthode pour récupérer l'utilisateur "connecté" ou par défaut
    private User getCurrentUser() {
        User user = new User();
        user.setId(1);
        user.setNom("Tattou");
        user.setPrenom("Itaf");
        return user;
    }

    @FXML
    void onButtonClicked(ActionEvent event) {
        try {
            // 🔹 Vérifier l'utilisateur
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                System.out.println("Erreur : Aucun utilisateur disponible !");
                return;
            }

            // 🔹 Vérifier que tous les champs sont remplis
            if (prixservice.getText().isEmpty() ||
                    localisationservice.getText().isEmpty() ||
                    adresseservice.getText().isEmpty() ||
                    descriptionservice.getText().isEmpty() ||
                    typeservice.getText().isEmpty() ||        // texte libre
                    typeserviceservice.getValue() == null || // enum
                    statutservice.getValue() == null) {      // statut

                System.out.println("Veuillez remplir tous les champs.");
                return;
            }

            // 🔹 Création de l'objet Services
            Services service = new Services();
            service.setPrix(Float.parseFloat(prixservice.getText().trim()));
            service.setLocalisation(localisationservice.getText().trim());
            service.setAdresse(adresseservice.getText().trim());
            service.setDescription(descriptionservice.getText().trim());

            // remplir type texte libre et TypeService enum
            service.setType(typeservice.getText().trim());
            service.setTypeService(TypeService.valueOf(typeserviceservice.getValue()));

            service.setStatut(Statut.valueOf(statutservice.getValue()));
            service.setUser(currentUser);

            // 🔹 Ajouter le service en base
            ServiceServices ss = new ServiceServices();
            ss.ajouterServices(service);

            System.out.println("Service ajouté avec succès ! ID=" + service.getId());

            // 🔹 Afficher le service dans la vue si MainFXML existe
            MainFXML.showAfficherService(service);

        } catch (NumberFormatException e) {
            System.out.println("Erreur : Le prix doit être un nombre valide.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
