package esprit.tn.souha_pi.entities;

public class BankCard {

    private int id;
    private int userId;
    private String cardHolder;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardType;
    private String rib;  // NOUVEAU : RIB pour les transferts

    public BankCard(){}

    public BankCard(int id, int userId, String cardHolder, String cardNumber,
                    String expiryDate, String cvv, String cardType, String rib) {
        this.id = id;
        this.userId = userId;
        this.cardHolder = cardHolder;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
        this.rib = rib;
    }

    public BankCard(int userId, String cardHolder, String cardNumber,
                    String expiryDate, String cvv, String cardType, String rib) {
        this.userId = userId;
        this.cardHolder = cardHolder;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
        this.rib = rib;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getRib() { return rib; }  // NOUVEAU
    public void setRib(String rib) { this.rib = rib; }  // NOUVEAU
}