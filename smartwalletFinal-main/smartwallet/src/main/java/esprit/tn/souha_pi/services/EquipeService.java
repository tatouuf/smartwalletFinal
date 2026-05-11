package esprit.tn.souha_pi.services;
import esprit.tn.souha_pi.entities.Equipe;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeService {

    private Connection cnx = MyDataBase.getInstance().getConnection();

    // CREATE → Ajouter une équipe
    public void add(Equipe e) {
        String query = "INSERT INTO equipe (nom, logo, game, categorie, coach_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getLogo());
            ps.setString(3, e.getGame());
            ps.setString(4, e.getCategorie());
            ps.setInt(5, e.getCoachId());

            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // READ → afficher toutes les équipes
    public List<Equipe> getAll() {
        List<Equipe> list = new ArrayList<>();
        String query = "SELECT * FROM equipe";

        try (Statement st = cnx.createStatement()) {
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                Equipe e = new Equipe();

                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setLogo(rs.getString("logo"));
                e.setGame(rs.getString("game"));
                e.setCategorie(rs.getString("categorie"));
                e.setCoachId(rs.getInt("coach_id"));

                list.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    // UPDATE → modifier une équipe
    public boolean update(Equipe e) {
        String query = "UPDATE equipe SET nom=?, logo=?, game=?, categorie=?, coach_id=? WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getLogo());
            ps.setString(3, e.getGame());
            ps.setString(4, e.getCategorie());
            ps.setInt(5, e.getCoachId());
            ps.setInt(6, e.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // DELETE → supprimer une équipe
    public boolean delete(int id) {
        String query = "DELETE FROM equipe WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

