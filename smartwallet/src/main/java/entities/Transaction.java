package entities;
import java.sql.Timestamp;

public class Transaction {

    private int id;
    private int user_id;
    private String type;
    private double amount;
    private String target;
    private Timestamp createdAt;

    // constructeur vide (obligatoire pour JavaFX TableView)
    public Transaction(){}

    // constructeur pour lecture DB
    public Transaction(int id, int userId, String type, double amount, String target, Timestamp createdAt){
        this.id=id;
        this.user_id=userId;
        this.type=type;
        this.amount=amount;
        this.target=target;
        this.createdAt=createdAt;
    }

    // constructeur pour INSERT (TopUp / Send / Receive)
    public Transaction(int userId,String type,double amount,String target){
        this.user_id=userId;
        this.type=type;
        this.amount=amount;
        this.target=target;
    }

    // GETTERS
    public int getId(){ return id; }
    public int getUserId(){ return user_id; }
    public String getType(){ return type; }
    public double getAmount(){ return amount; }
    public String getTarget(){ return target; }
    public Timestamp getCreatedAt(){ return createdAt; }

    // SETTERS
    public void setId(int id){ this.id=id; }
    public void setUserId(int userId){ this.user_id=userId; }
    public void setType(String type){ this.type=type; }
    public void setAmount(double amount){ this.amount=amount; }
    public void setTarget(String target){ this.target=target; }
    public void setCreatedAt(Timestamp createdAt){ this.createdAt=createdAt; }
}