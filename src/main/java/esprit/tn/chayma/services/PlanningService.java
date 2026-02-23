package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlanningService {

    private final Connection conn;

    public PlanningService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    public List<Planning> getAllByUser(int userId) {
        List<Planning> list = new ArrayList<>();
        String sql = "SELECT id, user_id, nom, description, type, mois, annee, revenu_prevu, epargne_prevue, pourcentage_epargne, statut, created_at, updated_at FROM planning WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Planning p = new Planning();
                p.setId(rs.getInt("id"));
                p.setUserId(rs.getInt("user_id"));
                p.setNom(rs.getString("nom"));
                p.setDescription(rs.getString("description"));
                p.setType(rs.getString("type"));
                p.setMois(rs.getObject("mois") != null ? rs.getInt("mois") : null);
                p.setAnnee(rs.getObject("annee") != null ? rs.getInt("annee") : null);
                p.setRevenuPrevu(rs.getDouble("revenu_prevu"));
                p.setEpargnePrevue(rs.getDouble("epargne_prevue"));
                p.setPourcentageEpargne(rs.getObject("pourcentage_epargne") != null ? rs.getInt("pourcentage_epargne") : null);
                p.setStatut(rs.getString("statut"));
                java.sql.Timestamp c = rs.getTimestamp("created_at");
                if (c != null) p.setCreatedAt(c.toLocalDateTime().toLocalDate());
                java.sql.Timestamp u = rs.getTimestamp("updated_at");
                if (u != null) p.setUpdatedAt(u.toLocalDateTime().toLocalDate());
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(Planning p) {
        String sql = "INSERT INTO plannings (user_id, nom, description, `type`, mois, annee, revenu_prevu, epargne_prevue, pourcentage_epargne, statut, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP())";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getType());
            if (p.getMois() != null) ps.setInt(5, p.getMois()); else ps.setNull(5, java.sql.Types.INTEGER);
            if (p.getAnnee() != null) ps.setInt(6, p.getAnnee()); else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setDouble(7, p.getRevenuPrevu());
            ps.setDouble(8, p.getEpargnePrevue());
            if (p.getPourcentageEpargne() != null) ps.setInt(9, p.getPourcentageEpargne()); else ps.setNull(9, java.sql.Types.INTEGER);
            ps.setString(10, p.getStatut());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) p.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Planning p) {
        String sql = "UPDATE planning SET nom = ?, description = ?, type = ?, mois = ?, annee = ?, revenu_prevu = ?, epargne_prevue = ?, pourcentage_epargne = ?, statut = ?, updated_at = CURRENT_TIMESTAMP() WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getType());
            if (p.getMois() != null) ps.setInt(4, p.getMois()); else ps.setNull(4, java.sql.Types.INTEGER);
            if (p.getAnnee() != null) ps.setInt(5, p.getAnnee()); else ps.setNull(5, java.sql.Types.INTEGER);
            ps.setDouble(6, p.getRevenuPrevu());
            ps.setDouble(7, p.getEpargnePrevue());
            if (p.getPourcentageEpargne() != null) ps.setInt(8, p.getPourcentageEpargne()); else ps.setNull(8, java.sql.Types.INTEGER);
            ps.setString(9, p.getStatut());
            ps.setInt(10, p.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM planning WHERE id = ?";
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
