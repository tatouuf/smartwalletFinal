package esprit.tn.souha_pi.entities;

public class Wallet {

    private int id;
    private int userId;
    private double balance;
    private String type;  // AJOUTER CE CHAMP

    public Wallet(){
        this.type = "Standard"; // Valeur par défaut
    }

    public Wallet(int id, int userId, double balance){
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.type = "Standard"; // Valeur par défaut
    }

    // Nouveau constructeur avec type
    public Wallet(int id, int userId, double balance, String type){
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.type = type;
    }

    public int getId(){ return id; }
    public int getUserId(){ return userId; }
    public double getBalance(){ return balance; }
    public String getType(){ return type; }  // AJOUTER CE GETTER

    public void setId(int id){ this.id = id; }
    public void setUserId(int userId){ this.userId = userId; }
    public void setBalance(double balance){ this.balance = balance; }
    public void setType(String type){ this.type = type; }  // AJOUTER CE SETTER
}