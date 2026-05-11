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

    // 🔹 Ajouter une assurance - CORRIGÉ AVEC RETURN_GENERATED_KEYS
    @Override
    public void ajouterAssurance(Assurances a) throws SQLException {
        String sql = "INSERT INTO assurances (nom_assurance, type_assurance, description, prix, duree_mois, conditions, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNomAssurance());
            ps.setString(2, a.getTypeAssurance().name());
            ps.setString(3, a.getDescription());
            ps.setFloat(4, a.getPrix());
            ps.setInt(5, a.getDureeMois());
            ps.setString(6, a.getConditions());
            ps.setString(7, a.getStatut().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        a.setId(rs.getInt(1));
                        System.out.println("✅ Assurance ajoutée avec succès ! ID: " + a.getId());
                    }
                }
            } else {
                System.out.println("❌ Aucune ligne insérée");
            }
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
            ps.setFloat(4, a.getPrix());
            ps.setInt(5, a.getDureeMois());
            ps.setString(6, a.getConditions());
            ps.setString(7, a.getStatut().name());
            ps.setInt(8, a.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Assurance modifiée avec succès !");
            } else {
                System.out.println("❌ Assurance non trouvée avec ID: " + a.getId());
            }
        }
    }

    // 🔹 Supprimer une assurance
    @Override
    public void supprimerAssurance(Assurances a) throws SQLException {
        String sql = "DELETE FROM assurances WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Assurance supprimée avec succès !");
            } else {
                System.out.println("❌ Assurance non trouvée avec ID: " + a.getId());
            }
        }
    }

    // ================= MODIFIER LE STATUT D'UNE ASSURANCE =================
    public void modifierStatutAssurance(Assurances a) throws SQLException {
        String query = "UPDATE assurances SET statut = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, a.getStatut().name());
            pst.setInt(2, a.getId());
            pst.executeUpdate();
            System.out.println("✅ Statut de l'assurance modifié avec succès !");
        }
    }

    // 🔹 Récupérer toutes les assurances
    @Override
    public List<Assurances> recupererAssurance() throws SQLException {
        List<Assurances> assurances = new ArrayList<>();
        String sql = "SELECT id, nom_assurance, type_assurance, description, prix, duree_mois, conditions, date_creation, statut FROM assurances ORDER BY id DESC";

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
                a.setPrix(rs.getFloat("prix"));
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

        System.out.println("📊 " + assurances.size() + " assurance(s) récupérée(s)");
        return assurances;
    }

    // 🔹 Récupérer une assurance par ID
    public Assurances recupererAssuranceParId(int id) throws SQLException {
        String sql = "SELECT id, nom_assurance, type_assurance, description, prix, duree_mois, conditions, date_creation, statut FROM assurances WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Assurances a = new Assurances();
                    a.setId(rs.getInt("id"));
                    a.setNomAssurance(rs.getString("nom_assurance"));

                    String typeStr = rs.getString("type_assurance");
                    if (typeStr != null) {
                        try {
                            a.setTypeAssurance(TypeAssurance.valueOf(typeStr));
                        } catch (IllegalArgumentException e) {
                            a.setTypeAssurance(TypeAssurance.AUTRE);
                        }
                    }

                    a.setDescription(rs.getString("description"));
                    a.setPrix(rs.getFloat("prix"));
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

                    return a;
                }
            }
        }
        return null;
    }
}