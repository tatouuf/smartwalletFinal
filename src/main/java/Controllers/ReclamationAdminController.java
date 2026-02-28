package Controllers;

import entities.Reclamation;
import entities.ReclamationStatuts;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceReclamation;
import services.ServiceUser;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReclamationAdminController {

    private static final Logger logger = Logger.getLogger(ReclamationAdminController.class.getName());

    @FXML private TableView<ReclamationDisplay> tableReclamations;
    @FXML private TableColumn<ReclamationDisplay, String> colNom;
    @FXML private TableColumn<ReclamationDisplay, String> colPrenom;
    @FXML private TableColumn<ReclamationDisplay, String> colCategory; // NEW
    @FXML private TableColumn<ReclamationDisplay, String> colMessage;
    @FXML private TableColumn<ReclamationDisplay, String> colStatut;
    @FXML private TableColumn<ReclamationDisplay, String> colUrgent; // NEW
    @FXML private TableColumn<ReclamationDisplay, String> colDate;

    @FXML private TextArea txtReply;
    @FXML private Button btnAISuggestion; // NEW
    @FXML private Button btnReply;
    @FXML private Button btnResolve;
    @FXML private Button btnDelete;
    @FXML private Label lblAIInfo; // NEW

    private final ServiceReclamation reclamationService;
    private final ServiceUser userService;
    private final ObservableList<ReclamationDisplay> reclamationList;
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReclamationAdminController() {
        reclamationService = new ServiceReclamation();
        userService = new ServiceUser();
        reclamationList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupTable();
        loadReclamations();
    }

    private void setupTable() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category")); // NEW
        colMessage.setCellValueFactory(new PropertyValueFactory<>("messagePreview"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colUrgent.setCellValueFactory(new PropertyValueFactory<>("urgentLabel")); // NEW
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEnvoi"));

        // Color-code status
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
                        case "PENDING" ->
                                setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                        case "IN_PROGRESS" ->
                                setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                        case "RESOLVED" ->
                                setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // NEW: Color-code urgent
        colUrgent.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String urgent, boolean empty) {
                super.updateItem(urgent, empty);
                if (empty || urgent == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(urgent);
                    if (urgent.equals("🚨 URGENT")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #9ca3af;");
                    }
                }
            }
        });

        // NEW: Color-code category
        colCategory.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(category.replace("_", " "));
                    setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
                }
            }
        });

        tableReclamations.setItems(reclamationList);
        tableReclamations.setPlaceholder(new Label("No reclamations found."));

        tableReclamations.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    if (selected != null) {
                        try {
                            Reclamation r = reclamationService.getReclamationById(selected.getId());
                            if (r != null && r.getReponse() != null) {
                                txtReply.setText(r.getReponse());
                            } else {
                                txtReply.clear();
                            }

                            // NEW: Show AI info
                            if (r != null) {
                                lblAIInfo.setText(String.format(
                                        "🤖 AI Analysis: %s | %s | %s",
                                        r.getCategory().replace("_", " "),
                                        r.getSentiment(),
                                        r.isUrgent() ? "URGENT" : "Normal"
                                ));
                            }

                            if (btnResolve != null) {
                                btnResolve.setDisable(
                                        !selected.getStatut().equals("IN_PROGRESS"));
                            }
                        } catch (SQLException e) {
                            logger.log(Level.SEVERE, "Error loading reclamation", e);
                        }
                    }
                });
    }

    // NEW: Get AI Suggestion
    @FXML
    private void handleAISuggestion() {
        ReclamationDisplay selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reclamation first.");
            return;
        }

        try {
            btnAISuggestion.setDisable(true);
            btnAISuggestion.setText("🤖 Generating...");

            String suggestion = reclamationService.getAIReplySuggestion(selected.getId());

            if (suggestion != null) {
                txtReply.setText(suggestion);
                showAlert(Alert.AlertType.INFORMATION, "AI Suggestion",
                        "AI-generated reply loaded! Review and edit before sending.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Could not generate AI suggestion.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting AI suggestion", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred. Please try again.");
        } finally {
            btnAISuggestion.setDisable(false);
            btnAISuggestion.setText("🤖 AI Suggestion");
        }
    }

    @FXML
    private void replyReclamation() {
        ReclamationDisplay selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reclamation to reply to.");
            return;
        }

        if (selected.getStatut().equals("RESOLVED")) {
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
            lblAIInfo.setText("");
            loadReclamations();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error replying", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
        }
    }

    @FXML
    private void resolveReclamation() {
        ReclamationDisplay selected = tableReclamations.getSelectionModel().getSelectedItem();

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

    @FXML
    private void deleteReclamation() {
        ReclamationDisplay selected = tableReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reclamation to delete.");
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

    @FXML
    private void backToDashboard() {
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

    private void loadReclamations() {
        try {
            reclamationList.clear();

            var reclamations = reclamationService.getAllReclamations();

            for (Reclamation r : reclamations) {
                User user = userService.recupererParId(r.getUser_id());
                if (user == null) continue;

                String preview = r.getMessage();
                if (preview.length() > 60) {
                    preview = preview.substring(0, 60) + "...";
                }

                reclamationList.add(new ReclamationDisplay(
                        r.getId(),
                        user.getNom(),
                        user.getPrenom(),
                        r.getCategory() != null ? r.getCategory() : "GENERAL",
                        preview,
                        r.getStatut().name(),
                        r.isUrgent() ? "🚨 URGENT" : "Normal",
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
        private String nom;
        private String prenom;
        private String category;
        private String messagePreview;
        private String statut;
        private String urgentLabel;
        private String dateEnvoi;

        public ReclamationDisplay(int id, String nom, String prenom, String category,
                                  String preview, String statut, String urgentLabel, String date) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.category = category;
            this.messagePreview = preview;
            this.statut = statut;
            this.urgentLabel = urgentLabel;
            this.dateEnvoi = date;
        }

        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getCategory() { return category; }
        public String getMessagePreview() { return messagePreview; }
        public String getStatut() { return statut; }
        public String getUrgentLabel() { return urgentLabel; }
        public String getDateEnvoi() { return dateEnvoi; }
    }
}