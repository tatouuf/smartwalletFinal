package esprit.tn.souha_pi.controllers.admin;

import entities.User;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.UserService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.NotificationService;
import esprit.tn.souha_pi.controllers.WalletLayoutController;
import esprit.tn.souha_pi.utils.DialogUtil;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import utils.Session;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalWalletsLabel;
    @FXML private Label totalTransactionsLabel;
    @FXML private Label totalAmountLabel;

    @FXML private TextField searchUserField;

    @FXML private TableView<User> usersTable;
    @FXML private TableView<Wallet> walletsTable;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableView<User> demandesCompteTable;
    @FXML private TableView<User> demandesWalletTable;

    @FXML private PieChart roleChart;

    private UserService userService = new UserService();
    private WalletService walletService = new WalletService();
    private TransactionService transactionService = new TransactionService();
    private NotificationService notificationService = new NotificationService();

    private User currentAdmin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        currentAdmin = Session.getCurrentUser();

        if (currentAdmin != null) {
            adminNameLabel.setText("Admin: " + currentAdmin.getFullname() + " 👑");
        } else {
            adminNameLabel.setText("Admin: Non connecté");
        }

        configurerTableUtilisateurs();
        configurerTableWallets();
        configurerTableTransactions();

        chargerStatistiques();
        chargerDonnees();
    }

    private void configurerTableUtilisateurs() {

        TableColumn<User, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<User, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullname()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<User, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTelephone() == null ? "" : c.getValue().getTelephone()
        ));

        TableColumn<User, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().name()));

        TableColumn<User, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isIs_actif() ? "ACTIF" : "INACTIF"
        ));

        usersTable.getColumns().addAll(idCol, nomCol, emailCol, telCol, roleCol, statusCol);
    }

    private void configurerTableWallets() {

        TableColumn<Wallet, Number> walletIdCol = new TableColumn<>("ID");
        walletIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Wallet, Number> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getUserId()));

        TableColumn<Wallet, Number> balanceCol = new TableColumn<>("Solde");
        balanceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getBalance()));

        walletsTable.getColumns().addAll(walletIdCol, userIdCol, balanceCol);
    }

    private void configurerTableTransactions() {

        TableColumn<Transaction, Number> transIdCol = new TableColumn<>("ID");
        transIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Transaction, Number> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getUserId()));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));

        TableColumn<Transaction, Number> amountCol = new TableColumn<>("Montant");
        amountCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getAmount()));

        transactionsTable.getColumns().addAll(transIdCol, userIdCol, typeCol, amountCol);
    }

    private void chargerStatistiques() {

        List<User> users = userService.getAll();
        List<Wallet> wallets = walletService.getAll();
        List<Transaction> transactions = transactionService.getAll();

        totalUsersLabel.setText(String.valueOf(users.size()));
        totalWalletsLabel.setText(String.valueOf(wallets.size()));
        totalTransactionsLabel.setText(String.valueOf(transactions.size()));

        double totalAmount = wallets.stream().mapToDouble(Wallet::getBalance).sum();
        totalAmountLabel.setText(String.format("%.2f TND", totalAmount));

        long adminCount = users.stream().filter(u -> u.getRole().name().equals("ADMIN")).count();
        long userCount = users.size() - adminCount;

        roleChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Admins", adminCount),
                new PieChart.Data("Users", userCount)
        ));
    }

    private void chargerDonnees() {
        usersTable.setItems(FXCollections.observableArrayList(userService.getAll()));
        walletsTable.setItems(FXCollections.observableArrayList(walletService.getAll()));
        transactionsTable.setItems(FXCollections.observableArrayList(transactionService.getAll()));
    }
}