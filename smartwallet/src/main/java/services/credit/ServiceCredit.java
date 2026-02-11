package services.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCredit implements IServiceCredit {

    private Connection connection;

    public ServiceCredit() {
        try {
            connection = MyDataBase.getInstance().getConnection();
            if (connection == null) {
                throw new SQLException("Connexion à la base de données non établie !");
            }
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base : " + e.getMessage());
        }
    }

    // 🔹 Ajouter un crédit
    @Override
    public void ajouterCredit(Credit c) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion non disponible !");
        }

        String sql = "INSERT INTO credit (nom_client, montant, date_credit, description, statut) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getNomClient());
            ps.setFloat(2, c.getMontant());
            ps.setDate(3, Date.valueOf(c.getDateCredit()));
            ps.setString(4, c.getDescription());
            ps.setString(5, c.getStatut().name());
            ps.executeUpdate();
            System.out.println("✅ Crédit ajouté avec succès !");
        }
    }

    // 🔹 Modifier un crédit
    @Override
    public void modifierCredit(Credit c) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion non disponible !");
        }

        String sql = "UPDATE credit SET nom_client=?, montant=?, date_credit=?, description=?, statut=? WHERE id_credit=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getNomClient());
            ps.setFloat(2, c.getMontant());
            ps.setDate(3, Date.valueOf(c.getDateCredit()));
            ps.setString(4, c.getDescription());
            ps.setString(5, c.getStatut().name());
            ps.setInt(6, c.getIdCredit());
            ps.executeUpdate();
            System.out.println("✅ Crédit modifié avec succès !");
        }
    }

    // 🔹 Supprimer un crédit
    @Override
    public void supprimerCredit(Credit c) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion non disponible !");
        }

        String sql = "DELETE FROM credit WHERE id_credit=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCredit());
            ps.executeUpdate();
            System.out.println("✅ Crédit supprimé avec succès !");
        }
    }

    // 🔹 Récupérer tous les crédits
    @Override
    public List<Credit> recupererCredits() throws SQLException {
        if (connection == null) {
            throw new SQLException("Connexion non disponible !");
        }

        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT id_credit, nom_client, montant, date_credit, description, statut FROM credit";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Credit c = new Credit();
                c.setIdCredit(rs.getInt("id_credit"));
                c.setNomClient(rs.getString("nom_client"));
                c.setMontant(rs.getFloat("montant"));
                c.setDateCredit(rs.getDate("date_credit").toLocalDate());
                c.setDescription(rs.getString("description"));

                String statutStr = rs.getString("statut");
                try {
                    c.setStatut(StatutCredit.valueOf(statutStr));
                } catch (Exception e) {
                    c.setStatut(StatutCredit.NON_REMBOURSE);
                }

                credits.add(c);
            }
        }

        return credits;
    }
}
