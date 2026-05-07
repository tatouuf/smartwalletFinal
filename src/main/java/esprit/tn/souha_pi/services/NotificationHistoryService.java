package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Notification;
import esprit.tn.souha_pi.utils.MyDataBase;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationHistoryService {

    private Connection cnx = MyDataBase.getInstance().getConnection();

    /**
     * Ajouter une notification dans l'historique
     */
    public void ajouter(Notification notification) {
        // Adapté à votre structure de table : title au lieu de sujet
        String sql = "INSERT INTO notifications (user_id, title, message, type, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getTitle());      // title au lieu de sujet
            ps.setString(3, notification.getMessage());
            ps.setString(4, notification.getType());
            ps.setString(5, notification.getStatus() != null ? notification.getStatus() : "en_attente");
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            System.out.println("✅ Notification sauvegardée dans l'historique");

        } catch (SQLException e) {
            System.err.println("❌ Erreur sauvegarde notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    public List<Notification> getNonLues(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND status = 'en_attente' ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setTitle(rs.getString("title"));        // title au lieu de sujet
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setStatus(rs.getString("status"));
                n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupérer toutes les notifications d'un utilisateur
     */
    public List<Notification> getByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setTitle(rs.getString("title"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setStatus(rs.getString("status"));
                n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Marquer une notification comme lue
     */
    public void marquerCommeLue(int notificationId) {
        String sql = "UPDATE notifications SET status = 'lu' WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    public void marquerToutCommeLu(int userId) {
        String sql = "UPDATE notifications SET status = 'lu' WHERE user_id = ? AND status = 'en_attente'";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprimer une notification
     */
    public void supprimer(int notificationId) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}