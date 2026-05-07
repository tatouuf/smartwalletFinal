package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankCardService {

    private Connection cnx = MyDataBase.getInstance().getConnection();

    // Ajouter une carte
    public void add(BankCard card) {
        String query = "INSERT INTO bank_card (card_number, card_holder, expiry_date, cvv, card_type, rib, balance, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, card.getCardNumber());
            ps.setString(2, card.getCardHolder());
            ps.setString(3, card.getExpiryDate());
            ps.setString(4, card.getCvv());
            ps.setString(5, card.getCardType());
            ps.setString(6, card.getRib());
            ps.setDouble(7, card.getBalance());
            ps.setInt(8, card.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer toutes les cartes d'un utilisateur
    public List<BankCard> getAllByUser(int userId) {
        List<BankCard> cards = new ArrayList<>();
        String query = "SELECT * FROM bank_card WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    // Récupérer une carte par son RIB
    public BankCard getByRib(String rib) {
        String query = "SELECT * FROM bank_card WHERE rib = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, rib);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCard(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupérer une carte par son ID
    public BankCard getById(int id) {
        String query = "SELECT * FROM bank_card WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCard(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Mettre à jour une carte (solde, etc.)
    public boolean update(BankCard card) {
        String query = "UPDATE bank_card SET balance = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setDouble(1, card.getBalance());
            ps.setInt(2, card.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une carte
    public boolean delete(int cardId) {
        String query = "DELETE FROM bank_card WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, cardId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mapper ResultSet vers objet BankCard
    private BankCard mapResultSetToCard(ResultSet rs) throws SQLException {
        BankCard card = new BankCard();
        card.setId(rs.getInt("id"));
        card.setCardNumber(rs.getString("card_number"));
        card.setCardHolder(rs.getString("card_holder"));
        card.setExpiryDate(rs.getString("expiry_date"));
        card.setCvv(rs.getString("cvv"));
        card.setCardType(rs.getString("card_type"));
        card.setRib(rs.getString("rib"));
        card.setBalance(rs.getDouble("balance"));
        card.setUserId(rs.getInt("user_id"));
        return card;
    }

    // Vérifier si un RIB existe déjà
    public boolean ribExists(String rib) {
        String query = "SELECT COUNT(*) FROM bank_card WHERE rib = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, rib);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Compter le nombre de cartes d'un utilisateur
    public int countByUser(int userId) {
        String query = "SELECT COUNT(*) FROM bank_card WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}