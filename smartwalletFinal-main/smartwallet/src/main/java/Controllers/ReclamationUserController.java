package Controllers;

import entities.Reclamation;
import entities.ReclamationStatuts;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import services.ServiceNotification;
import services.ServiceReclamation;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReclamationUserController {

    private static final Logger logger = Logger.getLogger(ReclamationUserController.class.getName());

    @FXML private TextArea txtMessage;
    @FXML private TableView<ReclamationDisplay> tableReclamations;
    @FXML private TableColumn<ReclamationDisplay, String> colStatus;
    @FXML private TableColumn<ReclamationDisplay, String> colMessage;
    @FXML private TableColumn<ReclamationDisplay, String> colDate;
    @FXML private Label lblNotificationCount;  // ✅ NEW

    private final ServiceReclamation reclamationService;
    private final ServiceNotification notificationService;  // ✅ NEW
    private final ObservableList<ReclamationDisplay> reclamationList;
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReclamationUserController() {
        reclamationService = new ServiceReclamation();
        notificationService = new ServiceNotification();  // ✅ NEW
        reclamationList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupTable();
        loadReclamations();
        updateNotificationBadge();  // ✅ NEW

        // ✅ AUTO-REFRESH NOTIFICATION BADGE EVERY 5 SECONDS
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> updateNotificationBadge())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupTable() {
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("messagePreview"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEnvoi"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("NON_LU")) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tableReclamations.setItems(reclamationList);
        tableReclamations.setPlaceholder(new Label("No reclamations yet."));

        tableReclamations.setRowFactory(tv -> {
            TableRow<ReclamationDisplay> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleViewDetail(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void sendReclamation() {
        String message = txtMessage.getText().trim();

        if (message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter a message.");
            return;
        }

        try {
            reclamationService.sendReclamation(
                    Session.getCurrentUser().getId(), message);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Your reclamation has been sent!");
            txtMessage.clear();
            loadReclamations();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error sending reclamation", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
        }
    }

    private void handleViewDetail(ReclamationDisplay display) {
        if (display == null) return;
        ReclamationDetailController.setSelectedReclamationId(display.getId());
        MainFxml.getInstance().showReclamationDetail();
    }

    @FXML
    private void backToFriends() {
        MainFxml.getInstance().showFriendsList();
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

    private void loadReclamations() {
        try {
            reclamationList.clear();
            if (!Session.isLoggedIn()) return;

            var reclamations = reclamationService.getReclamationsByUser(
                    Session.getCurrentUser().getId());

            for (Reclamation r : reclamations) {
                String status = (r.getStatut() == ReclamationStatuts.PENDING)
                        ? "NON_LU" : "LU";

                String preview = r.getMessage();
                if (preview.length() > 50) {
                    preview = preview.substring(0, 50) + "...";
                }

                reclamationList.add(new ReclamationDisplay(
                        r.getId(),
                        status,
                        preview,
                        r.getDate_envoi().format(dateFormatter)
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading reclamations", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while loading.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ReclamationDisplay {
        private int id;
        private String status;
        private String messagePreview;
        private String dateEnvoi;

        public ReclamationDisplay(int id, String status, String preview, String date) {
            this.id = id;
            this.status = status;
            this.messagePreview = preview;
            this.dateEnvoi = date;
        }

        public int getId() { return id; }
        public String getStatus() { return status; }
        public String getMessagePreview() { return messagePreview; }
        public String getDateEnvoi() { return dateEnvoi; }
    }
}