package services.paymentservice;

import entities.bankcardassur.BankCardAssur;
import entities.bankcardassur.BankCardAssur;
import utils.MyDataBase;
import java.sql.*;

public class PaymentService {

    private Connection cnx;

    public PaymentService() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    // Récupérer une carte par user_id
    public BankCardAssur getBankCardByUserId(int userId) {
        String sql = "SELECT * FROM bank_card WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new BankCardAssur(
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Simuler un paiement PayPal (ou intégrer API PayPal)
    public boolean payWithPayPal(BankCardAssur card, double amount) {
        if (card == null) {
            System.out.println("Aucune carte trouvée pour cet utilisateur.");
            return false;
        }

        // Simulation : vérifier le CVV et numéro
        if (card.getCardNumber().length() >= 12 && card.getCvv().length() == 3) {
            System.out.println("Paiement PayPal de " + amount + " DT effectué avec succès pour la carte " + card.getCardNumber());
            return true;
        } else {
            System.out.println("Erreur de paiement : informations de carte invalides.");
            return false;
        }
    }
}