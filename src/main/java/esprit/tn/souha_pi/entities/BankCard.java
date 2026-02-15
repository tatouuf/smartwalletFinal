package esprit.tn.souha_pi.entities;

public class BankCard {

    private int id;
    private int userId;
    private String cardHolder;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardType;

    public BankCard(){}

    public BankCard(int id, int userId, String cardHolder, String cardNumber,
                    String expiryDate, String cvv, String cardType) {
        this.id = id;
        this.userId = userId;
        this.cardHolder = cardHolder;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
    }

    public BankCard(int userId, String cardHolder, String cardNumber,
                    String expiryDate, String cvv, String cardType) {
        this.userId = userId;
        this.cardHolder = cardHolder;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getCardHolder() { return cardHolder; }
    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
    public String getCardType() { return cardType; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public void setCardType(String cardType) { this.cardType = cardType; }
}
