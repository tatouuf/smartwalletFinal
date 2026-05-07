package com.example.smartwallet.Services;

import com.example.smartwallet.entities.FinanceProfile;
import com.example.smartwallet.utils.MyDataBase;

import java.sql.*;

public class ServiceFinanceProfile {
    private final Connection cnx;

    public ServiceFinanceProfile() {
        cnx = MyDataBase.getInstance().getConnection();
        if (cnx == null) throw new RuntimeException("DB connection is null");
    }

    public FinanceProfile getByUserId(int userId) throws SQLException {
        String q = "SELECT * FROM finance_profile WHERE user_id=?";
        PreparedStatement ps = cnx.prepareStatement(q);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new FinanceProfile(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("monthly_income"),
                    rs.getDouble("current_balance"),
                    rs.getString("currency")
            );
        }
        return null;
    }

    public void upsert(FinanceProfile p) throws SQLException {
        String q = "INSERT INTO finance_profile (user_id, monthly_income, current_balance, currency) " +
                "VALUES (?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE monthly_income=VALUES(monthly_income), current_balance=VALUES(current_balance), currency=VALUES(currency)";
        PreparedStatement ps = cnx.prepareStatement(q);
        ps.setInt(1, p.getUserId());
        ps.setDouble(2, p.getMonthlyIncome());
        ps.setDouble(3, p.getCurrentBalance());
        ps.setString(4, p.getCurrency());
        ps.executeUpdate();
    }
}