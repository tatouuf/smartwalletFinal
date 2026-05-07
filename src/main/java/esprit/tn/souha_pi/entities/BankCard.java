package esprit.tn.souha_pi.entities;

public class BankCard {
    private int id;
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String cvv;
    private String cardType;
    private String rib;
    private double balance;
    private int userId;

    // ========== NOUVEAUX CHAMPS POUR STRIPE ==========
    private String stripeCustomerId;      // ID du client chez Stripe
    private String stripePaymentMethodId; // ID de la méthode de paiement

    // Constructeurs
    public BankCard() {}

    public BankCard(String cardNumber, String cardHolder, String expiryDate,
                    String cvv, String cardType, String rib, double balance, int userId) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
        this.rib = rib;
        this.balance = balance;
        this.userId = userId;
    }

    // Getters et Setters existants...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getRib() { return rib; }
    public void setRib(String rib) { this.rib = rib; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    // ========== NOUVEAUX GETTERS/SETTERS POUR STRIPE ==========
    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String stripeCustomerId) { this.stripeCustomerId = stripeCustomerId; }

    public String getStripePaymentMethodId() { return stripePaymentMethodId; }
    public void setStripePaymentMethodId(String stripePaymentMethodId) { this.stripePaymentMethodId = stripePaymentMethodId; }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Masque le numéro de carte pour l'affichage
     * Exemple: "**** **** **** 1234"
     */
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    /**
     * Vérifie si la carte a des identifiants Stripe
     */
    public boolean hasStripeIds() {
        return stripeCustomerId != null && !stripeCustomerId.isEmpty()
                && stripePaymentMethodId != null && !stripePaymentMethodId.isEmpty();
    }

    /**
     * Extrait le mois d'expiration (pour Stripe)
     */
    public int getExpiryMonth() {
        if (expiryDate == null || !expiryDate.contains("/")) return 12;
        try {
            return Integer.parseInt(expiryDate.split("/")[0].trim());
        } catch (NumberFormatException e) {
            return 12;
        }
    }

    /**
     * Extrait l'année d'expiration (pour Stripe)
     */
    public int getExpiryYear() {
        if (expiryDate == null || !expiryDate.contains("/")) return 2030;
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
     * Nettoie le numéro de carte (enlève les espaces)
     */
    public String getCleanCardNumber() {
        if (cardNumber == null) return "";
        return cardNumber.replaceAll("\\s", "");
    }

    @Override
    public String toString() {
        return "BankCard{" +
                "id=" + id +
                ", cardHolder='" + cardHolder + '\'' +
                ", cardNumber='" + getMaskedCardNumber() + '\'' +
                ", cardType='" + cardType + '\'' +
                ", balance=" + balance +
                ", hasStripeIds=" + hasStripeIds() +
                '}';
    }
}