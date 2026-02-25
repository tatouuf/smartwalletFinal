package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.utils.MyDataBase;
import esprit.tn.souha_pi.controllers.WalletLayoutController;
import utils.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankCardService {

    Connection cnx = MyDataBase.getInstance().getConnection();

    // add
    public void add(BankCard c) {
        // Récupérer l'utilisateur connecté au lieu de l'ID fixe
        int currentUserId = getCurrentUserId();

        if (getAllByUser(currentUserId).size() >= 5) {
            throw new RuntimeException("Maximum 5 cards allowed");
        }

        String sql = """
                INSERT INTO bank_card(user_id, card_holder, card_number, expiry_date, cvv, card_type, rib)
                VALUES(?,?,?,?,?,?,?)
                """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getCardHolder());
            ps.setString(3, c.getCardNumber());
            ps.setString(4, c.getExpiryDate());
            ps.setString(5, c.getCvv());
            ps.setString(6, c.getCardType());
            ps.setString(7, c.getRib());  // NOUVEAU
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Dans BankCardService.java
    public BankCard getByRib(String rib) {
        String sql = "SELECT * FROM bank_card WHERE rib = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, rib);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new BankCard(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("card_holder"),
                        rs.getString("card_number"),
                        rs.getString("expiry_date"),
                        rs.getString("cvv"),
                        rs.getString("card_type"),
                        rs.getString("rib")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // list - récupère les cartes de l'utilisateur connecté
    public List<BankCard> getAllByUser(int userId) {
        List<BankCard> list = new ArrayList<>();
        String sql = "SELECT * FROM bank_card WHERE user_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new BankCard(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("card_holder"),
                        rs.getString("card_number"),
                        rs.getString("expiry_date"),
                        rs.getString("cvv"),
                        rs.getString("card_type"),
                        rs.getString("rib")  // NOUVEAU
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Récupérer l'ID de l'utilisateur connecté
    private int getCurrentUserId() {
        // À implémenter selon votre système de session
        return Session.getCurrentUser().getId();
    }

    // delete
    public void delete(int id) {
        try {
            PreparedStatement ps = cnx.prepareStatement("DELETE FROM bank_card WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}