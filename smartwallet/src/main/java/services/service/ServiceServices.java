package services.service;

import entities.service.Services;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceServices implements IServiceServices {

    private Connection cnx;

    public ServiceServices() {
        // Récupération de la connexion depuis MyDataBase
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouterServices(Services s) throws SQLException {
        // Vérification que l'utilisateur existe
        if (s.getUser() == null) {
            throw new SQLException("❌ Aucun utilisateur associé au service");
        }

        String query = "INSERT INTO services (prix, localisation, adresse, description, type_service, statut, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setFloat(1, s.getPrix());
            pst.setString(2, s.getLocalisation());
            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getTypeService().name());
            pst.setString(6, s.getStatut().name());
            pst.setInt(7, s.getUser().getId());

            pst.executeUpdate();

            // Récupérer l'ID généré pour le service
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                s.setId(rs.getInt(1));
                System.out.println("✅ Service ajouté avec ID: " + s.getId());
            }
        }
    }

    @Override
    public void modifierServices(Services s) throws SQLException {
        if (s.getUser() == null) {
            throw new SQLException("❌ Aucun utilisateur associé au service");
        }

        String query = "UPDATE services SET prix = ?, localisation = ?, adresse = ?, " +
                "description = ?, type_service = ?, statut = ?, user_id = ? WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setFloat(1, s.getPrix());
            pst.setString(2, s.getLocalisation());
            pst.setString(3, s.getAdresse());
            pst.setString(4, s.getDescription());
            pst.setString(5, s.getTypeService().name());
            pst.setString(6, s.getStatut().name());
            pst.setInt(7, s.getUser().getId());
            pst.setInt(8, s.getId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Service modifié avec succès");
            } else {
                System.out.println("⚠️ Aucun service trouvé avec l'ID: " + s.getId());
            }
        }
    }

    @Override
    public void supprimerServices(Services s) throws SQLException {
        String query = "DELETE FROM services WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, s.getId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Service supprimé avec succès");
            } else {
                System.out.println("⚠️ Aucun service trouvé avec l'ID: " + s.getId());
            }
        }
    }

    @Override
    public List<Services> recupererServices() throws SQLException {
        List<Services> services = new ArrayList<>();
        String query = "SELECT * FROM services";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Services s = new Services();
                s.setId(rs.getInt("id"));
                s.setPrix(rs.getFloat("prix"));
                s.setLocalisation(rs.getString("localisation"));
                s.setAdresse(rs.getString("adresse"));
                s.setDescription(rs.getString("description"));
                s.setTypeService(entities.service.TypeService.valueOf(rs.getString("type_service")));
                s.setStatut(entities.service.Statut.valueOf(rs.getString("statut")));

                // Note: user_id n'est pas chargé ici pour éviter une requête supplémentaire
                // Vous pouvez ajouter une méthode pour charger l'utilisateur séparément

                services.add(s);
            }
        }

        System.out.println("📋 " + services.size() + " services récupérés");
        return services;
    }

    // ===========================
    // 🔹 Méthodes supplémentaires
    // ===========================

    public List<Services> recupererServicesParUtilisateur(int userId) throws SQLException {
        List<Services> services = new ArrayList<>();
        String query = "SELECT * FROM services WHERE user_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Services s = new Services();
                s.setId(rs.getInt("id"));
                s.setPrix(rs.getFloat("prix"));
                s.setLocalisation(rs.getString("localisation"));
                s.setAdresse(rs.getString("adresse"));
                s.setDescription(rs.getString("description"));
                s.setTypeService(entities.service.TypeService.valueOf(rs.getString("type_service")));
                s.setStatut(entities.service.Statut.valueOf(rs.getString("statut")));

                services.add(s);
            }
        }

        return services;
    }

    public Services recupererServiceParId(int id) throws SQLException {
        String query = "SELECT * FROM services WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Services s = new Services();
                s.setId(rs.getInt("id"));
                s.setPrix(rs.getFloat("prix"));
                s.setLocalisation(rs.getString("localisation"));
                s.setAdresse(rs.getString("adresse"));
                s.setDescription(rs.getString("description"));
                s.setTypeService(entities.service.TypeService.valueOf(rs.getString("type_service")));
                s.setStatut(entities.service.Statut.valueOf(rs.getString("statut")));

                return s;
            }
        }

        return null;
    }

    public int compterServices() throws SQLException {
        String query = "SELECT COUNT(*) FROM services";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    public int compterServicesParUtilisateur(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM services WHERE user_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }
}