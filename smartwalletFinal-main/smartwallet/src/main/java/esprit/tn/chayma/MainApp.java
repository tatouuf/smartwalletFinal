package esprit.tn.chayma;

import esprit.tn.chayma.services.NotificationInitializationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private NotificationInitializationService notificationService;
    private static final int CURRENT_USER_ID = 1; // À remplacer par l'utilisateur connecté

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialiser le service de notifications
        notificationService = new NotificationInitializationService();
        notificationService.initialize(CURRENT_USER_ID);

        // Effectuer une vérification au démarrage
        notificationService.performCheck();

        // Lancer les vérifications périodiques (toutes les 30 minutes)
        notificationService.startPeriodicChecks(30);

        System.out.println("[MainApp] Service de notifications intelligent activé");

        // FXML principal (chemin exact dans resources)
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dep/deplayoutLayout.fxml"));

        // Crée la scène
        Scene scene = new Scene(root, 1200, 700);

        // Charge le fichier CSS pour le style du sidebar
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());

        primaryStage.setTitle("Smart Wallet");
        primaryStage.setScene(scene);

        // Arrêter les vérifications quand l'app ferme
        primaryStage.setOnCloseRequest(e -> {
            notificationService.stopPeriodicChecks();
            System.out.println("[MainApp] Arrêt du service de notifications");
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}