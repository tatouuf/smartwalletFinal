package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    private final Connection conn;

    public BudgetService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    // ==================== MÉTHODES POUR BUDGETS ====================

    public List<Budget> getAll() {
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT b.*, c.nom as categorie_nom FROM budgets b " +
                "LEFT JOIN categories c ON b.categorie_id = c.id " +
                "ORDER BY b.annee DESC, b.mois DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setCategorieId(rs.getInt("categorie_id"));
                if (rs.wasNull()) budget.setCategorieId(null);

                // Ajouter le nom de la catégorie
                budget.setCategorie(rs.getString("categorie_nom"));

                budget.setMontantMax(rs.getDouble("montant_max"));
                budget.setMois(rs.getInt("mois"));
                if (rs.wasNull()) budget.setMois(null);
                budget.setAnnee(rs.getInt("annee"));
                if (rs.wasNull()) budget.setAnnee(null);
                budget.setPlanningId(rs.getInt("planning_id"));
                if (rs.wasNull()) budget.setPlanningId(null);

                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    public List<Budget> getAllByUser(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT b.*, c.nom as categorie_nom FROM budgets b " +
                "LEFT JOIN categories c ON b.categorie_id = c.id " +
                "WHERE b.user_id = ? ORDER BY b.annee DESC, b.mois DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setCategorieId(rs.getInt("categorie_id"));
                if (rs.wasNull()) budget.setCategorieId(null);

                // Ajouter le nom de la catégorie
                budget.setCategorie(rs.getString("categorie_nom"));

                budget.setMontantMax(rs.getDouble("montant_max"));
                budget.setMois(rs.getInt("mois"));
                if (rs.wasNull()) budget.setMois(null);
                budget.setAnnee(rs.getInt("annee"));
                if (rs.wasNull()) budget.setAnnee(null);
                budget.setPlanningId(rs.getInt("planning_id"));
                if (rs.wasNull()) budget.setPlanningId(null);

                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    // ==================== MÉTHODES POUR DÉPENSES (CORRIGÉES) ====================

    public List<Depense> getAllDepenses() {
        List<Depense> depenses = new ArrayList<>();
        String query = "SELECT d.*, c.nom as categorie_nom FROM depenses d " +
                "LEFT JOIN categories c ON d.categorie_id = c.id " +
                "ORDER BY d.date_depense DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Depense depense = new Depense();
                depense.setId(rs.getInt("id"));
                depense.setUserId(rs.getInt("user_id"));

                // Utiliser setCategorie() au lieu de setCategorieId()
                String categorieNom = rs.getString("categorie_nom");
                depense.setCategorie(categorieNom != null ? categorieNom : "Autre");

                depense.setMontant(rs.getDouble("montant"));
                depense.setDescription(rs.getString("description"));

                if (rs.getDate("date_depense") != null) {
                    depense.setDateDepense(rs.getDate("date_depense").toLocalDate());
                }

                depenses.add(depense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return depenses;
    }

    public List<Depense> getAllDepensesByUser(int userId) {
        List<Depense> depenses = new ArrayList<>();
        String query = "SELECT d.*, c.nom as categorie_nom FROM depenses d " +
                "LEFT JOIN categories c ON d.categorie_id = c.id " +
                "WHERE d.user_id = ? ORDER BY d.date_depense DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Depense depense = new Depense();
                depense.setId(rs.getInt("id"));
                depense.setUserId(rs.getInt("user_id"));

                // Utiliser setCategorie() au lieu de setCategorieId()
                String categorieNom = rs.getString("categorie_nom");
                depense.setCategorie(categorieNom != null ? categorieNom : "Autre");

                depense.setMontant(rs.getDouble("montant"));
                depense.setDescription(rs.getString("description"));

                if (rs.getDate("date_depense") != null) {
                    depense.setDateDepense(rs.getDate("date_depense").toLocalDate());
                }

                depenses.add(depense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return depenses;
    }

    // ==================== AUTRES MÉTHODES ====================

    public boolean add(Budget b) {
        String sql = "INSERT INTO budgets (user_id, categorie_id, montant_max, mois, annee, planning_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getUserId());
            ps.setObject(2, b.getCategorieId(), java.sql.Types.INTEGER);
            ps.setDouble(3, b.getMontantMax());
            ps.setObject(4, b.getMois(), java.sql.Types.INTEGER);
            ps.setObject(5, b.getAnnee(), java.sql.Types.INTEGER);
            ps.setObject(6, b.getPlanningId(), java.sql.Types.INTEGER);

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
        String sql = "UPDATE budgets SET categorie_id = ?, montant_max = ?, mois = ?, annee = ?, planning_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, b.getCategorieId(), java.sql.Types.INTEGER);
            ps.setDouble(2, b.getMontantMax());
            ps.setObject(3, b.getMois(), java.sql.Types.INTEGER);
            ps.setObject(4, b.getAnnee(), java.sql.Types.INTEGER);
            ps.setObject(5, b.getPlanningId(), java.sql.Types.INTEGER);
            ps.setInt(6, b.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère un budget par utilisateur, catégorie, mois et année
     * @param userId ID de l'utilisateur
     * @param categorie Nom de la catégorie (String)
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Budget correspondant ou null si non trouvé
     */
    public Budget getByUserCategoryMonthYear(int userId, String categorie, int mois, int annee) {
        String sql = "SELECT b.*, c.nom as categorie_nom FROM budgets b " +
                "LEFT JOIN categories c ON b.categorie_id = c.id " +
                "WHERE b.user_id = ? AND c.nom = ? AND b.mois = ? AND b.annee = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, categorie);
            ps.setInt(3, mois);
            ps.setInt(4, annee);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setUserId(rs.getInt("user_id"));

                int categorieId = rs.getInt("categorie_id");
                if (!rs.wasNull()) budget.setCategorieId(categorieId);

                budget.setMontantMax(rs.getDouble("montant_max"));

                int moisVal = rs.getInt("mois");
                if (!rs.wasNull()) budget.setMois(moisVal);

                int anneeVal = rs.getInt("annee");
                if (!rs.wasNull()) budget.setAnnee(anneeVal);

                int planningId = rs.getInt("planning_id");
                if (!rs.wasNull()) budget.setPlanningId(planningId);

                return budget;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Version alternative qui utilise categorie_id au lieu du nom
     */
    public Budget getByUserCategoryIdMonthYear(int userId, int categorieId, int mois, int annee) {
        String sql = "SELECT * FROM budgets WHERE user_id = ? AND categorie_id = ? AND mois = ? AND annee = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, categorieId);
            ps.setInt(3, mois);
            ps.setInt(4, annee);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setUserId(rs.getInt("user_id"));

                int catId = rs.getInt("categorie_id");
                if (!rs.wasNull()) budget.setCategorieId(catId);

                budget.setMontantMax(rs.getDouble("montant_max"));

                int moisVal = rs.getInt("mois");
                if (!rs.wasNull()) budget.setMois(moisVal);

                int anneeVal = rs.getInt("annee");
                if (!rs.wasNull()) budget.setAnnee(anneeVal);

                int planningId = rs.getInt("planning_id");
                if (!rs.wasNull()) budget.setPlanningId(planningId);

                return budget;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}