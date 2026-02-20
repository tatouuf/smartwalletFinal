package Controllers;

import entities.Reclamation;
import entities.ReclamationStatuts;
import entities.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceReclamation;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReclamationController {

    private static final Logger logger = Logger.getLogger(ReclamationController.class.getName());

    @FXML private TableView<Reclamation> tableReclamations;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String>  colMessage;
    @FXML private TableColumn<Reclamation, String>  colStatut;
    @FXML private TableColumn<Reclamation, String>  colReponse;
    @FXML private TableColumn<Reclamation, String>  colDateEnvoi;
    @FXML private Button btnDelete;
    @FXML private Button btnBack;
    @FXML private Button btnResolve;

    // User only
    @FXML private TextArea txtMessage;
    @FXML private Button btnSend;

    // Admin only
    @FXML private TextArea txtReply;
    @FXML private Button btnReply;

    private final ServiceReclamation reclamationService;
    private final ObservableList<Reclamation> reclamationList;
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReclamationController() {
        reclamationService = new ServiceReclamation();
        reclamationList    = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        adjustViewForRole();
        loadReclamations();
    }

    // ==================== TABLE SETUP ====================

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));

        colStatut.setCellValueFactory(cell -> {
            ReclamationStatuts statut = cell.getValue().getStatut();
            boolean isAdmin = Session.isLoggedIn() &&
                    Session.getCurrentUser().getRole() == Role.ADMIN;

            String display;
            if (isAdmin) {
                // Admin sees full status
                display = statut != null ? statut.name() : "";
            } else {
                // User sees LU / NON_LU
                if (statut == ReclamationStatuts.PENDING) {
                    display = "NON_LU";
                } else {
                    display = "LU";
                }
            }
            return new javafx.beans.property.SimpleStringProperty(display);
        });

        // Color code status column
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "PENDING",  "NON_LU"     ->
                                setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                        case "IN_PROGRESS", "LU"      ->
                                setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                        case "RESOLVED"               ->
                                setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                        default                        -> setStyle("");
                    }
                }
            }
        });

        colReponse.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getReponse() != null
                                ? cell.getValue().getReponse() : "—"));

        colDateEnvoi.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDate_envoi() != null
                                ? cell.getValue().getDate_envoi().format(dateFormatter) : ""));

        tableReclamations.setPlaceholder(new Label("No reclamations found."));
        tableReclamations.setItems(reclamationList);

        // Auto-fill reply box when admin selects a row
        tableReclamations.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    if (selected != null && txtReply != null) {
                        txtReply.setText(selected.getReponse() != null
                                ? selected.getReponse() : "");
                    }
                    // Enable resolve only if IN_PROGRESS
                    if (btnResolve != null && selected != null) {
                        btnResolve.setDisable(
                                selected.getStatut() != ReclamationStatuts.IN_PROGRESS);
                    }
                });
    }

    // ==================== ROLE-BASED VIEW ====================

    private void adjustViewForRole() {
        boolean isAdmin = Session.isLoggedIn() &&
                Session.getCurrentUser().getRole() == Role.ADMIN;

        // User elements
        if (txtMessage != null) {
            txtMessage.setVisible(!isAdmin);
            txtMessage.setManaged(!isAdmin);
        }
        if (btnSend != null) {
            btnSend.setVisible(!isAdmin);
            btnSend.setManaged(!isAdmin);
        }

        // Admin elements
        if (txtReply != null) {
            txtReply.setVisible(isAdmin);
            txtReply.setManaged(isAdmin);
        }
        if (btnReply != null) {
            btnReply.setVisible(isAdmin);
            btnReply.setManaged(isAdmin);
        }
        if (btnResolve != null) {
            btnResolve.setVisible(isAdmin);
            btnResolve.setManaged(isAdmin);
            btnResolve.setDisable(true); // enabled only when IN_PROGRESS is selected
        }
    }

    // ==================== SEND (USER) ====================

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

    // ==================== REPLY (ADMIN) ====================

    @FXML
    private void replyReclamation() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reclamation to reply to.");
            return;
        }

        if (selected.getStatut() == ReclamationStatuts.RESOLVED) {
            showAlert(Alert.AlertType.WARNING, "Already Resolved",
                    "This reclamation is already resolved.");
            return;
        }

        String reply = txtReply.getText().trim();
        if (reply.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter a reply message.");
            return;
        }

        try {
            reclamationService.replyReclamation(
                    selected.getId(),
                    Session.getCurrentUser().getId(),
                    reply);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Reply sent! Status set to IN_PROGRESS.");
            txtReply.clear();
            loadReclamations();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error replying", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
        }
    }

    // ==================== RESOLVE (ADMIN) ====================

    @FXML
    private void resolveReclamation() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reclamation to resolve.");
            return;
        }

        try {
            reclamationService.resolveReclamation(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Resolved",
                    "Reclamation marked as RESOLVED.");
            loadReclamations();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error resolving", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
        }
    }

    // ==================== DELETE ====================

    @FXML
    private void deleteReclamation() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reclamation to delete.");
            return;
        }

        if (Session.getCurrentUser().getRole() != Role.ADMIN &&
                selected.getUser_id() != Session.getCurrentUser().getId()) {
            showAlert(Alert.AlertType.ERROR, "Unauthorized",
                    "You can only delete your own reclamations.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this reclamation?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reclamationService.deleteReclamation(selected.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Reclamation deleted!");
                    loadReclamations();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error deleting", e);
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "An error occurred. Please try again.");
                }
            }
        });
    }

    // ==================== BACK ====================

    @FXML
    private void backToDashboard() {
        if (Session.isLoggedIn() && Session.getCurrentUser().getRole() == Role.ADMIN) {
            MainFxml.getInstance().showDashboard();
        } else {
            MainFxml.getInstance().showAmitie();
        }
    }

    // ==================== LOAD ====================

    private void loadReclamations() {
        try {
            reclamationList.clear();
            if (!Session.isLoggedIn()) return;

            if (Session.getCurrentUser().getRole() == Role.ADMIN) {
                reclamationList.addAll(reclamationService.getAllReclamations());
            } else {
                reclamationList.addAll(
                        reclamationService.getReclamationsByUser(
                                Session.getCurrentUser().getId()));
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
}