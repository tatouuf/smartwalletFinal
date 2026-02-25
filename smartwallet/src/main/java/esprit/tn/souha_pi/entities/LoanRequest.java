package esprit.tn.souha_pi.entities;

import java.sql.Timestamp;

public class LoanRequest {

    private int id;

    private int borrowerId;   // celui qui demande l'argent
    private int lenderId;     // celui qui va prêter

    private double amount;
    private String message;

    private String status; // PENDING / ACCEPTED / REJECTED

    private Timestamp createdAt;
    private Timestamp respondedAt;

    public LoanRequest(){}

    public LoanRequest(int borrowerId, int lenderId, double amount, String message){
        this.borrowerId = borrowerId;
        this.lenderId = lenderId;
        this.amount = amount;
        this.message = message;
        this.status = "PENDING";
    }

    // GETTERS
    public int getId(){ return id; }
    public int getBorrowerId(){ return borrowerId; }
    public int getLenderId(){ return lenderId; }
    public double getAmount(){ return amount; }
    public String getMessage(){ return message; }
    public String getStatus(){ return status; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public Timestamp getRespondedAt(){ return respondedAt; }

    // SETTERS
    public void setId(int id){ this.id=id; }
    public void setBorrowerId(int borrowerId){ this.borrowerId=borrowerId; }
    public void setLenderId(int lenderId){ this.lenderId=lenderId; }
    public void setAmount(double amount){ this.amount=amount; }
    public void setMessage(String message){ this.message=message; }
    public void setStatus(String status){ this.status=status; }
    public void setCreatedAt(Timestamp createdAt){ this.createdAt=createdAt; }
    public void setRespondedAt(Timestamp respondedAt){ this.respondedAt=respondedAt; }
}
