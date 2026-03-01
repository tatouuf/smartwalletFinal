package esprit.tn.souha_pi.entities;

import java.sql.Timestamp;

public class LoanPayment {

    private int id;

    private int loanId;
    private int payerId;      // celui qui rembourse (borrower)
    private int receiverId;   // celui qui reçoit (lender)

    private double amountPaid;

    private Timestamp paymentDate;

    public LoanPayment(){}

    public LoanPayment(int loanId, int payerId, int receiverId, double amountPaid){
        this.loanId = loanId;
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amountPaid = amountPaid;
    }

    // GETTERS
    public int getId(){ return id; }
    public int getLoanId(){ return loanId; }
    public int getPayerId(){ return payerId; }
    public int getReceiverId(){ return receiverId; }
    public double getAmountPaid(){ return amountPaid; }
    public Timestamp getPaymentDate(){ return paymentDate; }

    // SETTERS
    public void setId(int id){ this.id=id; }
    public void setLoanId(int loanId){ this.loanId=loanId; }
    public void setPayerId(int payerId){ this.payerId=payerId; }
    public void setReceiverId(int receiverId){ this.receiverId=receiverId; }
    public void setAmountPaid(double amountPaid){ this.amountPaid=amountPaid; }
    public void setPaymentDate(Timestamp paymentDate){ this.paymentDate=paymentDate; }
}
