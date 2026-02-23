package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    private final Connection conn;

    public BudgetService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    public List<Budget> getAllByUser(int userId) {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT id, user_id, categorie_id, montant_max, mois, annee, planning_id, montant_actuel, description, date_creation, categorie FROM budgets WHERE user_id = ? ORDER BY date_creation DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Budget b = new Budget();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setCategorieId(rs.getObject("categorie_id") != null ? rs.getInt("categorie_id") : null);
                b.setMontantMax(rs.getDouble("montant_max"));
                b.setMois(rs.getObject("mois") != null ? rs.getInt("mois") : null);
                b.setAnnee(rs.getObject("annee") != null ? rs.getInt("annee") : null);
                b.setPlanningId(rs.getObject("planning_id") != null ? rs.getInt("planning_id") : null);
                b.setMontantActuel(rs.getDouble("montant_actuel"));
                b.setDescription(rs.getString("description"));
                java.sql.Date dt = rs.getDate("date_creation");
                if (dt != null) b.setDateCreation(dt.toLocalDate());
                b.setCategorie(rs.getString("categorie"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Budget getByUserCategoryMonthYear(int userId, String categorie, Integer mois, Integer annee) {
        String sql = "SELECT id, user_id, categorie_id, montant_max, mois, annee, planning_id, montant_actuel, description, date_creation, categorie FROM budgets WHERE user_id = ? AND categorie = ? AND mois = ? AND annee = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, categorie);
            if (mois != null) ps.setInt(3, mois); else ps.setNull(3, java.sql.Types.INTEGER);
            if (annee != null) ps.setInt(4, annee); else ps.setNull(4, java.sql.Types.INTEGER);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Budget b = new Budget();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setCategorieId(rs.getObject("categorie_id") != null ? rs.getInt("categorie_id") : null);
                b.setMontantMax(rs.getDouble("montant_max"));
                b.setMois(rs.getObject("mois") != null ? rs.getInt("mois") : null);
                b.setAnnee(rs.getObject("annee") != null ? rs.getInt("annee") : null);
                b.setPlanningId(rs.getObject("planning_id") != null ? rs.getInt("planning_id") : null);
                b.setMontantActuel(rs.getDouble("montant_actuel"));
                b.setDescription(rs.getString("description"));
                java.sql.Date dt = rs.getDate("date_creation");
                if (dt != null) b.setDateCreation(dt.toLocalDate());
                b.setCategorie(rs.getString("categorie"));
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean add(Budget b) {
        String sql = "INSERT INTO budgets (user_id, categorie_id, montant_max, mois, annee, planning_id, montant_actuel, description, date_creation, categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getUserId());
            if (b.getCategorieId() != null) ps.setInt(2, b.getCategorieId()); else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setDouble(3, b.getMontantMax());
            if (b.getMois() != null) ps.setInt(4, b.getMois()); else ps.setNull(4, java.sql.Types.INTEGER);
            if (b.getAnnee() != null) ps.setInt(5, b.getAnnee()); else ps.setNull(5, java.sql.Types.INTEGER);
            if (b.getPlanningId() != null) ps.setInt(6, b.getPlanningId()); else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setDouble(7, b.getMontantActuel());
            ps.setString(8, b.getDescription());
            ps.setString(9, b.getCategorie());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) b.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Budget b) {
        String sql = "UPDATE budgets SET categorie_id = ?, montant_max = ?, mois = ?, annee = ?, planning_id = ?, montant_actuel = ?, description = ?, categorie = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (b.getCategorieId() != null) ps.setInt(1, b.getCategorieId()); else ps.setNull(1, java.sql.Types.INTEGER);
            ps.setDouble(2, b.getMontantMax());
            if (b.getMois() != null) ps.setInt(3, b.getMois()); else ps.setNull(3, java.sql.Types.INTEGER);
            if (b.getAnnee() != null) ps.setInt(4, b.getAnnee()); else ps.setNull(4, java.sql.Types.INTEGER);
            if (b.getPlanningId() != null) ps.setInt(5, b.getPlanningId()); else ps.setNull(5, java.sql.Types.INTEGER);
            ps.setDouble(6, b.getMontantActuel());
            ps.setString(7, b.getDescription());
            ps.setString(8, b.getCategorie());
            ps.setInt(9, b.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
