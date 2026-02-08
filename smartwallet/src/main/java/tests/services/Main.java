package tests.services;

import entities.service.Services;
import entities.service.Statut; // Statut pour Services
import entities.service.TypeService;
import entities.assurances.Assurances;
import entities.assurances.TypeAssurance;
import entities.user.User;
import services.service.ServiceServices;
import services.assurances.ServiceAssurances;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ServiceServices serviceServices = new ServiceServices();
        ServiceAssurances serviceAssurances = new ServiceAssurances();
        Scanner sc = new Scanner(System.in);

        // Création d’utilisateur pour tester
        User u1 = new User(1, "Itaf", "Tattou", "itaf@example.com");

        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1. Ajouter un service");
            System.out.println("2. Modifier un service");
            System.out.println("3. Supprimer un service");
            System.out.println("4. Afficher un service");
            System.out.println("5. Quitter");
            System.out.print("Choisissez une option : ");
            int choix = readInt(sc, "");

            try {
                switch (choix) {
                    case 1 -> ajouter(sc, serviceServices, serviceAssurances, u1);
                    case 2 -> modifier(sc, serviceServices, serviceAssurances, u1);
                    case 3 -> supprimer(sc, serviceServices, serviceAssurances);
                    case 4 -> afficher(sc, serviceServices, serviceAssurances);
                    case 5 -> {
                        exit = true;
                        System.out.println("Au revoir !");
                    }
                    default -> System.out.println("Option invalide !");
                }
            } catch (SQLException e) {
                System.out.println("Erreur SQL : " + e.getMessage());
            }
        }

        sc.close();
    }

    // ===================== AJOUTER =====================
    private static void ajouter(Scanner sc, ServiceServices serviceServices, ServiceAssurances serviceAssurances, User u1) throws SQLException {
        System.out.println("\n--- AJOUTER ---");
        System.out.println("1. Location Voiture");
        System.out.println("2. Location Maison");
        System.out.println("3. Assurance");
        System.out.print("Choisissez une option : ");
        int ajout = readInt(sc, "");

        switch (ajout) {
            case 1, 2 -> {
                float prix = readFloat(sc, "Prix : ");
                System.out.print("Description : ");
                String desc = sc.nextLine();
                System.out.print("Type : ");
                String type = sc.nextLine();
                Statut statutService = readStatutService(sc);
                String loc = readLocalisation(sc);
                System.out.print("Adresse : ");
                String adresse = sc.nextLine();
                TypeService typeService = readTypeService(sc);

                Services s = new Services(prix, desc, type, statutService, u1, loc, adresse, typeService);
                serviceServices.ajouterServices(s);
                System.out.println("Service ajouté avec succès !");
            }
            case 3 -> {
                System.out.print("Nom assurance : ");
                String nom = sc.nextLine();
                TypeAssurance typeAssurance = readTypeAssurance(sc);
                System.out.print("Description : ");
                String desc = sc.nextLine();
                float prix = readFloat(sc, "Prix : ");
                int duree = readInt(sc, "Durée (mois) : ");
                System.out.print("Conditions : ");
                String cond = sc.nextLine();
                entities.assurances.Statut statutAssurance = readStatutAssurance(sc);

                Assurances a = new Assurances(nom, typeAssurance, desc, prix, duree, cond, statutAssurance);
                serviceAssurances.ajouterAssurance(a);
                System.out.println("Assurance ajoutée avec succès !");
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== MODIFIER =====================
    private static void modifier(Scanner sc, ServiceServices serviceServices, ServiceAssurances serviceAssurances, User u1) throws SQLException {
        System.out.println("\n--- MODIFIER ---");
        System.out.println("1. Location Voiture/Maison");
        System.out.println("2. Assurance");
        System.out.print("Choisissez une option : ");
        int modif = readInt(sc, "");

        switch (modif) {
            case 1 -> {
                int id = readInt(sc, "ID du service : ");
                float prix = readFloat(sc, "Prix : ");
                System.out.print("Description : ");
                String desc = sc.nextLine();
                System.out.print("Type : ");
                String type = sc.nextLine();
                Statut statutService = readStatutService(sc);
                String loc = readLocalisation(sc);
                System.out.print("Adresse : ");
                String adresse = sc.nextLine();
                TypeService typeService = readTypeService(sc);

                Services s = new Services(id, prix, desc, type, statutService, u1, loc, adresse, typeService);
                serviceServices.modifierServices(s);
                System.out.println("Service modifié avec succès !");
            }
            case 2 -> {
                int id = readInt(sc, "ID de l'assurance : ");
                System.out.print("Nom assurance : ");
                String nom = sc.nextLine();
                TypeAssurance typeAssurance = readTypeAssurance(sc);
                System.out.print("Description : ");
                String desc = sc.nextLine();
                float prix = readFloat(sc, "Prix : ");
                int duree = readInt(sc, "Durée (mois) : ");
                System.out.print("Conditions : ");
                String cond = sc.nextLine();
                entities.assurances.Statut statutAssurance = readStatutAssurance(sc);

                Assurances a = new Assurances(nom, typeAssurance, desc, prix, duree, cond, statutAssurance);
                a.setId(id);
                serviceAssurances.modifierAssurance(a);
                System.out.println("Assurance modifiée avec succès !");
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== SUPPRIMER =====================
    private static void supprimer(Scanner sc, ServiceServices serviceServices, ServiceAssurances serviceAssurances) throws SQLException {
        System.out.println("\n--- SUPPRIMER ---");
        System.out.println("1. Location Voiture/Maison");
        System.out.println("2. Assurance");
        System.out.print("Choisissez une option : ");
        int suppr = readInt(sc, "");

        switch (suppr) {
            case 1 -> {
                int id = readInt(sc, "ID du service : ");
                Services s = new Services();
                s.setId(id);
                serviceServices.supprimerServices(s);
                System.out.println("Service supprimé !");
            }
            case 2 -> {
                int id = readInt(sc, "ID de l'assurance : ");
                Assurances a = new Assurances();
                a.setId(id);
                serviceAssurances.supprimerAssurance(a);
                System.out.println("Assurance supprimée !");
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== AFFICHER =====================
    private static void afficher(Scanner sc, ServiceServices serviceServices, ServiceAssurances serviceAssurances) throws SQLException {
        System.out.println("\n--- AFFICHER ---");
        System.out.println("1. Location Voiture/Maison");
        System.out.println("2. Assurance");
        System.out.print("Choisissez une option : ");
        int aff = readInt(sc, "");

        switch (aff) {
            case 1 -> {
                List<Services> services = serviceServices.recupererServices();
                if (services.isEmpty()) System.out.println("Aucun service trouvé.");
                else services.forEach(System.out::println);
            }
            case 2 -> {
                List<Assurances> assurances = serviceAssurances.recupererAssurance();
                if (assurances.isEmpty()) System.out.println("Aucune assurance trouvée.");
                else assurances.forEach(System.out::println);
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== MÉTHODES DE SAISIE =====================
    private static float readFloat(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Float.parseFloat(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Erreur : veuillez entrer un nombre valide.");
            }
        }
    }

    private static int readInt(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Erreur : veuillez entrer un entier valide.");
            }
        }
    }

    private static Statut readStatutService(Scanner sc) {
        while (true) {
            System.out.print("Statut (DISPONIBLE/NON_DISPONIBLE) : ");
            String input = sc.nextLine().toUpperCase();
            try {
                return Statut.valueOf(input);
            } catch (Exception e) {
                System.out.println("Statut invalide. Reessayez.");
            }
        }
    }

    private static entities.assurances.Statut readStatutAssurance(Scanner sc) {
        while (true) {
            System.out.print("Statut (ACTIVE/INACTIVE) : ");
            String input = sc.nextLine().toUpperCase();
            try {
                return entities.assurances.Statut.valueOf(input);
            } catch (Exception e) {
                System.out.println("Statut invalide. Reessayez.");
            }
        }
    }

    private static TypeService readTypeService(Scanner sc) {
        while (true) {
            System.out.print("Type de service (voiture/maison) : ");
            String input = sc.nextLine().toLowerCase();
            try {
                return TypeService.valueOf(input); // correspond à enum voiture/maison
            } catch (Exception e) {
                System.out.println("TypeService invalide. Reessayez (voiture/maison).");
            }
        }
    }

    private static TypeAssurance readTypeAssurance(Scanner sc) {
        while (true) {
            System.out.print("Type d'assurance (AUTO, SANTE, MAISON, VIE, HABITATION, AUTRE) : ");
            String input = sc.nextLine().toUpperCase();
            try {
                return TypeAssurance.valueOf(input); // correspond à enum majuscule
            } catch (Exception e) {
                System.out.println("TypeAssurance invalide. Reessayez.");
            }
        }
    }

    private static String readLocalisation(Scanner sc) {
        while (true) {
            System.out.print("Localisation (lat,lon) : ");
            String loc = sc.nextLine().trim();
            if (loc.matches("-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?")) return loc;
            else System.out.println("Format invalide. Exemple : 36.8065,10.1815");
        }
    }
}
