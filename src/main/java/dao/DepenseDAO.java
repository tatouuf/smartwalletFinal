package dao;

import com.example.smartwallet.model.Depense;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DepenseDAO {

    public void ajouterDepense(Depense depense) {
        String sql = "INSERT INTO depenses (montant, description, date_depense, categorie, user_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, depense.getMontant());
            ps.setString(2, depense.getDescription());
            ps.setDate(3, Date.valueOf(depense.getDateDepense()));
            ps.setString(4, depense.getCategorie());
            ps.setInt(5, depense.getUserId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Depense> obtenirToutesDepenses(int userId) {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT id, montant, description, date_depense, categorie FROM depenses WHERE user_id = ? ORDER BY date_depense DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Depense depense = new Depense(
                    rs.getInt("id"),
                    rs.getDouble("montant"),
                    rs.getString("description"),
                    rs.getDate("date_depense").toLocalDate(),
                    rs.getString("categorie")
                );
                depense.setUserId(userId);
                depenses.add(depense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return depenses;
    }

    public List<Depense> obtenirDepensesParCategorie(int userId, String categorie) {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT id, montant, description, date_depense, categorie FROM depenses WHERE user_id = ? AND categorie = ? ORDER BY date_depense DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, categorie);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Depense depense = new Depense(
                    rs.getInt("id"),
                    rs.getDouble("montant"),
                    rs.getString("description"),
                    rs.getDate("date_depense").toLocalDate(),
                    rs.getString("categorie")
                );
                depense.setUserId(userId);
                depenses.add(depense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return depenses;
    }

    public List<Depense> obtenirDepensesMois(int userId, int mois, int annee) {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT id, montant, description, date_depense, categorie FROM depenses WHERE user_id = ? AND MONTH(date_depense) = ? AND YEAR(date_depense) = ? ORDER BY date_depense DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, mois);
            ps.setInt(3, annee);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Depense depense = new Depense(
                    rs.getInt("id"),
                    rs.getDouble("montant"),
                    rs.getString("description"),
                    rs.getDate("date_depense").toLocalDate(),
                    rs.getString("categorie")
                );
                depense.setUserId(userId);
                depenses.add(depense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return depenses;
    }

    public double getTotalDepenses(int userId) {
        String sql = "SELECT SUM(montant) as total FROM depenses WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalDepensesMois(int userId, int mois, int annee) {
        String sql = "SELECT SUM(montant) as total FROM depenses WHERE user_id = ? AND MONTH(date_depense) = ? AND YEAR(date_depense) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, mois);
            ps.setInt(3, annee);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalDepensesCategorieMois(int userId, String categorie, int mois, int annee) {
        String sql = "SELECT SUM(montant) as total FROM depenses WHERE user_id = ? AND categorie = ? AND MONTH(date_depense) = ? AND YEAR(date_depense) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, categorie);
            ps.setInt(3, mois);
            ps.setInt(4, annee);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Depense getLargestExpenseForMonth(int userId, int mois, int annee) {
        String sql = "SELECT * FROM depenses WHERE user_id = ? AND MONTH(date_depense) = ? AND YEAR(date_depense) = ? ORDER BY montant DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, mois);
            ps.setInt(3, annee);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Depense depense = new Depense(
                    rs.getInt("id"),
                    rs.getDouble("montant"),
                    rs.getString("description"),
                    rs.getDate("date_depense").toLocalDate(),
                    rs.getString("categorie")
                );
                depense.setUserId(userId);
                return depense;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void supprimerDepense(int depenseId) {
        String sql = "DELETE FROM depenses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, depenseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void supprimerPlusieursDepenses(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        
        StringBuilder sql = new StringBuilder("DELETE FROM depenses WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1) sql.append(",");
        }
        sql.append(")");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierDepense(Depense depense) {
        String sql = "UPDATE depenses SET montant = ?, description = ?, date_depense = ?, categorie = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, depense.getMontant());
            ps.setString(2, depense.getDescription());
            ps.setDate(3, Date.valueOf(depense.getDateDepense()));
            ps.setString(4, depense.getCategorie());
            ps.setInt(5, depense.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public java.util.Map<String, Double> getMonthlyTotals(int userId) {
        java.util.Map<String, Double> totals = new java.util.LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(date_depense, '%Y-%m') as mois, SUM(montant) as total " +
                     "FROM depenses WHERE user_id = ? " +
                     "GROUP BY DATE_FORMAT(date_depense, '%Y-%m') " +
                     "ORDER BY mois ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                totals.put(rs.getString("mois"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totals;
    }
}
