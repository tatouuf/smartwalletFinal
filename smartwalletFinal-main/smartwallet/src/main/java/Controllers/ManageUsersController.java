package Controllers;

import entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceUser;
import services.ServiceAmitie;
import services.ServiceReclamation;
import services.ServiceTransaction;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ManageUsersController {

    private static final Logger logger = Logger.getLogger(ManageUsersController.class.getName());

    // Table
    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPrenom;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colStatus;

    // Search & Filters
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> comboStatus;
    @FXML private Button btnSearch;

    // User Details Panel (Read-only labels)
    @FXML private Label lblSelectedUser;
    @FXML private Label lblUserName;
    @FXML private Label lblUserEmail;
    @FXML private Label lblUserPhone;
    @FXML private Label lblRegistered;
    @FXML private ToggleGroup statusToggleGroup;
    @FXML private RadioButton radioActive;
    @FXML private RadioButton radioInactive;

    // Statistics
    @FXML private Label lblFriendsCount;
    @FXML private Label lblReclamationsCount;
    @FXML private Label lblTransactionsCount;

    // Action Buttons
    @FXML private Button btnSave;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final ServiceUser userService;
    private final ServiceAmitie amitieService;
    private final ServiceReclamation reclamationService;
    private final ServiceTransaction transactionService;
    private final ObservableList<User> userList;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private User selectedUser;

    public ManageUsersController() {
        userService = new ServiceUser();
        amitieService = new ServiceAmitie();
        reclamationService = new ServiceReclamation();
        transactionService = new ServiceTransaction();
        userList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupTable();
        setupFilters();
        loadAllUsers();
        clearDetailsPanel();
    }

    // ==================== TABLE SETUP ====================
    private void setupTable() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isIs_actif() ? "Active" : "Inactive"));

        // Color code status column
        colStatus.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("Active")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tableUsers.setItems(userList);
        tableUsers.setPlaceholder(new Label("No users found."));

        // Selection listener
        tableUsers.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadUserDetails(newSelection);
                    }
                }
        );
    }

    // ==================== FILTERS SETUP (NO ROLE FILTER) ====================
    private void setupFilters() {
        comboStatus.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        comboStatus.setValue("All");

        // Live search on combo change
        comboStatus.setOnAction(e -> handleSearch());
    }

    // ==================== LOAD ALL USERS (ONLY USERS, NO ADMINS) ====================
    private void loadAllUsers() {
        try {
            userList.clear();
            List<User> allUsers = userService.recupererUsersOnly(); // Only users, no admins
            userList.addAll(allUsers);
            logger.info("Loaded " + allUsers.size() + " users (admins excluded)");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading users", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load users. Please try again.");
        }
    }

    // ==================== SEARCH & FILTER (NO ROLE FILTER) ====================
    @FXML
    private void handleSearch() {
        try {
            String searchText = txtSearch.getText().toLowerCase().trim();
            String statusFilter = comboStatus.getValue();

            List<User> allUsers = userService.recupererUsersOnly(); // Only users

            List<User> filtered = allUsers.stream()
                    .filter(user -> {
                        // Search filter (name or email)
                        boolean matchesSearch = searchText.isEmpty() ||
                                user.getNom().toLowerCase().contains(searchText) ||
                                user.getPrenom().toLowerCase().contains(searchText) ||
                                user.getEmail().toLowerCase().contains(searchText);

                        // Status filter
                        boolean matchesStatus = statusFilter.equals("All") ||
                                (statusFilter.equals("Active") && user.isIs_actif()) ||
                                (statusFilter.equals("Inactive") && !user.isIs_actif());

                        return matchesSearch && matchesStatus;
                    })
                    .collect(Collectors.toList());

            userList.clear();
            userList.addAll(filtered);

            logger.info("Filtered to " + filtered.size() + " users");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error filtering users", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter users.");
        }
    }

    // ==================== LOAD USER DETAILS (READ-ONLY) ====================
    private void loadUserDetails(User user) {
        selectedUser = user;

        lblSelectedUser.setText("User: " + user.getPrenom() + " " + user.getNom());
        lblUserName.setText(user.getPrenom() + " " + user.getNom());
        lblUserEmail.setText(user.getEmail());
        lblUserPhone.setText(user.getTelephone());
        lblRegistered.setText(user.getDate_creation().format(dateFormatter));

        if (user.isIs_actif()) {
            radioActive.setSelected(true);
        } else {
            radioInactive.setSelected(true);
        }

        loadUserStatistics(user.getId());

        // Enable buttons
        btnSave.setDisable(false);
        btnDelete.setDisable(false);
    }

    // ==================== LOAD USER STATISTICS ====================
    private void loadUserStatistics(int userId) {
        try {
            // Count friends
            List<entities.Amitie> allFriendships = amitieService.getAcceptedFriends();
            long friendsCount = allFriendships.stream()
                    .filter(a -> a.getUser_id() == userId || a.getFriend_id() == userId)
                    .count();
            lblFriendsCount.setText(String.valueOf(friendsCount));

            // Count reclamations
            int reclamationsCount = reclamationService.getReclamationsByUser(userId).size();
            lblReclamationsCount.setText(String.valueOf(reclamationsCount));

            // Count transactions
            List<entities.Transaction> transactions = transactionService.recuperer();
            long transactionsCount = transactions.stream()
                    .filter(t -> t.getUserId() == userId)
                    .count();
            lblTransactionsCount.setText(String.valueOf(transactionsCount));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading user statistics", e);
            lblFriendsCount.setText("N/A");
            lblReclamationsCount.setText("N/A");
            lblTransactionsCount.setText("N/A");
        }
    }

    // ==================== SAVE (UPDATE STATUS ONLY) ====================
    @FXML
    private void handleSave() {
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a user to edit.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Changes");
        confirm.setHeaderText("Update User Status");
        confirm.setContentText("Are you sure you want to update this user's status?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    selectedUser.setIs_actif(radioActive.isSelected());
                    userService.modifier(selectedUser);

                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "User status updated successfully!");

                    handleRefresh();

                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error updating user", e);
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Failed to update user. Error: " + e.getMessage());
                }
            }
        });
    }

    // ==================== DELETE USER ====================
    @FXML
    private void handleDelete() {
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a user to delete.");
            return;
        }

        // Prevent admin from deleting themselves
        if (selectedUser.getId() == Session.getCurrentUser().getId()) {
            showAlert(Alert.AlertType.ERROR, "Operation Not Allowed",
                    "You cannot delete your own account!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete User: " + selectedUser.getPrenom() + " " + selectedUser.getNom());
        confirm.setContentText(
                "⚠️ WARNING: This will permanently delete the user and all related data.\n\n" +
                        "This action cannot be undone!\n\n" +
                        "Are you sure you want to proceed?"
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.supprimer(selectedUser);

                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "User deleted successfully!");

                    clearDetailsPanel();
                    handleRefresh();

                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error deleting user", e);
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Failed to delete user. Error: " + e.getMessage());
                }
            }
        });
    }

    // ==================== REFRESH ====================
    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        comboStatus.setValue("All");
        loadAllUsers();
        clearDetailsPanel();
    }

    // ==================== CLEAR DETAILS PANEL ====================
    private void clearDetailsPanel() {
        selectedUser = null;
        lblSelectedUser.setText("Select a user to view details");
        lblUserName.setText("—");
        lblUserEmail.setText("—");
        lblUserPhone.setText("—");
        lblRegistered.setText("—");
        radioActive.setSelected(true);
        lblFriendsCount.setText("0");
        lblReclamationsCount.setText("0");
        lblTransactionsCount.setText("0");
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
    }

    // ==================== NAVIGATION ====================
    @FXML
    private void handleBack() {
        MainFxml.getInstance().showDashboard();
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

    // ==================== ALERT HELPER ====================
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
