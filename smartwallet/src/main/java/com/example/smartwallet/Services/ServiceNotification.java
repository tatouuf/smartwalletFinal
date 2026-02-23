package com.example.smartwallet.Services;

import com.example.smartwallet.entities.Notification;
import com.example.smartwallet.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceNotification implements IService<Notification> {

    private Connection cnx;

    public ServiceNotification() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Notification n) throws SQLException {
        String req = "INSERT INTO notifications (user_id, title, message, type, status, recurring_id, reminder_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, n.getUserId());
        ps.setString(2, n.getTitle());
        ps.setString(3, n.getMessage());
        ps.setString(4, n.getType().name());     // CRITICAL/SUCCESS/PENDING
        ps.setString(5, n.getStatus().name());   // UNREAD/READ
        ps.setObject(6, n.getRecurringId());
        ps.setObject(7, n.getReminderId());
        ps.executeUpdate();
        System.out.println("Notification ajoutée");
    }

    @Override
    public void modifier(Notification n) throws SQLException {
        String req = "UPDATE notifications SET title=?, message=?, type=?, status=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, n.getTitle());
        ps.setString(2, n.getMessage());
        ps.setString(3, n.getType().name());
        ps.setString(4, n.getStatus().name());
        ps.setInt(5, n.getId());
        ps.executeUpdate();
        System.out.println("Notification modifiée");
    }

    @Override
    public void supprimer(Notification n) throws SQLException {
        String req = "DELETE FROM notifications WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, n.getId());
        ps.executeUpdate();
        System.out.println("Notification supprimée");
    }

    @Override
    public List<Notification> recuperer() throws SQLException {
        List<Notification> list = new ArrayList<>();
        String req = "SELECT * FROM notifications ORDER BY created_at DESC";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            list.add(new Notification(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("title"),
                    rs.getString("message"),
                    Notification.Type.valueOf(rs.getString("type")),
                    Notification.Status.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getObject("recurring_id") != null ? rs.getInt("recurring_id") : null,
                    rs.getObject("reminder_id") != null ? rs.getInt("reminder_id") : null
            ));
        }
        return list;
    }

    public List<Notification> recupererParUser(int userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String req = "SELECT * FROM notifications WHERE user_id=? ORDER BY created_at DESC";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Notification(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("title"),
                    rs.getString("message"),
                    Notification.Type.valueOf(rs.getString("type")),
                    Notification.Status.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getObject("recurring_id") != null ? rs.getInt("recurring_id") : null,
                    rs.getObject("reminder_id") != null ? rs.getInt("reminder_id") : null
            ));
        }
        return list;
    }

    // Bonus utile: marquer comme lu
    public void marquerCommeLu(int notificationId) throws SQLException {
        String req = "UPDATE notifications SET status='READ' WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, notificationId);
        ps.executeUpdate();
    }
}
