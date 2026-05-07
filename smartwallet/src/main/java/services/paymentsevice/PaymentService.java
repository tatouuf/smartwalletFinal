package services.aymentservice;

import esprit.tn.souha_pi.entities.BankCard;  // ← UN SEUL IMPORT, le bon
import utils.MyDataBase;
import utils.StripeConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private Connection cnx;
    private services.paymentservice.StripeService stripeService;  // ← Simplifié

    public PaymentService() {
        this.cnx = MyDataBase.getInstance().getConnection();
        this.stripeService = new services.paymentservice.StripeService();  // ← Simplifié
        System.out.println("✅ PaymentService initialisé avec Stripe");
    }

    // Récupérer une carte par user_id
    public BankCard getBankCardByUserId(int userId) {
        String sql = "SELECT * FROM bank_card WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BankCard card = new BankCard(
                        rs.getString("card_number"),
                        rs.getString("card_holder"),
                        rs.getString("expiry_date"),
                        rs.getString("cvv"),
                        rs.getString("card_type"),
                        rs.getString("rib"),
                        rs.getDouble("balance"),
                        rs.getInt("user_id")
                );
                card.setId(rs.getInt("id"));

                // Récupérer les IDs Stripe si les colonnes existent
                try {
                    // Note: Votre classe BankCard n'a pas ces champs
                    // Si vous voulez les ajouter, décommentez :
                    // card.setStripeCustomerId(rs.getString("stripe_customer_id"));
                    // card.setStripePaymentMethodId(rs.getString("stripe_payment_method_id"));
                } catch (Exception e) {
                    // Les colonnes n'existent pas encore dans la base
                    System.out.println("⚠️ Colonnes Stripe non trouvées dans la base de données");
                }

                return card;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ MÉTHODE AJOUTÉE - Récupérer l'ID client Stripe d'une carte
    public String getStripeCustomerId(BankCard card) {
        if (card == null) return null;
        // Note: Votre classe BankCard n'a pas ce champ
        // Si vous voulez l'ajouter, décommentez :
        // return card.getStripeCustomerId();
        return null;
    }

    // ✅ MÉTHODE AJOUTÉE - Récupérer l'ID de méthode de paiement Stripe
    public String getStripePaymentMethodId(BankCard card) {
        if (card == null) return null;
        // Note: Votre classe BankCard n'a pas ce champ
        // return card.getStripePaymentMethodId();
        return null;
    }

    // Vérifier si une carte est valide pour Stripe
    public boolean isCardValidForStripe(BankCard card) {
        if (card == null) {
            System.out.println("❌ Carte null");
            return false;
        }

        if (card.getCardNumber() == null || card.getCardNumber().length() < 13) {
            System.out.println("❌ Numéro de carte invalide");
            return false;
        }

        if (card.getCvv() == null || card.getCvv().length() < 3) {
            System.out.println("❌ CVV invalide");
            return false;
        }

        if (card.getExpiryDate() == null || !card.getExpiryDate().contains("/")) {
            System.out.println("❌ Date d'expiration invalide");
            return false;
        }

        return true;
    }

    // Préparer une carte pour Stripe
    public boolean prepareCardForStripe(BankCard card) {
        if (!isCardValidForStripe(card)) {
            return false;
        }

        System.out.println("📝 Carte prête pour premier paiement Stripe");
        return true;
    }

    // Simuler un paiement
    public boolean simulatePayment(BankCard card, double amount) {
        if (card == null) {
            System.out.println("❌ Aucune carte trouvée");
            return false;
        }

        if (card.getCardNumber().length() >= 12 && card.getCvv().length() == 3) {
            String maskedNumber = getMaskedCardNumber(card.getCardNumber());
            System.out.println("💰 Simulation de paiement de " + amount + " DT");
            System.out.println("   Carte: " + maskedNumber);
            System.out.println("   ✅ Simulation réussie (en mode test)");
            return true;
        } else {
            System.out.println("❌ Simulation échouée: informations invalides");
            return false;
        }
    }

    // ✅ MÉTHODE UTILITAIRE - Masquer le numéro de carte
    private String getMaskedCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    // Vérifier si c'est une carte de test Stripe
    public boolean isStripeTestCard(String cardNumber) {
        String cleanNumber = cardNumber.replaceAll("\\s", "");

        String[] testCards = {
                "4242424242424242", "4000056655665556", "5555555555554444",
                "2223003122003222", "378282246310005", "371449635398431",
                "6011111111111117", "3056930009020004", "3566002020360505",
                "6200000000000005", "4000000000000002", "4000000000009995",
                "4000000000000069", "4000000000000127"
        };

        for (String testCard : testCards) {
            if (testCard.equals(cleanNumber)) {
                return true;
            }
        }
        return false;
    }
}