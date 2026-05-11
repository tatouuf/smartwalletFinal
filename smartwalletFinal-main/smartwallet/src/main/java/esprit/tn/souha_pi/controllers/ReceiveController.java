package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Transaction;
import entities.User;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.DialogUtil;
import esprit.tn.souha_pi.utils.MyDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.Session;

import java.sql.*;
import java.time.LocalDateTime;

public class ReceiveController {

    @FXML private TextField ribField;        // RIB de l'expéditeur
    @FXML private TextField amountField;     // Montant à recevoir
    @FXML private TextField senderNameField; // Nom de l'expéditeur (optionnel)
    @FXML private Label balanceLabel;        // Solde actuel
    @FXML private Label infoLabel;           // Informations

    private WalletService walletService = new WalletService();
    private BankCardService cardService = new BankCardService();
    private TransactionService transactionService = new TransactionService();

    private User currentUser = Session.getCurrentUser();
    private int currentUserId;

    @FXML
    public void initialize() {
        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        currentUserId = currentUser.getId();

        try {
            // Afficher le solde actuel
            double balance = walletService.getByUserId(currentUserId).getBalance();
            balanceLabel.setText(String.format("Solde actuel: %.2f TND", balance));

            infoLabel.setText("Entrez le RIB de l'expéditeur pour recevoir de l'argent");

        } catch (Exception e) {
            balanceLabel.setText("Solde: 0.00 TND");
        }
    }

    @FXML
    private void receive() {
        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        String rib = ribField.getText().trim().replace(" ", "");
        String amountStr = amountField.getText().trim();

        if (rib.isEmpty() || amountStr.isEmpty()) {
            DialogUtil.error("Erreur", "Veuillez remplir tous les champs");
            return;
        }


        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                DialogUtil.error("Erreur", "Le montant doit être positif");
                return;
            }

            // Rechercher la carte de l'expéditeur par RIB
            BankCard carteExpéditeur = cardService.getByRib(rib);

            if (carteExpéditeur == null) {
                DialogUtil.error("Erreur", "RIB invalide ou inexistant");
                return;
            }

            if (carteExpéditeur.getUserId() == currentUserId) {
                DialogUtil.error("Erreur", "Vous ne pouvez pas recevoir de l'argent de vous-même");
                return;
            }

            // Récupérer les informations de l'expéditeur
            User expéditeur = null;
            try {
                expéditeur = new services.ServiceUser().getById(carteExpéditeur.getUserId());
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'expéditeur: " + e.getMessage());
            }

            String senderName = senderNameField.getText().trim();
            if (senderName.isEmpty() && expéditeur != null) {
                senderName = expéditeur.getPrenom() + " " + expéditeur.getNom();
            } else if (senderName.isEmpty()) {
                senderName = "Expéditeur inconnu";
            }

            // Ajouter le montant au wallet du receveur
            walletService.addBalance(currentUserId, amount);

            // Enregistrer la transaction pour le receveur
            transactionService.add(new Transaction(
                    currentUserId,
                    "RECEIVE",
                    amount,
                    "Réception de " + senderName + " (RIB: " + rib + ")"
            ));

            // Note: La transaction pour l'expéditeur sera créée quand il envoie l'argent
            // via SendController

            DialogUtil.success("Succès",
                    String.format("✅ %.2f TND reçus avec succès de %s", amount, senderName));

            // Retour au dashboard
            cancel();

        } catch (NumberFormatException e) {
            DialogUtil.error("Erreur", "Montant invalide");
        } catch (Exception e) {
            DialogUtil.error("Erreur", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        if (WalletLayoutController.instance != null) {
            WalletLayoutController.instance.goDashboard();
        }
    }

    // ================== AJOUTER DU SOLDE (CRÉDIT) ==================
    public void addBalance(int walletId, double amount) throws SQLException {
        if (amount <= 0) {
            throw new SQLException("Le montant à ajouter doit être positif");
        }

        String sql = "UPDATE wallets SET balance = balance + ?, updated_at = ? WHERE id = ?";

        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, walletId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Wallet avec ID " + walletId + " non trouvé");
            }

            System.out.println("✅ " + amount + " DT ajoutés au wallet " + walletId);

            // Optionnel: Enregistrer la transaction
            enregistrerTransaction(walletId, null, amount, "DEPOT", "COMPLETED");
        }
    }

    // ================== RETIRER DU SOLDE (DÉBIT) ==================
    public void subtractBalance(int walletId, double amount) throws SQLException {
        if (amount <= 0) {
            throw new SQLException("Le montant à retirer doit être positif");
        }

        // Vérifier le solde d'abord
        if (!hasSufficientBalance(walletId, amount)) {
            throw new SQLException("Solde insuffisant pour effectuer ce retrait");
        }

        String sql = "UPDATE wallets SET balance = balance - ?, updated_at = ? WHERE id = ? AND balance >= ?";

        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, walletId);
            ps.setDouble(4, amount);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Échec du retrait: solde insuffisant ou wallet inexistant");
            }

            System.out.println("✅ " + amount + " DT retirés du wallet " + walletId);

            // Optionnel: Enregistrer la transaction
            enregistrerTransaction(null, walletId, amount, "RETRAIT", "COMPLETED");
        }
    }

    // ================== METTRE À JOUR LE SOLDE ==================
    public void updateBalance(int walletId, double newBalance) throws SQLException {
        if (newBalance < 0) {
            throw new SQLException("Le solde ne peut pas être négatif");
        }

        String sql = "UPDATE wallets SET balance = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, walletId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Wallet avec ID " + walletId + " non trouvé");
            }

            System.out.println("✅ Solde du wallet " + walletId + " mis à jour: " + newBalance + " DT");
        }
    }

    // ================== VÉRIFIER SOLDE SUFFISANT ==================
    public boolean hasSufficientBalance(int walletId, double amount) throws SQLException {
        String sql = "SELECT balance FROM wallets WHERE id = ?";

        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                return balance >= amount;
            }
        }
        return false;
    }

    // ================== GET SOLDE ==================
    public double getBalance(int walletId) throws SQLException {
        String sql = "SELECT balance FROM wallets WHERE id = ?";

        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0;
    }

    // ================== ENREGISTRER TRANSACTION ==================
    private void enregistrerTransaction(Integer fromWalletId, Integer toWalletId,
                                        double amount, String type, String status) {
        // Cette méthode est optionnelle - à implémenter si vous avez une table transactions
        try {
            String sql = "INSERT INTO transactions (from_wallet_id, to_wallet_id, amount, type, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = MyDataBase.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                if (fromWalletId != null) {
                    ps.setInt(1, fromWalletId);
                } else {
                    ps.setNull(1, Types.INTEGER);
                }

                if (toWalletId != null) {
                    ps.setInt(2, toWalletId);
                } else {
                    ps.setNull(2, Types.INTEGER);
                }

                ps.setDouble(3, amount);
                ps.setString(4, type);
                ps.setString(5, status);
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible d'enregistrer la transaction: " + e.getMessage());
        }
    }
}