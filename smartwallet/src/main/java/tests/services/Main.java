package tests.services;

import entities.service.Services;
import entities.service.Statut;       // Statut pour Services
import entities.service.TypeService;

import entities.assurances.Assurances;
import entities.assurances.TypeAssurance;

import entities.credit.Credit;
import entities.credit.StatutCredit;

import entities.user.User;

import services.service.ServiceServices;
import services.assurances.ServiceAssurances;
import services.credit.ServiceCredit;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ServiceServices serviceServices = new ServiceServices();
        ServiceAssurances serviceAssurances = new ServiceAssurances();
        ServiceCredit serviceCredit = new ServiceCredit();
        Scanner sc = new Scanner(System.in);

        // Utilisateur par défaut
        User u1 = new User(1, "Tattou", "Itaf", "itaf@example.com");

        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1. Ajouter");
            System.out.println("2. Modifier");
            System.out.println("3. Supprimer");
            System.out.println("4. Afficher");
            System.out.println("5. Quitter");
            System.out.print("Choisissez une option : ");
            int choix = readInt(sc, "");

            try {
                switch (choix) {
                    case 1 -> ajouter(sc, serviceServices, serviceAssurances, serviceCredit, u1);
                    case 2 -> modifier(sc, serviceServices, serviceAssurances, serviceCredit, u1);
                    case 3 -> supprimer(sc, serviceServices, serviceAssurances, serviceCredit);
                    case 4 -> afficher(sc, serviceServices, serviceAssurances, serviceCredit);
                    case 5 -> {
                        exit = true;
                        System.out.println("Au revoir !");
                    }
                    default -> System.out.println("Option invalide !");
                }
            } catch (SQLException e) {
                System.out.println("Erreur SQL : " + e.getMessage());
                e.printStackTrace();
            }
        }

        sc.close();
    }

    // ===================== AJOUTER =====================
    private static void ajouter(Scanner sc, ServiceServices serviceServices,
                                ServiceAssurances serviceAssurances,
                                ServiceCredit serviceCredit, User u1) throws SQLException {

        System.out.println("\n--- AJOUTER ---");
        System.out.println("1. Location Voiture");
        System.out.println("2. Location Maison");
        System.out.println("3. Assurance");
        System.out.println("4. Crédit");
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
                System.out.print("Localisation (lat,lon) : ");
                String loc = sc.nextLine();
                System.out.print("Adresse : ");
                String adresse = sc.nextLine();
                TypeService typeService = readTypeService(sc);

                Services s = new Services(prix, loc, adresse, desc, type, statutService, typeService, u1);
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
            case 4 -> {
                System.out.print("Nom client : ");
                String nomClient = sc.nextLine();
                float montant = readFloat(sc, "Montant : ");
                LocalDate dateCredit = readDate(sc);
                System.out.print("Description : ");
                String description = sc.nextLine();
                StatutCredit statutCredit = readStatutCredit(sc);

                Credit c = new Credit(nomClient, montant, dateCredit, description, statutCredit);
                serviceCredit.ajouterCredit(c);
                System.out.println("Crédit ajouté avec succès !");
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== MODIFIER =====================
    private static void modifier(Scanner sc, ServiceServices serviceServices,
                                 ServiceAssurances serviceAssurances,
                                 ServiceCredit serviceCredit, User u1) throws SQLException {

        System.out.println("\n--- MODIFIER ---");
        System.out.println("1. Location Voiture/Maison");
        System.out.println("2. Assurance");
        System.out.println("3. Crédit");
        System.out.print("Choisissez une option : ");
        int choix = readInt(sc, "");

        switch (choix) {
            case 1 -> {
                int id = readInt(sc, "ID du service : ");
                float prix = readFloat(sc, "Prix : ");
                System.out.print("Description : ");
                String desc = sc.nextLine();
                System.out.print("Type : ");
                String type = sc.nextLine();
                Statut statutService = readStatutService(sc);
                System.out.print("Localisation (lat,lon) : ");
                String loc = sc.nextLine();
                System.out.print("Adresse : ");
                String adresse = sc.nextLine();
                TypeService typeService = readTypeService(sc);

                Services s = new Services(id, prix, loc, adresse, desc, type, statutService, typeService, u1);
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
            case 3 -> {
                int id = readInt(sc, "ID du crédit : ");
                System.out.print("Nom client : ");
                String nomClient = sc.nextLine();
                float montant = readFloat(sc, "Montant : ");
                LocalDate dateCredit = readDate(sc);
                System.out.print("Description : ");
                String description = sc.nextLine();
                StatutCredit statutCredit = readStatutCredit(sc);

                Credit c = new Credit(nomClient, montant, dateCredit, description, statutCredit);
                c.setIdCredit(id);
                serviceCredit.modifierCredit(c);
                System.out.println("Crédit modifié avec succès !");
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== SUPPRIMER =====================
    private static void supprimer(Scanner sc, ServiceServices serviceServices,
                                  ServiceAssurances serviceAssurances,
                                  ServiceCredit serviceCredit) throws SQLException {

        System.out.println("\n--- SUPPRIMER ---");
        System.out.println("1. Location Voiture/Maison");
        System.out.println("2. Assurance");
        System.out.println("3. Crédit");
        System.out.print("Choisissez une option : ");
        int choix = readInt(sc, "");

        switch (choix) {
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
            case 3 -> {
                int id = readInt(sc, "ID du crédit : ");
                Credit c = new Credit();
                c.setIdCredit(id);
                serviceCredit.supprimerCredit(c);
                System.out.println("Crédit supprimé !");
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== AFFICHER =====================
    private static void afficher(Scanner sc, ServiceServices serviceServices,
                                 ServiceAssurances serviceAssurances,
                                 ServiceCredit serviceCredit) throws SQLException {

        System.out.println("\n--- AFFICHER ---");
        System.out.println("1. Location Voiture/Maison");
        System.out.println("2. Assurance");
        System.out.println("3. Crédit");
        System.out.print("Choisissez une option : ");
        int choix = readInt(sc, "");

        switch (choix) {
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
            case 3 -> {
                List<Credit> credits = serviceCredit.recupererCredits();
                if (credits.isEmpty()) System.out.println("Aucun crédit trouvé.");
                else credits.forEach(System.out::println);
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== MÉTHODES DE SAISIE =====================
    private static int readInt(Scanner sc, String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) { System.out.println("Veuillez entrer un entier valide."); }
        }
    }

    private static float readFloat(Scanner sc, String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Float.parseFloat(sc.nextLine());
            } catch (Exception e) { System.out.println("Veuillez entrer un nombre valide."); }
        }
    }

    private static LocalDate readDate(Scanner sc) {
        while (true) {
            try {
                System.out.print("Date (YYYY-MM-DD) : ");
                return LocalDate.parse(sc.nextLine());
            } catch (Exception e) { System.out.println("Format invalide. Exemple : 2026-02-10"); }
        }
    }

    private static Statut readStatutService(Scanner sc) {
        while (true) {
            System.out.print("Statut service (DISPONIBLE/NON_DISPONIBLE) : ");
            String input = sc.nextLine().toUpperCase();
            try { return Statut.valueOf(input); }
            catch (Exception e) { System.out.println("Statut invalide."); }
        }
    }

    private static entities.assurances.Statut readStatutAssurance(Scanner sc) {
        while (true) {
            System.out.print("Statut assurance (ACTIVE/INACTIVE) : ");
            String input = sc.nextLine().toUpperCase();
            try { return entities.assurances.Statut.valueOf(input); }
            catch (Exception e) { System.out.println("Statut invalide."); }
        }
    }

    private static StatutCredit readStatutCredit(Scanner sc) {
        while (true) {
            System.out.print("Statut crédit (REMBOURSE/NON_REMBOURSE/PARTIELLEMENT_REMBOURSE) : ");
            String input = sc.nextLine().toUpperCase();
            try { return StatutCredit.valueOf(input); }
            catch (Exception e) { System.out.println("Statut invalide."); }
        }
    }

    private static TypeService readTypeService(Scanner sc) {
        while (true) {
            System.out.print("Type service (voiture/maison) : ");
            String input = sc.nextLine().toLowerCase();
            try { return TypeService.valueOf(input); }
            catch (Exception e) { System.out.println("Type invalide."); }
        }
    }

    private static TypeAssurance readTypeAssurance(Scanner sc) {
        while (true) {
            System.out.print("Type assurance (AUTO/SANTE/MAISON/VIE/HABITATION/AUTRE) : ");
            String input = sc.nextLine().toUpperCase();
            try { return TypeAssurance.valueOf(input); }
            catch (Exception e) { System.out.println("Type invalide."); }
        }
    }
}
