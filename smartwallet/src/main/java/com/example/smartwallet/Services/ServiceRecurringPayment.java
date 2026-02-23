package com.example.smartwallet.Services;

import com.example.smartwallet.entities.RecurringPayment;
import com.example.smartwallet.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecurringPayment implements IService<RecurringPayment> {

    private Connection cnx;

    public ServiceRecurringPayment() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(RecurringPayment r) throws SQLException {

        String req = "INSERT INTO recurring_payments (user_id, name, amount, frequency, next_payment_date, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, r.getUserId());
        ps.setString(2, r.getName());
        ps.setDouble(3, r.getAmount());
        ps.setString(4, r.getFrequency().name());
        ps.setDate(5, Date.valueOf(r.getNextPaymentDate()));
        ps.setBoolean(6, r.isActive());

        ps.executeUpdate();
    }

    @Override
    public void modifier(RecurringPayment r) throws SQLException {

        String req = "UPDATE recurring_payments SET name=?, amount=?, frequency=?, next_payment_date=?, is_active=? WHERE id=?";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, r.getName());
        ps.setDouble(2, r.getAmount());
        ps.setString(3, r.getFrequency().name());
        ps.setDate(4, Date.valueOf(r.getNextPaymentDate()));
        ps.setBoolean(5, r.isActive());
        ps.setInt(6, r.getId());

        ps.executeUpdate();
    }

    @Override
    public void supprimer(RecurringPayment r) throws SQLException {

        String req = "DELETE FROM recurring_payments WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, r.getId());
        ps.executeUpdate();
    }

    @Override
    public List<RecurringPayment> recuperer() throws SQLException {

        List<RecurringPayment> list = new ArrayList<>();
        String req = "SELECT * FROM recurring_payments";

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            list.add(new RecurringPayment(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    RecurringPayment.Frequency.valueOf(rs.getString("frequency")),
                    rs.getDate("next_payment_date").toLocalDate(),
                    rs.getBoolean("is_active")
            ));
        }

        return list;
    }

    public List<RecurringPayment> recupererParUser(int userId) throws SQLException {
        List<RecurringPayment> list = new ArrayList<>();
        String req = "SELECT * FROM recurring_payments WHERE user_id=? ORDER BY next_payment_date ASC";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new RecurringPayment(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    RecurringPayment.Frequency.valueOf(rs.getString("frequency")),
                    rs.getDate("next_payment_date").toLocalDate(),
                    rs.getBoolean("is_active")
            ));
        }
        return list;
    }
}