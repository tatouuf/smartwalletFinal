package esprit.tn.souha_pi.entities;

import java.sql.Timestamp;

public class Loan {

    private int id;

    private int lenderId;      // qui a prêté
    private int borrowerId;    // qui doit rembourser

    private double principalAmount; // montant initial
    private double remainingAmount; // reste à payer

    private String status; // ACTIVE / PAID / CANCELLED

    private Timestamp startDate;
    private Timestamp endDate;

    public Loan(){}

    public Loan(int lenderId,int borrowerId,double principalAmount){
        this.lenderId = lenderId;
        this.borrowerId = borrowerId;
        this.principalAmount = principalAmount;
        this.remainingAmount = principalAmount;
        this.status = "ACTIVE";
    }

    // GETTERS
    public int getId(){ return id; }
    public int getLenderId(){ return lenderId; }
    public int getBorrowerId(){ return borrowerId; }
    public double getPrincipalAmount(){ return principalAmount; }
    public double getRemainingAmount(){ return remainingAmount; }
    public String getStatus(){ return status; }
    public Timestamp getStartDate(){ return startDate; }
    public Timestamp getEndDate(){ return endDate; }

    // SETTERS
    public void setId(int id){ this.id=id; }
    public void setLenderId(int lenderId){ this.lenderId=lenderId; }
    public void setBorrowerId(int borrowerId){ this.borrowerId=borrowerId; }
    public void setPrincipalAmount(double principalAmount){ this.principalAmount=principalAmount; }
    public void setRemainingAmount(double remainingAmount){ this.remainingAmount=remainingAmount; }
    public void setStatus(String status){ this.status=status; }
    public void setStartDate(Timestamp startDate){ this.startDate=startDate; }
    public void setEndDate(Timestamp endDate){ this.endDate=endDate; }


}
