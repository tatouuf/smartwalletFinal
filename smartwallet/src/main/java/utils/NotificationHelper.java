package utils;

import entities.Notification;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import services.ServiceNotification;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationHelper {

    private static final Logger logger = Logger.getLogger(NotificationHelper.class.getName());
    private static final ServiceNotification notificationService = new ServiceNotification();

    // ================= NOTIFICATION TYPES =================
    public static final String FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String FRIEND_ACCEPTED = "FRIEND_ACCEPTED";
    public static final String RECLAMATION_REPLY = "RECLAMATION_REPLY";
    public static final String NEW_RECLAMATION = "NEW_RECLAMATION";
    public static final String MILESTONE_USERS = "MILESTONE_USERS";
    public static final String MILESTONE_PROFIT = "MILESTONE_PROFIT";

    // ================= CREATE & SHOW NOTIFICATION =================
    public static void createAndShowNotification(int userId, String type, String message, Integer relatedId) {
        try {
            // Save to database
            Notification notification = new Notification(userId, type, message, relatedId);
            notificationService.createNotification(notification);

            // Show popup ONLY if JavaFX is initialized
            try {
                if (isJavaFXInitialized()) {
                    showPopupNotification(getNotificationTitle(type), message);
                }
            } catch (Exception e) {
                // Silently ignore if JavaFX is not available (e.g., during testing)
                logger.log(Level.FINE, "JavaFX not available for popup notification", e);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating notification", e);
        }
    }

    // ================= CHECK IF JAVAFX IS INITIALIZED =================
    private static boolean isJavaFXInitialized() {
        try {
            Platform.runLater(() -> {});
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    // ================= SHOW POPUP NOTIFICATION =================
    public static void showPopupNotification(String title, String message) {
        if (!isJavaFXInitialized()) {
            logger.info("Notification (popup skipped): " + title + " - " + message);
            return;
        }

        Platform.runLater(() -> {
            try {
                Notifications.create()
                        .title(title)
                        .text(message)
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.TOP_RIGHT)
                        .showInformation();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not show popup notification", e);
            }
        });
    }

    // ================= SHOW ALERT DIALOG =================
    public static void showAlertNotification(String title, String message) {
        if (!isJavaFXInitialized()) {
            logger.info("Notification (alert skipped): " + title + " - " + message);
            return;
        }

        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not show alert notification", e);
            }
        });
    }

    // ================= GET NOTIFICATION TITLE BY TYPE =================
    private static String getNotificationTitle(String type) {
        return switch (type) {
            case FRIEND_REQUEST -> "👥 New Friend Request";
            case FRIEND_ACCEPTED -> "✅ Friend Request Accepted";
            case RECLAMATION_REPLY -> "💬 Reclamation Reply";
            case NEW_RECLAMATION -> "📩 New Reclamation";
            case MILESTONE_USERS -> "🎉 Milestone Reached";
            case MILESTONE_PROFIT -> "💰 Profit Milestone";
            default -> "🔔 Notification";
        };
    }

    // ================= GET NOTIFICATION ICON BY TYPE =================
    public static String getNotificationIcon(String type) {
        return switch (type) {
            case FRIEND_REQUEST -> "👥";
            case FRIEND_ACCEPTED -> "✅";
            case RECLAMATION_REPLY -> "💬";
            case NEW_RECLAMATION -> "📩";
            case MILESTONE_USERS -> "🎉";
            case MILESTONE_PROFIT -> "💰";
            default -> "🔔";
        };
    }
}