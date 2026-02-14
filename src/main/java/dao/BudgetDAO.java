package dao;

import com.example.smartwallet.model.Budget;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public void ajouterBudget(Budget budget) {
        String sql = "INSERT INTO budgets (categorie, montant_max, montant_actuel, mois, annee, user_id, description, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, budget.getCategorie());
            ps.setDouble(2, budget.getMontantMax());
            ps.setDouble(3, budget.getMontantActuel());
            ps.setInt(4, budget.getMois());
            ps.setInt(5, budget.getAnnee());
            ps.setInt(6, budget.getUserId());
            ps.setString(7, budget.getDescription());
            ps.setDate(8, Date.valueOf(budget.getDateCreation() != null ? budget.getDateCreation() : LocalDate.now()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Budget> obtenirTousBudgets(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT id, categorie, montant_max, montant_actuel, mois, annee, description, date_creation FROM budgets WHERE user_id = ? ORDER BY date_creation DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setCategorie(rs.getString("categorie"));
                budget.setMontantMax(rs.getDouble("montant_max"));
                budget.setMontantActuel(rs.getDouble("montant_actuel"));
                budget.setMois(rs.getInt("mois"));
                budget.setAnnee(rs.getInt("annee"));
                budget.setDescription(rs.getString("description"));
                budget.setDateCreation(rs.getDate("date_creation").toLocalDate());
                budget.setUserId(userId);
                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    public List<Budget> obtenirBudgetsMois(int userId, int mois, int annee) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT id, categorie, montant_max, montant_actuel, mois, annee, description, date_creation FROM budgets WHERE user_id = ? AND mois = ? AND annee = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, mois);
            ps.setInt(3, annee);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setCategorie(rs.getString("categorie"));
                budget.setMontantMax(rs.getDouble("montant_max"));
                budget.setMontantActuel(rs.getDouble("montant_actuel"));
                budget.setMois(rs.getInt("mois"));
                budget.setAnnee(rs.getInt("annee"));
                budget.setDescription(rs.getString("description"));
                budget.setDateCreation(rs.getDate("date_creation").toLocalDate());
                budget.setUserId(userId);
                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    public Budget obtenirBudgetParCategorie(int userId, String categorie, int mois, int annee) {
        String sql = "SELECT id, categorie, montant_max, montant_actuel, mois, annee, description, date_creation FROM budgets WHERE user_id = ? AND categorie = ? AND mois = ? AND annee = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, categorie);
            ps.setInt(3, mois);
            ps.setInt(4, annee);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setCategorie(rs.getString("categorie"));
                budget.setMontantMax(rs.getDouble("montant_max"));
                budget.setMontantActuel(rs.getDouble("montant_actuel"));
                budget.setMois(rs.getInt("mois"));
                budget.setAnnee(rs.getInt("annee"));
                budget.setDescription(rs.getString("description"));
                budget.setDateCreation(rs.getDate("date_creation").toLocalDate());
                budget.setUserId(userId);
                return budget;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getTotalBudgets(int userId) {
        String sql = "SELECT SUM(montant_max) as total FROM budgets WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return Double.isNaN(total) ? 0.0 : total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void supprimerBudget(int budgetId) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierBudget(Budget budget) {
        String sql = "UPDATE budgets SET categorie = ?, montant_max = ?, montant_actuel = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, budget.getCategorie());
            ps.setDouble(2, budget.getMontantMax());
            ps.setDouble(3, budget.getMontantActuel());
            ps.setString(4, budget.getDescription());
            ps.setInt(5, budget.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mettreAJourMontantActuel(int budgetId, double montantActuel) {
        String sql = "UPDATE budgets SET montant_actuel = montant_actuel + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, montantActuel);
            ps.setInt(2, budgetId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
