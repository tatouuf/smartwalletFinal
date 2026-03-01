package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DepenseService {

    private final Connection conn;

    public DepenseService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    // ================= GET ALL =================
    public List<Depense> getAll() {
        List<Depense> list = new ArrayList<>();
        String sql = "SELECT * FROM depenses ORDER BY date_depense DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDepense(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= GET BY USER =================
    public List<Depense> getAllByUser(int userId) {
        List<Depense> list = new ArrayList<>();
        String sql = "SELECT * FROM depenses WHERE user_id=? ORDER BY date_depense DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToDepense(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= ADD =================
    public boolean add(Depense d) {

        String sql = "INSERT INTO depenses (user_id, montant, description, date_depense, categorie) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, d.getUserId());
            ps.setDouble(2, d.getMontant());
            ps.setString(3, d.getDescription());

            if (d.getDateDepense() != null)
                ps.setDate(4, Date.valueOf(d.getDateDepense()));
            else
                ps.setDate(4, Date.valueOf(LocalDate.now()));

            ps.setString(5, d.getCategorie());

            int affected = ps.executeUpdate();

            if (affected == 0)
                return false;

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                d.setId(keys.getInt(1));

            // 🔥 METTRE À JOUR BUDGET + VERIFIER DEPASSEMENT
            updateBudgetAndCheck(d);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= UPDATE =================
    public boolean update(Depense d) {

        String sql = "UPDATE depenses SET montant=?, description=?, date_depense=?, categorie=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, d.getMontant());
            ps.setString(2, d.getDescription());

            if (d.getDateDepense() != null)
                ps.setDate(3, Date.valueOf(d.getDateDepense()));
            else
                ps.setDate(3, Date.valueOf(LocalDate.now()));

            ps.setString(4, d.getCategorie());
            ps.setInt(5, d.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= DELETE =================
    public boolean delete(int id) {

        String sql = "DELETE FROM depenses WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= LOGIQUE BUDGET =================
    private void updateBudgetAndCheck(Depense d) throws SQLException {

        String sqlBudget = "SELECT * FROM budgets WHERE user_id=? AND categorie=?";

        try (PreparedStatement ps = conn.prepareStatement(sqlBudget)) {

            ps.setInt(1, d.getUserId());
            ps.setString(2, d.getCategorie());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                double montantMax = rs.getDouble("montant_max");
                double montantActuel = rs.getDouble("montant_actuel");

                double nouveauTotal = montantActuel + d.getMontant();

                // UPDATE montant_actuel
                String update = "UPDATE budgets SET montant_actuel=? WHERE id=?";
                try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
                    psUpdate.setDouble(1, nouveauTotal);
                    psUpdate.setInt(2, rs.getInt("id"));
                    psUpdate.executeUpdate();
                }

                // 🔥 SI DEPASSEMENT → CREER NOTIFICATION
                if (nouveauTotal > montantMax) {

                    NotificationService ns = new NotificationService();

                    Notification notif = new Notification(
                            d.getUserId(),
                            "🚨 Budget dépassé",
                            "Vous avez dépassé le budget pour la catégorie : "
                                    + d.getCategorie()
                                    + "\nBudget max : " + montantMax
                                    + "\nTotal actuel : " + nouveauTotal,
                            "BUDGET_ALERT"
                    );

                    ns.ajouter(notif);
                }
            }
        }
    }

    // ================= MAPPING =================
    private Depense mapResultSetToDepense(ResultSet rs) throws SQLException {

        Depense d = new Depense();

        d.setId(rs.getInt("id"));
        d.setMontant(rs.getDouble("montant"));
        d.setDescription(rs.getString("description"));

        Date dt = rs.getDate("date_depense");
        if (dt != null)
            d.setDateDepense(dt.toLocalDate());

        d.setCategorie(rs.getString("categorie"));
        d.setUserId(rs.getInt("user_id"));

        return d;
    }
}