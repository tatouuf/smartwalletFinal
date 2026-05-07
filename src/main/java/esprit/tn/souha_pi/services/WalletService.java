package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Wallet;
import utils.MyDataBase;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WalletService {

    public WalletService() {
    }


    // ================== CRÉER WALLET (EN ATTENTE) ==================
    public void creerWallet(int userId, double depotInitial, String type) throws SQLException {
        // Générer un numéro de compte unique
        String numeroCompte = "WT" + userId + System.currentTimeMillis();

        String sql = "INSERT INTO wallets (user_id, balance, numero_compte, type, status, created_at) " +
                "VALUES (?, ?, ?, ?, 'PENDING', ?)";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDouble(2, depotInitial);
            ps.setString(3, numeroCompte);  // Ajouter le numéro de compte
            ps.setString(4, type);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Demande de wallet créée pour l'utilisateur " + userId);
            }
        }
    }

    // ================== APPROUVER WALLET ==================
    public void approuverWallet(int walletId) throws SQLException {
        String sql = "UPDATE wallets SET status = 'ACTIF', updated_at = ? WHERE id = ? AND status = 'PENDING'";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, walletId);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                System.out.println("✅ Wallet ID " + walletId + " approuvé avec succès (ACTIF)");
            } else {
                throw new SQLException("Wallet non trouvé ou déjà traité");
            }
        }
    }

    // ================== REJETER WALLET ==================
    public void rejeterWallet(int walletId) throws SQLException {
        String sql = "UPDATE wallets SET status = 'REJECTED', updated_at = ? WHERE id = ? AND status = 'PENDING'";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, walletId);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                System.out.println("✅ Wallet ID " + walletId + " rejeté");
            } else {
                throw new SQLException("Wallet non trouvé ou déjà traité");
            }
        }
    }

    // ================== GET WALLETS EN ATTENTE (CORRIGÉ) ==================
    public List<Wallet> getWalletsEnAttente() throws SQLException {
        List<Wallet> wallets = new ArrayList<>();
        // CHANGEMENT: LEFT JOIN ici aussi
        String sql = "SELECT w.*, u.nom, u.prenom, u.email FROM wallets w " +
                "LEFT JOIN users u ON w.user_id = u.id " +  // ← LEFT JOIN
                "WHERE w.status = 'PENDING' ORDER BY w.created_at DESC";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Wallet wallet = mapWallet(rs);
                wallet.setUserNom(rs.getString("nom"));
                wallet.setUserPrenom(rs.getString("prenom"));
                wallet.setUserEmail(rs.getString("email"));
                wallets.add(wallet);
            }
        }
        return wallets;
    }

    // ================== GET WALLET BY USER ID ==================
    public Wallet getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM wallets WHERE user_id = ? ORDER BY id DESC LIMIT 1";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapWallet(rs);
            }
        }
        return null;
    }

    // ================== GET WALLET BY ID ==================
    public Wallet getById(int walletId) throws SQLException {
        String sql = "SELECT * FROM wallets WHERE id = ?";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapWallet(rs);
            }
        }
        return null;
    }

    // ================== GET ALL WALLETS (CORRIGÉ) ==================
    public List<Wallet> getAll() throws SQLException {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT w.*, u.nom, u.prenom, u.email FROM wallets w " +
                "LEFT JOIN users u ON w.user_id = u.id " +
                "ORDER BY w.created_at DESC";

        System.out.println("🔍 Exécution de la requête SQL: " + sql);

        Connection conn = MyDataBase.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                try {
                    Wallet wallet = mapWallet(rs);
                    wallets.add(wallet);

                    // Log de débogage pour les 5 premiers wallets
                    if (rowCount <= 5) {
                        System.out.println("  Wallet #" + rowCount + ": ID=" + wallet.getId() +
                                ", User=" + wallet.getUserId() +
                                ", Balance=" + wallet.getBalance() +
                                ", Status=" + wallet.getStatus() +
                                ", UserName=" + wallet.getUserPrenom() + " " + wallet.getUserNom());
                    }
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors du mapping du wallet à la ligne " + rowCount + ": " + e.getMessage());
                    // Continuer avec le prochain wallet
                }
            }

            System.out.println("✅ " + wallets.size() + " wallets chargés avec succès");

            // Vérifier si des wallets n'ont pas d'utilisateur associé
            long walletsWithoutUser = wallets.stream()
                    .filter(w -> w.getUserNom() == null && w.getUserPrenom() == null)
                    .count();

            if (walletsWithoutUser > 0) {
                System.out.println("⚠️ " + walletsWithoutUser + " wallet(s) sans utilisateur associé");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans getAll(): " + e.getMessage());
            throw e;
        }

        return wallets;
    }

    // ================== AJOUTER DU SOLDE (CRÉDIT) ==================
    public void addBalance(int walletId, double amount) throws SQLException {
        if (amount <= 0) {
            throw new SQLException("Le montant à ajouter doit être positif");
        }

        String sql = "UPDATE wallets SET balance = balance + ?, updated_at = ? WHERE id = ?";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, walletId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Wallet avec ID " + walletId + " non trouvé");
            }

            System.out.println("✅ " + amount + " DT ajoutés au wallet " + walletId);
        }
    }

    // ================== RETIRER DU SOLDE (DÉBIT) ==================
    public void subtractBalance(int walletId, double amount) throws SQLException {
        if (amount <= 0) {
            throw new SQLException("Le montant à retirer doit être positif");
        }

        if (!hasSufficientBalance(walletId, amount)) {
            throw new SQLException("Solde insuffisant pour effectuer ce retrait");
        }

        String sql = "UPDATE wallets SET balance = balance - ?, updated_at = ? WHERE id = ? AND balance >= ?";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, walletId);
            ps.setDouble(4, amount);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Échec du retrait: solde insuffisant ou wallet inexistant");
            }

            System.out.println("✅ " + amount + " DT retirés du wallet " + walletId);
        }
    }

    // ================== VÉRIFIER SOLDE SUFFISANT ==================
    public boolean hasSufficientBalance(int walletId, double amount) throws SQLException {
        String sql = "SELECT balance FROM wallets WHERE id = ?";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

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

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0;
    }

    // ================== CRÉDITER UN WALLET (ALIAS) ==================
    public void crediter(int walletId, double montant) throws SQLException {
        addBalance(walletId, montant);
    }

    // ================== DÉBITER UN WALLET ==================
    public void debiter(int walletId, double montant) throws SQLException {
        subtractBalance(walletId, montant);
    }

    // ================== TRANSFERT ENTRE WALLETS ==================
    public void transfer(int fromWalletId, int toWalletId, double amount) throws SQLException {
        Connection conn = null;
        try {
            conn = MyDataBase.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Vérifications
            if (amount <= 0) {
                throw new SQLException("Le montant du transfert doit être positif");
            }

            Wallet fromWallet = getById(fromWalletId);
            Wallet toWallet = getById(toWalletId);

            if (fromWallet == null) {
                throw new SQLException("Wallet source non trouvé");
            }
            if (toWallet == null) {
                throw new SQLException("Wallet destination non trouvé");
            }

            if (fromWallet.getBalance() < amount) {
                throw new SQLException("Solde insuffisant. Disponible: " + fromWallet.getBalance() + " DT");
            }

            // Débiter la source
            String sql1 = "UPDATE wallets SET balance = balance - ?, updated_at = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setDouble(1, amount);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(3, fromWalletId);
                ps.executeUpdate();
            }

            // Créditer la destination
            String sql2 = "UPDATE wallets SET balance = balance + ?, updated_at = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setDouble(1, amount);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(3, toWalletId);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Transfert de " + amount + " DT effectué avec succès");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Erreur reset auto-commit: " + e.getMessage());
                }
            }
        }
    }

    // ================== TRANSFERT ENTRE WALLETS (ALIAS) ==================
    public void transferer(int fromWalletId, int toWalletId, double montant) throws SQLException {
        transfer(fromWalletId, toWalletId, montant);
    }

    // ================== RECEVOIR DE L'ARGENT ==================
    public void recevoir(int walletId, double montant, String expediteur) throws SQLException {
        addBalance(walletId, montant);

        // Enregistrer la transaction
        String sql = "INSERT INTO transactions (to_wallet_id, amount, type, description, status, created_at) " +
                "VALUES (?, ?, 'RECEPTION', ?, 'COMPLETED', ?)";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ps.setDouble(2, montant);
            ps.setString(3, "Réception de " + expediteur);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            System.out.println("✅ Réception de " + montant + " DT de " + expediteur);
        }
    }

    // ================== ENVOYER DE L'ARGENT ==================
    public void envoyer(int fromWalletId, int toWalletId, double montant, String motif) throws SQLException {
        transfer(fromWalletId, toWalletId, montant);

        // Enregistrer la transaction
        String sql = "INSERT INTO transactions (from_wallet_id, to_wallet_id, amount, type, description, status, created_at) " +
                "VALUES (?, ?, ?, 'ENVOI', ?, 'COMPLETED', ?)";

        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, fromWalletId);
            ps.setInt(2, toWalletId);
            ps.setDouble(3, montant);
            ps.setString(4, motif);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            System.out.println("✅ Envoi de " + montant + " DT effectué");
        }
    }

    // ================== MAP WALLET (CORRIGÉ) ==================
    private Wallet mapWallet(ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet();

        wallet.setId(rs.getInt("id"));
        wallet.setUserId(rs.getInt("user_id"));
        wallet.setBalance(rs.getDouble("balance"));

        try {
            wallet.setNumeroCompte(rs.getString("numero_compte"));
        } catch (SQLException ignored) {}

        try {
            wallet.setType(rs.getString("type"));
        } catch (SQLException ignored) {}

        try {
            wallet.setStatus(rs.getString("status"));
        } catch (SQLException ignored) {}

        try {
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                wallet.setCreatedAt(createdAt.toLocalDateTime());
            }
        } catch (SQLException ignored) {}

        try {
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                wallet.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException ignored) {}

        // Champs wallet comme Symfony
        try {
            wallet.setCin(rs.getString("cin"));
        } catch (SQLException ignored) {}

        try {
            wallet.setRib(rs.getString("rib"));
        } catch (SQLException ignored) {}

        try {
            wallet.setAdresse(rs.getString("adresse"));
        } catch (SQLException ignored) {}

        try {
            wallet.setRne(rs.getString("rne"));
        } catch (SQLException ignored) {}

        try {
            wallet.setRaisonSociale(rs.getString("raison_sociale"));
        } catch (SQLException ignored) {}

        try {
            wallet.setMatriculeFiscale(rs.getString("matricule_fiscale"));
        } catch (SQLException ignored) {}

        try {
            wallet.setDevise(rs.getString("devise"));
        } catch (SQLException ignored) {}

        try {
            double plafond = rs.getDouble("plafond_journalier");
            if (!rs.wasNull()) {
                wallet.setPlafondJournalier(plafond);
            }
        } catch (SQLException ignored) {}

        try {
            boolean sansContact = rs.getBoolean("sans_contact");
            if (!rs.wasNull()) {
                wallet.setSansContact(sansContact);
            }
        } catch (SQLException ignored) {}

        try {
            boolean paiementEtranger = rs.getBoolean("paiement_etranger");
            if (!rs.wasNull()) {
                wallet.setPaiementEtranger(paiementEtranger);
            }
        } catch (SQLException ignored) {}

        try {
            boolean retraitDistributeur = rs.getBoolean("retrait_distributeur");
            if (!rs.wasNull()) {
                wallet.setRetraitDistributeur(retraitDistributeur);
            }
        } catch (SQLException ignored) {}

        try {
            boolean decouvertAutorise = rs.getBoolean("decouvert_autorise");
            if (!rs.wasNull()) {
                wallet.setDecouvertAutorise(decouvertAutorise);
            }
        } catch (SQLException ignored) {}

        try {
            wallet.setCouleurCarte(rs.getString("couleur_carte"));
        } catch (SQLException ignored) {}

        try {
            wallet.setFormule(rs.getString("formule"));
        } catch (SQLException ignored) {}

        // Infos user depuis JOIN
        try {
            wallet.setUserNom(rs.getString("nom"));
            wallet.setUserPrenom(rs.getString("prenom"));
            wallet.setUserEmail(rs.getString("email"));
        } catch (SQLException ignored) {}

        return wallet;
    }
    // ================== SUPPRIMER WALLET ==================
    public void supprimerWallet(int walletId) throws SQLException {
        String sql = "DELETE FROM wallets WHERE id = ?";
        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            ps.executeUpdate();
            System.out.println("✅ Wallet ID " + walletId + " supprimé");
        }
    }

    // ================== VÉRIFIER SI L'UTILISATEUR A UN WALLET ==================
    public boolean hasWallet(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM wallets WHERE user_id = ?";
        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // ================== VÉRIFIER SI L'UTILISATEUR A UN WALLET ACTIF ==================
    public boolean hasActiveWallet(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM wallets WHERE user_id = ? AND status = 'ACTIF'";
        Connection conn = MyDataBase.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}