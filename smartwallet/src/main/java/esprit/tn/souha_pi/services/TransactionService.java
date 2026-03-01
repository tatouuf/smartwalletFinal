package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    Connection cnx = MyDataBase.getInstance().getConnection();

    public void add(Transaction t) {
        String sql = """
            INSERT INTO transaction(user_id, type, amount, target, created_at)
            VALUES(?, ?, ?, ?, NOW())
            """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getTarget());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> getAll(int userId) {
        List<Transaction> list = new ArrayList<>();

        String sql = """
            SELECT * FROM transaction
            WHERE user_id = ?
            ORDER BY created_at DESC, id DESC
            """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("target"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public void addTransaction(Transaction t) {
        String sql = "INSERT INTO transaction(user_id, type, amount, target, created_at) VALUES(?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getTarget());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère les transactions d'un utilisateur (alias pour getAll)
     */
    public List<Transaction> getUserTransactions(int userId) {
        return getAll(userId);
    }

    /**
     * Récupère les transactions de type CREDIT (revenus)
     */
    public List<Transaction> getCreditTransactions(int userId) {
        List<Transaction> all = getAll(userId);
        List<Transaction> credits = new ArrayList<>();

        for (Transaction t : all) {
            // Considérer comme crédit si type est "CREDIT" ou amount > 0
            if ("CREDIT".equals(t.getType()) || t.getAmount() > 0) {
                credits.add(t);
            }
        }
        return credits;
    }

    /**
     * Récupère les transactions de type DEBIT (dépenses)
     */
    public List<Transaction> getDebitTransactions(int userId) {
        List<Transaction> all = getAll(userId);
        List<Transaction> debits = new ArrayList<>();

        for (Transaction t : all) {
            if ("DEBIT".equals(t.getType()) || t.getAmount() < 0) {
                debits.add(t);
            }
        }
        return debits;
    }

    /**
     * Calcule le total des crédits (revenus)
     */
    public double getTotalCredits(int userId) {
        List<Transaction> credits = getCreditTransactions(userId);
        double total = 0;
        for (Transaction t : credits) {
            total += Math.abs(t.getAmount()); // Prendre la valeur absolue
        }
        return total;
    }
    // Dans TransactionService.java
    public List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transaction ORDER BY created_at DESC";

        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("target"),
                        rs.getTimestamp("created_at")
                );
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * Calcule le total des débits (dépenses)
     */
    public double getTotalDebits(int userId) {
        List<Transaction> debits = getDebitTransactions(userId);
        double total = 0;
        for (Transaction t : debits) {
            total += Math.abs(t.getAmount()); // Prendre la valeur absolue
        }
        return total;
    }
}