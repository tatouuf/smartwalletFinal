package esprit.tn.souha_pi.controllers.admin;

import esprit.tn.souha_pi.entities.User;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    // ======================= LABELS STATISTIQUES =======================
    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalWalletsLabel;
    @FXML private Label totalTransactionsLabel;
    @FXML private Label totalAmountLabel;

    // ======================= CHAMPS DE RECHERCHE =======================
    @FXML private TextField searchUserField;

    // ======================= TABLEAUX =======================
    @FXML private TableView<User> usersTable;
    @FXML private TableView<Wallet> walletsTable;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableView<User> demandesCompteTable;     // Table pour les demandes de compte
    @FXML private TableView<User> demandesWalletTable;     // Table pour les demandes de wallet

    // ======================= GRAPHIQUES =======================
    @FXML private PieChart roleChart;

    // ======================= SERVICES =======================
    private UserService userService = new UserService();
    private WalletService walletService = new WalletService();
    private TransactionService transactionService = new TransactionService();
    private NotificationService notificationService = new NotificationService();

    // ======================= UTILISATEUR CONNECTÉ =======================
    private User currentAdmin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Récupérer l'admin connecté
        currentAdmin = userService.getById(7); // Souha Said est ADMIN

        if (currentAdmin != null) {
            adminNameLabel.setText("Admin: " + currentAdmin.getFullname() + " 👑");
        } else {
            adminNameLabel.setText("Admin: Non connecté");
        }

        // Configurer tous les tableaux
        configurerTableUtilisateurs();
        configurerTableWallets();
        configurerTableTransactions();
        configurerTableDemandesCompte();
        configurerTableDemandesWallet();

        // Charger les données
        chargerStatistiques();
        chargerDonnees();
        chargerDemandesCompte();
        chargerDemandesWallet();
    }

    // ======================= CONFIGURATION DES TABLEAUX =======================

    /**
     * Configuration de la table des utilisateurs
     */
    private void configurerTableUtilisateurs() {
        TableColumn<User, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()));

        TableColumn<User, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullname()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<User, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTelephone() != null ?
                        cellData.getValue().getTelephone() : ""));

        TableColumn<User, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole()));

        TableColumn<User, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");

                editBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    modifierUser(user);
                });

                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    supprimerUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        usersTable.getColumns().addAll(idCol, nomCol, emailCol, telCol, roleCol, statusCol, actionsCol);
    }

    /**
     * Configuration de la table des wallets
     */
    private void configurerTableWallets() {
        TableColumn<Wallet, Number> walletIdCol = new TableColumn<>("ID");
        walletIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()));

        TableColumn<Wallet, Number> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getUserId()));

        TableColumn<Wallet, Number> balanceCol = new TableColumn<>("Solde (TND)");
        balanceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getBalance()));

        walletsTable.getColumns().addAll(walletIdCol, userIdCol, balanceCol);
    }

    /**
     * Configuration de la table des transactions
     */
    private void configurerTableTransactions() {
        TableColumn<Transaction, Number> transIdCol = new TableColumn<>("ID");
        transIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()));

        TableColumn<Transaction, Number> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getUserId()));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));

        TableColumn<Transaction, Number> amountCol = new TableColumn<>("Montant");
        amountCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getAmount()));

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTarget()));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ?
                        cellData.getValue().getCreatedAt().toString() : ""));

        transactionsTable.getColumns().addAll(transIdCol, userIdCol, typeCol, amountCol, descCol, dateCol);
    }

    /**
     * Configuration de la table des demandes de compte
     */
    private void configurerTableDemandesCompte() {
        TableColumn<User, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));

        TableColumn<User, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullname()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<User, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTelephone() != null ? cellData.getValue().getTelephone() : ""));

        TableColumn<User, String> dateCol = new TableColumn<>("Date demande");
        dateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateCreation() != null ?
                        cellData.getValue().getDateCreation().toLocalDate().toString() : ""));

        TableColumn<User, String> roleCol = new TableColumn<>("Rôle demandé");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approuverBtn = new Button("✅ Approuver");
            private final Button refuserBtn = new Button("❌ Refuser");
            private final HBox pane = new HBox(10, approuverBtn, refuserBtn);

            {
                approuverBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 5 10;");
                refuserBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 5 10;");

                approuverBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    approuverCompte(user);
                });

                refuserBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    refuserCompte(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        demandesCompteTable.getColumns().addAll(idCol, nomCol, emailCol, telCol, dateCol, roleCol, actionsCol);
    }

    /**
     * Configuration de la table des demandes de wallet
     */
    private void configurerTableDemandesWallet() {
        TableColumn<User, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));

        TableColumn<User, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullname()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<User, Number> montantCol = new TableColumn<>("Montant demandé");
        montantCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(0));

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button creerBtn = new Button("💰 Créer Wallet");
            private final HBox pane = new HBox(10, creerBtn);

            {
                creerBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 5 10;");

                creerBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    creerWallet(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        demandesWalletTable.getColumns().addAll(idCol, nomCol, emailCol, montantCol, actionsCol);
    }

    // ======================= CHARGEMENT DES DONNÉES =======================

    private void chargerStatistiques() {
        List<User> users = userService.getAll();
        List<Wallet> wallets = walletService.getAll();
        List<Transaction> transactions = transactionService.getAll();

        totalUsersLabel.setText(String.valueOf(users.size()));
        totalWalletsLabel.setText(String.valueOf(wallets.size()));
        totalTransactionsLabel.setText(String.valueOf(transactions.size()));

        double totalAmount = wallets.stream()
                .mapToDouble(Wallet::getBalance)
                .sum();
        totalAmountLabel.setText(String.format("%.2f TND", totalAmount));

        // Statistiques par rôle
        long adminCount = users.stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .count();
        long userCount = users.size() - adminCount;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Administrateurs (" + adminCount + ")", adminCount),
                new PieChart.Data("Utilisateurs (" + userCount + ")", userCount)
        );
        roleChart.setData(pieData);
    }

    private void chargerDonnees() {
        usersTable.setItems(FXCollections.observableArrayList(userService.getAll()));
        walletsTable.setItems(FXCollections.observableArrayList(walletService.getAll()));
        transactionsTable.setItems(FXCollections.observableArrayList(transactionService.getAll()));
    }

    /**
     * Charger les demandes de compte (utilisateurs avec statut EN_ATTENTE)
     */
    private void chargerDemandesCompte() {
        List<User> enAttente = userService.getEnAttente();
        demandesCompteTable.setItems(FXCollections.observableArrayList(enAttente));
    }

    /**
     * Charger les demandes de wallet (utilisateurs avec compte approuvé mais sans wallet)
     */
    private void chargerDemandesWallet() {
        List<User> users = userService.getAll();
        List<User> sansWallet = new ArrayList<>();

        for (User user : users) {
            try {
                walletService.getByUserId(user.getId());
            } catch (Exception e) {
                sansWallet.add(user);
            }
        }
        demandesWalletTable.setItems(FXCollections.observableArrayList(sansWallet));
    }

    // ======================= GESTION DES DEMANDES DE COMPTE =======================

    /**
     * Approuver une demande de compte avec notification
     */
    private void approuverCompte(User user) {
        try {
            // Approuver le compte
            userService.approuverCompte(user.getId());

            // Envoyer notification (email + SMS)
            notificationService.notifierApprobation(user);

            // Recharger les tables
            chargerDemandesCompte();
            chargerDemandesWallet();
            chargerDonnees();

            DialogUtil.success("Succès",
                    "✅ Compte de " + user.getFullname() + " approuvé\n" +
                            "📧 Email envoyé à " + user.getEmail() + "\n" +
                            "📱 SMS envoyé au " + user.getTelephone());

        } catch (Exception e) {
            DialogUtil.error("Erreur", e.getMessage());
        }
    }

    /**
     * Refuser une demande de compte avec notification
     */
    private void refuserCompte(User user) {
        if (DialogUtil.confirm("Confirmation", "❌ Refuser le compte de " + user.getFullname() + " ?")) {
            try {
                // Envoyer notification de rejet
                notificationService.notifierRejet(user);

                // Supprimer l'utilisateur
                userService.rejeterUser(user.getId());

                chargerDemandesCompte();

                DialogUtil.success("Succès",
                        "Demande refusée\n" +
                                "📧 Email de notification envoyé à " + user.getEmail());

            } catch (Exception e) {
                DialogUtil.error("Erreur", e.getMessage());
            }
        }
    }

    // ======================= GESTION DES DEMANDES DE WALLET =======================

    /**
     * Créer un wallet pour un utilisateur
     */
    private void creerWallet(User user) {
        // Créer un dialogue personnalisé pour le montant
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Création de wallet");
        dialog.setHeaderText("💰 Créer un wallet pour " + user.getFullname());

        ButtonType creerButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(creerButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField montantField = new TextField();
        montantField.setPromptText("50");
        montantField.setText("50");

        ComboBox<String> typeCarteCombo = new ComboBox<>();
        typeCarteCombo.getItems().addAll("Wallet Standard", "Carte Virtuelle", "Carte Physique", "Premium");
        typeCarteCombo.setValue("Wallet Standard");

        grid.add(new Label("Montant initial (TND):"), 0, 0);
        grid.add(montantField, 1, 0);
        grid.add(new Label("Type de carte:"), 0, 1);
        grid.add(typeCarteCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == creerButtonType) {
                try {
                    return Double.parseDouble(montantField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(montant -> {
            if (montant == null || montant <= 0) {
                DialogUtil.error("Erreur", "Montant invalide");
                return;
            }

            try {
                walletService.creerWallet(user.getId(), montant);

                chargerDemandesWallet();
                chargerDonnees();
                chargerStatistiques();

                DialogUtil.success("Succès", "✅ Wallet créé pour " + user.getFullname() +
                        " avec " + montant + " TND");

            } catch (Exception e) {
                DialogUtil.error("Erreur", e.getMessage());
            }
        });
    }

    // ======================= ACTIONS SUR LES UTILISATEURS =======================

    @FXML
    private void rechercherUser() {
        String search = searchUserField.getText().toLowerCase().trim();
        List<User> allUsers = userService.getAll();

        if (search.isEmpty()) {
            usersTable.setItems(FXCollections.observableArrayList(allUsers));
            return;
        }

        List<User> filtered = allUsers.stream()
                .filter(u -> u.getFullname().toLowerCase().contains(search) ||
                        u.getEmail().toLowerCase().contains(search) ||
                        (u.getTelephone() != null && u.getTelephone().contains(search)))
                .toList();

        usersTable.setItems(FXCollections.observableArrayList(filtered));
        DialogUtil.success("Recherche", filtered.size() + " résultat(s) trouvé(s)");
    }

    @FXML
    private void ajouterUser() {
        WalletLayoutController.instance.openInscription();
    }

    private void modifierUser(User user) {
        DialogUtil.success("Modification", "Modification de " + user.getFullname() + "\nFonctionnalité à venir");
    }

    private void supprimerUser(User user) {
        if (user.getId() == currentAdmin.getId()) {
            DialogUtil.error("Erreur", "Vous ne pouvez pas supprimer votre propre compte");
            return;
        }

        if (DialogUtil.confirm("Confirmation", "Supprimer " + user.getFullname() + " ?")) {
            userService.delete(user.getId());
            chargerDonnees();
            chargerStatistiques();
            DialogUtil.success("Succès", "✅ Utilisateur supprimé");
        }
    }
}