package services;

import entities.Transaction;
import entities.User;
import utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransaction {

    private Connection cnx;

    public ServiceTransaction() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    // ================= AJOUTER =================
    public void ajouter(Transaction t) {
        try {
            // Check if the user exists
            String checkUserSql = "SELECT id FROM users WHERE id = ?";
            try (PreparedStatement psCheck = cnx.prepareStatement(checkUserSql)) {
                psCheck.setInt(1, t.getUserId());
                ResultSet rs = psCheck.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException("Erreur ajout transaction : user_id " + t.getUserId() + " n'existe pas");
                }
            }

            // Insert the transaction
            String sql = """
                    INSERT INTO `transaction`
                    (user_id, type, amount, target, created_at)
                    VALUES (?, ?, ?, ?, NOW())
                    """;

            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, t.getUserId());
                ps.setString(2, t.getType());
                ps.setDouble(3, t.getAmount());
                ps.setString(4, t.getTarget());
                ps.executeUpdate();
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur ajout transaction : " + e.getMessage());
        }
    }

    // ================= RECUPERER PAR USER =================
    public List<Transaction> recupererParUser(int userId) {

        List<Transaction> list = new ArrayList<>();

        String sql = """
                SELECT * FROM `transaction`
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération transactions user : " + e.getMessage());
        }

        return list;
    }

    // ================= RECUPERER ALL (DASHBOARD) =================
    public List<Transaction> recuperer() {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM `transaction` ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération transactions : " + e.getMessage());
        }

        return list;
    }

    // ================= CALCUL BENEFICE =================
    public double calculerBenefice() {

        double benefice = 0;

        String sql = """
                SELECT SUM(amount * 0.02) AS benefice
                FROM `transaction`
                """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                benefice = rs.getDouble("benefice");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur calcul benefice : " + e.getMessage());
        }

        return benefice;
    }

    // ================= MAPPER =================
    private Transaction mapTransaction(ResultSet rs) throws Exception {

        return new Transaction(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("type"),
                rs.getDouble("amount"),
                rs.getString("target"),
                rs.getTimestamp("created_at")
        );
    }
    public int getNextId() throws Exception {
        String req = "SELECT MAX(id) FROM user"; // change table name if needed
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
        return 1;
    }

}
