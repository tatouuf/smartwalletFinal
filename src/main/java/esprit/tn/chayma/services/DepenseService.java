package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.utils.DialogUtil;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DepenseService {

    private final Connection conn;
    private final BudgetService budgetService = new BudgetService();
    private final NotificationService notificationService = new NotificationService();

    public DepenseService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    public List<Depense> getAll() {
        List<Depense> list = new ArrayList<>();
        String sql = "SELECT id, montant, description, date_depense, categorie, user_id FROM depenses ORDER BY date_depense DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Depense d = new Depense();
                d.setId(rs.getInt("id"));
                d.setMontant(rs.getDouble("montant"));
                d.setDescription(rs.getString("description"));
                java.sql.Date dt = rs.getDate("date_depense");
                if (dt != null) d.setDateDepense(dt.toLocalDate());
                d.setCategorie(rs.getString("categorie"));
                d.setUserId(rs.getInt("user_id"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Depense> getAllByUser(int userId) {
        List<Depense> list = new ArrayList<>();
        String sql = "SELECT id, montant, description, date_depense, categorie, user_id FROM depenses WHERE user_id = ? ORDER BY date_depense DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Depense d = new Depense();
                d.setId(rs.getInt("id"));
                d.setMontant(rs.getDouble("montant"));
                d.setDescription(rs.getString("description"));
                java.sql.Date dt = rs.getDate("date_depense");
                if (dt != null) d.setDateDepense(dt.toLocalDate());
                d.setCategorie(rs.getString("categorie"));
                d.setUserId(rs.getInt("user_id"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Depense getById(int id) {
        String sql = "SELECT id, montant, description, date_depense, categorie, user_id FROM depenses WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Depense d = new Depense();
                d.setId(rs.getInt("id"));
                d.setMontant(rs.getDouble("montant"));
                d.setDescription(rs.getString("description"));
                java.sql.Date dt = rs.getDate("date_depense");
                if (dt != null) d.setDateDepense(dt.toLocalDate());
                d.setCategorie(rs.getString("categorie"));
                d.setUserId(rs.getInt("user_id"));
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean add(Depense d) {
        String sql = "INSERT INTO depenses (user_id, planning_id, categorie_id, montant, description, date_depense, created_at, categorie) VALUES (?, NULL, NULL, ?, ?, ?, CURRENT_TIMESTAMP(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getUserId());
            ps.setDouble(2, d.getMontant());
            ps.setString(3, d.getDescription());
            if (d.getDateDepense() != null) ps.setDate(4, java.sql.Date.valueOf(d.getDateDepense())); else ps.setDate(4, null);
            ps.setString(5, d.getCategorie());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) d.setId(keys.getInt(1));

            // Après insertion: vérifier le total des dépenses pour cette catégorie/mois/année
            try {
                LocalDate date = d.getDateDepense() != null ? d.getDateDepense() : LocalDate.now();
                int mois = date.getMonthValue();
                int annee = date.getYear();
                String categorie = d.getCategorie();
                int userId = d.getUserId();

                // Calculer somme des dépenses pour user/categorie/mois/annee
                String sumSql = "SELECT SUM(montant) as total FROM depenses WHERE user_id = ? AND categorie = ? AND MONTH(date_depense) = ? AND YEAR(date_depense) = ?";
                try (PreparedStatement sumPs = conn.prepareStatement(sumSql)) {
                    sumPs.setInt(1, userId);
                    sumPs.setString(2, categorie);
                    sumPs.setInt(3, mois);
                    sumPs.setInt(4, annee);
                    ResultSet rs = sumPs.executeQuery();
                    double total = 0;
                    if (rs.next()) total = rs.getDouble("total");

                    // Vérifier budget
                    Budget budget = budgetService.getByUserCategoryMonthYear(userId, categorie, mois, annee);
                    if (budget != null && budget.getMontantMax() > 0 && total > budget.getMontantMax()) {
                        // Créer une notification de dépassement
                        String msg = String.format("🔔 Dépassement du budget pour %s: total %.2f DT > limite %.2f DT", categorie, total, budget.getMontantMax());
                        Notification n = new Notification(userId, "depassement_budget", msg, budget.getId());
                        notificationService.add(n);
                        // Afficher alerte immédiate à l'utilisateur
                        DialogUtil.info("Alerte budget", msg);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Depense d) {
        String sql = "UPDATE depenses SET montant = ?, description = ?, date_depense = ?, categorie = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, d.getMontant());
            ps.setString(2, d.getDescription());
            if (d.getDateDepense() != null) ps.setDate(3, java.sql.Date.valueOf(d.getDateDepense())); else ps.setDate(3, null);
            ps.setString(4, d.getCategorie());
            ps.setInt(5, d.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM depenses WHERE id = ?";
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
