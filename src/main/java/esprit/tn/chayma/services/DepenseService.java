package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.*;
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

    // ✅ AJOUTEZ CETTE MÉTHODE
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

                // Utiliser categorie_nom au lieu de categorie
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

                // Utiliser categorie_nom au lieu de categorie
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

    // Dans DepenseService.java
    public AddResponse addWithMessage(Depense depense, String categorieNom) {
        try {
            // Nettoyer la catégorie
            String cleanCategorie = categorieNom.replaceAll("^[^A-Za-zÀ-ÿ]+", "").trim();

            // Trouver ou créer l'ID de la catégorie
            int categorieId = getOrCreateCategoryId(cleanCategorie);

            String sql = "INSERT INTO depenses (user_id, categorie_id, montant, description, date_depense) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, depense.getUserId());
                ps.setInt(2, categorieId);
                ps.setDouble(3, depense.getMontant());
                ps.setString(4, depense.getDescription());
                ps.setDate(5, java.sql.Date.valueOf(depense.getDateDepense()));

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    return new AddResponse(AddResult.FAILED, "Échec de l'insertion");
                }

                // Récupérer l'ID généré
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    depense.setId(rs.getInt(1));
                }
            }

            // Vérifier le dépassement de budget
            NotificationService notificationService = new NotificationService();
            notificationService.checkBudgetExceeded(depense.getUserId(), cleanCategorie, depense.getMontant(),
                    depense.getDateDepense().getMonthValue(), depense.getDateDepense().getYear());

            return new AddResponse(AddResult.ADDED, "Dépense ajoutée avec succès");

        } catch (SQLException e) {
            e.printStackTrace();
            return new AddResponse(AddResult.FAILED, "Erreur SQL: " + e.getMessage());
        }
    }

    private int getOrCreateCategoryId(String categorieNom) throws SQLException {
        // Chercher la catégorie
        String sql = "SELECT id FROM categories WHERE nom = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categorieNom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // Créer la catégorie si elle n'existe pas
        String insertSql = "INSERT INTO categories (nom, type) VALUES (?, 'DEPENSE')";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categorieNom);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        // En dernier recours, retourner 1 (Alimentation) ou 13 (Autre)
        return 1;
    }

    private int getCategoryIdByName(String categorieNom) throws SQLException {
        String sql = "SELECT id FROM categories WHERE nom = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categorieNom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        // Si la catégorie n'existe pas, créer une nouvelle ou retourner 1 (Autre)
        return 1; // ID de la catégorie "Autre" ou "Alimentation"
    }



    public boolean update(Depense d) {
        String sql = "UPDATE depenses SET montant = ?, description = ?, date_depense = ?, categorie = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, d.getMontant());
            ps.setString(2, d.getDescription());
            if (d.getDateDepense() != null) {
                ps.setDate(3, java.sql.Date.valueOf(d.getDateDepense()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
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