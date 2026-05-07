package com.example.smartwallet.Services;

import com.example.smartwallet.entities.StripeTransaction;
import com.example.smartwallet.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceStripeTransaction implements IService<StripeTransaction> {
    private final Connection cnx;

    public ServiceStripeTransaction() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(StripeTransaction t) throws SQLException {
        String req = "INSERT INTO stripe_transactions (user_id, profile_id, stripe_payment_intent_id, amount, currency, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, t.getUserId());
        ps.setInt(2, t.getProfileId());
        ps.setString(3, t.getStripePaymentIntentId());
        ps.setDouble(4, t.getAmount());
        ps.setString(5, t.getCurrency());
        ps.setString(6, t.getStatus());
        ps.setTimestamp(7, Timestamp.valueOf(t.getCreatedAt()));
        ps.executeUpdate();
    }

    @Override
    public void modifier(StripeTransaction t) throws SQLException {
        String req = "UPDATE stripe_transactions SET status=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, t.getStatus());
        ps.setInt(2, t.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(StripeTransaction t) throws SQLException {
        String req = "DELETE FROM stripe_transactions WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, t.getId());
        ps.executeUpdate();
    }

    @Override
    public List<StripeTransaction> recuperer() throws SQLException {
        List<StripeTransaction> list = new ArrayList<>();
        String req = "SELECT * FROM stripe_transactions ORDER BY created_at DESC";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            list.add(mapResultSetToTransaction(rs));
        }
        return list;
    }

    public List<StripeTransaction> recupererParUser(int userId) throws SQLException {
        List<StripeTransaction> list = new ArrayList<>();
        String req = "SELECT * FROM stripe_transactions WHERE user_id=? ORDER BY created_at DESC";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSetToTransaction(rs));
        }
        return list;
    }

    private StripeTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new StripeTransaction(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("profile_id"),
                rs.getString("stripe_payment_intent_id"),
                rs.getDouble("amount"),
                rs.getString("currency"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
