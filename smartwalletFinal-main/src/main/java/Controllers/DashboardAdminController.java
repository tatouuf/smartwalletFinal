package Controllers;

import controller.service.ModifierService;
import entities.Reclamation;
import entities.ReclamationStatuts;
import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.services.BudgetService;
import esprit.tn.chayma.services.DepenseService;
import esprit.tn.chayma.services.PlanningService;
import javafx.scene.chart.BarChart;
import utils.MyDataBase;
import entities.Transaction;
import entities.User;
import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.TransactionService;
import javafx.scene.layout.*;
import services.ServiceUser;
import esprit.tn.souha_pi.services.WalletService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import services.*;
import entities.DashboardAdmin;
import tests.MainFxml;
import utils.Session;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.application.Platform;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.SQLException;
import java.util.stream.Collectors;

import javafx.scene.shape.Circle;
import esprit.tn.souha_pi.entities.BankCard;

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
    @FXML private Label totalPendingWalletsLabel;
    @FXML private Label totalServicesLabel;
    @FXML private Label activeServicesLabel;
    @FXML private Label servicesRevenueLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label pendingWalletsCountLabel;

    // ======================= CARTES BANCAIRES =======================
    @FXML private VBox bankCardsContainer;

    // ======================= SEARCH FIELDS =======================
    @FXML private TextField searchUserField;
    @FXML private TextField searchTransactionField;
    @FXML private TextField searchAccountField;
    @FXML private TextField searchWalletField;
    @FXML private TextField searchServiceField;

    // ======================= CHARTS =======================
    @FXML private PieChart pieReclamations;
    @FXML private LineChart<String, Number> lineUsers;
    @FXML private PieChart pieDistribution;
    @FXML private PieChart pieAccountStatus;
    @FXML private PieChart pieWalletStatus;

    // ======================= TABLES =======================
    @FXML private TableView<User> tableUsers;
    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableView<Wallet> tableWallets;
    @FXML private TableView<User> tableAccountRequests;
    @FXML private TableView<Wallet> tableWalletRequests;
    @FXML private TableView<Services> tableServices;
    @FXML private TableView<?> tableCredits;

    // ======================= TABLE COLUMNS =======================
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPrenom;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<Transaction, Integer> colTransactionId;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colDate;

    // ======================= BUDGET & DÉPENSES =======================
    @FXML private ComboBox<Integer> adminUserFilterCombo;
    @FXML private Label adminTotalBudgetsGlobalLabel;
    @FXML private Label adminTotalDepensesGlobalLabel;
    @FXML private Label adminTotalPlanningsGlobalLabel;
    @FXML private Label adminUsersActifsLabel;
    @FXML private Label adminTotalWalletBalanceLabel;
    @FXML private Label adminTotalCardBalanceLabel;
    @FXML private BarChart<String, Number> adminBudgetChart;
    @FXML private PieChart adminDepensesPieChart;
    @FXML private PieChart adminDepensesCategorieChart;
    @FXML private BarChart<String, Number> adminTopUsersChart;
    @FXML private TableView<Budget> adminBudgetsTable;
    @FXML private TableView<Planning> adminPlanningsTable;
    @FXML private TableView<Depense> adminDepensesDetailTable;

    // ======================= SERVICES =======================
    private final ServiceDashboardAdmin dashboardService;
    private final ServiceNotification notificationService;
    private final ServiceUser userService = new ServiceUser();
    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();
    private final BudgetService budgetService = new BudgetService();
    private final DepenseService depenseService = new DepenseService();
    private PlanningService planningService;
    private boolean isLoadingBudgetData = false;
    // ======================= FORMATTERS =======================
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private User currentAdmin;

    // ======================= CONSTRUCTEUR =======================
    public DashboardAdminController() {
        dashboardService = new ServiceDashboardAdmin();
        notificationService = new ServiceNotification();
    }

    // ======================= INITIALIZATION =======================
    @FXML
    private void initialize() {
        System.out.println("=== INITIALIZATION START ===");

        loadCurrentAdmin();
        System.out.println("✅ Admin loaded");

        setupTables();
        System.out.println("✅ Tables setup complete");

        setupSearchListeners();
        System.out.println("✅ Search listeners setup");

        loadDashboardData();
        System.out.println("✅ Dashboard data loaded");

        loadAdditionalData();
        System.out.println("✅ Additional data loaded");

        loadAccountStatusChart();
        loadWalletStatusChart();
        loadServicesData();
        loadImages();

        // Initialiser PlanningService
        planningService = PlanningService.getInstance();

        // Charger les données budget avec gestion des erreurs
        loadAllBudgetData();  // Cette méthode gère maintenant les tables manquantes

        // CHARGER LES CARTES BANCAIRES
        Platform.runLater(() -> {
            if (bankCardsContainer != null) {
                loadAllBankCards();
            }
        });

        // FORCER LE RAFRAÎCHISSEMENT APRÈS CHARGEMENT
        Platform.runLater(() -> {
            forceRefreshAllTables();
            addTabPaneListener();
        });

        System.out.println("=== INITIALIZATION COMPLETE ===");
    }

    private void loadCurrentAdmin() {
        if (Session.isLoggedIn()) {
            currentAdmin = Session.getCurrentUser();
            lblAdminName.setText("Admin: " + currentAdmin.getNom() + " " + currentAdmin.getPrenom() + " 👑");
        } else {
            lblAdminName.setText("Admin: Not logged in");
        }
    }

    // ======================= CONFIGURATION DES TABLES =======================
    private void setupTables() {
        System.out.println("=== CONFIGURATION DES TABLES ===");

        setupUsersTable();
        setupTransactionsTable();
        setupWalletsTable();
        setupAccountRequestsTable();
        setupWalletRequestsTable();
        setupServicesTable();

        System.out.println("✅ Toutes les tables configurées");
    }

    private void setupUsersTable() {
        tableUsers.getColumns().clear();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<User, String> nomCol = new TableColumn<>("Last Name");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(100);

        TableColumn<User, String> prenomCol = new TableColumn<>("First Name");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomCol.setPrefWidth(100);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().name()));
        roleCol.setPrefWidth(80);

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
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
                    badge.setStyle(getStatusStyle(status));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<User, String> walletStatusCol = new TableColumn<>("Wallet");
        walletStatusCol.setCellValueFactory(cellData -> {
            try {
                Wallet wallet = walletService.getByUserId(cellData.getValue().getId());
                return new SimpleStringProperty(wallet != null ? "✅ Actif" : "❌ Inactif");
            } catch (Exception e) {
                return new SimpleStringProperty("❌ Inactif");
            }
        });
        walletStatusCol.setPrefWidth(80);

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
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

        tableUsers.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, roleCol, statusCol, walletStatusCol, actionsCol);
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

        TableColumn<Transaction, String> userFullNameCol = new TableColumn<>("User Full Name");
        userFullNameCol.setCellValueFactory(cellData -> {
            try {
                int userId = cellData.getValue().getUserId();
                User user = userService.recupererParId(userId);
                if (user != null) {
                    return new SimpleStringProperty(user.getPrenom() + " " + user.getNom());
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error fetching user for transaction", e);
            }
            return new SimpleStringProperty("Unknown");
        });
        userFullNameCol.setPrefWidth(150);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        typeCol.setPrefWidth(80);

        TableColumn<Transaction, String> targetCol = new TableColumn<>("Target");
        targetCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTarget() != null ? cellData.getValue().getTarget() : ""));
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
                }
            }
        });

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            var ts = cellData.getValue().getCreatedAt();
            return new SimpleStringProperty(ts != null ? ts.toLocalDateTime().format(dtf) : "—");
        });
        dateCol.setPrefWidth(150);

        tableTransactions.getColumns().addAll(idCol, userIdCol, userFullNameCol, typeCol, targetCol, amountCol, dateCol);
        tableTransactions.setPlaceholder(new Label("No transactions found."));
    }

    private void setupWalletsTable() {
        if (tableWallets == null) return;
        tableWallets.getColumns().clear();

        TableColumn<Wallet, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Wallet, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(70);

        TableColumn<Wallet, String> userNomCol = new TableColumn<>("User");
        userNomCol.setCellValueFactory(cellData -> {
            String nom = cellData.getValue().getUserNom();
            String prenom = cellData.getValue().getUserPrenom();
            if (nom != null && prenom != null) {
                return new SimpleStringProperty(prenom + " " + nom);
            } else if (nom != null) {
                return new SimpleStringProperty(nom);
            } else if (prenom != null) {
                return new SimpleStringProperty(prenom);
            } else {
                return new SimpleStringProperty("User #" + cellData.getValue().getUserId());
            }
        });
        userNomCol.setPrefWidth(150);

        TableColumn<Wallet, Double> balanceCol = new TableColumn<>("Balance (DT)");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(120);
        balanceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                setText(empty ? null : df.format(balance) + " DT");
            }
        });

        TableColumn<Wallet, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType() != null ? cellData.getValue().getType() : "Standard"));
        typeCol.setPrefWidth(100);

        TableColumn<Wallet, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                    badge.setStyle(getStatusStyle(status));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        tableWallets.getColumns().addAll(idCol, userIdCol, userNomCol, balanceCol, typeCol, statusCol);
        tableWallets.setPlaceholder(new Label("No wallets found."));
    }

    private void setupAccountRequestsTable() {
        tableAccountRequests.getColumns().clear();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()));
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

        TableColumn<User, String> dateCol = new TableColumn<>("Request Date");
        dateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDate_creation() != null) {
                return new SimpleStringProperty(cellData.getValue().getDate_creation().toString());
            }
            return new SimpleStringProperty("-");
        });
        dateCol.setPrefWidth(120);

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                    badge.setStyle(getStatusStyle(status));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✅ Approve");
            private final Button rejectBtn = new Button("❌ Reject");
            private final Button viewBtn = new Button("👁 View");
            private final HBox pane = new HBox(10, viewBtn, approveBtn, rejectBtn);
            {
                approveBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                viewBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                approveBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleApproveAccount(user);
                });
                rejectBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleRejectAccount(user);
                });
                viewBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserDetails(user);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableAccountRequests.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, roleCol, dateCol, statusCol, actionsCol);
        tableAccountRequests.setPlaceholder(new Label("No account requests found."));
    }

    private void setupWalletRequestsTable() {
        tableWalletRequests.getColumns().clear();

        TableColumn<Wallet, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Wallet, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserFullName()));
        userCol.setPrefWidth(150);

        TableColumn<Wallet, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        emailCol.setPrefWidth(200);

        TableColumn<Wallet, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        amountCol.setPrefWidth(100);
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty ? null : df.format(amount) + " DT");
            }
        });

        TableColumn<Wallet, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        TableColumn<Wallet, String> dateCol = new TableColumn<>("Request Date");
        dateCol.setCellValueFactory(cellData -> {
            Wallet w = cellData.getValue();
            if (w.getCreatedAt() != null) {
                return new SimpleStringProperty(w.getCreatedAt().format(dtf));
            }
            return new SimpleStringProperty("-");
        });
        dateCol.setPrefWidth(150);

        TableColumn<Wallet, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                    badge.setStyle(getStatusStyle(status));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<Wallet, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✅ Approve");
            private final Button rejectBtn = new Button("❌ Reject");
            private final Button viewBtn = new Button("👁 View");
            private final HBox pane = new HBox(10, viewBtn, approveBtn, rejectBtn);
            {
                approveBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                viewBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                approveBtn.setOnAction(e -> {
                    Wallet wallet = getTableView().getItems().get(getIndex());
                    handleApproveWallet(wallet);
                });
                rejectBtn.setOnAction(e -> {
                    Wallet wallet = getTableView().getItems().get(getIndex());
                    handleRejectWallet(wallet);
                });
                viewBtn.setOnAction(e -> {
                    Wallet wallet = getTableView().getItems().get(getIndex());
                    showWalletDetails(wallet);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableWalletRequests.getColumns().addAll(idCol, userCol, emailCol, amountCol, typeCol, dateCol, statusCol, actionsCol);
        tableWalletRequests.setPlaceholder(new Label("No wallet requests found."));
    }

    private void setupServicesTable() {
        if (tableServices == null) return;
        tableServices.getColumns().clear();

        TableColumn<Services, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Services, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        TableColumn<Services, String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeServiceString()));
        categoryCol.setPrefWidth(120);

        TableColumn<Services, Float> priceCol = new TableColumn<>("Prix (TND)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        priceCol.setPrefWidth(100);
        priceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Float price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : df.format(price) + " TND");
            }
        });

        TableColumn<Services, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatutString()));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    if ("DISPONIBLE".equals(status) || "ACTIF".equals(status)) {
                        badge.setStyle("-fx-background-color: #22c55e20; -fx-text-fill: #22c55e; -fx-padding: 3 8; -fx-background-radius: 10;");
                    } else if ("LOUÉ".equals(status) || "EN_COURS".equals(status)) {
                        badge.setStyle("-fx-background-color: #f59e0b20; -fx-text-fill: #f59e0b; -fx-padding: 3 8; -fx-background-radius: 10;");
                    } else {
                        badge.setStyle("-fx-background-color: #ef444420; -fx-text-fill: #ef4444; -fx-padding: 3 8; -fx-background-radius: 10;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<Services, String> addressCol = new TableColumn<>("Adresse");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        addressCol.setPrefWidth(150);

        TableColumn<Services, String> userCol = new TableColumn<>("Propriétaire");
        userCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getUser() != null) {
                return new SimpleStringProperty(cellData.getValue().getUser().getNom() + " " + cellData.getValue().getUser().getPrenom());
            }
            return new SimpleStringProperty("-");
        });
        userCol.setPrefWidth(120);

        TableColumn<Services, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand;");
                editBtn.setOnAction(event -> {
                    Services service = getTableView().getItems().get(getIndex());
                    handleEditService(service);
                });
                deleteBtn.setOnAction(event -> {
                    Services service = getTableView().getItems().get(getIndex());
                    handleDeleteService(service);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableServices.getColumns().addAll(idCol, typeCol, categoryCol, priceCol, statusCol, addressCol, userCol, actionsCol);
        tableServices.setPlaceholder(new Label("Aucun service trouvé."));
    }

    // ======================= LOAD DATA =======================
    private void loadImages() {
        try {
            // Load images if needed
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
        if (searchAccountField != null) {
            searchAccountField.textProperty().addListener((obs, oldVal, newVal) -> handleSearchAccountRequests());
        }
        if (searchWalletField != null) {
            searchWalletField.textProperty().addListener((obs, oldVal, newVal) -> handleSearchWalletRequests());
        }
        if (searchServiceField != null) {
            searchServiceField.textProperty().addListener((obs, oldVal, newVal) -> searchServices());
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
            if (tableWallets != null) {
                tableWallets.setItems(FXCollections.observableArrayList(wallets));
            }
            refreshAccountRequests();
            refreshWalletRequests();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading additional data", e);
        }
    }

    private void loadAccountStatusChart() {
        try {
            List<User> users = userService.recuperer();
            long approvedCount = users.stream().filter(u -> "APPROVED".equals(u.getStatus())).count();
            long pendingCount = users.stream().filter(u -> "PENDING".equals(u.getStatus())).count();
            long rejectedCount = users.stream().filter(u -> "REJECTED".equals(u.getStatus())).count();

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Approved (" + approvedCount + ")", approvedCount),
                    new PieChart.Data("Pending (" + pendingCount + ")", pendingCount),
                    new PieChart.Data("Rejected (" + rejectedCount + ")", rejectedCount)
            );

            if (pieAccountStatus != null) {
                pieAccountStatus.setData(pieData);
                pieAccountStatus.setTitle("Account Status");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading account status chart", e);
        }
    }

    private void loadWalletStatusChart() {
        try {
            List<Wallet> wallets = walletService.getAll();
            List<User> allUsers = userService.recuperer();

            long activeWallets = wallets.stream().filter(w -> "ACTIF".equals(w.getStatus())).count();
            long pendingWallets = wallets.stream().filter(w -> "PENDING".equals(w.getStatus())).count();
            long usersWithoutWallet = allUsers.stream()
                    .filter(u -> "APPROVED".equals(u.getStatus()))
                    .filter(u -> {
                        try {
                            Wallet w = walletService.getByUserId(u.getId());
                            return w == null;
                        } catch (Exception e) {
                            return true;
                        }
                    }).count();

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Active (" + activeWallets + ")", activeWallets),
                    new PieChart.Data("Pending (" + pendingWallets + ")", pendingWallets),
                    new PieChart.Data("No Wallet (" + usersWithoutWallet + ")", usersWithoutWallet)
            );

            if (pieWalletStatus != null) {
                pieWalletStatus.setData(pieData);
                pieWalletStatus.setTitle("Wallet Status");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading wallet status chart", e);
        }
    }

    private void loadServicesData() {
        try {
            if (totalServicesLabel != null) totalServicesLabel.setText("0");
            if (activeServicesLabel != null) activeServicesLabel.setText("0");
            if (servicesRevenueLabel != null) servicesRevenueLabel.setText("0 TND");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading services", e);
        }
    }

    private void loadReclamationsPieChart(DashboardAdmin dashboard) {
        int pending = 0, inProgress = 0, resolved = 0;
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
        pieReclamations.setTitle("Reclamations Status");
    }

    private void loadUsersLineChart(DashboardAdmin dashboard) {
        java.util.Map<String, Long> byMonth = new LinkedHashMap<>();
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        for (String m : months) byMonth.put(m, 0L);

        dashboard.getListeUsers().forEach(u -> {
            if (u.getDate_creation() != null) {
                String month = months[u.getDate_creation().getMonthValue() - 1];
                byMonth.merge(month, 1L, Long::sum);
            }
        });

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Users");
        byMonth.forEach((month, count) -> series.getData().add(new XYChart.Data<>(month, count)));

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

            if (totalAdminsStatLabel != null) totalAdminsStatLabel.setText(String.valueOf(adminCount));
            if (totalUsersStatLabel2 != null) totalUsersStatLabel2.setText(String.valueOf(users.size() - adminCount));
            if (totalPendingLabel != null) totalPendingLabel.setText(String.valueOf(pendingCount));

            List<Wallet> wallets = walletService.getAll();
            if (lblTotalWallets != null) lblTotalWallets.setText(String.valueOf(wallets.size()));

            double totalAmount = wallets.stream().mapToDouble(Wallet::getBalance).sum();
            if (lblTotalAmount != null) lblTotalAmount.setText(df.format(totalAmount) + " DT");

            if (totalServicesLabel != null) totalServicesLabel.setText("0");
            if (activeServicesLabel != null) activeServicesLabel.setText("0");
            if (servicesRevenueLabel != null) servicesRevenueLabel.setText("0 TND");
            if (pendingCountLabel != null) pendingCountLabel.setText(pendingCount + " en attente");
            if (pendingWalletsCountLabel != null) pendingWalletsCountLabel.setText("0 en attente");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading statistics", e);
        }
    }

    // ======================= BUDGET & DÉPENSES =======================
    private void loadAllBudgetData() {
        // Éviter la récursion
        if (isLoadingBudgetData) {
            return;
        }
        isLoadingBudgetData = true;

        try {
            List<User> users = userService.recuperer();
            planningService = PlanningService.getInstance();

            // Initialiser le filtre SEULEMENT la première fois
            if (adminUserFilterCombo != null && adminUserFilterCombo.getItems().isEmpty()) {
                adminUserFilterCombo.getItems().clear();
                adminUserFilterCombo.getItems().add(0); // 0 = Tous
                for (User u : users) {
                    if ("APPROVED".equals(u.getStatus()) && u.isIs_actif()) {
                        adminUserFilterCombo.getItems().add(u.getId());
                    }
                }
                adminUserFilterCombo.setValue(0);
                // Ajouter le listener UNE SEULE FOIS
                adminUserFilterCombo.setOnAction(e -> onUserFilterChange());
            }

            // Récupérer les données avec gestion des erreurs
            List<Budget> allBudgets = new ArrayList<>();
            List<Depense> allDepenses = new ArrayList<>();
            List<Planning> allPlannings = new ArrayList<>();

            // Pas de try-catch pour SQLException ici car elle n'est pas lancée
            // Utilisez plutôt un try-catch pour Exception générale
            try {
                allBudgets = budgetService.getAll();
            } catch (Exception e) {
                if (e.getMessage() != null && !e.getMessage().contains("doesn't exist")) {
                    logger.log(Level.SEVERE, "Error loading budgets", e);
                }
                allBudgets = new ArrayList<>();
            }

            try {
                allDepenses = depenseService.getAllDepenses();
            } catch (Exception e) {
                if (e.getMessage() != null && !e.getMessage().contains("doesn't exist")) {
                    logger.log(Level.SEVERE, "Error loading depenses", e);
                }
                allDepenses = new ArrayList<>();
            }

            try {
                allPlannings = planningService.getAllPlannings();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading plannings", e);
                allPlannings = new ArrayList<>();
            }

            // Statistiques globales
            double totalBudgets = allBudgets.stream().mapToDouble(Budget::getMontantMax).sum();
            double totalDepenses = allDepenses.stream().mapToDouble(Depense::getMontant).sum();
            double totalWalletBalance = getTotalWalletBalance();
            double totalCardBalance = getTotalCardBalance();

            if (adminTotalBudgetsGlobalLabel != null) {
                adminTotalBudgetsGlobalLabel.setText(String.format("%.2f DT", totalBudgets));
            }
            if (adminTotalDepensesGlobalLabel != null) {
                adminTotalDepensesGlobalLabel.setText(String.format("%.2f DT", totalDepenses));
            }
            if (adminTotalPlanningsGlobalLabel != null) {
                adminTotalPlanningsGlobalLabel.setText(String.valueOf(allPlannings.size()));
            }
            if (adminUsersActifsLabel != null) {
                adminUsersActifsLabel.setText(String.valueOf(getActiveUsersCount()));
            }
            if (adminTotalWalletBalanceLabel != null) {
                adminTotalWalletBalanceLabel.setText(String.format("%.2f DT", totalWalletBalance));
            }
            if (adminTotalCardBalanceLabel != null) {
                adminTotalCardBalanceLabel.setText(String.format("%.2f DT", totalCardBalance));
            }

            // Remplir les tables
            if (adminBudgetsTable != null) {
                adminBudgetsTable.setItems(FXCollections.observableArrayList(allBudgets));
                setupBudgetTableColumns();
            }
            if (adminPlanningsTable != null) {
                adminPlanningsTable.setItems(FXCollections.observableArrayList(allPlannings));
                setupPlanningsTableColumns();
            }
            if (adminDepensesDetailTable != null) {
                adminDepensesDetailTable.setItems(FXCollections.observableArrayList(allDepenses));
                setupAdminDepensesTableColumns();
            }

            // Mettre à jour les graphiques
            updateBudgetChart(allBudgets);
            updateDepensesPieChart(allDepenses);
            updateDepensesCategorieChart(allDepenses);
            updateTopUsersChart(allDepenses);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading budget data", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données: " + e.getMessage());
        } finally {
            isLoadingBudgetData = false;
        }
    }
    private void loadBudgetDataForUser(int userId) {
        if (isLoadingBudgetData) {
            return;
        }
        isLoadingBudgetData = true;

        try {
            List<Budget> budgets = new ArrayList<>();
            List<Depense> depenses = new ArrayList<>();
            List<Planning> plannings = new ArrayList<>();

            try {
                budgets = budgetService.getAllByUser(userId);
            } catch (Exception e) {
                budgets = new ArrayList<>();
            }

            try {
                depenses = depenseService.getAllByUser(userId);
            } catch (Exception e) {
                depenses = new ArrayList<>();
            }

            try {
                plannings = planningService.getPlanningsByUser(userId);
            } catch (Exception e) {
                plannings = new ArrayList<>();
            }



            double totalBudgets = budgets.stream().mapToDouble(Budget::getMontantMax).sum();
            double totalDepenses = depenses.stream().mapToDouble(Depense::getMontant).sum();

            if (adminTotalBudgetsGlobalLabel != null) {
                adminTotalBudgetsGlobalLabel.setText(String.format("%.2f DT", totalBudgets));
            }
            if (adminTotalDepensesGlobalLabel != null) {
                adminTotalDepensesGlobalLabel.setText(String.format("%.2f DT", totalDepenses));
            }
            if (adminTotalPlanningsGlobalLabel != null) {
                adminTotalPlanningsGlobalLabel.setText(String.valueOf(plannings.size()));
            }
            if (adminUsersActifsLabel != null) {
                adminUsersActifsLabel.setText("1");
            }

            if (adminBudgetsTable != null) {
                adminBudgetsTable.setItems(FXCollections.observableArrayList(budgets));
            }
            if (adminPlanningsTable != null) {
                adminPlanningsTable.setItems(FXCollections.observableArrayList(plannings));
            }
            if (adminDepensesDetailTable != null) {
                adminDepensesDetailTable.setItems(FXCollections.observableArrayList(depenses));
            }

            updateBudgetChart(budgets);
            updateDepensesPieChart(depenses);
            updateDepensesCategorieChart(depenses);

            Map<String, Double> userDepenses = new HashMap<>();
            try {
                User user = userService.recupererParId(userId);
                if (user != null) {
                    userDepenses.put(user.getPrenom() + " " + user.getNom(), totalDepenses);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            updateTopUsersChartFromMap(userDepenses);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading budget data for user " + userId, e);
        } finally {
            isLoadingBudgetData = false;
        }
    }

    private void updateBudgetChart(List<Budget> budgets) {
        if (adminBudgetChart == null) return;
        Map<String, Double> budgetByCategory = new HashMap<>();
        for (Budget b : budgets) {
            String cat = b.getCategorie() != null ? b.getCategorie() : "Autre";
            budgetByCategory.put(cat, budgetByCategory.getOrDefault(cat, 0.0) + b.getMontantMax());
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Budget");
        for (Map.Entry<String, Double> entry : budgetByCategory.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        adminBudgetChart.getData().clear();
        adminBudgetChart.getData().add(series);
    }

    private void updateDepensesPieChart(List<Depense> depenses) {
        if (adminDepensesPieChart == null) return;
        Map<String, Double> depenseByCategory = new HashMap<>();
        for (Depense d : depenses) {
            String cat = d.getCategorie() != null ? d.getCategorie() : "Autre";
            depenseByCategory.put(cat, depenseByCategory.getOrDefault(cat, 0.0) + d.getMontant());
        }
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : depenseByCategory.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey() + " (" + String.format("%.2f", entry.getValue()) + " DT)", entry.getValue()));
        }
        adminDepensesPieChart.setData(pieData);
    }

    private void updateDepensesCategorieChart(List<Depense> depenses) {
        if (adminDepensesCategorieChart == null) return;
        Map<String, Double> depenseByCategory = new HashMap<>();
        for (Depense d : depenses) {
            String cat = d.getCategorie() != null ? d.getCategorie() : "Autre";
            depenseByCategory.put(cat, depenseByCategory.getOrDefault(cat, 0.0) + d.getMontant());
        }
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : depenseByCategory.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey() + " (" + String.format("%.2f", entry.getValue()) + " DT)", entry.getValue()));
        }
        adminDepensesCategorieChart.setData(pieData);
    }

    private void updateTopUsersChart(List<Depense> depenses) {
        if (adminTopUsersChart == null) return;
        Map<String, Double> depenseByUser = new HashMap<>();
        try {
            List<User> users = userService.recuperer();
            Map<Integer, String> userNames = new HashMap<>();
            for (User u : users) {
                userNames.put(u.getId(), u.getPrenom() + " " + u.getNom());
            }
            for (Depense d : depenses) {
                String userName = userNames.getOrDefault(d.getUserId(), "User #" + d.getUserId());
                depenseByUser.put(userName, depenseByUser.getOrDefault(userName, 0.0) + d.getMontant());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateTopUsersChartFromMap(depenseByUser);
    }

    private void updateTopUsersChartFromMap(Map<String, Double> depenseByUser) {
        if (adminTopUsersChart == null) return;
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Dépenses par Utilisateur");
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(depenseByUser.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            series.getData().add(new XYChart.Data<>(sorted.get(i).getKey(), sorted.get(i).getValue()));
        }
        adminTopUsersChart.getData().clear();
        adminTopUsersChart.getData().add(series);
    }

    private void setupBudgetTableColumns() {
        if (adminBudgetsTable == null) return;
        adminBudgetsTable.getColumns().clear();

        TableColumn<Budget, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<Budget, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUserId()));

        TableColumn<Budget, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategorie()));

        TableColumn<Budget, Double> montantCol = new TableColumn<>("Montant Max");
        montantCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMontantMax()));

        TableColumn<Budget, Integer> moisCol = new TableColumn<>("Mois");
        moisCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMois()));

        TableColumn<Budget, Integer> anneeCol = new TableColumn<>("Année");
        anneeCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAnnee()));

        adminBudgetsTable.getColumns().addAll(idCol, userIdCol, categorieCol, montantCol, moisCol, anneeCol);
    }

    private void setupPlanningsTableColumns() {
        if (adminPlanningsTable == null) return;
        adminPlanningsTable.getColumns().clear();

        TableColumn<Planning, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<Planning, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUserId()));

        TableColumn<Planning, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));

        TableColumn<Planning, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategorie()));

        TableColumn<Planning, Double> budgetCol = new TableColumn<>("Budget");
        budgetCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBudgetTotal()));

        TableColumn<Planning, Double> depenseCol = new TableColumn<>("Dépensé");
        depenseCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDepensesActuelles()));

        TableColumn<Planning, Double> resteCol = new TableColumn<>("Reste");
        resteCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReste()));

        TableColumn<Planning, Double> progressionCol = new TableColumn<>("Progression %");
        progressionCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPourcentage()));

        TableColumn<Planning, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));

        adminPlanningsTable.getColumns().addAll(idCol, userIdCol, nomCol, categorieCol, budgetCol, depenseCol, resteCol, progressionCol, statutCol);
    }

    private void setupAdminDepensesTableColumns() {
        if (adminDepensesDetailTable == null) return;
        adminDepensesDetailTable.getColumns().clear();

        TableColumn<Depense, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<Depense, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUserId()));

        TableColumn<Depense, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Depense, Double> montantCol = new TableColumn<>("Montant");
        montantCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMontant()));

        TableColumn<Depense, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategorie()));

        TableColumn<Depense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateDepense() != null) {
                return new SimpleStringProperty(cellData.getValue().getDateDepense().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("-");
        });

        adminDepensesDetailTable.getColumns().addAll(idCol, userIdCol, descriptionCol, montantCol, categorieCol, dateCol);
    }

    private double getTotalWalletBalance() {
        try {
            List<Wallet> wallets = walletService.getAll();
            return wallets.stream().mapToDouble(Wallet::getBalance).sum();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total wallet balance", e);
            return 0;
        }
    }


    private double getTotalCardBalance() {
        try {
            List<BankCard> cards = getAllBankCardsWithUsers();
            return cards.stream().mapToDouble(BankCard::getBalance).sum();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total card balance", e);
            return 0;
        }
    }
    private int getActiveUsersCount() {
        try {
            List<User> users = userService.recuperer();
            return (int) users.stream()
                    .filter(u -> "APPROVED".equals(u.getStatus()) && u.isIs_actif())
                    .count();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting active users count", e);
            return 0;
        }
    }

    // ======================= GESTION DES CARTES BANCAIRES =======================
    private List<BankCard> getAllBankCardsWithUsers() throws SQLException {
        List<BankCard> cards = new ArrayList<>();
        String sql = "SELECT bc.*, u.nom, u.prenom FROM bank_card bc JOIN users u ON bc.user_id = u.id ORDER BY u.nom, u.prenom, bc.id";

        try (Connection conn = MyDataBase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BankCard card = new BankCard();
                card.setId(rs.getInt("id"));
                card.setUserId(rs.getInt("user_id"));
                card.setCardHolder(rs.getString("card_holder"));
                card.setCardNumber(rs.getString("card_number"));
                card.setExpiryDate(rs.getString("expiry_date"));
                card.setCvv(rs.getString("cvv"));
                card.setCardType(rs.getString("card_type"));
                card.setRib(rs.getString("rib"));
                card.setBalance(rs.getDouble("balance"));
                cards.add(card);
            }
        }
        return cards;
    }

    private String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return "**** **** **** " + (cardNumber != null && cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "****");
        }
        return cardNumber.replaceAll("(.{4})(.{4})(.{4})(.{4})", "$1 $2 $3 $4");
    }

    private VBox createElegantBankCard(BankCard card, String userName) {
        VBox cardBox = new VBox(12);
        cardBox.setPrefWidth(320);
        cardBox.setPrefHeight(220);
        String normalStyle = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667eea, #764ba2);" +
                "-fx-background-radius: 20;-fx-padding: 25 20 20 20;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);-fx-cursor: hand;";
        cardBox.setStyle(normalStyle);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Circle chip = new Circle(8);
        chip.setFill(javafx.scene.paint.Color.GOLD);
        Label cardTypeLabel = new Label(card.getCardType() != null ? card.getCardType() : "BANCAIRE");
        cardTypeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label bankLabel = new Label("SMARTWALLET");
        bankLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 12px; -fx-font-weight: bold;");
        header.getChildren().addAll(chip, cardTypeLabel, spacer, bankLabel);

        String formattedNumber = formatCardNumber(card.getCardNumber());
        Label numberLabel = new Label(formattedNumber);
        numberLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: 'Courier New'; -fx-font-weight: bold;");

        HBox holderBox = new HBox(20);
        holderBox.setAlignment(Pos.CENTER_LEFT);
        Label holderLabel = new Label(card.getCardHolder().toUpperCase());
        holderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label expiryLabel = new Label("EXP: " + card.getExpiryDate());
        expiryLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 12px; -fx-font-weight: bold;");
        holderBox.getChildren().addAll(holderLabel, expiryLabel);

        HBox adminInfoBox = new HBox(15);
        adminInfoBox.setAlignment(Pos.CENTER_LEFT);
        adminInfoBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10; -fx-padding: 10;");
        Label userLabel = new Label("👤 " + userName);
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label balanceLabel = new Label(String.format("💰 %.2f DT", card.getBalance()));
        balanceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label idLabel = new Label("🆔 " + card.getId());
        idLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 10px;");
        adminInfoBox.getChildren().addAll(userLabel, balanceLabel, idLabel);

        cardBox.getChildren().addAll(header, numberLabel, holderBox, adminInfoBox);
        cardBox.setOnMouseClicked(e -> showDetailedCardInfo(card, userName));
        return cardBox;
    }

    private void showDetailedCardInfo(BankCard card, String userName) {
        String message = String.format("DÉTAILS DE LA CARTE\n\nID: %d\nUtilisateur: %s\nTitulaire: %s\nNuméro: %s\nExpiration: %s\nCVV: %s\nType: %s\nRIB: %s\nSolde: %.2f DT",
                card.getId(), userName, card.getCardHolder(), card.getCardNumber(), card.getExpiryDate(),
                card.getCvv(), card.getCardType(), card.getRib() != null ? card.getRib() : "Non spécifié", card.getBalance());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la carte");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void loadAllBankCards() {
        try {
            List<BankCard> cards = getAllBankCardsWithUsers();
            List<User> users = userService.recuperer();
            Map<Integer, String> userNames = new HashMap<>();
            for (User u : users) userNames.put(u.getId(), u.getPrenom() + " " + u.getNom());

            bankCardsContainer.getChildren().clear();
            bankCardsContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 15;");

            int totalCards = cards.size();
            double totalBalance = cards.stream().mapToDouble(BankCard::getBalance).sum();
            long totalUsers = cards.stream().map(BankCard::getUserId).distinct().count();

            HBox statsBar = new HBox(20);
            statsBar.setAlignment(Pos.CENTER_LEFT);
            statsBar.setStyle("-fx-padding: 10 0 20 0;");

            VBox totalCardsBox = createStatBox("📊 Total Cartes", String.valueOf(totalCards), "#4f46e5");
            VBox totalUsersBox = createStatBox("👥 Utilisateurs", String.valueOf(totalUsers), "#3b82f6");
            VBox totalBalanceBox = createStatBox("💰 Solde Total", String.format("%.2f DT", totalBalance), "#10b981");
            statsBar.getChildren().addAll(totalCardsBox, totalUsersBox, totalBalanceBox);
            bankCardsContainer.getChildren().add(statsBar);

            Label titleLabel = new Label("💳 Cartes Bancaires des Utilisateurs");
            titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 10 0 20 0;");
            bankCardsContainer.getChildren().add(titleLabel);

            if (cards.isEmpty()) {
                Label noCards = new Label("❌ Aucune carte bancaire trouvée");
                noCards.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b; -fx-padding: 50;");
                bankCardsContainer.getChildren().add(noCards);
                return;
            }

            VBox cardsWrapper = new VBox();
            cardsWrapper.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 15;");

            GridPane cardsGrid = new GridPane();
            cardsGrid.setHgap(25);
            cardsGrid.setVgap(25);
            cardsGrid.setAlignment(Pos.TOP_LEFT);
            cardsGrid.setStyle("-fx-background-color: white; -fx-padding: 10;");

            int col = 0, row = 0;
            for (BankCard card : cards) {
                String userName = userNames.getOrDefault(card.getUserId(), "Utilisateur inconnu");
                VBox cardBox = createElegantBankCard(card, userName);
                cardsGrid.add(cardBox, col, row);
                col++;
                if (col > 2) { col = 0; row++; }
            }
            cardsWrapper.getChildren().add(cardsGrid);
            bankCardsContainer.getChildren().add(cardsWrapper);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading bank cards", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les cartes bancaires: " + e.getMessage());
        }
    }

    private VBox createStatBox(String label, String value, String color) {
        VBox box = new VBox(5);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; -fx-min-width: 180; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        box.setAlignment(Pos.CENTER_LEFT);
        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        box.getChildren().addAll(labelLbl, valueLbl);
        return box;
    }

    @FXML
    private void refreshBankCards() {
        loadAllBankCards();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Liste des cartes rafraîchie !");
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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching users", e);
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
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching transactions", e);
        }
    }

    @FXML
    private void handleSearchAccountRequests() {
        String searchText = searchAccountField.getText().toLowerCase().trim();
        try {
            List<User> pendingUsers = userService.getUsersEnAttente();
            if (searchText.isEmpty()) {
                tableAccountRequests.setItems(FXCollections.observableArrayList(pendingUsers));
                return;
            }
            List<User> filtered = pendingUsers.stream()
                    .filter(u -> u.getNom().toLowerCase().contains(searchText) ||
                            u.getPrenom().toLowerCase().contains(searchText) ||
                            u.getEmail().toLowerCase().contains(searchText) ||
                            (u.getTelephone() != null && u.getTelephone().toLowerCase().contains(searchText)))
                    .collect(Collectors.toList());
            tableAccountRequests.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching account requests", e);
        }
    }

    @FXML
    private void handleSearchWalletRequests() {
        String searchText = searchWalletField.getText().toLowerCase().trim();
        try {
            List<Wallet> pendingWallets = walletService.getWalletsEnAttente();
            if (searchText.isEmpty()) {
                tableWalletRequests.setItems(FXCollections.observableArrayList(pendingWallets));
                return;
            }
            List<Wallet> filtered = pendingWallets.stream()
                    .filter(w -> (w.getUserNom() != null && w.getUserNom().toLowerCase().contains(searchText)) ||
                            (w.getUserPrenom() != null && w.getUserPrenom().toLowerCase().contains(searchText)) ||
                            (w.getUserEmail() != null && w.getUserEmail().toLowerCase().contains(searchText)))
                    .collect(Collectors.toList());
            tableWalletRequests.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching wallet requests", e);
        }
    }

    @FXML
    private void searchServices() {
        String searchText = searchServiceField.getText().toLowerCase().trim();
        System.out.println("Searching services for: " + searchText);
    }

    // ======================= ACCOUNT REQUEST HANDLERS =======================
    private void handleApproveAccount(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Account");
        confirm.setHeaderText("Approve account for " + user.getNom() + " " + user.getPrenom());
        confirm.setContentText("Are you sure you want to approve this account?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.approuverCompte(user.getId());
                refreshAccountRequests();
                loadDashboardData();
                loadAccountStatusChart();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account approved successfully for " + user.getNom() + " " + user.getPrenom());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error approving account", e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve account: " + e.getMessage());
            }
        }
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
                refreshAccountRequests();
                loadAccountStatusChart();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account rejected.");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error rejecting account", e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject account.");
            }
        });
    }

    @FXML
    private void refreshAccountRequests() {
        try {
            List<User> pendingUsers = userService.getUsersEnAttente();
            tableAccountRequests.setItems(FXCollections.observableArrayList(pendingUsers));
            if (pendingCountLabel != null) pendingCountLabel.setText(pendingUsers.size() + " en attente");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error refreshing account requests", e);
        }
    }

    private void refreshWalletRequests() {
        try {
            List<Wallet> pendingWallets = walletService.getWalletsEnAttente();
            tableWalletRequests.setItems(FXCollections.observableArrayList(pendingWallets));
            if (pendingWalletsCountLabel != null) pendingWalletsCountLabel.setText(pendingWallets.size() + " en attente");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error refreshing wallet requests", e);
        }
    }

    private void showUserDetails(User user) {
        String message = String.format("User Details:\n\nID: %d\nName: %s %s\nEmail: %s\nPhone: %s\nRole: %s\nStatus: %s\nCreated: %s",
                user.getId(), user.getPrenom(), user.getNom(), user.getEmail(),
                user.getTelephone() != null ? user.getTelephone() : "-",
                user.getRole(), user.getStatus(),
                user.getDate_creation() != null ? user.getDate_creation().toString() : "-");
        showAlert(Alert.AlertType.INFORMATION, "User Details", message);
    }

    private void handleEditUser(User user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("💾 Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // ── Form fields ───────────────────────────────────────────────────────
        TextField tfNom    = new TextField(user.getNom()       != null ? user.getNom()       : "");
        TextField tfPrenom = new TextField(user.getPrenom()    != null ? user.getPrenom()    : "");
        TextField tfEmail  = new TextField(user.getEmail()     != null ? user.getEmail()     : "");
        TextField tfTel    = new TextField(user.getTelephone() != null ? user.getTelephone() : "");
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Active", "Inactive");
        cbStatus.setValue(user.isIs_actif() ? "Active" : "Inactive");

        // ── Shared field style matching .input from DashboardAdmin.css ────────
        String fieldStyle =
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-font-size: 13px;" +
                        "-fx-pref-width: 240px;";
        String fieldFocusedStyle =
                "-fx-background-color: white;" +
                        "-fx-border-color: #7C4DFF;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-font-size: 13px;" +
                        "-fx-pref-width: 240px;";

        for (TextField tf : new TextField[]{tfNom, tfPrenom, tfEmail, tfTel}) {
            tf.setStyle(fieldStyle);
            tf.focusedProperty().addListener((obs, o, focused) ->
                    tf.setStyle(focused ? fieldFocusedStyle : fieldStyle));
        }
        cbStatus.setStyle(fieldStyle + "-fx-cursor: hand;");

        String labelStyle =
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #6b7280;" +
                        "-fx-pref-width: 90px;";

        // ── Header banner ─────────────────────────────────────────────────────
        Label titleLabel = new Label("✏️  Edit User");
        titleLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );
        Label subLabel = new Label(user.getPrenom() + " " + user.getNom() + " · " + user.getEmail());
        subLabel.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: rgba(255,255,255,0.8);"
        );
        VBox headerBox = new VBox(4, titleLabel, subLabel);
        headerBox.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E88E5, #7C4DFF);" +
                        "-fx-padding: 18 24 18 24;" +
                        "-fx-background-radius: 8 8 0 0;"
        );
        headerBox.setMaxWidth(Double.MAX_VALUE);

        // ── Form grid ─────────────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 8, 24));

        Label[] labels = {
                new Label("Last Name:"),
                new Label("First Name:"),
                new Label("Email:"),
                new Label("Phone:"),
                new Label("Status:")
        };
        for (Label l : labels) l.setStyle(labelStyle);

        grid.add(labels[0], 0, 0); grid.add(tfNom,    1, 0);
        grid.add(labels[1], 0, 1); grid.add(tfPrenom, 1, 1);
        grid.add(labels[2], 0, 2); grid.add(tfEmail,  1, 2);
        grid.add(labels[3], 0, 3); grid.add(tfTel,    1, 3);
        grid.add(labels[4], 0, 4); grid.add(cbStatus, 1, 4);

        VBox content = new VBox(0, headerBox, grid);
        content.setStyle(
                "-fx-background-color: #F0F2F8;" +
                        "-fx-background-radius: 8;"
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #F0F2F8;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 5);" +
                        "-fx-padding: 0;"
        );

        // ── Style the Save button to match .btn-gradient ──────────────────────
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E88E5, #7C4DFF);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 20 8 20;"
        );
        javafx.scene.Node cancelButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle(
                "-fx-background-color: #f3f4f6;" +
                        "-fx-text-fill: #374151;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 20 8 20;"
        );

        // Disable Save if required fields are empty
        saveButton.setDisable(false);
        tfNom.textProperty().addListener((obs, o, n) ->
                saveButton.setDisable(n.trim().isEmpty() || tfEmail.getText().trim().isEmpty()));
        tfEmail.textProperty().addListener((obs, o, n) ->
                saveButton.setDisable(n.trim().isEmpty() || tfNom.getText().trim().isEmpty()));

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveButtonType) {
                if (tfNom.getText().trim().isEmpty() || tfEmail.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                            "Last name and email are required.");
                    return;
                }
                user.setNom(tfNom.getText().trim());
                user.setPrenom(tfPrenom.getText().trim());
                user.setEmail(tfEmail.getText().trim());
                user.setTelephone(tfTel.getText().trim());
                user.setIs_actif(cbStatus.getValue().equals("Active"));
                try {
                    userService.modifier(user);
                    loadDashboardData();
                    loadAdditionalData();
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "User " + user.getPrenom() + " " + user.getNom() + " updated successfully.");
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error updating user", e);
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Failed to update user: " + e.getMessage());
                }
            }
        });
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
                    loadAccountStatusChart();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error deleting user", e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
                }
            }
        });
    }

    private void handleApproveWallet(Wallet wallet) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Wallet");
        confirm.setHeaderText("Approve wallet for " + wallet.getUserFullName());
        confirm.setContentText("Are you sure you want to approve this wallet request?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                walletService.approuverWallet(wallet.getId());
                refreshWalletRequests();
                loadWalletStatusChart();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Wallet approved successfully!");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error approving wallet", e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve wallet: " + e.getMessage());
            }
        }
    }

    private void handleRejectWallet(Wallet wallet) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Wallet");
        dialog.setHeaderText("Reject wallet for " + wallet.getUserFullName());
        dialog.setContentText("Reason for rejection:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            try {
                walletService.rejeterWallet(wallet.getId());
                refreshWalletRequests();
                loadWalletStatusChart();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Wallet rejected.\nReason: " + reason);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error rejecting wallet", e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject wallet: " + e.getMessage());
            }
        });
    }

    private void showWalletDetails(Wallet wallet) {
        String message = String.format("Wallet Details:\n\nID: %d\nUser: %s\nEmail: %s\nBalance: %s DT\nType: %s\nStatus: %s\nCreated: %s",
                wallet.getId(), wallet.getUserFullName(), wallet.getUserEmail(),
                df.format(wallet.getBalance()), wallet.getType(), wallet.getStatus(),
                wallet.getCreatedAt() != null ? wallet.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
        showAlert(Alert.AlertType.INFORMATION, "Wallet Details", message);
    }

    private void handleEditService(Services service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/services/ModifierService.fxml"));
            Parent root = loader.load();
            ModifierService controller = loader.getController();
            controller.setService(service);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Service");
            stage.show();
            stage.setOnHidden(e -> loadServicesData());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error editing service", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de modification.");
        }
    }

    private void handleDeleteService(Services service) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer ce service ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé avec succès.");
                loadServicesData();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error deleting service", e);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le service.");
            }
        }
    }

    // ======================= ACTIONS =======================
    @FXML
    private void handleManageReclamations() {
        MainFxml.getInstance().showReclamationAdmin();
    }

    @FXML
    private void handleNotifications() {
        MainFxml.getInstance().showNotifications();
    }








    @FXML
    private void showDashboardDepenses() {
        try {
            MainFxml.getInstance().openBudgetExpensesPopup();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le dashboard des dépenses: " + e.getMessage());
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

    @FXML
    private void refreshBudgetData() {
        loadAllBudgetData();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Données rafraîchies !");
    }

    @FXML
    private void onUserFilterChange() {
        // Éviter la récursion
        if (isLoadingBudgetData) {
            return;
        }

        Integer selectedUserId = adminUserFilterCombo.getValue();
        if (selectedUserId != null && selectedUserId > 0) {
            loadBudgetDataForUser(selectedUserId);
        } else {
            loadAllBudgetData();
        }
    }

    @FXML
    private void showAllUsersBudget() {
        if (adminUserFilterCombo != null) {
            adminUserFilterCombo.setValue(0);
        }
        // Ne pas appeler loadAllBudgetData() ici car le changement de valeur
        // du ComboBox déclenchera onUserFilterChange()
    }



    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getStatusStyle(String status) {
        switch (status) {
            case "APPROVED":
            case "ACTIF":
                return "-fx-background-color: #22c55e20; -fx-text-fill: #22c55e; -fx-padding: 3 8; -fx-background-radius: 10;";
            case "PENDING":
                return "-fx-background-color: #f59e0b20; -fx-text-fill: #f59e0b; -fx-padding: 3 8; -fx-background-radius: 10;";
            case "REJECTED":
                return "-fx-background-color: #ef444420; -fx-text-fill: #ef4444; -fx-padding: 3 8; -fx-background-radius: 10;";
            default:
                return "-fx-background-color: #64748b20; -fx-text-fill: #64748b; -fx-padding: 3 8; -fx-background-radius: 10;";
        }
    }

    private void forceRefreshAllTables() {
        Platform.runLater(() -> {
            if (tableUsers != null) tableUsers.refresh();
            if (tableTransactions != null) tableTransactions.refresh();
            if (tableWallets != null) tableWallets.refresh();
            if (tableAccountRequests != null) tableAccountRequests.refresh();
            if (tableWalletRequests != null) tableWalletRequests.refresh();
            if (tableServices != null) tableServices.refresh();
            if (pieReclamations != null) pieReclamations.requestLayout();
            if (lineUsers != null) lineUsers.requestLayout();
            if (pieAccountStatus != null) pieAccountStatus.requestLayout();
            if (pieWalletStatus != null) pieWalletStatus.requestLayout();
        });
    }

    private void addTabPaneListener() {
        Platform.runLater(() -> {
            try {
                TabPane tabPane = null;
                if (tableWallets != null && tableWallets.getScene() != null) {
                    tabPane = (TabPane) tableWallets.getScene().lookup(".tab-pane");
                }
                if (tabPane != null) {
                    tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                        if (newTab != null) {
                            String tabText = newTab.getText();
                            Platform.runLater(() -> {
                                if (tabText.contains("Wallets") && tableWallets != null) tableWallets.refresh();
                                else if (tabText.contains("Reclamations") && pieReclamations != null) pieReclamations.requestLayout();
                                else if (tabText.contains("Users") && tableUsers != null) tableUsers.refresh();
                                else if (tabText.contains("Transactions") && tableTransactions != null) tableTransactions.refresh();
                                else if (tabText.contains("Account Requests") && tableAccountRequests != null) tableAccountRequests.refresh();
                                else if (tabText.contains("Wallet Requests") && tableWalletRequests != null) tableWalletRequests.refresh();
                                else if (tabText.contains("Cartes Bancaires") && bankCardsContainer != null) loadAllBankCards();
                                else if (tabText.contains("Budget") || tabText.contains("Dépenses")) loadAllBudgetData();
                            });
                        }
                    });
                }
            } catch (Exception e) {
                System.out.println("⚠️ Impossible d'ajouter le listener au TabPane: " + e.getMessage());
            }
        });
    }








    // ======================= POPUP METHODE CORRIGÉE =======================


    @FXML
    private void handleManageUsers() {
        try {
            MainFxml.getInstance().showManageUsers();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la gestion des utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageWallets() {
        showAlert(Alert.AlertType.INFORMATION, "Manage Wallets",
                "Wallet management functionality will be implemented soon.\n\n" +
                        "For now, you can view wallets in the Wallets tab.");
    }

    @FXML
    private void handleRefreshData() {
        try {
            loadDashboardData();
            loadAdditionalData();
            loadDistributionChart();
            loadAccountStatusChart();
            loadWalletStatusChart();
            loadServicesData();
            loadAllBudgetData();
            showAlert(Alert.AlertType.INFORMATION, "Refresh", "Data refreshed successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error refreshing data", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rafraîchissement: " + e.getMessage());
        }
    }

    @FXML
    private void refreshServices() {
        try {
            loadServicesData();
            showAlert(Alert.AlertType.INFORMATION, "Services", "Services data refreshed.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error refreshing services", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de rafraîchir les services.");
        }
    }

    @FXML
    private void approveAllRequests() {
        try {
            List<User> pendingUsers = userService.getUsersEnAttente();
            if (pendingUsers.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "No pending requests to approve.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Approve All");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to approve all " + pendingUsers.size() + " pending requests?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (User user : pendingUsers) {
                    userService.approuverCompte(user.getId());
                }
                refreshAccountRequests();
                loadDashboardData();
                loadAccountStatusChart();
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        pendingUsers.size() + " accounts approved successfully.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error approving all requests", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve all requests: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        showAlert(Alert.AlertType.INFORMATION, "Add User",
                "Add user functionality will be implemented soon.");
    }

    @FXML
    private void loadDistributionChart() {
        try {
            List<User> users = userService.recuperer();
            long adminCount = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
            long userCount = users.size() - adminCount;

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Admins (" + adminCount + ")", adminCount),
                    new PieChart.Data("Users (" + userCount + ")", userCount)
            );

            if (pieDistribution != null) {
                pieDistribution.setData(pieData);
                pieDistribution.setTitle("User Distribution");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading distribution chart", e);
        }
    }


    // ======================= CREDIT METHODS =======================


    // ======================= BUDGET METHODS =======================
    @FXML
    private void showAllBudgets() {
        if (adminUserFilterCombo != null) {
            adminUserFilterCombo.setValue(0);
        }
        loadAllBudgetData();
        showAlert(Alert.AlertType.INFORMATION, "Info", "Affichage de tous les budgets");
    }

    @FXML
    private void showAllPlannings() {
        if (adminUserFilterCombo != null) {
            adminUserFilterCombo.setValue(0);
        }
        loadAllBudgetData();
        showAlert(Alert.AlertType.INFORMATION, "Info", "Affichage de tous les plannings");
    }

    // ======================= ACCUEIL SERVICES =======================
    @FXML
    private void showAcceuilServices() {
        try {
            ouvrirPopup("/AcceuilService.fxml", "Accueil Services", 1100, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'accueil des services: " + e.getMessage());
        }
    }

    @FXML
    private void showAcceuilServiceClient() {
        try {
            ouvrirPopup("/Acceuil ServiceClient.fxml", "Accueil Service Client", 1100, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'accueil service client: " + e.getMessage());
        }
    }

    // ======================= SERVICE METHODS =======================
    @FXML
    private void showAfficherService() {
        try {
            ouvrirPopup("/services/AfficherService.fxml", "Gestion des Services", 1100, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'affichage des services: " + e.getMessage());
        }
    }

    @FXML
    private void showAjouterService() {
        try {
            ouvrirPopup("/services/AjouterService.fxml", "Ajouter un Service", 900, 600);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'ajout de service: " + e.getMessage());
        }
    }

    @FXML
    private void showGestionService() {
        try {
            ouvrirPopup("/services/GestionService.fxml", "Administration Services", 900, 500);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la gestion des services: " + e.getMessage());
        }
    }

    @FXML
    private void showServiceStats() {
        try {
            ouvrirPopup("/services/StatsService.fxml", "Statistiques des Services", 800, 600);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les statistiques des services: " + e.getMessage());
        }
    }

    @FXML
    private void manageServiceCategories() {
        try {
            ouvrirPopup("/services/GestionCategories.fxml", "Gestion des Catégories", 700, 500);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la gestion des catégories: " + e.getMessage());
        }
    }

    // ======================= ASSURANCE METHODS =======================
    @FXML
    private void showAfficherAssurance() {
        try {
            ouvrirPopup("/assurance/AfficherAssurance.fxml", "Gestion des Assurances", 1100, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'affichage des assurances: " + e.getMessage());
        }
    }

    @FXML
    private void showAfficherAssuranceClient() {
        try {
            ouvrirPopup("/assurance/Afficher AssuranceClient.fxml", "Assurances Client", 1100, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'affichage des assurances client: " + e.getMessage());
        }
    }

    @FXML
    private void showAjouterAssurance() {
        try {
            ouvrirPopup("/assurance/Ajouter Assurance.fxml", "Ajouter une Assurance", 900, 600);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'ajout d'assurance: " + e.getMessage());
        }
    }

    @FXML
    private void showModifierAssurance() {
        try {
            ouvrirPopup("/assurance/ModifierAssurance.fxml", "Modifier une Assurance", 900, 600);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la modification d'assurance: " + e.getMessage());
        }
    }

    @FXML
    private void showGestionAssurance() {
        try {
            ouvrirPopup("/assurance/GestionAssurance.fxml", "Gestion des Assurances", 900, 500);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la gestion des assurances: " + e.getMessage());
        }
    }

    @FXML
    private void haamdiah() {
        showAjouterAssurance();
    }

    // ======================= CREDIT METHODS =======================
    @FXML
    private void showAfficherCredit() {
        try {
            ouvrirPopup("/credit/AfficherCredit.fxml", "Gestion des Crédits", 1100, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'affichage des crédits: " + e.getMessage());
        }
    }

    @FXML
    private void showAfficherCreditClient() {
        try {
            ouvrirPopup("/credit/AfficherCreditClient.fxml", "Crédits Client", 1100, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'affichage des crédits client: " + e.getMessage());
        }
    }

    @FXML
    private void showAjouterCredit() {
        try {
            ouvrirPopup("/credit/Ajouter Credit.fxml", "Ajouter un Crédit", 900, 600);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'ajout de crédit: " + e.getMessage());
        }
    }

    @FXML
    private void showModifierCredit() {
        try {
            ouvrirPopup("/credit/ModifierCredit.fxml", "Modifier un Crédit", 900, 600);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la modification de crédit: " + e.getMessage());
        }
    }

    @FXML
    private void showGestionCredit() {
        try {
            ouvrirPopup("/credit/GestionCredit.fxml", "Gestion des Crédits", 900, 500);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la gestion des crédits: " + e.getMessage());
        }
    }

    @FXML
    private void itafaaction() {
        showAjouterCredit();
    }

    // ======================= POPUP METHODE =======================
    private void ouvrirPopup(String fxmlPath, String titre, int largeur, int hauteur) {
        try {
            // Vérifier si le fichier existe
            if (getClass().getResource(fxmlPath) == null) {
                // Essayer avec un chemin alternatif
                String altPath = "/com/example/smartwallet/" + fxmlPath;
                if (getClass().getResource(altPath) != null) {
                    fxmlPath = altPath;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier introuvable: " + fxmlPath);
                    return;
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root, largeur, hauteur));
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de " + fxmlPath, e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page: " + e.getMessage());
        }
    }
}