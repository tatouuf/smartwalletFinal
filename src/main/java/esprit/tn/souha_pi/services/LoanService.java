package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.*;
import esprit.tn.souha_pi.utils.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanService  {

    Connection cnx = Mydatabase.getInstance().getConnection();

    WalletService walletService = new WalletService();
    TransactionService transactionService = new TransactionService();
    LoanPaymentService paymentService = new LoanPaymentService();

    /* ===================================================== */
    /* ================= GET LOAN BY ID ==================== */
    /* ===================================================== */

    public Loan getById(int id) throws Exception {

        String sql = "SELECT * FROM loan WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Loan l = new Loan();
            l.setId(rs.getInt("id"));
            l.setLenderId(rs.getInt("lender_id"));
            l.setBorrowerId(rs.getInt("borrower_id"));
            l.setPrincipalAmount(rs.getDouble("principal_amount"));
            l.setRemainingAmount(rs.getDouble("remaining_amount"));
            l.setStatus(rs.getString("status"));
            return l;
        }

        throw new Exception("Loan not found");
    }

    /* ===================================================== */
    /* ================= GET LOANS FOR USER ================= */
    /* ===================================================== */

    public List<Loan> getLoansForUser(int userId){

        List<Loan> list = new ArrayList<>();

        String sql = """
                SELECT * FROM loan
                WHERE borrower_id=? OR lender_id=?
                ORDER BY id DESC
                """;

        try{
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1,userId);
            ps.setInt(2,userId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Loan l = new Loan();
                l.setId(rs.getInt("id"));
                l.setLenderId(rs.getInt("lender_id"));
                l.setBorrowerId(rs.getInt("borrower_id"));
                l.setPrincipalAmount(rs.getDouble("principal_amount"));
                l.setRemainingAmount(rs.getDouble("remaining_amount"));
                l.setStatus(rs.getString("status"));
                list.add(l);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    /* ===================================================== */
    /* ======================= PAY LOAN ===================== */
    /* ===================================================== */

    public void payLoan(int loanId, int borrowerId, double amount) throws Exception {

        if(amount <= 0)
            throw new Exception("Invalid payment amount");

        Loan loan = getById(loanId);

        // security: only borrower can pay
        if(loan.getBorrowerId() != borrowerId)
            throw new Exception("You are not the borrower of this loan");

        // already paid
        if(loan.getStatus().equals("PAID"))
            throw new Exception("Loan already paid");

        // overpayment protection
        if(amount > loan.getRemainingAmount())
            amount = loan.getRemainingAmount();

        try{

            cnx.setAutoCommit(false);

            /* ---------- TRANSFER MONEY ---------- */

            walletService.transfer(
                    borrowerId,
                    loan.getLenderId(),
                    amount
            );

            /* ---------- SAVE PAYMENT HISTORY ---------- */

            LoanPayment payment = new LoanPayment(
                    loanId,
                    borrowerId,
                    loan.getLenderId(),
                    amount
            );
            paymentService.add(payment);

            /* ---------- UPDATE REMAINING ---------- */

            double newRemaining = loan.getRemainingAmount() - amount;
            String status = loan.getStatus();

            if(newRemaining <= 0){
                newRemaining = 0;
                status = "PAID";
            }

            String sql = """
                    UPDATE loan
                    SET remaining_amount=?, status=?
                    WHERE id=?
                    """;

            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setDouble(1,newRemaining);
            ps.setString(2,status);
            ps.setInt(3,loanId);
            ps.executeUpdate();

            /* ---------- TRANSACTIONS ---------- */

            transactionService.add(new Transaction(
                    borrowerId,
                    "LOAN_PAYMENT_SENT",
                    amount,
                    "Loan #" + loanId
            ));

            transactionService.add(new Transaction(
                    loan.getLenderId(),
                    "LOAN_PAYMENT_RECEIVED",
                    amount,
                    "Loan #" + loanId
            ));

            cnx.commit();

        }catch(Exception e){
            cnx.rollback();
            throw new Exception("Payment failed: " + e.getMessage());
        }
        finally{
            cnx.setAutoCommit(true);
        }
    }
}
