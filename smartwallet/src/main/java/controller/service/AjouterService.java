package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
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
    private ComboBox<String> typeserviceservice; // ComboBox pour TypeService

    @FXML
    public void initialize() {
        // Initialiser le ComboBox avec les valeurs de l'enum TypeService
        for (TypeService ts : TypeService.values()) {
            typeserviceservice.getItems().add(ts.name());
        }
    }

    @FXML
    void onButtonClicked(ActionEvent event) {
        try {
            // Vérifier que tous les champs sont remplis
            if (prixservice.getText().isEmpty() ||
                    localisationservice.getText().isEmpty() ||
                    adresseservice.getText().isEmpty() ||
                    descriptionservice.getText().isEmpty() ||
                    typeserviceservice.getValue() == null) {

                System.out.println("Veuillez remplir tous les champs.");
                return;
            }

            // Récupération des valeurs depuis les champs
            float prix = Float.parseFloat(prixservice.getText());
            String localisation = localisationservice.getText();
            String adresse = adresseservice.getText();
            String description = descriptionservice.getText();
            String typeServiceSelected = typeserviceservice.getValue();

            // Conversion du String en TypeService
            TypeService typeServiceEnum = TypeService.valueOf(typeServiceSelected);

            // Création du service
            Services s = new Services();
            s.setPrix(prix);
            s.setDescription(description);
            s.setLocalisation(localisation);
            s.setAdresse(adresse);
            s.setTypeService(typeServiceEnum);
            s.setStatut(Statut.DISPONIBLE);

            // Appel du service pour l'ajout
            ServiceServices ss = new ServiceServices();
            ss.ajouterServices(s); // ou ss.ajouterService(s) selon ton interface

            // 🔹 Passer automatiquement à la page d'affichage
            MainFXML.showAfficherService();

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Erreur de format pour le prix.");
        } catch (IllegalArgumentException e) {
            System.out.println("TypeService invalide.");
        }
    }
}
