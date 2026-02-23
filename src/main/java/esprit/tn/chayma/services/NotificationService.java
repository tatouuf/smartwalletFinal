package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final Connection conn;

    public NotificationService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    public boolean add(Notification n) {
        String sql = "INSERT INTO notifications (user_id, type, message, is_read, created_at, related_id) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getType());
            ps.setString(3, n.getMessage());
            ps.setBoolean(4, n.isRead());
            if (n.getRelatedId() != null) ps.setInt(5, n.getRelatedId()); else ps.setNull(5, java.sql.Types.INTEGER);
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) n.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notification> listByUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT id, user_id, type, message, is_read, created_at, related_id FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setType(rs.getString("type"));
                n.setMessage(rs.getString("message"));
                n.setRead(rs.getBoolean("is_read"));
                java.sql.Timestamp c = rs.getTimestamp("created_at");
                if (c != null) n.setCreatedAt(c.toLocalDateTime());
                n.setRelatedId(rs.getObject("related_id") != null ? rs.getInt("related_id") : null);
                list.add(n);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean markAsRead(int id) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
