package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final Connection conn;

    public NotificationService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    // Méthode pour récupérer les notifications non lues d'un utilisateur
    public List<Notification> afficherParUser(int userId) {
        List<Notification> notifications = new ArrayList<>();

        String sql = "SELECT * FROM notifications WHERE user_id=? AND is_read=0 ORDER BY created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Utilisation du constructeur avec les paramètres
                Notification notification = new Notification(
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getString("type")
                );

                // Initialisation des autres propriétés
                notification.setId(rs.getInt("id"));
                notification.setStatus(rs.getString("status"));
                notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                notification.setRead(rs.getBoolean("is_read"));

                notifications.add(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    // Ajout d'une notification dans la base de données
    public boolean ajouter(Notification n) {
        String sql = "INSERT INTO notifications (user_id, title, message, type, status, is_read) VALUES (?, ?, ?, ?, 'UNREAD', 0)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getMessage());
            ps.setString(4, n.getType());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}