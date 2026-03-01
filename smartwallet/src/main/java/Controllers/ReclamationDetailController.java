package Controllers;

import entities.Reclamation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import services.ServiceNotification;
import services.ServiceReclamation;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReclamationDetailController {

    private static final Logger logger = Logger.getLogger(ReclamationDetailController.class.getName());
    private static int selectedReclamationId;

    @FXML private Label lblStatus;
    @FXML private TextArea txtMyMessage;
    @FXML private TextArea txtAdminReply;
    @FXML private Label lblDateSent;
    @FXML private Label lblDateReplied;
    @FXML private Label lblNotificationCount;  // ✅ NEW

    private final ServiceReclamation reclamationService;
    private final ServiceNotification notificationService;  // ✅ NEW
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReclamationDetailController() {
        reclamationService = new ServiceReclamation();
        notificationService = new ServiceNotification();  // ✅ NEW
    }

    public static void setSelectedReclamationId(int id) {
        selectedReclamationId = id;
    }

    @FXML
    private void initialize() {
        loadReclamationDetail();
        updateNotificationBadge();  // ✅ NEW

        // ✅ AUTO-REFRESH NOTIFICATION BADGE EVERY 5 SECONDS
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> updateNotificationBadge())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadReclamationDetail() {
        try {
            Reclamation r = reclamationService.getReclamationById(selectedReclamationId);
            if (r == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Reclamation not found.");
                handleBack();
                return;
            }

            txtMyMessage.setText(r.getMessage());
            txtMyMessage.setEditable(false);

            if (r.getReponse() != null && !r.getReponse().isEmpty()) {
                txtAdminReply.setText(r.getReponse());
                lblStatus.setText("✅ LU");
                lblStatus.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 16px;");
            } else {
                txtAdminReply.setText("No response yet.");
                lblStatus.setText("⏳ NON_LU");
                lblStatus.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 16px;");
            }
            txtAdminReply.setEditable(false);

            lblDateSent.setText(r.getDate_envoi().format(dateFormatter));
            if (r.getDate_reponse() != null) {
                lblDateReplied.setText(r.getDate_reponse().format(dateFormatter));
            } else {
                lblDateReplied.setText("—");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading reclamation detail", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while loading details.");
        }
    }

    @FXML
    private void handleBack() {
        MainFxml.getInstance().showReclamationUser();
    }

    // ✅ NEW: NOTIFICATION BADGE UPDATE
    private void updateNotificationBadge() {
        try {
            if (!Session.isLoggedIn()) return;

            int unreadCount = notificationService.getUnreadCount(Session.getCurrentUser().getId());

            if (lblNotificationCount != null) {
                lblNotificationCount.setText(String.valueOf(unreadCount));
                lblNotificationCount.setVisible(unreadCount > 0);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating notification badge", e);
        }
    }

    // ✅ NEW: NAVIGATE TO NOTIFICATIONS
    @FXML
    private void handleNotifications() {
        MainFxml.getInstance().showNotifications();
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to logout?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Session.clearSession();
                MainFxml.getInstance().showSignIn();
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}