package Controllers;

import entities.Reclamation;
import entities.ReclamationStatuts;
import entities.Transaction;
import entities.User;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.TransactionService;
import javafx.scene.layout.FlowPane;
import services.ServiceUser;
import esprit.tn.souha_pi.services.WalletService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import services.*;
import entities.DashboardAdmin;
import tests.MainFxml;
import utils.Session;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DashboardAdminController {

    private static final Logger logger = Logger.getLogger(DashboardAdminController.class.getName());

    // ======================= STATISTICS LABELS =======================
    @FXML private Label lblAdminName;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalTransactions;
    @FXML private Label lblBenefice;
    @FXML private Label lblTotalWallets;
    @FXML private Label lblTotalAmount;
    @FXML private Label totalAdminsStatLabel;
    @FXML private Label totalUsersStatLabel2;
    @FXML private Label totalPendingLabel;

    // ======================= SEARCH FIELDS =======================
    @FXML private TextField searchUserField;
    @FXML private TextField searchTransactionField;

    // ======================= CHARTS =======================
    @FXML private PieChart pieReclamations;
    @FXML private LineChart<String, Number> lineUsers;
    @FXML private PieChart pieDistribution;

    // ======================= TABLES =======================
    @FXML private TableView<User> tableUsers;
    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableView<Wallet> tableWallets;
    @FXML private TableView<User> tableAccountRequests;
    @FXML private TableView<User> tableWalletRequests;

    // ======================= CREDIT TABLES (Itaf) =======================
    @FXML private TableView<?> tableCredits;
    @FXML private TableColumn<?, ?> colIdCredit;
    @FXML private TableColumn<?, ?> colNomClient;
    @FXML private TableColumn<?, ?> colMontant;
    @FXML private TableColumn<?, ?> colDateCredit;
    @FXML private TableColumn<?, ?> colStatutCredit;
    @FXML private TableColumn<?, ?> colActionsCredit;

    // ======================= FLOWPANE (Itaf) =======================
    @FXML private FlowPane cardAffAssurance;

    // ======================= IMAGE VIEWS (Itaf) =======================
    @FXML private ImageView imgServiceLogo;
    @FXML private ImageView imgService;
    @FXML private ImageView imgServiceAdd;
    @FXML private ImageView imgLogoAssurance;
    @FXML private ImageView imgLogoCredit;

    // ======================= TABLE COLUMNS =======================
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPrenom;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<Transaction, Integer> colTransactionId;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colDate;

    // ======================= SERVICES =======================
    private final ServiceDashboardAdmin dashboardService;
    private final ServiceNotification notificationService;
    private final ServiceUser userService = new ServiceUser();
    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();

    // ======================= FORMATTERS =======================
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Current admin user
    private User currentAdmin;

    public DashboardAdminController() {
        dashboardService = new ServiceDashboardAdmin();
        notificationService = new ServiceNotification();
    }

    @FXML
    private void initialize() {
        loadCurrentAdmin();
        setupTables();
        setupSearchListeners();
        loadDashboardData();
        loadAdditionalData();
        //loadDistributionChart();
        loadImages();
    }

    // ======================= MÉTHODES DE NAVIGATION =======================

    /**
     * Afficher le tableau de bord admin
     */
    public void showDashboard() {
        loadScene("/DashboardAdmin.fxml");
    }

    /**
     * Afficher la gestion des réclamations admin
     */
    public void showReclamationAdmin() {
        loadScene("/ReclamationAdmin.fxml");
    }

    /**
     * Afficher la gestion des services admin
     */
    public void showServiceAdmin() {
        loadScene("/acceuilservices/AcceuilService.fxml");
    }

    /**
     * Afficher la gestion des utilisateurs
     * Cette méthode est appelée depuis le bouton "Gérer les utilisateurs"
     */
    @FXML
    private void showManageUsers() {
        try {
            // Essayer de charger la page de gestion des utilisateurs
            String fxmlPath = "/ManageUsers.fxml";

            // Vérifier si le fichier existe
            if (getClass().getResource(fxmlPath) != null) {
                // Ouvrir en popup pour ne pas perdre le dashboard
                openPopup(fxmlPath, "Gestion des Utilisateurs", 1000, 700, true);
            } else {
                // Si le fichier n'existe pas, afficher un message
                showAlert(Alert.AlertType.INFORMATION,
                        "Fonctionnalité en développement",
                        "La gestion des utilisateurs sera disponible dans la prochaine version.\n\n" +
                                "Pour l'instant, vous pouvez gérer les utilisateurs directement dans les tableaux ci-dessous.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de la gestion des utilisateurs", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la gestion des utilisateurs.");
        }
    }

    /**
     * Gestionnaire pour le bouton "Gérer les utilisateurs" dans l'interface
     */
    @FXML
    private void handleManageUsers() {
        showManageUsers();
    }

    /**
     * Gestionnaire pour le bouton "Gérer les réclamations"
     */
    @FXML
    private void handleManageReclamations() {
        MainFxml.getInstance().showReclamationAdmin();
    }

    /**
     * Gestionnaire pour le bouton "Notifications"
     */
    @FXML
    private void handleNotifications() {
        MainFxml.getInstance().showNotifications();
    }

    /**
     * Gestionnaire pour le bouton "Gérer les wallets"
     */
    @FXML
    private void handleManageWallets() {
        showAlert(Alert.AlertType.INFORMATION, "Gérer les Wallets",
                "La gestion avancée des wallets sera implémentée prochainement.\n\n" +
                        "Pour l'instant, vous pouvez visualiser les wallets dans l'onglet 'Wallets'.");
    }

    // ======================= MÉTHODES EXISTANTES =======================

    private void loadCurrentAdmin() {
        if (Session.isLoggedIn()) {
            currentAdmin = Session.getCurrentUser();
            lblAdminName.setText("Admin: " + currentAdmin.getNom() + " " + currentAdmin.getPrenom() + " 👑");
        } else {
            lblAdminName.setText("Admin: Not logged in");
        }
    }

    private void setupTables() {
        setupUsersTable();
        setupTransactionsTable();
        setupWalletsTable();
        setupAccountRequestsTable();
        setupWalletRequestsTable();
    }

    private void setupUsersTable() {
        tableUsers.getColumns().clear();

        // ID Column
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        // Last Name Column
        TableColumn<User, String> nomCol = new TableColumn<>("Last Name");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(100);

        // First Name Column
        TableColumn<User, String> prenomCol = new TableColumn<>("First Name");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomCol.setPrefWidth(100);

        // Email Column
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        // Role column
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().name()));
        roleCol.setPrefWidth(80);

        // Status column with badge
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("status-badge");
                    switch (status) {
                        case "APPROVED":
                            badge.getStyleClass().add("status-approved");
                            break;
                        case "PENDING":
                            badge.getStyleClass().add("status-pending");
                            break;
                        case "REJECTED":
                            badge.getStyleClass().add("status-rejected");
                            break;
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Actions column
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(120);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand;");

                editBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableUsers.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, roleCol, statusCol, actionsCol);
        tableUsers.setPlaceholder(new Label("No users found."));
    }

    private void setupTransactionsTable() {
        tableTransactions.getColumns().clear();

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Transaction, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(70);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));
        typeCol.setPrefWidth(80);

        TableColumn<Transaction, String> targetCol = new TableColumn<>("Target");
        targetCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTarget() != null ?
                        cellData.getValue().getTarget() : ""));
        targetCol.setPrefWidth(150);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(df.format(amount) + " DT");
                    setStyle(amount >= 0 ? "-fx-text-fill: #10b981; -fx-font-weight: bold;"
                            : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            var ts = cellData.getValue().getCreatedAt();
            String display = ts != null ? ts.toLocalDateTime().format(dtf) : "—";
            return new SimpleStringProperty(display);
        });
        dateCol.setPrefWidth(150);

        tableTransactions.getColumns().addAll(idCol, userIdCol, typeCol, targetCol, amountCol, dateCol);
        tableTransactions.setPlaceholder(new Label("No transactions found."));
    }

    private void setupWalletsTable() {
        tableWallets.getColumns().clear();

        TableColumn<Wallet, Integer> walletIdCol = new TableColumn<>("ID");
        walletIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        walletIdCol.setPrefWidth(50);

        TableColumn<Wallet, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(70);

        TableColumn<Wallet, Double> balanceCol = new TableColumn<>("Balance (DT)");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(120);
        balanceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText(df.format(balance) + " DT");
                }
            }
        });

        TableColumn<Wallet, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType() != null ?
                        cellData.getValue().getType() : "Standard"));
        typeCol.setPrefWidth(100);

        tableWallets.getColumns().addAll(walletIdCol, userIdCol, balanceCol, typeCol);
        tableWallets.setPlaceholder(new Label("No wallets found."));
    }

    private void setupAccountRequestsTable() {
        tableAccountRequests.getColumns().clear();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()));
        nameCol.setPrefWidth(150);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        phoneCol.setPrefWidth(120);

        TableColumn<User, String> roleCol = new TableColumn<>("Requested Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✅ Approve");
            private final Button rejectBtn = new Button("❌ Reject");
            private final HBox pane = new HBox(10, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

                approveBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleApproveAccount(user);
                });

                rejectBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleRejectAccount(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableAccountRequests.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, roleCol, actionsCol);
        tableAccountRequests.setPlaceholder(new Label("No account requests found."));
    }

    private void setupWalletRequestsTable() {
        tableWalletRequests.getColumns().clear();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()));
        nameCol.setPrefWidth(200);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button createBtn = new Button("💰 Create Wallet");
            private final HBox pane = new HBox(10, createBtn);

            {
                createBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

                createBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleCreateWallet(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableWalletRequests.getColumns().addAll(idCol, nameCol, emailCol, actionsCol);
        tableWalletRequests.setPlaceholder(new Label("No wallet requests found."));
    }

    private void loadImages() {
        try {
            // Charger les images si elles existent
            // imgServiceLogo.setImage(new Image(getClass().getResourceAsStream("/icons/services.png")));
            // imgService.setImage(new Image(getClass().getResourceAsStream("/icons/service.png")));
            // imgServiceAdd.setImage(new Image(getClass().getResourceAsStream("/icons/add.png")));
            // imgLogoAssurance.setImage(new Image(getClass().getResourceAsStream("/icons/assurance.png")));
            // imgLogoCredit.setImage(new Image(getClass().getResourceAsStream("/icons/credit.png")));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load images", e);
        }
    }

    private void setupSearchListeners() {
        if (searchUserField != null) {
            searchUserField.textProperty().addListener((obs, oldVal, newVal) -> handleSearchUsers());
        }
        if (searchTransactionField != null) {
            searchTransactionField.textProperty().addListener((obs, oldVal, newVal) -> handleSearchTransactions());
        }
    }

    private void loadDashboardData() {
        try {
            DashboardAdmin dashboard = dashboardService.getDashboard();

            loadStatistics(dashboard);
            loadReclamationsPieChart(dashboard);
            loadUsersLineChart(dashboard);
            loadUsersTable(dashboard);
            loadTransactionsTable(dashboard);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading dashboard", e);
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading dashboard data.");
        }
    }

    private void loadAdditionalData() {
        try {
            List<Wallet> wallets = walletService.getAll();
            tableWallets.setItems(FXCollections.observableArrayList(wallets));

            List<User> pendingUsers = userService.getUsersEnAttente();
            tableAccountRequests.setItems(FXCollections.observableArrayList(pendingUsers));

            loadWalletRequests();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading additional data", e);
        }
    }

    private void loadDistributionChart() {
        try {
            List<User> users = userService.recuperer();
            long adminCount = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
            long userCount = users.size() - adminCount;

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Admins (" + adminCount + ")", adminCount),
                    new PieChart.Data("Users (" + userCount + ")", userCount)
            );

            pieDistribution.setData(pieData);
            pieDistribution.setTitle("User Distribution");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading distribution chart", e);
        }
    }

    private void loadWalletRequests() throws SQLException {
        List<User> allUsers = userService.recuperer();
        List<User> usersWithoutWallet = new ArrayList<>();

        for (User user : allUsers) {
            if ("APPROVED".equals(user.getStatus())) {
                try {
                    Wallet wallet = walletService.getByUserId(user.getId());
                    if (wallet == null) {
                        usersWithoutWallet.add(user);
                    }
                } catch (Exception e) {
                    usersWithoutWallet.add(user);
                }
            }
        }

        tableWalletRequests.setItems(FXCollections.observableArrayList(usersWithoutWallet));
    }

    private void loadReclamationsPieChart(DashboardAdmin dashboard) {
        int pending = 0;
        int inProgress = 0;
        int resolved = 0;

        for (Reclamation r : dashboard.getListeReclamations()) {
            if (r.getStatut() == null) continue;
            switch (r.getStatut()) {
                case PENDING -> pending++;
                case IN_PROGRESS -> inProgress++;
                case RESOLVED -> resolved++;
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Pending (" + pending + ")", pending),
                new PieChart.Data("In Progress (" + inProgress + ")", inProgress),
                new PieChart.Data("Resolved (" + resolved + ")", resolved)
        );

        pieReclamations.setData(pieData);
        pieReclamations.setLegendVisible(true);
        pieReclamations.setLabelsVisible(true);
        pieReclamations.setTitle("Reclamations Status");
    }

    private void loadUsersLineChart(DashboardAdmin dashboard) {
        java.util.Map<String, Long> byMonth = new java.util.LinkedHashMap<>();
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
                "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc"};

        for (String m : months) byMonth.put(m, 0L);

        dashboard.getListeUsers().forEach(u -> {
            if (u.getDate_creation() != null) {
                String month = months[u.getDate_creation().getMonthValue() - 1];
                byMonth.merge(month, 1L, Long::sum);
            }
        });

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Users");
        byMonth.forEach((month, count) ->
                series.getData().add(new XYChart.Data<>(month, count)));

        lineUsers.getData().clear();
        lineUsers.getData().add(series);
        lineUsers.setTitle("User Registration Trend");
    }

    private void loadUsersTable(DashboardAdmin dashboard) {
        tableUsers.setItems(FXCollections.observableArrayList(dashboard.getListeUsers()));
    }

    private void loadTransactionsTable(DashboardAdmin dashboard) {
        tableTransactions.setItems(FXCollections.observableArrayList(dashboard.getListeTransactions()));
    }

    private void loadStatistics(DashboardAdmin dashboard) {
        lblTotalUsers.setText(String.valueOf(dashboard.getTotalUsers()));
        lblTotalTransactions.setText(String.valueOf(dashboard.getTotalTransactions()));
        lblBenefice.setText(df.format(dashboard.getBenefice()) + " DT");

        try {
            List<User> users = userService.recuperer();
            long adminCount = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
            long pendingCount = users.stream().filter(u -> "PENDING".equals(u.getStatus())).count();

            totalAdminsStatLabel.setText(String.valueOf(adminCount));
            totalUsersStatLabel2.setText(String.valueOf(users.size() - adminCount));
            totalPendingLabel.setText(String.valueOf(pendingCount));

            List<Wallet> wallets = walletService.getAll();
            lblTotalWallets.setText(String.valueOf(wallets.size()));

            double totalAmount = wallets.stream().mapToDouble(Wallet::getBalance).sum();
            lblTotalAmount.setText(df.format(totalAmount) + " DT");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading statistics", e);
        }
    }

    // ======================= SEARCH HANDLERS =======================

    @FXML
    private void handleSearchUsers() {
        String searchText = searchUserField.getText().toLowerCase().trim();

        try {
            List<User> allUsers = userService.recuperer();

            if (searchText.isEmpty()) {
                tableUsers.setItems(FXCollections.observableArrayList(allUsers));
                return;
            }

            List<User> filtered = allUsers.stream()
                    .filter(u -> u.getNom().toLowerCase().contains(searchText) ||
                            u.getPrenom().toLowerCase().contains(searchText) ||
                            u.getEmail().toLowerCase().contains(searchText) ||
                            (u.getTelephone() != null && u.getTelephone().toLowerCase().contains(searchText)))
                    .collect(Collectors.toList());

            tableUsers.setItems(FXCollections.observableArrayList(filtered));

            if (filtered.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Search", "No users found matching: " + searchText);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching users", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to search users.");
        }
    }

    @FXML
    private void handleSearchTransactions() {
        String searchText = searchTransactionField.getText().toLowerCase().trim();

        try {
            List<esprit.tn.souha_pi.entities.Transaction> externalTransactions = transactionService.getAll();
            List<Transaction> allTransactions = new ArrayList<>();

            for (esprit.tn.souha_pi.entities.Transaction ext : externalTransactions) {
                Transaction trans = new Transaction();
                trans.setId(ext.getId());
                trans.setUserId(ext.getUserId());
                trans.setAmount(ext.getAmount());
                trans.setType(ext.getType());
                trans.setTarget(ext.getTarget());
                trans.setCreatedAt(ext.getCreatedAt());
                allTransactions.add(trans);
            }

            if (searchText.isEmpty()) {
                tableTransactions.setItems(FXCollections.observableArrayList(allTransactions));
                return;
            }

            List<Transaction> filtered = allTransactions.stream()
                    .filter(t -> String.valueOf(t.getId()).contains(searchText) ||
                            String.valueOf(t.getUserId()).contains(searchText) ||
                            (t.getType() != null && t.getType().toLowerCase().contains(searchText)) ||
                            (t.getTarget() != null && t.getTarget().toLowerCase().contains(searchText)))
                    .collect(Collectors.toList());

            tableTransactions.setItems(FXCollections.observableArrayList(filtered));

            if (filtered.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Search", "No transactions found matching: " + searchText);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching transactions", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to search transactions.");
        }
    }

    // ======================= ACCOUNT REQUEST HANDLERS =======================

    private void handleApproveAccount(User user) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Approve Account");
        dialog.setHeaderText("Approve account for " + user.getNom() + " " + user.getPrenom());

        ButtonType approveButtonType = new ButtonType("Approve", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(approveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField initialDepositField = new TextField();
        initialDepositField.setPromptText("0.00");
        initialDepositField.setText("50.00");

        grid.add(new Label("Initial Deposit (DT):"), 0, 0);
        grid.add(initialDepositField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == approveButtonType) {
                try {
                    return Double.parseDouble(initialDepositField.getText());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
            return null;
        });

        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(initialDeposit -> {
            try {
                userService.approuverCompte(user.getId(), initialDeposit);
                loadAdditionalData();
                loadDashboardData();
                loadStatistics(dashboardService.getDashboard());

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Account approved for " + user.getNom() + " " + user.getPrenom() +
                                " with wallet of " + df.format(initialDeposit) + " DT");

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error approving account", e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve account: " + e.getMessage());
            }
        });
    }

    private void handleRejectAccount(User user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Account");
        dialog.setHeaderText("Reject account for " + user.getNom() + " " + user.getPrenom());
        dialog.setContentText("Reason for rejection:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            try {
                userService.rejeterCompte(user.getId());
                loadAdditionalData();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Account rejected.");

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error rejecting account", e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject account.");
            }
        });
    }

    // ======================= WALLET REQUEST HANDLERS =======================

    private void handleCreateWallet(User user) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Create Wallet");
        dialog.setHeaderText("Create wallet for " + user.getNom() + " " + user.getPrenom());

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField initialBalanceField = new TextField();
        initialBalanceField.setPromptText("0.00");
        initialBalanceField.setText("50.00");

        grid.add(new Label("Initial Balance (DT):"), 0, 0);
        grid.add(initialBalanceField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    return Double.parseDouble(initialBalanceField.getText());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
            return null;
        });

        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(initialBalance -> {
            try {
                walletService.creerWallet(user.getId(), initialBalance);
                loadAdditionalData();
                loadDashboardData();

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Wallet created for " + user.getNom() + " " + user.getPrenom() +
                                " with " + df.format(initialBalance) + " DT");

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error creating wallet", e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create wallet.");
            }
        });
    }

    // ======================= USER MANAGEMENT HANDLERS =======================

    private void handleEditUser(User user) {
        showAlert(Alert.AlertType.INFORMATION, "Edit User",
                "Edit functionality for " + user.getNom() + " " + user.getPrenom() + " will be implemented soon.");
    }

    private void handleDeleteUser(User user) {
        if (currentAdmin != null && user.getId() == currentAdmin.getId()) {
            showAlert(Alert.AlertType.ERROR, "Error", "You cannot delete your own account.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete " + user.getNom() + " " + user.getPrenom() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.supprimer(user);
                    loadDashboardData();
                    loadAdditionalData();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");

                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error deleting user", e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
                }
            }
        });
    }

    @FXML
    private void handleAddUser() {
        showAlert(Alert.AlertType.INFORMATION, "Add User",
                "Add user functionality will be implemented soon.");
    }

    @FXML
    private void handleRefreshData() {
        try {
            loadDashboardData();
            loadAdditionalData();
            loadDistributionChart();
            showAlert(Alert.AlertType.INFORMATION, "Refresh", "Data refreshed successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error refreshing data", e);
        }
    }

    @FXML
    private void logout() {
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

// ======================= MÉTHODES POUR LES SERVICES D'ITAF HAMDI =======================

    @FXML
    private void showAfficherService() {
        openFXMLInSameStage("/services/AfficherService.fxml", "Gestion des Services");
    }

    @FXML
    private void showAjouterService() {
        openFXMLInSameStage("/services/AjouterService.fxml", "Ajouter un Service");
    }

    @FXML
    private void showAfficherAssurance() {
        openFXMLInSameStage("/assurance/AfficherAssurance.fxml", "Gestion des Assurances");
    }

    @FXML
    private void showGestionAssurance() {
        openFXMLInSameStage("/assurance/GestionAssurance.fxml", "Administration Assurances");
    }

    @FXML
    private void showAfficherCredit() {
        openFXMLInSameStage("/credit/AfficherCredit.fxml", "Gestion des Crédits");
    }

    @FXML
    private void showGestionCredit() {
        openFXMLInSameStage("/credit/GestionCredit.fxml", "Administration Crédits");
    }

    @FXML
    private void haamdiah() {
        openFXMLInSameStage("/assurance/AjouterAssurance.fxml", "Ajouter une Assurance");
    }

    @FXML
    private void itafaaction() {
        openFXMLInSameStage("/credit/AjouterCredit.fxml", "Ajouter un Crédit");
    }

    // ================== UTILS ==================

    private void openFXMLInSameStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) lblAdminName.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading FXML: " + fxmlPath, e);
            showAlert(Alert.AlertType.ERROR, "Error", "Impossible d'ouvrir " + fxmlPath);
        }
    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) lblAdminName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading scene: " + fxmlPath, e);
        }
    }

    private Stage openPopup(String fxmlPath, String title, double width, double height, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setResizable(true);

            if (modal) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }

            stage.centerOnScreen();
            stage.show();
            return stage;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to open popup: " + fxmlPath, e);
            return null;
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