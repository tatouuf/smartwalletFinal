package Controllers;

import entities.Amitie;
import entities.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import services.ServiceAmitie;
import services.ServiceNotification;
import services.ServiceUser;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmitieController {

    private static final Logger logger = Logger.getLogger(AmitieController.class.getName());

    @FXML private TableView<AmitieDisplay> tableAmitie;
    @FXML private TableColumn<AmitieDisplay, String> colRequester;
    @FXML private TableColumn<AmitieDisplay, String> colStatut;
    @FXML private TableColumn<AmitieDisplay, String> colDate;
    @FXML private Button btnAccept;
    @FXML private Button btnDelete;

    @FXML private TextField searchField;
    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colUserNom;
    @FXML private TableColumn<User, String> colUserPrenom;
    @FXML private Button btnSendRequest;

    @FXML private Button btnBack;
    @FXML private Label lblNotificationCount;  // ✅ NEW

    private final ServiceAmitie amitieService;
    private final ServiceUser userService;
    private final ServiceNotification notificationService;  // ✅ NEW
    private final ObservableList<AmitieDisplay> amitieList;
    private final ObservableList<User> userList;
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AmitieController() {
        amitieService = new ServiceAmitie();
        userService = new ServiceUser();
        notificationService = new ServiceNotification();  // ✅ NEW
        amitieList = FXCollections.observableArrayList();
        userList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupRequestsTable();
        setupUsersTable();
        loadFriendRequests();
        updateNotificationBadge();  // ✅ NEW

        // ✅ AUTO-REFRESH NOTIFICATION BADGE EVERY 5 SECONDS
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> updateNotificationBadge())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupRequestsTable() {
        colRequester.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("requesterPrenom"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        tableAmitie.setPlaceholder(new Label("No incoming friend requests."));
        tableAmitie.setItems(amitieList);
    }

    private void setupUsersTable() {
        colUserNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colUserPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        tableUsers.setPlaceholder(new Label("Search for users to add."));
        tableUsers.setItems(userList);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            userList.clear();
            return;
        }

        try {
            userList.clear();
            userList.addAll(amitieService.searchUsers(keyword));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching users", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while searching. Please try again.");
        }
    }

    @FXML
    private void handleSendRequest() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a user to send a friend request to.");
            return;
        }

        try {
            amitieService.addFriend(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Friend request sent to " + selected.getPrenom()
                            + " " + selected.getNom() + "!");
            userList.clear();
            searchField.clear();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void acceptRequest() {
        AmitieDisplay selected = tableAmitie.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a friend request to accept.");
            return;
        }

        try {
            amitieService.acceptFriend(selected.getUserId());
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Friend request accepted!");
            loadFriendRequests();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error accepting friend request", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
        }
    }

    @FXML
    private void deleteRequest() {
        AmitieDisplay selected = tableAmitie.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a friend request to refuse.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Refusal");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to refuse this friend request?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    amitieService.deleteFriend(selected.getUserId());
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Friend request refused!");
                    loadFriendRequests();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error refusing friend request", e);
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "An error occurred. Please try again.");
                }
            }
        });
    }

    @FXML
    private void handleBack() {
     //   MainFxml.getInstance().showFriendsList();
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

    private void loadFriendRequests() {
        try {
            amitieList.clear();
            if (!Session.isLoggedIn()) return;

            var requests = amitieService.getPendingRequests();
            for (Amitie req : requests) {
                User requester = userService.recupererParId(req.getUser_id());
                if (requester != null) {
                    amitieList.add(new AmitieDisplay(
                            req.getUser_id(),
                            requester.getNom(),
                            requester.getPrenom(),
                            req.getDateCreation().format(dateFormatter)
                    ));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading friend requests", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while loading friend requests.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class AmitieDisplay {
        private int userId;
        private String requesterName;
        private String requesterPrenom;
        private String dateCreation;

        public AmitieDisplay(int userId, String nom, String prenom, String date) {
            this.userId = userId;
            this.requesterName = nom;
            this.requesterPrenom = prenom;
            this.dateCreation = date;
        }

        public int getUserId() { return userId; }
        public String getRequesterName() { return requesterName; }
        public String getRequesterPrenom() { return requesterPrenom; }
        public String getDateCreation() { return dateCreation; }
    }
}