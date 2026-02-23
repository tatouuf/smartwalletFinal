package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    Connection cnx = MyDataBase.getInstance().getConnection();


    // ======================= INSCRIPTION (EN ATTENTE) =======================
    public User inscrire(User user, double depotInitial) throws Exception {
        String sql = "INSERT INTO users (nom, prenom, fullname, telephone, email, password, role, status, active, dateCreation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getTelephone());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPassword());
            ps.setString(7, user.getRole());
            ps.setString(8, "EN_ATTENTE");
            ps.setBoolean(9, false);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new Exception("Échec de l'inscription");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }

            // Le wallet sera créé lors de l'approbation, pas ici
            // Le montant est stocké pour être utilisé lors de l'approbation

            System.out.println("✅ Demande d'inscription reçue: " + user.getEmail() + " (en attente)");
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    public void approuverCompte(int userId) throws Exception {
        String sql = "UPDATE users SET status = 'ACTIF', active = true WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            System.out.println("✅ Compte ID " + userId + " approuvé");
        }
    }

    // Vérifier si le téléphone existe déjà
    public boolean telephoneExiste(String telephone) {
        String sql = "SELECT id FROM users WHERE telephone = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, telephone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public User creerWallet(int userId, double montantDepot) throws Exception {
        Connection conn = null;
        try {
            conn = MyDataBase.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Créer le wallet
            WalletService walletService = new WalletService();
            walletService.creerWallet(userId, montantDepot);

            // Mettre à jour le statut de l'utilisateur si nécessaire
            String sql = "UPDATE users SET status = 'ACTIF', active = 1 WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();

            conn.commit();

            // Récupérer l'utilisateur mis à jour
            return getById(userId);

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Erreur lors de la création du wallet: " + e.getMessage());
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }
    // ======================= APPROUVER UN UTILISATEUR =======================
    public void approuverUser(int userId, double depotInitial) throws Exception {
        Connection conn = null;
        try {
            conn = MyDataBase.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Mettre à jour le statut
            String sql = "UPDATE users SET status = 'ACTIF', active = true WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();

            // Créer le wallet
            WalletService walletService = new WalletService();
            walletService.creerWallet(userId, depotInitial);

            conn.commit();
            System.out.println("✅ Utilisateur ID " + userId + " approuvé avec wallet de " + depotInitial + " TND");

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Erreur lors de l'approbation: " + e.getMessage());
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    // ======================= REJETER UN UTILISATEUR =======================
    public void rejeterUser(int userId) throws Exception {
        String sql = "DELETE FROM users WHERE id = ? AND status = 'EN_ATTENTE'";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Demande d'inscription rejetée (ID: " + userId + ")");
            } else {
                throw new Exception("Utilisateur non trouvé ou déjà traité");
            }
        }
    }

    // ======================= GET ALL USERS =======================
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setFullname(rs.getString("fullname"));
                user.setTelephone(rs.getString("telephone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setActive(rs.getBoolean("active"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ======================= GET USERS EN ATTENTE =======================
    public List<User> getEnAttente() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE status = 'EN_ATTENTE' ORDER BY dateCreation DESC";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setFullname(rs.getString("fullname"));
                user.setTelephone(rs.getString("telephone"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ======================= GET BY ID =======================
    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setFullname(rs.getString("fullname"));
                user.setTelephone(rs.getString("telephone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setActive(rs.getBoolean("active"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ======================= GET BY EMAIL =======================
    public User getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setFullname(rs.getString("fullname"));
                user.setTelephone(rs.getString("telephone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setActive(rs.getBoolean("active"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ======================= DELETE =======================
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}