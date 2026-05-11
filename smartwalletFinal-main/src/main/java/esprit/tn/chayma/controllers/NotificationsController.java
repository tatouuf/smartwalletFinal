package esprit.tn.chayma.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NotificationsController {

    @FXML
    private Button btnRetourDashboard;

    @FXML
    private Label notificationsList;

    @FXML
    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartWallet Admin Dashboard");
            stage.centerOnScreen();
            System.out.println("✅ Retour au DashboardAdmin effectué");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Rafraîchit la liste des notifications
     * Cette méthode est appelée par NotificationsBridge
     */
    public void refreshNotifications() {
        try {
            // Logique pour rafraîchir les notifications
            System.out.println("🔄 Rafraîchissement des notifications...");

            if (notificationsList != null) {
                notificationsList.setText("Aucune nouvelle notification");
            }

            // Vous pouvez ajouter ici la logique pour charger les notifications depuis la base de données
            // List<Notification> notifications = notificationService.getAll();
            // afficherNotifications(notifications);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du rafraîchissement des notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche les notifications dans l'interface
     */
    public void displayNotifications(String notifications) {
        if (notificationsList != null) {
            notificationsList.setText(notifications);
        }
    }
}