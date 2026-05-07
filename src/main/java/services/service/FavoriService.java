package services.service;

import entities.User;
import entities.service.Services;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriService {

    private final Connection cnx;

    public FavoriService() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    // ================= AJOUTER AUX FAVORIS =================
    public void ajouterFavori(int userId, int serviceId) throws SQLException {
        String query = "INSERT INTO favoris (id_user, id_service, date_ajout) VALUES (?, ?, NOW())";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, serviceId);
            pst.executeUpdate();
        }
    }

    // ================= SUPPRIMER DES FAVORIS =================
    public void supprimerFavori(int userId, int serviceId) throws SQLException {
        String query = "DELETE FROM favoris WHERE id_user = ? AND id_service = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, serviceId);
            pst.executeUpdate();
        }
    }

    // ================= SUPPRIMER TOUS LES FAVORIS D'UN SERVICE =================
    public void supprimerFavorisParService(int serviceId) throws SQLException {
        String query = "DELETE FROM favoris WHERE id_service = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, serviceId);
            pst.executeUpdate();
        }
    }

    // ================= VÉRIFIER SI UN SERVICE EST EN FAVORI =================
    public boolean estEnFavori(int userId, int serviceId) throws SQLException {
        String query = "SELECT COUNT(*) FROM favoris WHERE id_user = ? AND id_service = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, serviceId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // ================= NOMBRE DE FAVORIS POUR UN SERVICE =================
    public int nombreFavorisPourService(int serviceId) throws SQLException {
        String query = "SELECT COUNT(*) FROM favoris WHERE id_service = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, serviceId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // ================= LISTE DES FAVORIS D'UN UTILISATEUR =================
    public List<Services> getFavorisUtilisateur(int userId) throws SQLException {
        List<Services> favoris = new ArrayList<>();
        String query = """
            SELECT s.* FROM services s
            INNER JOIN favoris f ON s.id = f.id_service
            WHERE f.id_user = ?
            ORDER BY f.date_ajout DESC
        """;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {
                ServiceServices serviceServices = new ServiceServices();
                while (rs.next()) {
                    // Vous pouvez utiliser la méthode de ServiceServices pour mapper les résultats
                    // Ou créer un mapper spécifique ici
                }
            }
        }
        return favoris;
    }
    // ================= RÉCUPÉRER LES UTILISATEURS QUI ONT CE SERVICE EN FAVORI =================
    public List<User> getUtilisateursParFavori(int serviceId) throws SQLException {
        List<User> utilisateurs = new ArrayList<>();
        String query = """
        SELECT u.* FROM user u
        INNER JOIN favoris f ON u.id = f.id_user
        WHERE f.id_service = ?
    """;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, serviceId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setTelephone(rs.getString("telephone"));
                    // Ajoutez d'autres champs selon votre classe User
                    utilisateurs.add(user);
                }
            }
        }
        return utilisateurs;
    }
}