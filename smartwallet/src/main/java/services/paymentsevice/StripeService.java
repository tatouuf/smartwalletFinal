package services.paymentservice;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import esprit.tn.souha_pi.entities.BankCard;  // ← VOTRE classe
import utils.StripeConfig;

import java.util.HashMap;
import java.util.Map;

public class StripeService {

    private final String mode;
    private final String currency;

    public StripeService() {
        // Initialiser Stripe
        StripeConfig.initializeStripe();
        this.mode = StripeConfig.getMode();
        this.currency = StripeConfig.getDefaultCurrency();

        System.out.println("💳 StripeService prêt");
        System.out.println("   Mode: " + mode);
        System.out.println("   Devise: " + currency);
    }

    /**
     * Paiement direct avec la carte utilisant des tokens de test
     */
    public Charge payDirectlyWithCard(BankCard card, double amount, String currency, String description)
            throws StripeException {

        if (!StripeConfig.isInitialized()) {
            throw new com.stripe.exception.AuthenticationException(
                    "Stripe non initialisé. Vérifiez votre clé API.",
                    null,
                    null,
                    0
            );
        }

        // 🔑 Remplacer la carte réelle par un token de test correspondant
        String tokenId = getTestTokenForCard(card.getCardNumber());

        System.out.println("💰 Paiement direct Stripe (mode test tokenisé):");
        System.out.println("   Titulaire: " + card.getCardHolder());
        System.out.println("   Carte originale: " + getMaskedCardNumber(card.getCardNumber()));
        System.out.println("   Token utilisé: " + tokenId);
        System.out.println("   Montant: " + amount + " " + currency);

        try {
            // Créer la charge directement avec le token
            Charge charge = createCharge(tokenId, amount, currency, description);

            System.out.println("   ✅ Charge créée: " + charge.getId());
            System.out.println("   ✅ Statut: " + charge.getStatus());

            return charge;

        } catch (StripeException e) {
            System.err.println("❌ Erreur Stripe: " + e.getMessage());
            System.err.println("   Code: " + e.getCode());
            if (e.getStripeError() != null) {
                System.err.println("   Type: " + e.getStripeError().getType());
            }
            throw e;
        }
    }

    /**
     * Paiement direct avec la carte (devise par défaut)
     */
    public Charge payDirectlyWithCard(BankCard card, double amount, String description)
            throws StripeException {
        return payDirectlyWithCard(card, amount, this.currency, description);
    }

    /**
     * 🔑 Retourne un token de test correspondant au type de carte
     */
    private String getTestTokenForCard(String cardNumber) {
        String cleanNumber = cardNumber.replaceAll("\\s", "");

        // Mapping des numéros de carte vers des tokens de test Stripe
        Map<String, String> testTokens = new HashMap<>();
        testTokens.put("4242424242424242", "tok_visa");
        testTokens.put("4000056655665556", "tok_visa_debit");
        testTokens.put("5555555555554444", "tok_mastercard");
        testTokens.put("2223003122003222", "tok_mastercard");
        testTokens.put("378282246310005",  "tok_amex");
        testTokens.put("371449635398431",  "tok_amex");
        testTokens.put("6011111111111117", "tok_discover");
        testTokens.put("3056930009020004", "tok_diners");
        testTokens.put("3566002020360505", "tok_jcb");
        testTokens.put("6200000000000005", "tok_unionpay");

        // Cartes pour tester les erreurs
        testTokens.put("4000000000000002", "tok_chargeDeclined");
        testTokens.put("4000000000009995", "tok_chargeDeclinedInsufficientFunds");
        testTokens.put("4000000000000069", "tok_chargeDeclinedExpiredCard");
        testTokens.put("4000000000000127", "tok_chargeDeclinedIncorrectCvc");

        // Valeur par défaut (Visa)
        String token = testTokens.getOrDefault(cleanNumber, "tok_visa");
        System.out.println("   🔄 Mapping carte " + maskCardNumber(cleanNumber) + " -> token: " + token);

        return token;
    }

    /**
     * Crée une charge
     */
    private Charge createCharge(String tokenId, double amount, String currency, String description)
            throws StripeException {

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (amount * 100));
        chargeParams.put("currency", currency.toLowerCase());
        chargeParams.put("description", description);
        chargeParams.put("source", tokenId);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("integration", "SmartWallet");
        metadata.put("mode", mode);
        metadata.put("timestamp", String.valueOf(System.currentTimeMillis()));
        chargeParams.put("metadata", metadata);

        chargeParams.put("capture", true);

        return Charge.create(chargeParams);
    }

    /**
     * Paiement avec PaymentMethod (recommandé pour la production)
     */
    public Charge payWithPaymentMethod(BankCard card, double amount, String currency, String description)
            throws StripeException {

        if (!StripeConfig.isInitialized()) {
            throw new com.stripe.exception.AuthenticationException(
                    "Stripe non initialisé", null, null, 0);
        }

        System.out.println("💰 Paiement avec PaymentMethod:");
        System.out.println("   Carte: " + getMaskedCardNumber(card.getCardNumber()));
        System.out.println("   Montant: " + amount + " " + currency);

        // 1. Créer un PaymentMethod avec la carte
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", card.getCardNumber().replaceAll("\\s", ""));
        cardParams.put("exp_month", extractExpiryMonth(card.getExpiryDate()));
        cardParams.put("exp_year", extractExpiryYear(card.getExpiryDate()));
        cardParams.put("cvc", card.getCvv());

        Map<String, Object> paymentMethodParams = new HashMap<>();
        paymentMethodParams.put("type", "card");
        paymentMethodParams.put("card", cardParams);

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);
        System.out.println("   ✅ PaymentMethod créé: " + paymentMethod.getId());

        // 2. Créer la charge avec le PaymentMethod
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (amount * 100));
        chargeParams.put("currency", currency.toLowerCase());
        chargeParams.put("description", description);
        chargeParams.put("payment_method", paymentMethod.getId());
        chargeParams.put("confirm", true);

        Charge charge = Charge.create(chargeParams);
        System.out.println("   ✅ Charge créée: " + charge.getId());

        return charge;
    }

    /**
     * Sauvegarde une carte pour un client
     */
    public Customer saveCardForCustomer(BankCard card, String email, String name)
            throws StripeException {

        if (!StripeConfig.isInitialized()) {
            throw new com.stripe.exception.AuthenticationException(
                    "Stripe non initialisé",
                    null,
                    null,
                    0
            );
        }

        // Créer le client
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", email);
        customerParams.put("name", name);
        customerParams.put("description", "Client SmartWallet - ID: " + card.getUserId());
        customerParams.put("metadata", Map.of(
                "user_id", String.valueOf(card.getUserId()),
                "card_id", String.valueOf(card.getId())
        ));

        Customer customer = Customer.create(customerParams);

        // Créer le PaymentMethod
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", card.getCardNumber().replaceAll("\\s", ""));
        cardParams.put("exp_month", extractExpiryMonth(card.getExpiryDate()));
        cardParams.put("exp_year", extractExpiryYear(card.getExpiryDate()));
        cardParams.put("cvc", card.getCvv());

        Map<String, Object> paymentMethodParams = new HashMap<>();
        paymentMethodParams.put("type", "card");
        paymentMethodParams.put("card", cardParams);

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

        // Attacher au client
        Map<String, Object> attachParams = new HashMap<>();
        attachParams.put("customer", customer.getId());
        paymentMethod = paymentMethod.attach(attachParams);

        System.out.println("✅ Client Stripe créé: " + customer.getId());
        System.out.println("   PaymentMethod: " + paymentMethod.getId());
        System.out.println("   Carte: " + getMaskedCardNumber(card.getCardNumber()));

        return customer;
    }

    /**
     * Extrait le mois d'expiration
     */
    private int extractExpiryMonth(String expiryDate) {
        if (expiryDate == null || !expiryDate.contains("/")) {
            return 12;
        }
        try {
            return Integer.parseInt(expiryDate.split("/")[0].trim());
        } catch (NumberFormatException e) {
            return 12;
        }
    }

    /**
     * Extrait l'année d'expiration
     */
    private int extractExpiryYear(String expiryDate) {
        if (expiryDate == null || !expiryDate.contains("/")) {
            return 2030;
        }
        try {
            String year = expiryDate.split("/")[1].trim();
            if (year.length() == 2) {
                return Integer.parseInt("20" + year);
            }
            return Integer.parseInt(year);
        } catch (NumberFormatException e) {
            return 2030;
        }
    }

    /**
     * Masque un numéro de carte pour l'affichage
     */
    private String getMaskedCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    /**
     * Masque un numéro de carte (version courte)
     */
    private String maskCardNumber(String cardNumber) {
        return getMaskedCardNumber(cardNumber);
    }

    /**
     * Vérifie si c'est une carte de test Stripe
     */
    public boolean isTestCard(String cardNumber) {
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

    /**
     * Affiche les détails d'une charge
     */
    public void printChargeDetails(String chargeId) throws StripeException {
        Charge charge = Charge.retrieve(chargeId);

        System.out.println("\n📋 DÉTAILS DE LA TRANSACTION:");
        System.out.println("   ID: " + charge.getId());
        System.out.println("   Montant: " + charge.getAmount() / 100.0 + " " + charge.getCurrency());
        System.out.println("   Statut: " + charge.getStatus());
        System.out.println("   Date: " + new java.util.Date(charge.getCreated() * 1000));

        if (charge.getPaymentMethodDetails() != null &&
                charge.getPaymentMethodDetails().getCard() != null) {
            var card = charge.getPaymentMethodDetails().getCard();
            System.out.println("   Carte: " + card.getBrand() + " **** " + card.getLast4());
            System.out.println("   Expiration: " + card.getExpMonth() + "/" + card.getExpYear());
        }

        if (charge.getMetadata() != null && !charge.getMetadata().isEmpty()) {
            System.out.println("   Métadonnées: " + charge.getMetadata());
        }

        System.out.println("   Reçu: " + charge.getReceiptUrl());
        System.out.println();
    }
}