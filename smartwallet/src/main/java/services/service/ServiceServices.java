package services.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import entities.user.User;
import org.locationtech.jts.geom.*;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceServices implements IServiceServices {

    private final Connection cnx;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public ServiceServices() {
        this.cnx = MyDataBase.getInstance().getConnection();
        if (cnx == null) {
            System.err.println("❌ Connexion DB NULL");
        }
    }

    // ================= AJOUTER =================

    @Override
    public void ajouterServices(Services s) throws SQLException {

        String query = """
                INSERT INTO services
                (prix, localisation, adresse, description, type, statut, id_user, TypeService, image)
                VALUES (?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pst =
                     cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setFloat(1, s.getPrix());

            // POINT(longitude latitude)
            String pointWKT = "POINT(" +
                    s.getLocalisation().getX() + " " +
                    s.getLocalisation().getY() + ")";
            pst.setString(2, pointWKT);

            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());
            pst.setString(6,
                    s.getStatut() != null ? s.getStatut().name() : null);
            pst.setInt(7,
                    s.getUser() != null ? s.getUser().getId() : 0);
            pst.setString(8,
                    s.getTypeService() != null ? s.getTypeService().name() : null);
            pst.setString(9, s.getImage());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setId(rs.getInt(1));
                }
            }
        }
    }

    // ================= MODIFIER =================

    @Override
    public void modifierServices(Services s) throws SQLException {

        String query = """
                UPDATE services SET
                prix=?,
                localisation=ST_GeomFromText(?),
                adresse=?,
                description=?,
                type=?,
                statut=?,
                id_user=?,
                TypeService=?,
                image=?
                WHERE id=?
                """;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {

            pst.setFloat(1, s.getPrix());

            String pointWKT = "POINT(" +
                    s.getLocalisation().getX() + " " +
                    s.getLocalisation().getY() + ")";
            pst.setString(2, pointWKT);

            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());
            pst.setString(6,
                    s.getStatut() != null ? s.getStatut().name() : null);
            pst.setInt(7,
                    s.getUser() != null ? s.getUser().getId() : 0);
            pst.setString(8,
                    s.getTypeService() != null ? s.getTypeService().name() : null);
            pst.setString(9, s.getImage());
            pst.setInt(10, s.getId());

            pst.executeUpdate();
        }
    }

    // ================= SUPPRIMER =================

    @Override
    public void supprimerServices(Services s) throws SQLException {
        String query = "DELETE FROM services WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, s.getId());
            pst.executeUpdate();
        }
    }

    // ================= RECUPERER =================

    @Override
    public List<Services> recupererServices() throws SQLException {

        List<Services> list = new ArrayList<>();

        String req = "SELECT *, ST_AsText(localisation) AS loc FROM services";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {

                Services s = new Services();

                s.setId(rs.getInt("id"));
                s.setPrix(rs.getFloat("prix"));
                s.setDescription(rs.getString("description"));
                s.setType(rs.getString("type"));
                s.setAdresse(rs.getString("adresse"));
                s.setImage(rs.getString("image"));

                // ===== statut =====
                try {
                    String statutStr = rs.getString("statut");
                    s.setStatut(
                            statutStr != null ? Statut.valueOf(statutStr) : null
                    );
                } catch (Exception ignored) {}

                // ===== type service =====
                try {
                    String typeServiceStr = rs.getString("TypeService");
                    s.setTypeService(
                            typeServiceStr != null ? TypeService.valueOf(typeServiceStr) : null
                    );
                } catch (Exception ignored) {}

                // ===== user =====
                User u = new User();
                u.setId(rs.getInt("id_user"));
                s.setUser(u);

                // ===== localisation =====
                String loc = rs.getString("loc");

                if (loc != null && loc.startsWith("POINT")) {
                    try {
                        loc = loc.replace("POINT(", "").replace(")", "");
                        String[] coords = loc.split(" ");

                        double lng = Double.parseDouble(coords[0]);
                        double lat = Double.parseDouble(coords[1]);

                        Point p = geometryFactory.createPoint(
                                new Coordinate(lng, lat)
                        );
                        s.setLocalisation(p);

                    } catch (Exception e) {
                        System.err.println("Erreur parsing POINT: " + loc);
                    }
                }

                list.add(s);
            }
        }

        return list;
    }

    // ================= MODIFIER STATUT =================

    @Override
    public void modifierServiceStatut(Services s) throws SQLException {

        String req = "UPDATE services SET statut=? WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1,
                    s.getStatut() != null ? s.getStatut().name() : null);
            ps.setInt(2, s.getId());
            ps.executeUpdate();
        }
    }
}