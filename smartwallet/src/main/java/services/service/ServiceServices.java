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

    private Connection connection;

    public ServiceServices() {
        connection = MyDataBase.getInstance().getConnection();
    }

    // 🔹 Ajouter un service
    @Override
    public void ajouterServices(Services s) throws SQLException {
        // Utilisation de ST_GeomFromText pour le POINT
        String sql = "INSERT INTO services (prix, description, type, statut, id_user, localisation, adresse, TypeService) " +
                "VALUES (?, ?, ?, ?, ?, ST_GeomFromText(?), ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setFloat(1, s.getPrix());
        ps.setString(2, s.getDescription());
        ps.setString(3, s.getType());
        ps.setString(4, s.getStatut().name());
        ps.setInt(5, s.getUser().getId());

        // Conversion lat,lon -> POINT(lon lat)
        String[] coords = s.getLocalisation().split(",");
        String pointWKT = "POINT(" + coords[1].trim() + " " + coords[0].trim() + ")";
        ps.setString(6, pointWKT);

        ps.setString(7, s.getAdresse());
        ps.setString(8, s.getTypeService().name());
        ps.executeUpdate();
        System.out.println("Service ajouté avec succès !");
    }

    // 🔹 Modifier un service
    @Override
    public void modifierServices(Services s) throws SQLException {
        String sql = "UPDATE services SET prix=?, description=?, type=?, statut=?, id_user=?, localisation=ST_GeomFromText(?), adresse=?, TypeService=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setFloat(1, s.getPrix());
        ps.setString(2, s.getDescription());
        ps.setString(3, s.getType());
        ps.setString(4, s.getStatut().name());
        ps.setInt(5, s.getUser().getId());

        String[] coords = s.getLocalisation().split(",");
        String pointWKT = "POINT(" + coords[1].trim() + " " + coords[0].trim() + ")";
        ps.setString(6, pointWKT);

        ps.setString(7, s.getAdresse());
        ps.setString(8, s.getTypeService().name());
        ps.setInt(9, s.getId());
        ps.executeUpdate();
        System.out.println("Service modifié avec succès !");
    }

    // 🔹 Supprimer un service
    @Override
    public void supprimerServices(Services s) throws SQLException {
        String sql = "DELETE FROM services WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, s.getId());
        ps.executeUpdate();
        System.out.println("Service supprimé avec succès !");
    }

    // 🔹 Récupérer tous les services
    @Override
    public List<Services> recupererServices() throws SQLException {
        List<Services> services = new ArrayList<>();
        String sql = "SELECT id, prix, description, type, statut, id_user, ST_AsText(localisation) AS localisation, adresse, TypeService FROM services";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int userId = rs.getInt("id_user");
            User u = new User();
            u.setId(userId);

            // Conversion "POINT(lon lat)" -> "lat,lon"
            String loc = rs.getString("localisation"); // ex: "POINT(10.1815 36.8065)"
            loc = loc.replace("POINT(", "").replace(")", ""); // "10.1815 36.8065"
            String[] coords = loc.split(" ");
            String localisationStr = coords[1] + "," + coords[0]; // "36.8065,10.1815"

            Services s = new Services(
                    rs.getInt("id"),
                    rs.getFloat("prix"),
                    rs.getString("description"),
                    rs.getString("type"),
                    Statut.valueOf(rs.getString("statut")),
                    u,
                    localisationStr,
                    rs.getString("adresse"),
                    TypeService.valueOf(rs.getString("TypeService"))
            );
            services.add(s);
        }
        return services;
    }
}
