package services;

import entities.Notification;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceNotification {

    private Connection cnx;

    public ServiceNotification() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    // ================= CREATE NOTIFICATION =================
    public void createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications(user_id, type, message, is_read, created_at, related_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getType());
            ps.setString(3, notification.getMessage());
            ps.setBoolean(4, notification.isRead());
            ps.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));
            if (notification.getRelatedId() != null) {
                ps.setInt(6, notification.getRelatedId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    // ================= GET UNREAD NOTIFICATIONS =================
    public List<Notification> getUnreadNotifications(int userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapNotification(rs));
            }
        }
        return list;
    }

    // ================= GET ALL NOTIFICATIONS =================
    public List<Notification> getAllNotifications(int userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapNotification(rs));
            }
        }
        return list;
    }

    // ================= COUNT UNREAD NOTIFICATIONS =================
    public int countUnreadNotifications(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ================= GET UNREAD COUNT (ALIAS) =================
    public int getUnreadCount(int userId) throws SQLException {
        return countUnreadNotifications(userId);
    }

    // ================= MARK AS READ =================
    public void markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }

    // ================= MARK ALL AS READ =================
    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ================= DELETE NOTIFICATION =================
    public void deleteNotification(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }

    // ================= DELETE OLD NOTIFICATIONS =================
    public void deleteOldNotifications(int days) throws SQLException {
        String sql = "DELETE FROM notifications WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, days);
            ps.executeUpdate();
        }
    }

    // ================= MAPPER =================
    private Notification mapNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setType(rs.getString("type"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        int relatedId = rs.getInt("related_id");
        if (!rs.wasNull()) {
            n.setRelatedId(relatedId);
        }

        return n;
    }
}