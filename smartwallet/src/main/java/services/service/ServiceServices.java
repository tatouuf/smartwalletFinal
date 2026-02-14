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

    // ======== AJOUTER UN SERVICE ========
    @Override
    public void ajouterServices(Services s) throws SQLException {

        if (s.getLocalisation() == null || s.getLocalisation().isEmpty()) {
            throw new SQLException("Localisation invalide !");
        }

        String[] coords = s.getLocalisation().split(",");
        if (coords.length != 2)
            throw new SQLException("Localisation invalide ! Format attendu : lat,lon");

        String pointWKT = "POINT(" + coords[0].trim() + " " + coords[1].trim() + ")";

        String query = "INSERT INTO services (prix, localisation, adresse, description, type, statut, id_user, TypeService, image) " +
                "VALUES (?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setFloat(1, s.getPrix());
            pst.setString(2, pointWKT);
            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());

            // Statut
            if (s.getStatut() != null)
                pst.setString(6, s.getStatut().name());
            else
                pst.setNull(6, java.sql.Types.VARCHAR);

            // User
            pst.setInt(7, s.getUser() != null ? s.getUser().getId() : 0);

            // TypeService
            if (s.getTypeService() != null)
                pst.setString(8, s.getTypeService().name());
            else
                pst.setNull(8, java.sql.Types.VARCHAR);

            // ✅ IMAGE (NOUVEAU)
            if (s.getImage() != null && !s.getImage().isEmpty())
                pst.setString(9, s.getImage());
            else
                pst.setNull(9, java.sql.Types.VARCHAR);

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
                "type=?, statut=?, id_user=?, TypeService=? WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setFloat(1, s.getPrix());
            pst.setString(2, pointWKT);
            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());

            if (s.getStatut() != null) pst.setString(6, s.getStatut().name());
            else pst.setNull(6, java.sql.Types.VARCHAR);

            pst.setInt(7, s.getUser() != null ? s.getUser().getId() : 0);

            if (s.getTypeService() != null) pst.setString(8, s.getTypeService().name());
            else pst.setNull(8, java.sql.Types.VARCHAR);

            pst.setInt(9, s.getId());

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

            // ===== STATUT (ENUM) =====
            String statutStr = rs.getString("statut");
            if (statutStr != null) {
                s.setStatut(Statut.valueOf(statutStr));
            }

            // ===== TYPE SERVICE (ENUM) =====
            String typeServiceStr = rs.getString("TypeService");
            if (typeServiceStr != null) {
                s.setTypeService(TypeService.valueOf(typeServiceStr));
            }

            // ===== USER =====
            int userId = rs.getInt("id_user");
            User u = new User();
            u.setId(userId);
            s.setUser(u);

            // ===== LOCALISATION (POINT → lat,lon) =====
            ResultSet rsLoc = cnx.createStatement().executeQuery(
                    "SELECT ST_AsText(localisation) AS loc FROM services WHERE id=" + rs.getInt("id")
            );

            if (rsLoc.next()) {
                String point = rsLoc.getString("loc"); // ex: POINT(36.8 10.1)
                if (point != null && point.startsWith("POINT(")) {
                    point = point.replace("POINT(", "").replace(")", "");
                    point = point.replace(" ", ",");
                    s.setLocalisation(point);
                }
            }

            s.setAdresse(rs.getString("adresse"));
            s.setImage(rs.getString("image"));

            list.add(s);
        }

        return list;
    }


}
