package com.example.smartwallet.Services;

import com.example.smartwallet.entities.Reminder;
import com.example.smartwallet.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReminder implements IService<Reminder> {

    private Connection cnx;

    public ServiceReminder() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reminder r) throws SQLException {
        String req = "INSERT INTO reminders (recurring_id, user_id, remind_before_days, is_enabled) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, r.getRecurringId());
        ps.setInt(2, r.getUserId());
        ps.setInt(3, r.getRemindBeforeDays());
        ps.setBoolean(4, r.isEnabled());
        ps.executeUpdate();
        System.out.println("Reminder ajouté");
    }

    @Override
    public void modifier(Reminder r) throws SQLException {
        String req = "UPDATE reminders SET remind_before_days=?, is_enabled=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, r.getRemindBeforeDays());
        ps.setBoolean(2, r.isEnabled());
        ps.setInt(3, r.getId());
        ps.executeUpdate();
        System.out.println("Reminder modifié");
    }

    @Override
    public void supprimer(Reminder r) throws SQLException {
        String req = "DELETE FROM reminders WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, r.getId());
        ps.executeUpdate();
        System.out.println("Reminder supprimé");
    }

    @Override
    public List<Reminder> recuperer() throws SQLException {
        List<Reminder> list = new ArrayList<>();
        String req = "SELECT * FROM reminders";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            list.add(new Reminder(
                    rs.getInt("id"),
                    rs.getInt("recurring_id"),
                    rs.getInt("user_id"),
                    rs.getInt("remind_before_days"),
                    rs.getBoolean("is_enabled"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            ));
        }
        return list;
    }

    public List<Reminder> recupererParRecurring(int recurringId) throws SQLException {
        List<Reminder> list = new ArrayList<>();
        String req = "SELECT * FROM reminders WHERE recurring_id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, recurringId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Reminder(
                    rs.getInt("id"),
                    rs.getInt("recurring_id"),
                    rs.getInt("user_id"),
                    rs.getInt("remind_before_days"),
                    rs.getBoolean("is_enabled"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            ));
        }
        return list;
    }
}
