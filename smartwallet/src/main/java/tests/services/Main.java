package tests.services;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import entities.user.User;
import services.service.ServiceServices;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ServiceServices serviceServices = new ServiceServices();
        Scanner sc = new Scanner(System.in);

        // 🔹 Création d’utilisateurs pour tester
        User u1 = new User(1, "Itaf", "Tattou", "itaf@example.com");
        User u2 = new User(2, "Ali", "Ben Ali", "ali@example.com");

        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== MENU SERVICES =====");
            System.out.println("1. Ajouter un service");
            System.out.println("2. Modifier un service");
            System.out.println("3. Supprimer un service");
            System.out.println("4. Afficher tous les services");
            System.out.println("5. Quitter");
            System.out.print("Choisissez une option : ");

            int choix = sc.nextInt();
            sc.nextLine(); // consommer le \n

            try {
                switch (choix) {
                    case 1 -> {
                        // ===== AJOUTER =====
                        System.out.print("Prix : ");
                        float prix = sc.nextFloat();
                        sc.nextLine();

                        System.out.print("Description : ");
                        String desc = sc.nextLine();

                        System.out.print("Type : ");
                        String type = sc.nextLine();

                        System.out.print("Statut (DISPONIBLE/NON_DISPONIBLE) : ");
                        Statut statut = Statut.valueOf(sc.nextLine().toUpperCase());

                        System.out.print("Localisation (lat,lon) : ");
                        String loc = sc.nextLine().trim();

                        // Vérification format lat,lon
                        if (!loc.matches("\\d+(\\.\\d+)?,\\d+(\\.\\d+)?")) {
                            System.out.println("Erreur : format invalide. Exemple attendu : 36.8065,10.1815");
                            break;
                        }

                        System.out.print("Adresse : ");
                        String adresse = sc.nextLine();

                        System.out.print("Type de service (maison, voiture, etc.) : ");
                        TypeService typeService = TypeService.valueOf(sc.nextLine().toLowerCase());

                        Services s = new Services(prix, desc, type, statut, u1, loc, adresse, typeService);
                        serviceServices.ajouterServices(s);
                    }

                    case 2 -> {
                        // ===== MODIFIER =====
                        System.out.print("ID du service à modifier : ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Prix : ");
                        float prix = sc.nextFloat();
                        sc.nextLine();

                        System.out.print("Description : ");
                        String desc = sc.nextLine();

                        System.out.print("Type : ");
                        String type = sc.nextLine();

                        System.out.print("Statut (DISPONIBLE/NON_DISPONIBLE) : ");
                        Statut statut = Statut.valueOf(sc.nextLine().toUpperCase());

                        System.out.print("Localisation (lat,lon) : ");
                        String loc = sc.nextLine().trim();
                        if (!loc.matches("\\d+(\\.\\d+)?,\\d+(\\.\\d+)?")) {
                            System.out.println("Erreur : format invalide. Exemple attendu : 36.8065,10.1815");
                            break;
                        }

                        System.out.print("Adresse : ");
                        String adresse = sc.nextLine();

                        System.out.print("Type de service (maison, voiture, etc.) : ");
                        TypeService typeService = TypeService.valueOf(sc.nextLine().toLowerCase());

                        Services s = new Services(id, prix, desc, type, statut, u1, loc, adresse, typeService);
                        serviceServices.modifierServices(s);
                    }

                    case 3 -> {
                        // ===== SUPPRIMER =====
                        System.out.print("ID du service à supprimer : ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        Services s = new Services();
                        s.setId(id);
                        serviceServices.supprimerServices(s);
                    }

                    case 4 -> {
                        // ===== AFFICHER =====
                        List<Services> services = serviceServices.recupererServices();
                        System.out.println("===== LISTE DES SERVICES =====");
                        if (services.isEmpty()) {
                            System.out.println("Aucun service trouvé.");
                        } else {
                            services.forEach(System.out::println);
                        }
                    }

                    case 5 -> {
                        exit = true;
                        System.out.println("Au revoir !");
                    }

                    default -> System.out.println("Option invalide ! Veuillez choisir entre 1 et 5.");
                }
            } catch (SQLException e) {
                System.out.println("Erreur SQL : " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Erreur : valeur invalide pour Statut ou TypeService.");
            }
        }

        sc.close();
    }
}
