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

    private Connection cnx;

    public ServiceServices() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    // ======== AJOUTER ========
    @Override
    public void ajouterServices(Services s) throws SQLException {
        // Vérifier localisation
        String[] coords = s.getLocalisation().split(",");
        if (coords.length != 2) throw new SQLException("Localisation invalide ! Format attendu : lat,lon");
        String pointWKT = "POINT(" + coords[0].trim() + " " + coords[1].trim() + ")";

        String query = "INSERT INTO services (prix, localisation, adresse, description, type, statut, id_user, TypeService) " +
                "VALUES (?, ST_GeomFromText(?), ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setFloat(1, s.getPrix());
            pst.setString(2, pointWKT);
            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getType());
            pst.setString(6, s.getStatut().name());
            pst.setInt(7, s.getUser().getId());
            pst.setString(8, s.getTypeService().name());

            pst.executeUpdate();

            ResultSet rsKeys = pst.getGeneratedKeys();
            if (rsKeys.next()) {
                s.setId(rsKeys.getInt(1));
                System.out.println("Service ajouté avec ID: " + s.getId());
            }
        }
    }

    // ======== MODIFIER ========
    @Override
    public void modifierServices(Services s) throws SQLException {
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
            pst.setString(6, s.getStatut().name());
            pst.setInt(7, s.getUser().getId());
            pst.setString(8, s.getTypeService().name());
            pst.setInt(9, s.getId());

            int rows = pst.executeUpdate();
            if (rows > 0) System.out.println("Service modifié avec succès");
            else System.out.println("Aucun service trouvé avec l'ID: " + s.getId());
        }
    }

    // ======== SUPPRIMER ========
    @Override
    public void supprimerServices(Services s) throws SQLException {
        String query = "DELETE FROM services WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, s.getId());
            int rows = pst.executeUpdate();
            if (rows > 0) System.out.println("Service supprimé avec succès");
            else System.out.println("Aucun service trouvé avec l'ID: " + s.getId());
        }
    }

    // ======== RECUPERER ========
    @Override
    public List<Services> recupererServices() throws SQLException {
        List<Services> list = new ArrayList<>();
        String query = "SELECT id, prix, ST_AsText(localisation) AS localisation, adresse, description, type, statut, id_user, TypeService FROM services";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);

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
            s.setStatut(Statut.valueOf(rs.getString("statut")));
            s.setTypeService(TypeService.valueOf(rs.getString("TypeService")));
            s.setUser(new User(rs.getInt("id_user"), "", "", ""));
            list.add(s);
        }
        return list;
    }
}
