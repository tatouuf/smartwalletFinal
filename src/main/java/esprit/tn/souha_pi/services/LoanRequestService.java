package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.*;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanRequestService  {

    Connection cnx= MyDataBase.getInstance().getConnection();

    WalletService walletService = new WalletService();
    TransactionService transactionService = new TransactionService();

    /* ================================================= */
    /* ================= CREATE REQUEST ================= */
    /* ================================================= */

    public void createRequest(int borrowerId,int lenderId,double amount,String message) throws Exception{

        if(amount <= 0)
            throw new Exception("Invalid amount");

        cnx = fixConnection();

        String sql = """
            INSERT INTO loan_request(borrower_id,lender_id,amount,message,status)
            VALUES(?,?,?,?, 'PENDING')
            """;

        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1,borrowerId);
        ps.setInt(2,lenderId);
        ps.setDouble(3,amount);
        ps.setString(4,message);
        ps.executeUpdate();
    }

    /* ================================================= */
    /* ================= GET RECEIVED =================== */
    /* ================================================= */

    public List<LoanRequest> getRequestsForLender(int lenderId){

        List<LoanRequest> list = new ArrayList<>();

        try{
            cnx = fixConnection();

            String sql="SELECT * FROM loan_request WHERE lender_id=? AND status='PENDING'";
            PreparedStatement ps=cnx.prepareStatement(sql);
            ps.setInt(1,lenderId);
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                LoanRequest r=new LoanRequest();
                r.setId(rs.getInt("id"));
                r.setBorrowerId(rs.getInt("borrower_id"));
                r.setLenderId(rs.getInt("lender_id"));
                r.setAmount(rs.getDouble("amount"));
                r.setMessage(rs.getString("message"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(r);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    /* ================================================= */
    /* ================= ACCEPT REQUEST ================= */
    /* ================================================= */
    private Connection fixConnection() {

        try {
            if (cnx == null || cnx.isClosed() || !cnx.isValid(2)) {

                System.out.println("⚠ LoanRequestService reconnecting DB...");

                // destroy singleton
                java.lang.reflect.Field instance =
                        MyDataBase.class.getDeclaredField("instance");
                instance.setAccessible(true);
                instance.set(null, null);

                // recreate connection
                cnx = MyDataBase.getInstance().getConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnx;
    }

    public void acceptRequest(int requestId) throws Exception {

        cnx = fixConnection();

        String sql="SELECT * FROM loan_request WHERE id=? AND status='PENDING'";
        PreparedStatement ps=cnx.prepareStatement(sql);
        ps.setInt(1,requestId);
        ResultSet rs=ps.executeQuery();

        if(!rs.next())
            throw new Exception("Request already processed");

        int borrowerId=rs.getInt("borrower_id");
        int lenderId=rs.getInt("lender_id");
        double amount=rs.getDouble("amount");

        try{
            cnx.setAutoCommit(false);

            // check lender balance
            Wallet lender = walletService.getByUserId(lenderId);
            if(lender.getBalance() < amount)
                throw new Exception("Insufficient balance");

            // transfer money
            walletService.transfer(lenderId, borrowerId, amount);

            // create loan
            String loanSql = """
            INSERT INTO loan(lender_id,borrower_id,principal_amount,remaining_amount,status)
            VALUES(?,?,?,?, 'ACTIVE')
        """;

            PreparedStatement loanPS=cnx.prepareStatement(loanSql);
            loanPS.setInt(1,lenderId);
            loanPS.setInt(2,borrowerId);
            loanPS.setDouble(3,amount);
            loanPS.setDouble(4,amount);
            loanPS.executeUpdate();

            // update request
            PreparedStatement upd=cnx.prepareStatement(
                    "UPDATE loan_request SET status='ACCEPTED', responded_at=NOW() WHERE id=?"
            );
            upd.setInt(1,requestId);
            upd.executeUpdate();

            cnx.commit();

        }catch(Exception e){
            cnx.rollback();
            throw new Exception("Accept failed: "+e.getMessage());
        }
        finally{
            cnx.setAutoCommit(true);
        }
    }

    /* ================================================= */
    /* ================= REJECT REQUEST ================= */
    /* ================================================= */

    public void rejectRequest(int requestId) throws Exception{

        cnx = fixConnection();

        String sql="UPDATE loan_request SET status='REJECTED' WHERE id=?";
        PreparedStatement ps=cnx.prepareStatement(sql);
        ps.setInt(1,requestId);
        ps.executeUpdate();
    }

}
