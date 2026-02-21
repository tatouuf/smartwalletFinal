package dao;

import com.example.smartwallet.model.Notification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void addNotification(Notification notification) {
        // Changement de table : notifications -> notifidepbud
        String sql = "INSERT INTO notifidepbud (user_id, title, message, type, status, created_at, recurring_id, reminder_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getMessage());
            ps.setString(4, notification.getType());
            ps.setString(5, notification.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(notification.getCreatedAt()));
            if (notification.getRecurringId() != null) {
                ps.setInt(7, notification.getRecurringId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            if (notification.getReminderId() != null) {
                ps.setInt(8, notification.getReminderId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getAllNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        // Changement de table : notifications -> notifidepbud
        String sql = "SELECT id, user_id, title, message, type, status, created_at, recurring_id, reminder_id FROM notifidepbud WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setTitle(rs.getString("title"));
                notification.setMessage(rs.getString("message"));
                notification.setType(rs.getString("type"));
                notification.setStatus(rs.getString("status"));
                notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                int recurringId = rs.getInt("recurring_id");
                if (!rs.wasNull()) {
                    notification.setRecurringId(recurringId);
                }
                int reminderId = rs.getInt("reminder_id");
                if (!rs.wasNull()) {
                    notification.setReminderId(reminderId);
                }
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    public void clearAllNotifications(int userId) {
        // Changement de table : notifications -> notifidepbud
        String sql = "DELETE FROM notifidepbud WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
