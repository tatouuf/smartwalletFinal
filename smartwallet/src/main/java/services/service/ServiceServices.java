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
    @Override
    public List<Services> recupererServices() throws SQLException {
        List<Services> list = new ArrayList<>();
        String query = "SELECT id, prix, ST_AsText(localisation) AS localisation, adresse, description, " +
                "type, statut, id_user, TypeService FROM services";

        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Services s = new Services();
                s.setId(rs.getInt("id"));
                s.setPrix(rs.getFloat("prix"));

                String loc = rs.getString("localisation");
                if (loc != null) loc = loc.replace("POINT(", "").replace(")", "").replace(" ", ",");
                s.setLocalisation(loc);

                s.setAdresse(rs.getString("adresse"));
                s.setDescription(rs.getString("description"));
                s.setType(rs.getString("type"));

                // ⚠️ Ne pas mettre de valeur par défaut
                String statutStr = rs.getString("statut");
                s.setStatut(statutStr != null ? Statut.valueOf(statutStr) : null);

                String typeServiceStr = rs.getString("TypeService");
                s.setTypeService(typeServiceStr != null ? TypeService.valueOf(typeServiceStr) : null);

                int userId = rs.getInt("id_user");
                s.setUser(new User(userId, "", "", ""));

                list.add(s);
            }
        }
        return list;
    }

}
