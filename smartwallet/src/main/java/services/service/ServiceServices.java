package services.service;

import entities.User;
import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
 import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceServices implements IServiceServices {

    private final Connection cnx;

    public ServiceServices() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    // ======== AJOUTER ========
    @Override
    public void ajouterServices(Services s) throws SQLException {

        String query = "INSERT INTO services (prix, localisation, adresse, description, type, statut, id_user, TypeService, image) " +
                "VALUES (?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setFloat(1, s.getPrix());

            String pointWKT = "POINT(" + s.getLocalisation().getX() + " " + s.getLocalisation().getY() + ")";
            pst.setString(2, pointWKT);

            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());
            pst.setString(6, s.getStatut() != null ? s.getStatut().name() : null);
            pst.setInt(7, s.getUser() != null ? s.getUser().getId() : 0);
            pst.setString(8, s.getTypeService() != null ? s.getTypeService().name() : null);
            pst.setString(9, s.getImage());

            pst.executeUpdate();

            try (ResultSet rsKeys = pst.getGeneratedKeys()) {
                if (rsKeys.next()) {
                    s.setId(rsKeys.getInt(1));
                }
            }
        }
    }

    // ======== MODIFIER ========
    @Override
    public void modifierServices(Services s) throws SQLException {

        String location = "POINT(" + s.getLocalisation().getX() + " " + s.getLocalisation().getY() + ")";

        String query = "UPDATE services SET prix=?, localisation=ST_GeomFromText(?), adresse=?, description=?, " +
                "type=?, statut=?, id_user=?, TypeService=?, image=? WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {

            pst.setFloat(1, s.getPrix());
            pst.setString(2, location);
            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());
            pst.setString(6, s.getStatut() != null ? s.getStatut().name() : null);
            pst.setInt(7, s.getUser() != null ? s.getUser().getId() : 0);
            pst.setString(8, s.getTypeService() != null ? s.getTypeService().name() : null);
            pst.setString(9, s.getImage());
            pst.setInt(10, s.getId());

            pst.executeUpdate();
        }
    }

    // ======== SUPPRIMER ========
    @Override
    public void supprimerServices(Services s) throws SQLException {
        String query = "DELETE FROM services WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, s.getId());
            pst.executeUpdate();
        }
    }

    // ======== RECUPERER ========
    @Override
    public List<Services> recupererServices() throws SQLException {

        List<Services> list = new ArrayList<>();

        // ✅ UNE SEULE REQUÊTE PROPRE
        String req = "SELECT *, ST_AsText(localisation) AS loc FROM services";

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        GeometryFactory geometryFactory = new GeometryFactory();

        while (rs.next()) {

            Services s = new Services();

            s.setId(rs.getInt("id"));
            s.setPrix(rs.getFloat("prix"));
            s.setDescription(rs.getString("description"));
            s.setType(rs.getString("type"));
            s.setAdresse(rs.getString("adresse"));
            s.setImage(rs.getString("image"));

            // ===== Statut =====
            String statutStr = rs.getString("statut");
            try {
                s.setStatut(statutStr != null ? Statut.valueOf(statutStr) : null);
            } catch (Exception e) {
                s.setStatut(null);
            }

            // ===== TypeService =====
            String typeServiceStr = rs.getString("TypeService");
            try {
                s.setTypeService(typeServiceStr != null ? TypeService.valueOf(typeServiceStr) : null);
            } catch (Exception e) {
                s.setTypeService(null);
            }

            // ===== User =====
            int userId = rs.getInt("id_user");
            User u = new User();
            u.setId(userId);
            s.setUser(u);

            // ===== LOCALISATION (FIX MAJEUR) =====
            String localisation = rs.getString("loc");

            if (localisation != null && localisation.startsWith("POINT")) {

                localisation = localisation.replace("POINT(", "").replace(")", "");
                String[] coords = localisation.split(" ");

                double longitude = Double.parseDouble(coords[0]);
                double latitude = Double.parseDouble(coords[1]);

                Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                s.setLocalisation(point);
            }

            list.add(s);
        }

        return list;
    }
    @Override
    public void modifierServiceStatut(Services s) throws SQLException {
        String req = "UPDATE services SET statut = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, s.getStatut() != null ? s.getStatut().name() : null);
            ps.setInt(2, s.getId());
            ps.executeUpdate();
        }
    }
}