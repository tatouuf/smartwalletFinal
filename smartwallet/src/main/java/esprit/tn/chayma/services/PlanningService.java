package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanningService {

    private final Connection conn;

    public PlanningService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    // ==========================
    // EXISTS (UNIQUE CHECK)
    // ==========================
    public boolean exists(int userId, Integer mois, Integer annee) {

        if (mois == null || annee == null)
            return false;

        String sql = "SELECT COUNT(*) FROM plannings WHERE user_id = ? AND mois = ? AND annee = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, mois);
            ps.setInt(3, annee);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.out.println("ERREUR EXISTS:");
            e.printStackTrace();
        }

        return false;
    }

    // ==========================
    // GET ALL
    // ==========================
    public List<Planning> getAllByUser(int userId) {

        List<Planning> list = new ArrayList<>();
        String sql = "SELECT * FROM plannings WHERE user_id = ? ORDER BY created_at DESC";

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
                p.setMois((Integer) rs.getObject("mois"));
                p.setAnnee((Integer) rs.getObject("annee"));
                p.setRevenuPrevu(rs.getDouble("revenu_prevu"));
                p.setEpargnePrevue(rs.getDouble("epargne_prevue"));
                p.setPourcentageEpargne((Double) rs.getObject("pourcentage_epargne"));
                p.setStatut(rs.getString("statut"));

                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("ERREUR ADD:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // ==========================
    // ADD
    // ==========================
    public boolean add(Planning p) {

        String sql = "INSERT INTO plannings " +
                "(user_id, nom, description, type, mois, annee, revenu_prevu, epargne_prevue, pourcentage_epargne, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getNom());

            if (p.getDescription() != null)
                ps.setString(3, p.getDescription());
            else
                ps.setNull(3, Types.VARCHAR);

            ps.setString(4, p.getType());

            if (p.getMois() != null)
                ps.setInt(5, p.getMois());
            else
                ps.setNull(5, Types.INTEGER);

            if (p.getAnnee() != null)
                ps.setInt(6, p.getAnnee());
            else
                ps.setNull(6, Types.INTEGER);

            ps.setDouble(7, p.getRevenuPrevu());
            ps.setDouble(8, p.getEpargnePrevue());

            if (p.getPourcentageEpargne() != null)
                ps.setDouble(9, p.getPourcentageEpargne());
            else
                ps.setNull(9, Types.DOUBLE);

            ps.setString(10, p.getStatut());

            int affected = ps.executeUpdate();

            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next())
                    p.setId(rs.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            System.out.println("ERREUR ADD:");
            e.printStackTrace();
        }

        return false;
    }

    // ==========================
    // UPDATE
    // ==========================
    public boolean update(Planning p) {

        String sql = "UPDATE plannings SET " +
                "nom=?, description=?, type=?, mois=?, annee=?, revenu_prevu=?, " +
                "epargne_prevue=?, pourcentage_epargne=?, statut=? " +
                "WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getType());

            if (p.getMois() != null)
                ps.setInt(4, p.getMois());
            else
                ps.setNull(4, Types.INTEGER);

            if (p.getAnnee() != null)
                ps.setInt(5, p.getAnnee());
            else
                ps.setNull(5, Types.INTEGER);

            ps.setDouble(6, p.getRevenuPrevu());
            ps.setDouble(7, p.getEpargnePrevue());

            if (p.getPourcentageEpargne() != null)
                ps.setDouble(8, p.getPourcentageEpargne());
            else
                ps.setNull(8, Types.DOUBLE);

            ps.setString(9, p.getStatut());
            ps.setInt(10, p.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("ERREUR UPDATE:");
            e.printStackTrace();
        }

        return false;
    }

    // ==========================
    // DELETE
    // ==========================
    public boolean delete(int id) {

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM plannings WHERE id=?")) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("ERREUR DELETE:");
            e.printStackTrace();
        }

        return false;
    }
}