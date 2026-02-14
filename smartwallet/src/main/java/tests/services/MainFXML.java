package tests.services;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFXML extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        try {
            // Chemin corrigé vers le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainALC/MainALC.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Smart Wallet - Menu Principal");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur : FXML non trouvé ou invalide !");
        }
    }


    // =====================================================
    // 🔹 PARTIE SERVICES (NE PAS MODIFIER)
    // =====================================================

    public static void showAjouterService() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/services/AjouterService.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Ajouter Service");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (NullPointerException e) {
            System.err.println("FXML non trouvé : Vérifie le chemin AjouterService.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de AjouterService.fxml");
        }
    }

    public static void showAfficherService() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/services/AfficherService.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Afficher Tous les Services");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NullPointerException e) {
            System.err.println("FXML non trouvé : Vérifie le chemin AfficherService.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de AfficherService.fxml");
        }
    }

    // =====================================================
    // 🔹 PARTIE ASSURANCES (NE PAS MODIFIER)
    // =====================================================

    public static void showAjouterAssurance() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/assurance/AjouterAssurance.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Ajouter Assurance");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (NullPointerException e) {
            System.err.println("FXML non trouvé : Vérifie le chemin AjouterAssurance.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de AjouterAssurance.fxml");
        }
    }

    public static void showAfficherAssurance() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/assurance/AfficherAssurance.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Afficher Toutes les Assurances");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NullPointerException e) {
            System.err.println("FXML non trouvé : Vérifie le chemin AfficherAssurance.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de AfficherAssurance.fxml");
        }
    }

    // =====================================================
    // 🔹 PARTIE CREDITS (AJOUTÉE)
    // =====================================================

    public static void showAjouterCredit() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/credit/AjouterCredit.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Ajouter Crédit");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (NullPointerException e) {
            System.err.println("FXML non trouvé : Vérifie le chemin AjouterCredit.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de AjouterCredit.fxml");
        }
    }

    public static void showAfficherCredit() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/credit/AfficherCredit.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Afficher Tous les Crédits");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NullPointerException e) {
            System.err.println("FXML non trouvé : Vérifie le chemin AfficherCredit.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de AfficherCredit.fxml");
        }
    }

    // =====================================================
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
