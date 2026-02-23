package services.assurances;

import entities.assurances.Assurances;
import entities.assurances.Statut;
import entities.assurances.TypeAssurance;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAssurances implements IServiceAssurance {

    private Connection connection;

    public ServiceAssurances() {
        connection = MyDataBase.getInstance().getConnection();
    }

    // 🔹 Ajouter une assurance
    @Override
    public void ajouterAssurance(Assurances a) throws SQLException {
        String sql = "INSERT INTO assurances (nom_assurance, type_assurance, description, prix, duree_mois, conditions, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, a.getNomAssurance());
            ps.setString(2, a.getTypeAssurance().name()); // Enum -> String
            ps.setString(3, a.getDescription());
            ps.setFloat(4, a.getPrix());                 // ⚡ Utiliser setFloat pour float
            ps.setInt(5, a.getDureeMois());
            ps.setString(6, a.getConditions());
            ps.setString(7, a.getStatut().name());       // Enum -> String
            ps.executeUpdate();
            System.out.println("Assurance ajoutée avec succès !");
        }
    }

    // 🔹 Modifier une assurance
    @Override
    public void modifierAssurance(Assurances a) throws SQLException {
        String sql = "UPDATE assurances SET nom_assurance=?, type_assurance=?, description=?, prix=?, duree_mois=?, conditions=?, statut=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, a.getNomAssurance());
            ps.setString(2, a.getTypeAssurance().name());
            ps.setString(3, a.getDescription());
            ps.setFloat(4, a.getPrix());                // ⚡ setFloat
            ps.setInt(5, a.getDureeMois());
            ps.setString(6, a.getConditions());
            ps.setString(7, a.getStatut().name());
            ps.setInt(8, a.getId());
            ps.executeUpdate();
            System.out.println("Assurance modifiée avec succès !");
        }
    }

    // 🔹 Supprimer une assurance
    @Override
    public void supprimerAssurance(Assurances a) throws SQLException {
        String sql = "DELETE FROM assurances WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            ps.executeUpdate();
            System.out.println("Assurance supprimée avec succès !");
        }
    }
    // ================= MODIFIER LE STATUT D'UNE ASSURANCE =================
    public void modifierStatutAssurance(Assurances a) throws SQLException {
        String query = "UPDATE assurances SET statut = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) { // <- utiliser 'connection'
            pst.setString(1, a.getStatut().name()); // ACTIVE ou INACTIVE
            pst.setInt(2, a.getId());
            pst.executeUpdate();
        }
    }

    // 🔹 Récupérer toutes les assurances
    @Override
    public List<Assurances> recupererAssurance() throws SQLException {
        List<Assurances> assurances = new ArrayList<>();
        String sql = "SELECT id, nom_assurance, type_assurance, description, prix, duree_mois, conditions, date_creation, statut FROM assurances";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Assurances a = new Assurances();
                a.setId(rs.getInt("id"));
                a.setNomAssurance(rs.getString("nom_assurance"));

                // 🔹 Conversion String -> Enum avec valeur par défaut si invalide
                String typeStr = rs.getString("type_assurance");
                if (typeStr != null) {
                    try {
                        a.setTypeAssurance(TypeAssurance.valueOf(typeStr));
                    } catch (IllegalArgumentException e) {
                        a.setTypeAssurance(TypeAssurance.AUTRE);
                    }
                }

                a.setDescription(rs.getString("description"));
                a.setPrix(rs.getFloat("prix"));           // ⚡ Récupération float
                a.setDureeMois(rs.getInt("duree_mois"));
                a.setConditions(rs.getString("conditions"));

                Timestamp ts = rs.getTimestamp("date_creation");
                if (ts != null) {
                    a.setDateCreation(ts.toLocalDateTime());
                }

                String statutStr = rs.getString("statut");
                if (statutStr != null) {
                    try {
                        a.setStatut(Statut.valueOf(statutStr));
                    } catch (IllegalArgumentException e) {
                        a.setStatut(Statut.INACTIVE);
                    }
                }

                assurances.add(a);
            }
        }

        return assurances;
    }
}
