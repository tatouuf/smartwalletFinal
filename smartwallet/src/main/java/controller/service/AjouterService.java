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
import utils.SessionManager;

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
    private ComboBox<String> typeserviceservice;
    @FXML
    private ComboBox<String> statutservice;

    @FXML
    public void initialize() {
        // Remplir ComboBox TypeService
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

    @FXML
    void onButtonClicked(ActionEvent event) {
        try {
            // 🔹 Vérifier que l'utilisateur est connecté
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                System.out.println("Erreur : Veuillez vous connecter d'abord !");
                return;
            }

            // 🔹 Vérifier les champs
            if (prixservice.getText().isEmpty() ||
                    localisationservice.getText().isEmpty() ||
                    adresseservice.getText().isEmpty() ||
                    descriptionservice.getText().isEmpty()) {
                System.out.println("Veuillez remplir tous les champs.");
                return;
            }

            // 🔹 Création du service avec l'utilisateur connecté
            Services service = new Services();
            service.setPrix(Float.parseFloat(prixservice.getText().trim()));
            service.setLocalisation(localisationservice.getText().trim());
            service.setAdresse(adresseservice.getText().trim());
            service.setDescription(descriptionservice.getText().trim());
            service.setTypeService(TypeService.valueOf(typeserviceservice.getValue()));
            service.setStatut(Statut.valueOf(statutservice.getValue()));
            service.setUser(currentUser);  // 👈 IMPORTANT: Associer l'utilisateur

            // 🔹 Ajout en base
            ServiceServices ss = new ServiceServices();
            ss.ajouterServices(service);

            System.out.println("Service ajouté avec succès !");
            MainFXML.showAfficherService(service);

        } catch (NumberFormatException e) {
            System.out.println("Erreur : Le prix doit être un nombre valide.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}