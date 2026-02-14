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
        showAjouterService(); // Démarre avec la fenêtre Ajouter Service
    }

    /**
     * Affiche la fenêtre AjouterService dans le stage principal
     */
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

    /**
     * Affiche la fenêtre AfficherService dans un nouveau Stage
     */
    public static void showAfficherService() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFXML.class.getResource("/services/AfficherService.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage(); // Nouveau stage pour afficher tous les services
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

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
