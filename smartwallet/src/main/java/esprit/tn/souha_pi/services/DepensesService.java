package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service dédié aux dépenses (transactions de type DEBIT).
 * <p>
 * Cette classe encapsule les opérations SQL relatives aux dépenses afin de
 * simplifier l'utilisation depuis les contrôleurs et autres composants.
 */
public class DepensesService {

    private final Connection cnx = MyDataBase.getInstance().getConnection();

    /**
     * Ajoute une dépense en base. La transaction fournie doit déjà avoir son
     * type défini en "DEBIT" ou un montant négatif.
     */
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
            throw new RuntimeException("Impossible d'ajouter la dépense", e);
        }
    }

    /**
     * Récupère toutes les dépenses (type DEBIT) pour un utilisateur donné.
     */
    public List<Transaction> getAll(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT * FROM transaction
            WHERE user_id = ?
              AND (type = 'DEBIT' OR amount < 0)
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
            throw new RuntimeException("Erreur lors de la lecture des dépenses", e);
        }
        return list;
    }

    /**
     * Retourne le total des dépenses pour un utilisateur. Le résultat est
     * toujours positif (valeur absolue des montants).
     */
    public double getTotal(int userId) {
        List<Transaction> debits = getAll(userId);
        double total = 0;
        for (Transaction t : debits) {
            total += Math.abs(t.getAmount());
        }
        return total;
    }

    /**
     * Fournit une liste de dépenses filtrée selon une catégorie cible (colonne
     * "target" dans la table transaction).
     */
    public List<Transaction> getByCategory(int userId, String category) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT * FROM transaction
            WHERE user_id = ?
              AND (type = 'DEBIT' OR amount < 0)
              AND target = ?
            ORDER BY created_at DESC
            """;
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
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
            throw new RuntimeException("Erreur lors de la lecture des dépenses par catégorie", e);
        }
        return list;
    }
}
