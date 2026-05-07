package services;

import entities.User;
import entities.Role;
import esprit.tn.souha_pi.services.WalletService;
import utils.MyDataBase;
import utils.PasswordUtils;
import utils.BaseService;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser extends BaseService implements IService<User> {

    private Connection cnx;

    public ServiceUser() {
        super();
        cnx = MyDataBase.getInstance().getConnection();
    }

    // ================== VÉRIFICATION CONNEXION ==================
    private void verifierConnexion() throws SQLException {
        try {
            if (cnx == null || cnx.isClosed()) {
                System.out.println("⚠️ Connexion fermée, reconnexion...");
                cnx = MyDataBase.getInstance().getConnection();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion: " + e.getMessage());
            cnx = MyDataBase.getInstance().getConnection();
        }
    }

    // ================== AJOUTER ==================
    @Override
    public void ajouter(User user) throws SQLException {
        verifierConnexion();

        if (user.getDate_creation() == null) user.setDate_creation(LocalDateTime.now());
        if (user.getDate_update() == null)   user.setDate_update(LocalDateTime.now());

        String req = "INSERT INTO users (nom, prenom, telephone, email, password, role, status, date_creation, date_update, is_actif) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getTelephone());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().name());
            ps.setString(7, user.getStatus() != null ? user.getStatus() : "PENDING");
            ps.setTimestamp(8, Timestamp.valueOf(user.getDate_creation()));
            ps.setTimestamp(9, Timestamp.valueOf(user.getDate_update()));
            ps.setBoolean(10, user.isIs_actif());
            ps.executeUpdate();
        }
        System.out.println("✅ User added successfully!");
    }

    // ================== MODIFIER ==================
    @Override
    public void modifier(User user) throws SQLException {
        verifierConnexion();

        String req = "UPDATE users SET nom=?, prenom=?, telephone=?, email=?, password=?, role=?, status=?, date_update=?, is_actif=? WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getTelephone());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().name());
            ps.setString(7, user.getStatus());
            ps.setTimestamp(8, Timestamp.valueOf(user.getDate_update()));
            ps.setBoolean(9, user.isIs_actif());
            ps.setInt(10, user.getId());
            ps.executeUpdate();
        }
        System.out.println("✅ User modified successfully!");
    }

    // ================== SUPPRIMER ==================
    @Override
    public void supprimer(User user) throws SQLException {
        verifierConnexion();

        String req = "DELETE FROM users WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, user.getId());
            ps.executeUpdate();
        }
        System.out.println("✅ User deleted successfully!");
    }

    // ================== RECUPERER ALL ==================
    @Override
    public List<User> recuperer() throws SQLException {
        verifierConnexion();

        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users ORDER BY id DESC";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    // ================== RECUPERER PAR ID ==================
    public User recupererParId(int id) throws SQLException {
        verifierConnexion();

        String req = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        }
        return null;
    }

    // ================== RECUPERER PAR EMAIL ==================
    public User recupererParEmail(String email) throws SQLException {
        verifierConnexion();

        String req = "SELECT * FROM users WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        }
        return null;
    }

    // ================== RECUPERER USERS ONLY ==================
    public List<User> recupererUsersOnly() throws SQLException {
        verifierConnexion();

        List<User> list = new ArrayList<>();
        String req = "SELECT * FROM users WHERE role = 'USER' ORDER BY id DESC";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        }
        return list;
    }

    // ================== RECUPERER ADMINS ONLY ==================
    public List<User> recupererAdminsOnly() throws SQLException {
        verifierConnexion();

        List<User> list = new ArrayList<>();
        String req = "SELECT * FROM users WHERE role = 'ADMIN' ORDER BY id DESC";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        }
        return list;
    }

    // ================== SEARCH BY NAME OR EMAIL ==================
    public List<User> searchByNameOrEmail(String keyword) throws SQLException {
        verifierConnexion();

        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) users.add(mapUser(rs));
        }
        return users;
    }

    // ================== CHECK EMAIL EXIST ==================
    public boolean isEmailTaken(String email) throws SQLException {
        verifierConnexion();

        String req = "SELECT id FROM users WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // ================== CHECK TELEPHONE EXIST ==================
    public boolean telephoneExiste(String telephone) throws SQLException {
        verifierConnexion();

        String req = "SELECT id FROM users WHERE telephone=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, telephone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // ================== LOGIN ==================
    public User login(String email, String password) throws SQLException {
        try {
            verifierConnexion();
        } catch (SQLException e) {
            cnx = MyDataBase.getInstance().getConnection();
        }

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email: '" + email + "'");

        String req = "SELECT * FROM users WHERE email=? AND status = 'APPROVED' AND is_actif=1";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("✅ User found in database");
                User user = mapUser(rs);
                String storedHash = user.getPassword();

                boolean passwordMatches = PasswordUtils.checkPassword(password, storedHash);
                System.out.println("Password match: " + passwordMatches);

                if (passwordMatches) {
                    System.out.println("✅ Login successful for: " + email);
                    return user;
                } else {
                    System.out.println("❌ Password does not match");
                }
            } else {
                System.out.println("❌ No user found with email: " + email);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Connection") || e.getMessage().contains("timeout")) {
                System.out.println("⚠️ Problème de connexion, reconnexion...");
                cnx = MyDataBase.getInstance().getConnection();
                return login(email, password);
            }
            throw e;
        }
        return null;
    }

    // ================== LOGIN ADMIN ==================
    public User loginAdmin(String email, String password) throws SQLException {
        verifierConnexion();

        String req = "SELECT * FROM users WHERE email=? AND role = 'ADMIN' AND status = 'APPROVED' AND is_actif=1";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = mapUser(rs);
                String storedHash = user.getPassword();

                if (PasswordUtils.checkPassword(password, storedHash)) {
                    return user;
                }
            }
        }
        return null;
    }

    // ================== UPDATE PASSWORD ==================
    public void updatePassword(String email, String newPassword) throws SQLException {
        verifierConnexion();

        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        String req = "UPDATE users SET password=?, date_update=NOW() WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("No user found with email: " + email);
        }
    }

    // ================== RECUPERER USERS EN ATTENTE ==================
    public List<User> getUsersEnAttente() throws SQLException {
        verifierConnexion();

        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users WHERE status = 'PENDING' ORDER BY date_creation DESC";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
            System.out.println("✅ " + users.size() + " users en attente trouvés");
        }
        return users;
    }

    // ================== RECUPERER USERS APPROUVES ==================
    public List<User> getUsersApprouves() throws SQLException {
        verifierConnexion();

        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users WHERE status = 'APPROVED' ORDER BY date_creation DESC";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    // ================== RECUPERER USERS REJETES ==================
    public List<User> getUsersRejetes() throws SQLException {
        verifierConnexion();

        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users WHERE status = 'REJECTED' ORDER BY date_creation DESC";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    // ================== APPROUVER COMPTE (SANS WALLET) ==================
    public void approuverCompte(int userId) throws Exception {
        Connection conn = null;
        try {
            conn = MyDataBase.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Vérifier si l'utilisateur existe et est en statut PENDING
            String checkSql = "SELECT id, status FROM users WHERE id = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Utilisateur avec ID " + userId + " non trouvé");
                    }
                    String currentStatus = rs.getString("status");
                    if (!"PENDING".equals(currentStatus)) {
                        throw new Exception("Le compte n'est pas en attente. Statut actuel: " + currentStatus);
                    }
                }
            }

            // Mettre à jour le statut de l'utilisateur
            String sql = "UPDATE users SET status = 'APPROVED', is_actif = true, date_update = NOW() WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    throw new Exception("Échec de la mise à jour de l'utilisateur");
                }
            }

            conn.commit();
            System.out.println("✅ Utilisateur ID " + userId + " approuvé avec succès");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("❌ Transaction annulée: " + e.getMessage());
                } catch (SQLException ex) {
                    throw new Exception("Erreur lors de l'annulation: " + ex.getMessage());
                }
            }
            throw new Exception("Erreur lors de l'approbation: " + e.getMessage());
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

    // ================== APPROUVER COMPTE AVEC WALLET ==================
    public void approuverCompteAvecWallet(int userId, double depotInitial) throws Exception {
        Connection conn = null;
        try {
            conn = MyDataBase.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Vérifier si l'utilisateur existe et est en statut PENDING
            String checkSql = "SELECT id, status FROM users WHERE id = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Utilisateur avec ID " + userId + " non trouvé");
                    }
                    String currentStatus = rs.getString("status");
                    if (!"PENDING".equals(currentStatus)) {
                        throw new Exception("Le compte n'est pas en attente. Statut actuel: " + currentStatus);
                    }
                }
            }

            // Mettre à jour le statut de l'utilisateur
            String sql = "UPDATE users SET status = 'APPROVED', is_actif = true, date_update = NOW() WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    throw new Exception("Échec de la mise à jour de l'utilisateur");
                }
            }

            // Créer le wallet
            WalletService walletService = new WalletService();
            walletService.creerWallet(userId, depotInitial, "Standard");

            conn.commit();
            System.out.println("✅ Utilisateur ID " + userId + " approuvé avec wallet de " + depotInitial + " DT");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new Exception("Erreur rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    // ================== REJETER COMPTE ==================
    public void rejeterCompte(int userId) throws SQLException {
        verifierConnexion();

        String req = "UPDATE users SET status = 'REJECTED', is_actif = false, date_update = NOW() WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, userId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID: " + userId);
            }
            System.out.println("✅ Compte ID " + userId + " rejeté");
        }
    }

    // ================== REJETER ET SUPPRIMER ==================
    public void rejeterUser(int userId) throws Exception {
        verifierConnexion();

        String sql = "DELETE FROM users WHERE id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Registration request rejected and deleted (ID: " + userId + ")");
            } else {
                throw new Exception("User not found or already processed");
            }
        }
    }

    // ================== RESET PASSWORD ==================
    public boolean resetPassword(String email, String newPassword) throws SQLException {
        verifierConnexion();

        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        String req = "UPDATE users SET password=?, date_update=NOW() WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
    }

    // ================== COUNT BY STATUS ==================
    public int countByStatus(String status) throws SQLException {
        verifierConnexion();

        String req = "SELECT COUNT(*) FROM users WHERE status = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ================== COUNT BY ROLE ==================
    public int countByRole(String role) throws SQLException {
        verifierConnexion();

        String req = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ================== GET TOTAL USERS ==================
    public int getTotalUsers() throws SQLException {
        verifierConnexion();

        String req = "SELECT COUNT(*) FROM users";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ================== DELETE BY ID ==================
    public void delete(int id) throws SQLException {
        verifierConnexion();

        String req = "DELETE FROM users WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        System.out.println("✅ User ID " + id + " deleted successfully!");
    }

    // ================== GET ALL ==================
    public List<User> getAll() throws SQLException {
        return recuperer();
    }

    // ================== GET BY ID ==================
    public User getById(int id) throws SQLException {
        return recupererParId(id);
    }

    // ================== GET BY EMAIL ==================
    public User getByEmail(String email) throws SQLException {
        return recupererParEmail(email);
    }

    // ================== GET EN ATTENTE ==================
    public List<User> getEnAttente() throws SQLException {
        verifierConnexion();

        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users WHERE status = 'PENDING' ORDER BY date_creation DESC";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapSimpleUser(rs));
            }
        }
        return users;
    }

    // ================== MAP USER (COMPLET) ==================
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                rs.getString("status") != null ? rs.getString("status") : "PENDING",
                rs.getTimestamp("date_creation") != null ? rs.getTimestamp("date_creation").toLocalDateTime() : null,
                rs.getTimestamp("date_update") != null ? rs.getTimestamp("date_update").toLocalDateTime() : null,
                rs.getBoolean("is_actif")
        );
    }

    // ================== MAP USER (SIMPLE) ==================
    private User mapSimpleUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setFullname(rs.getString("nom") + " " + rs.getString("prenom"));
        user.setTelephone(rs.getString("telephone"));
        user.setEmail(rs.getString("email"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setStatus(rs.getString("status"));
        user.setIs_actif(rs.getBoolean("is_actif"));
        return user;
    }
}