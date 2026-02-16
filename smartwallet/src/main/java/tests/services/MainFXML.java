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
            primaryStage.setScene(new Scene(root, 900, 500));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur : FXML non trouvé ou invalide !");
        }
    }


    public static void showAfficherService() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/services/AfficherService.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Afficher Tous les Services");
            primaryStage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
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
