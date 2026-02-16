package services.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import entities.user.User;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceServices implements IServiceServices {

    private final Connection cnx;

    public ServiceServices() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    public void modifierServiceStatut(Services s) throws SQLException {
        String req = "UPDATE services SET statut = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, s.getStatut().name()); // <-- utiliser name() de l'Enum
            ps.setInt(2, s.getId());
            ps.executeUpdate();
        }
    }


    // ======== AJOUTER UN SERVICE ========
    @Override
    public void ajouterServices(Services s) throws SQLException {

        if (s.getLocalisation() == null || s.getLocalisation().isEmpty()) {
            throw new SQLException("Localisation invalide !");
        }

        String[] coords = s.getLocalisation().split(",");
        if (coords.length != 2) throw new SQLException("Localisation invalide ! Format attendu : lat,lon");

        String pointWKT = "POINT(" + coords[0].trim() + " " + coords[1].trim() + ")";

        String query = "INSERT INTO services (prix, localisation, adresse, description, type, statut, id_user, TypeService, image) " +
                "VALUES (?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setFloat(1, s.getPrix());
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
                    System.out.println("Service ajouté avec ID: " + s.getId());
                }
            }
        }
    }

    // ======== MODIFIER UN SERVICE ========
    @Override
    public void modifierServices(Services s) throws SQLException {
        if (s.getLocalisation() == null || s.getLocalisation().isEmpty()) {
            throw new SQLException("Localisation invalide !");
        }

        String[] coords = s.getLocalisation().split(",");
        if (coords.length != 2) throw new SQLException("Localisation invalide ! Format attendu : lat,lon");

        String pointWKT = "POINT(" + coords[0].trim() + " " + coords[1].trim() + ")";
        String query = "UPDATE services SET prix=?, localisation=ST_GeomFromText(?), adresse=?, description=?, " +
                "type=?, statut=?, id_user=?, TypeService=?, image=? WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setFloat(1, s.getPrix());
            pst.setString(2, pointWKT);
            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());
            pst.setString(6, s.getStatut() != null ? s.getStatut().name() : null);
            pst.setInt(7, s.getUser() != null ? s.getUser().getId() : 0);
            pst.setString(8, s.getTypeService() != null ? s.getTypeService().name() : null);
            pst.setString(9, s.getImage());
            pst.setInt(10, s.getId());

            int rows = pst.executeUpdate();
            System.out.println(rows > 0 ? "Service modifié avec succès" : "Aucun service trouvé avec l'ID: " + s.getId());
        }
    }

    // ======== SUPPRIMER UN SERVICE ========
    @Override
    public void supprimerServices(Services s) throws SQLException {
        String query = "DELETE FROM services WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, s.getId());
            int rows = pst.executeUpdate();
            System.out.println(rows > 0 ? "Service supprimé avec succès" : "Aucun service trouvé avec l'ID: " + s.getId());
        }
    }

    // ======== RECUPERER TOUS LES SERVICES ========
    @Override
    public List<Services> recupererServices() throws SQLException {
        List<Services> list = new ArrayList<>();
        String req = "SELECT * FROM services";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Services s = new Services();
            s.setId(rs.getInt("id"));
            s.setPrix(rs.getFloat("prix"));
            s.setDescription(rs.getString("description"));
            s.setType(rs.getString("type"));
            s.setAdresse(rs.getString("adresse"));
            s.setImage(rs.getString("image"));

            // Statut
            String statutStr = rs.getString("statut");
            try {
                s.setStatut(statutStr != null ? Statut.valueOf(statutStr) : null);
            } catch (IllegalArgumentException e) {
                s.setStatut(null);
            }

            // TypeService
            String typeServiceStr = rs.getString("TypeService");
            try {
                s.setTypeService(typeServiceStr != null ? TypeService.valueOf(typeServiceStr) : null);
            } catch (IllegalArgumentException e) {
                s.setTypeService(null);
            }

            // User
            int userId = rs.getInt("id_user");
            User u = new User();
            u.setId(userId);
            s.setUser(u);

            // Localisation (POINT → lat,lon)
            ResultSet rsLoc = cnx.createStatement().executeQuery(
                    "SELECT ST_AsText(localisation) AS loc FROM services WHERE id=" + s.getId()
            );
            if (rsLoc.next()) {
                String point = rsLoc.getString("loc"); // ex: POINT(36.8 10.1)
                if (point != null && point.startsWith("POINT(")) {
                    point = point.replace("POINT(", "").replace(")", "");
                    point = point.replace(" ", ",");
                    s.setLocalisation(point);
                }
            }

            list.add(s);
        }

        return list;
    }
}
