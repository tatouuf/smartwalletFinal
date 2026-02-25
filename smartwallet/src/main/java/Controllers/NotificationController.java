package Controllers;

import entities.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceNotification;
import tests.MainFxml;
import utils.NotificationHelper;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationController {

    private static final Logger logger = Logger.getLogger(NotificationController.class.getName());

    @FXML private TableView<NotificationDisplay> tableNotifications;
    @FXML private TableColumn<NotificationDisplay, String> colIcon;
    @FXML private TableColumn<NotificationDisplay, String> colMessage;
    @FXML private TableColumn<NotificationDisplay, String> colDate;
    @FXML private TableColumn<NotificationDisplay, String> colStatus;

    @FXML private Button btnMarkAllRead;
    @FXML private Button btnDelete;
    @FXML private Button btnBack;

    private final ServiceNotification notificationService;
    private final ObservableList<NotificationDisplay> notificationList;
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotificationController() {
        notificationService = new ServiceNotification();
        notificationList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupTable();
        loadNotifications();
    }

    private void setupTable() {
        colIcon.setCellValueFactory(new PropertyValueFactory<>("icon"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Color code status
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("UNREAD")) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #9ca3af;");
                    }
                }
            }
        });

        tableNotifications.setItems(notificationList);
        tableNotifications.setPlaceholder(new Label("No notifications."));

        // Double-click to mark as read
        tableNotifications.setRowFactory(tv -> {
            TableRow<NotificationDisplay> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleMarkAsRead(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void handleMarkAllRead() {
        try {
            notificationService.markAllAsRead(Session.getCurrentUser().getId());
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "All notifications marked as read!");
            loadNotifications();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error marking all as read", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred. Please try again.");
        }
    }

    private void handleMarkAsRead(NotificationDisplay display) {
        try {
            notificationService.markAsRead(display.getId());
            loadNotifications();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error marking as read", e);
        }
    }

    @FXML
    private void handleDelete() {
        NotificationDisplay selected = tableNotifications.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a notification to delete.");
            return;
        }

        try {
            notificationService.deleteNotification(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Notification deleted!");
            loadNotifications();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting notification", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred. Please try again.");
        }
    }

    @FXML
    private void handleBack() {
        MainFxml.getInstance().showFriendsList();
    }

    private void loadNotifications() {
        try {
            notificationList.clear();
            if (!Session.isLoggedIn()) return;

            var notifications = notificationService.getAllNotifications(
                    Session.getCurrentUser().getId());

            for (Notification n : notifications) {
                notificationList.add(new NotificationDisplay(
                        n.getId(),
                        NotificationHelper.getNotificationIcon(n.getType()),
                        n.getMessage(),
                        n.getCreatedAt().format(dateFormatter),
                        n.isRead() ? "READ" : "UNREAD"
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading notifications", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while loading notifications.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class NotificationDisplay {
        private int id;
        private String icon;
        private String message;
        private String dateCreated;
        private String status;

        public NotificationDisplay(int id, String icon, String message, String date, String status) {
            this.id = id;
            this.icon = icon;
            this.message = message;
            this.dateCreated = date;
            this.status = status;
        }

        public int getId() { return id; }
        public String getIcon() { return icon; }
        public String getMessage() { return message; }
        public String getDateCreated() { return dateCreated; }
        public String getStatus() { return status; }
    }
}