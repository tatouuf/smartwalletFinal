package tests.services;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import entities.user.User;
import services.service.ServiceServices;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        ServiceServices serviceServices = new ServiceServices();

        // 🔹 Création d’utilisateurs
        User u1 = new User(1, "Itaf", "Tattou", "itaf@example.com");
        User u2 = new User(2, "Ali", "Ben Ali", "ali@example.com");

        // 🔹 Création d’un service sans ID (pour insertion)
        Services s1 = new Services(
                250.0f,
                "Maison moderne avec jardin",
                "Immobilier",
                Statut.DISPONIBLE,
                u1,   // objet User
                "36.8065,10.1815",
                "Tunis Centre",
                TypeService.maison
        );

        // 🔹 Service avec ID (pour modification/suppression)
        Services s2 = new Services(
                1,
                120.0f,
                "Voiture de location",
                "Transport",
                Statut.NON_DISPONIBLE,
                u2,   // objet User
                "35.8256,10.6084",
                "Sousse",
                TypeService.voiture
        );

        try {
            // ➕ Ajouter le service s1
            serviceServices.ajouterServices(s1);

            // ✏️ Modifier le service s2
            serviceServices.modifierServices(s2);

            // 🗑 Supprimer le service s2
            serviceServices.supprimerServices(s2);

            // 📋 Afficher tous les services
            System.out.println("Liste des services :");
            serviceServices.recupererServices().forEach(System.out::println);

            // 🔹 Exemple : accéder aux infos de l'utilisateur d’un service
            System.out.println("\nNom de l'utilisateur du service 1 : " + s1.getUser().getNom());

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
