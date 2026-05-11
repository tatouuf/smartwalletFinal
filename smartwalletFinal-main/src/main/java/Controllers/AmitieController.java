package Controllers;

import entities.Amitie;
import entities.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import services.ServiceAmitie;
import services.ServiceNotification;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmitieController {

    private static final Logger logger = Logger.getLogger(AmitieController.class.getName());

    @FXML private TableView<AmitieDisplay>           tableAmitie;
    @FXML private TableColumn<AmitieDisplay, String> colRequester;
    @FXML private TableColumn<AmitieDisplay, String> colDate;

    @FXML private TableView<User>          tableUsers;
    @FXML private TableColumn<User, String> colUserEmail;

    @FXML private TextField searchField;
    @FXML private Button    btnSendRequest;
    @FXML private Button    btnAccept;
    @FXML private Button    btnDelete;
    @FXML private Button    btnBack;
    @FXML private Label     lblNotificationCount;

    private final ServiceAmitie        amitieService;
    private final ServiceNotification  notificationService;
    private final ObservableList<AmitieDisplay> amitieList;
    private final ObservableList<User>          userList;

    // ── FIX: track selected user independently from table selection ──────────
    private User selectedUser = null;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AmitieController() {
        amitieService       = new ServiceAmitie();
        notificationService = new ServiceNotification();
        amitieList          = FXCollections.observableArrayList();
        userList            = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupRequestsTable();
        setupUsersTable();
        loadFriendRequests();
        updateNotificationBadge();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> updateNotificationBadge())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupRequestsTable() {
        colRequester.setCellValueFactory(new PropertyValueFactory<>("requesterDisplay"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        tableAmitie.setPlaceholder(new Label("No incoming friend requests."));
        tableAmitie.setItems(amitieList);
    }

    private void setupUsersTable() {
        colUserEmail.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String display = u.getEmail() + "   —   " + u.getPrenom() + " " + u.getNom();
            return new javafx.beans.property.SimpleStringProperty(display);
        });
        tableUsers.setPlaceholder(new Label("Search for users by email to add as friends."));
        tableUsers.setItems(userList);

        // Keep the selected row visually highlighted even when the table loses focus.
        // JavaFX removes the :selected style on unfocus by default — this overrides that.
        tableUsers.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    row.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white;");
                } else {
                    row.setStyle("");
                }
            });
            // Also re-apply when focus changes on the table
            tableUsers.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (row.isSelected()) {
                    row.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white;");
                }
            });
            return row;
        });

        tableUsers.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedUser = newVal;
                    }
                }
        );
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            // Do NOT clear the existing list — just return so clicking elsewhere
            // doesn't wipe out the search results the user is looking at.
            return;
        }

        // New search: reset selection and list
        selectedUser = null;
        userList.clear();

        try {
            userList.addAll(amitieService.searchUsers(keyword));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching users", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while searching. Please try again.");
        }
    }

    @FXML
    private void handleSendRequest() {
        // ── FIX Bug 1 & 2: use persisted selectedUser, not live table selection
        User target = selectedUser != null
                ? selectedUser
                : tableUsers.getSelectionModel().getSelectedItem();

        if (target == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a user from the list before sending a request.");
            return;
        }

        // ── FIX Bug 2: guard against invalid id just in case ────────────────
        if (target.getId() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid User",
                    "The selected user has an invalid ID. Please search again.");
            return;
        }

        try {
            amitieService.addFriend(target.getId());
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Friend request sent to " + target.getPrenom()
                            + " " + target.getNom() + "!");
            selectedUser = null;
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
            showAlert(Alert.AlertType.INFORMATION, "Success", "Friend request accepted!");
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
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Friend request refused!");
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
        // no-op — kept for FXML binding
    }

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

    private void loadFriendRequests() {
        try {
            amitieList.clear();
            if (!Session.isLoggedIn()) return;

            for (Amitie req : amitieService.getPendingRequests()) {
                User requester = amitieService.getUserById(req.getUser_id());
                if (requester != null) {
                    amitieList.add(new AmitieDisplay(
                            req.getUser_id(),
                            requester.getNom(),
                            requester.getPrenom(),
                            requester.getEmail(),
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
        private final int    userId;
        private final String requesterDisplay;
        private final String dateCreation;

        public AmitieDisplay(int userId, String nom, String prenom, String email, String date) {
            this.userId           = userId;
            this.requesterDisplay = email + "   —   " + prenom + " " + nom;
            this.dateCreation     = date;
        }

        public int    getUserId()            { return userId; }
        public String getRequesterDisplay()  { return requesterDisplay; }
        public String getDateCreation()      { return dateCreation; }
    }
}