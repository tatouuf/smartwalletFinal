package dao;

import com.example.smartwallet.model.Planning;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanningDAO {
    private static final Logger LOGGER = Logger.getLogger(PlanningDAO.class.getName());

    public void ajouterPlanning(Planning planning) {
        String sql = "INSERT INTO plannings (nom, description, type, mois, annee, revenu_prevu, epargne_prevue, pourcentage_epargne, statut, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Impossible d'établir la connexion à la base de données");
                return;
            }

            ps.setString(1, planning.getNom());
            ps.setString(2, planning.getDescription());
            ps.setString(3, planning.getType()); // doit correspondre à l'enum MySQL exactement
            ps.setInt(4, planning.getMois());
            ps.setInt(5, planning.getAnnee());
            ps.setDouble(6, planning.getRevenuPrevu());
            ps.setDouble(7, planning.getEpargnePrevue());
            ps.setInt(8, planning.getPourcentageEpargne());
            ps.setString(9, planning.getStatut()); // idem enum exact
            ps.setInt(10, planning.getUserId());

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'un planning", e);
        }
    }

    public List<Planning> obtenirTousPlannings(int userId) {
        List<Planning> plannings = new ArrayList<>();
        String sql = "SELECT id, nom, description, type, mois, annee, revenu_prevu, epargne_prevue, pourcentage_epargne, statut FROM plannings WHERE user_id = ? ORDER BY date_creation DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Impossible de établir la connexion à la base de données");
                return plannings;
            }

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Planning planning = new Planning();
                planning.setId(rs.getInt("id"));
                planning.setNom(rs.getString("nom"));
                planning.setDescription(rs.getString("description"));
                planning.setType(rs.getString("type"));
                planning.setMois(rs.getInt("mois"));
                planning.setAnnee(rs.getInt("annee"));
                planning.setRevenuPrevu(rs.getDouble("revenu_prevu"));
                planning.setEpargnePrevue(rs.getDouble("epargne_prevue"));
                planning.setPourcentageEpargne(rs.getInt("pourcentage_epargne"));
                planning.setStatut(rs.getString("statut"));
                planning.setUserId(userId);
                plannings.add(planning);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des plannings", e);
        }
        return plannings;
    }

    public List<Planning> obtenirPlanningsMois(int userId, int mois, int annee) {
        List<Planning> plannings = new ArrayList<>();
        String sql = "SELECT id, nom, description, type, mois, annee, revenu_prevu, epargne_prevue, pourcentage_epargne, statut FROM plannings WHERE user_id = ? AND mois = ? AND annee = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Impossible de établir la connexion à la base de données");
                return plannings;
            }

            ps.setInt(1, userId);
            ps.setInt(2, mois);
            ps.setInt(3, annee);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Planning planning = new Planning();
                planning.setId(rs.getInt("id"));
                planning.setNom(rs.getString("nom"));
                planning.setDescription(rs.getString("description"));
                planning.setType(rs.getString("type"));
                planning.setMois(rs.getInt("mois"));
                planning.setAnnee(rs.getInt("annee"));
                planning.setRevenuPrevu(rs.getDouble("revenu_prevu"));
                planning.setEpargnePrevue(rs.getDouble("epargne_prevue"));
                planning.setPourcentageEpargne(rs.getInt("pourcentage_epargne"));
                planning.setStatut(rs.getString("statut"));
                planning.setUserId(userId);
                plannings.add(planning);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des plannings du mois", e);
        }
        return plannings;
    }

    public int getTotalPlannings(int userId) {
        String sql = "SELECT COUNT(*) as total FROM plannings WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Impossible de établir la connexion à la base de données");
                return 0;
            }

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des plannings", e);
        }
        return 0;
    }

    public void supprimerPlanning(int planningId) {
        String sql = "DELETE FROM plannings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Impossible de établir la connexion à la base de données");
                return;
            }

            ps.setInt(1, planningId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du planning", e);
        }
    }

    public void supprimerPlusieursPlannings(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        
        StringBuilder sql = new StringBuilder("DELETE FROM plannings WHERE id IN (");
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
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression multiple de plannings", e);
        }
    }

    public void modifierPlanning(Planning planning) {
        String sql = "UPDATE plannings SET nom = ?, description = ?, type = ?, revenu_prevu = ?, epargne_prevue = ?, pourcentage_epargne = ?, statut = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Impossible de établir la connexion à la base de données");
                return;
            }

            ps.setString(1, planning.getNom());
            ps.setString(2, planning.getDescription());
            ps.setString(3, planning.getType());
            ps.setDouble(4, planning.getRevenuPrevu());
            ps.setDouble(5, planning.getEpargnePrevue());
            ps.setInt(6, planning.getPourcentageEpargne());
            ps.setString(7, planning.getStatut());
            ps.setInt(8, planning.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du planning", e);
        }
    }

    public double getTotalRevenuMois(int userId, int mois, int annee) {
        String sql = "SELECT SUM(revenu_prevu) as total FROM plannings WHERE user_id = ? AND mois = ? AND annee = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) return 0.0;

            ps.setInt(1, userId);
            ps.setInt(2, mois);
            ps.setInt(3, annee);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du revenu total", e);
        }
        return 0.0;
    }
}
