package Controllers;

import entities.Amitie;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.ServiceAmitie;
import services.ServiceNotification;
import services.ServiceUser;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FriendsListController {

    private static final Logger logger = Logger.getLogger(FriendsListController.class.getName());

    @FXML private TableView<FriendDisplay> tableFriends;
    @FXML private TableColumn<FriendDisplay, String> colFriendNom;
    @FXML private TableColumn<FriendDisplay, String> colFriendPrenom;
    @FXML private Button btnRemoveFriend;
    @FXML private Button btnBlockFriend;

    @FXML private TableView<FriendDisplay> tableBlocked;
    @FXML private TableColumn<FriendDisplay, String> colBlockedNom;
    @FXML private TableColumn<FriendDisplay, String> colBlockedPrenom;
    @FXML private Button btnUnblock;

    @FXML private TableView<FriendDisplay> tableSent;
    @FXML private TableColumn<FriendDisplay, String> colSentNom;
    @FXML private TableColumn<FriendDisplay, String> colSentPrenom;
    @FXML private Button btnCancelRequest;

    @FXML private Label lblPendingCount;
    @FXML private Label lblNotificationCount;

    private final ServiceAmitie amitieService;
    private final ServiceUser userService;
    private final ServiceNotification notificationService;

    private final ObservableList<FriendDisplay> friendsList;
    private final ObservableList<FriendDisplay> blockedList;
    private final ObservableList<FriendDisplay> sentList;

    public FriendsListController() {
        amitieService = new ServiceAmitie();
        userService = new ServiceUser();
        notificationService = new ServiceNotification();

        friendsList = FXCollections.observableArrayList();
        blockedList = FXCollections.observableArrayList();
        sentList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupTables();
        loadAllData();

        // ✅ Double click friend -> open friend management popup (NOT changing landing)
        tableFriends.setRowFactory(tv -> {
            TableRow<FriendDisplay> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 2) {
                    // open amitie popup
                    MainFxml.getInstance().openAmitiePopup();
                }
            });
            return row;
        });
    }

    private void setupTables() {
        colFriendNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colFriendPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        tableFriends.setItems(friendsList);
        tableFriends.setPlaceholder(new Label("No friends yet."));

        colBlockedNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colBlockedPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        tableBlocked.setItems(blockedList);
        tableBlocked.setPlaceholder(new Label("No blocked users."));

        colSentNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colSentPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        tableSent.setItems(sentList);
        tableSent.setPlaceholder(new Label("No pending requests."));
    }

    private void loadAllData() {
        try {
            if (!Session.isLoggedIn()) return;

            loadFriends();
            loadBlocked();
            loadSentRequests();
            updatePendingCount();
            updateNotificationCount();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading data", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while loading data.");
        }
    }

    private void loadFriends() throws SQLException {
        friendsList.clear();
        var friends = amitieService.getAcceptedFriends();

        for (Amitie a : friends) {
            int friendId = (a.getUser_id() == Session.getCurrentUser().getId())
                    ? a.getFriend_id() : a.getUser_id();

            User friend = userService.recupererParId(friendId);
            if (friend != null) {
                friendsList.add(new FriendDisplay(friendId, friend.getNom(), friend.getPrenom()));
            }
        }
    }

    private void loadBlocked() throws SQLException {
        blockedList.clear();
        var blocked = amitieService.getBlockedUsers();

        for (Amitie a : blocked) {
            User blockedUser = userService.recupererParId(a.getFriend_id());
            if (blockedUser != null) {
                blockedList.add(new FriendDisplay(
                        blockedUser.getId(), blockedUser.getNom(), blockedUser.getPrenom()));
            }
        }
    }

    private void loadSentRequests() throws SQLException {
        sentList.clear();
        var sent = amitieService.getSentRequests();

        for (Amitie a : sent) {
            User user = userService.recupererParId(a.getFriend_id());
            if (user != null) {
                sentList.add(new FriendDisplay(user.getId(), user.getNom(), user.getPrenom()));
            }
        }
    }

    private void updatePendingCount() throws SQLException {
        int count = amitieService.getPendingRequests().size();
        lblPendingCount.setText(String.valueOf(count));
    }

    private void updateNotificationCount() throws SQLException {
        int count = notificationService.countUnreadNotifications(Session.getCurrentUser().getId());
        lblNotificationCount.setText(String.valueOf(count));
    }

    // =========================================================
    // ✅ FIXED NAVIGATION: USE POPUPS (DO NOT REPLACE LANDING)
    // =========================================================

    @FXML
    private void handleManageRequests() {
        // ❌ OLD: MainFxml.getInstance().showAmitie();
        // ✅ NEW: open popup
        MainFxml.getInstance().openAmitiePopup();
    }

    @FXML
    private void handleReclamations() {
        // ❌ OLD: MainFxml.getInstance().showReclamationUser();
        // ✅ NEW: open popup
        MainFxml.getInstance().openReclamationUserPopup();
    }

    @FXML
    private void handleNotifications() {
        // ❌ OLD: MainFxml.getInstance().showNotifications();
        // ✅ NEW: open popup
        MainFxml.getInstance().openNotificationsPopup();
    }

    @FXML
    private void handlewallet() {
        // ✅ Return to landing without opening a new window:
        // close this FriendsList popup
        Stage current = (Stage) tableFriends.getScene().getWindow();
        current.close();

        // Ensure landing is shown in main stage (safe if already there)
        MainFxml.getInstance().showWalletHome();
    }

    // =========================================================
    // ACTIONS
    // =========================================================

    @FXML
    private void handleRemoveFriend() {
        FriendDisplay selected = tableFriends.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a friend to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText(null);
        confirm.setContentText("Remove " + selected.getPrenom() + " " + selected.getNom() + " from friends?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    amitieService.removeFriend(selected.getUserId());
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Friend removed successfully!");
                    loadAllData();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error removing friend", e);
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "An error occurred. Please try again.");
                }
            }
        });
    }

    @FXML
    private void handleBlockFriend() {
        FriendDisplay selected = tableFriends.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a friend to block.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Block");
        confirm.setHeaderText(null);
        confirm.setContentText("Block " + selected.getPrenom() + " " + selected.getNom() + "?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    amitieService.blockFriend(selected.getUserId());
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "User blocked successfully!");
                    loadAllData();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error blocking friend", e);
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "An error occurred. Please try again.");
                }
            }
        });
    }

    @FXML
    private void handleUnblock() {
        FriendDisplay selected = tableBlocked.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a user to unblock.");
            return;
        }

        try {
            amitieService.unblockUser(selected.getUserId());
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "User unblocked successfully!");
            loadAllData();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error unblocking", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred. Please try again.");
        }
    }

    @FXML
    private void handleCancelRequest() {
        FriendDisplay selected = tableSent.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a request to cancel.");
            return;
        }

        try {
            amitieService.cancelSentRequest(selected.getUserId());
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Request cancelled successfully!");
            loadAllData();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error canceling request", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred. Please try again.");
        }
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

                // close popup first (optional but clean)
                Stage current = (Stage) tableFriends.getScene().getWindow();
                current.close();

                // go to login on main stage
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

    // =========================================================
    // DTO FOR TABLE
    // =========================================================
    public static class FriendDisplay {
        private int userId;
        private String nom;
        private String prenom;

        public FriendDisplay(int id, String nom, String prenom) {
            this.userId = id;
            this.nom = nom;
            this.prenom = prenom;
        }

        public int getUserId() { return userId; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
    }
}