package services;

import entities.User;
import entities.Role;
import utils.MyDataBase;
import utils.PasswordUtils;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User> {

    private Connection cnx;

    public ServiceUser() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    // ================== AJOUTER ==================
    @Override
    public void ajouter(User user) throws SQLException {
        if (user.getDate_creation() == null) user.setDate_creation(LocalDateTime.now());
        if (user.getDate_update() == null)   user.setDate_update(LocalDateTime.now());

        String req = "INSERT INTO users (nom, prenom, telephone, email, password, role, date_creation, date_update, is_actif) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getTelephone());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().name());
            ps.setTimestamp(7, Timestamp.valueOf(user.getDate_creation()));
            ps.setTimestamp(8, Timestamp.valueOf(user.getDate_update()));
            ps.setBoolean(9, user.isIs_actif());
            ps.executeUpdate();
        }

        System.out.println("✅ User added successfully!");
    }

    // ================== MODIFIER ==================
    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE users SET nom=?, prenom=?, telephone=?, email=?, password=?, role=?, date_update=?, is_actif=? WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getTelephone());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().name());
            ps.setTimestamp(7, Timestamp.valueOf(user.getDate_update()));
            ps.setBoolean(8, user.isIs_actif());
            ps.setInt(9, user.getId());
            ps.executeUpdate();
        }

        System.out.println("✅ User modified successfully!");
    }

    // ================== SUPPRIMER ==================
    @Override
    public void supprimer(User user) throws SQLException {
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
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users";

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
        String req = "SELECT * FROM users WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        }
        return null;
    }
    // ================= RECUPERER USERS ONLY (NO ADMINS) =================
    public List<User> recupererUsersOnly() throws SQLException {
        List<User> list = new ArrayList<>();
        String req = "SELECT * FROM users WHERE role = 'USER' AND is_actif = 1";

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

    // ================== RECUPERER PAR NOM ET PRENOM ==================
    public List<User> recupererParNomPrenom(String nom, String prenom) throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users WHERE nom LIKE ? AND prenom LIKE ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + prenom + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) users.add(mapUser(rs));
        }
        return users;
    }

    // ================== CHECK EMAIL EXIST ==================
    public boolean isEmailTaken(String email) throws SQLException {
        String req = "SELECT id FROM users WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // ================== LOGIN ==================
    public User login(String email, String password) throws SQLException {
        String hashedPassword = PasswordUtils.hashPassword(password);
        String req = "SELECT * FROM users WHERE email=? AND password=? AND is_actif=1";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        }
        return null;
    }

    // ================== UPDATE PASSWORD ==================
    public void updatePassword(String email, String newPassword) throws SQLException {
        String req = "UPDATE users SET password=?, date_update=NOW() WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("No user found with email: " + email);
        }
    }

    // ================== RESET PASSWORD ==================
    public boolean resetPassword(String email, String newPassword) throws SQLException {
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        String req = "UPDATE users SET password=?, date_update=NOW() WHERE email=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
    }

    // ================== HELPER: MAP USER ==================
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("date_creation").toLocalDateTime(),
                rs.getTimestamp("date_update").toLocalDateTime(),
                rs.getBoolean("is_actif")
        );
    }


}