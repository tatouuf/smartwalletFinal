package services.service;

import entities.service.Favori;
import entities.service.Services;
import entities.user.User;
import utils.MyDataBase;
import utils.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriService {

    private final Connection cnx;

    public FavoriService() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    // ================= AJOUTER AUX FAVORIS =================
    public boolean ajouterFavori(int idService) throws SQLException {
        User utilisateurConnecte = Session.getCurrentUser();

        if (utilisateurConnecte == null) {
            throw new SQLException("Aucun utilisateur connecté.");
        }

        // Vérifier si déjà en favori
        if (estEnFavori(idService)) {
            return false;
        }

        String query = "INSERT INTO favoris (id_user, id_service, date_ajout) VALUES (?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, utilisateurConnecte.getId());
            pst.setInt(2, idService);
            pst.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // ================= SUPPRIMER DES FAVORIS =================
    public boolean supprimerFavori(int idService) throws SQLException {
        User utilisateurConnecte = Session.getCurrentUser();

        if (utilisateurConnecte == null) {
            throw new SQLException("Aucun utilisateur connecté.");
        }

        String query = "DELETE FROM favoris WHERE id_user = ? AND id_service = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, utilisateurConnecte.getId());
            pst.setInt(2, idService);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // ================= VÉRIFIER SI EN FAVORI =================
    public boolean estEnFavori(int idService) throws SQLException {
        User utilisateurConnecte = Session.getCurrentUser();

        if (utilisateurConnecte == null) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM favoris WHERE id_user = ? AND id_service = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, utilisateurConnecte.getId());
            pst.setInt(2, idService);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // ================= RÉCUPÉRER LES FAVORIS DE L'UTILISATEUR =================
    public List<Services> recupererFavoris() throws SQLException {
        List<Services> list = new ArrayList<>();
        User utilisateurConnecte = Session.getCurrentUser();

        if (utilisateurConnecte == null) {
            throw new SQLException("Aucun utilisateur connecté.");
        }

        String query = """
                SELECT s.*, ST_AsText(s.localisation) AS loc,
                       u.id as user_id, u.nom as user_nom, u.prenom as user_prenom,
                       u.email as user_email, u.telephone as user_telephone,
                       u.role as user_role, u.status as user_status, u.is_actif as user_is_actif
                FROM services s
                INNER JOIN favoris f ON s.id = f.id_service
                LEFT JOIN users u ON s.id_user = u.id
                WHERE f.id_user = ?
                ORDER BY f.date_ajout DESC
                """;

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, utilisateurConnecte.getId());

            try (ResultSet rs = ps.executeQuery()) {
                ServiceServices serviceServices = new ServiceServices();
                while (rs.next()) {
                    Services s = serviceServices.extractServiceFromResultSet(rs);
                    list.add(s);
                }
            }
        }

        return list;
    }

    // ================= RÉCUPÉRER LE NOMBRE DE FAVORIS =================
    public int nombreFavoris() throws SQLException {
        User utilisateurConnecte = Session.getCurrentUser();

        if (utilisateurConnecte == null) {
            return 0;
        }

        String query = "SELECT COUNT(*) FROM favoris WHERE id_user = ?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, utilisateurConnecte.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
}