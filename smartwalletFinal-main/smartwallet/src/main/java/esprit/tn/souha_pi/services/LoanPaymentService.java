package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.LoanPayment;
import esprit.tn.souha_pi.utils.MyDataBase;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanPaymentService  {

    Connection cnx= MyDataBase.getInstance().getConnection();


    public void add(LoanPayment p) throws SQLException{

        String sql="INSERT INTO loan_payment(loan_id,payer_id,receiver_id,amount_paid,payment_date) VALUES(?,?,?,?,NOW())";
        PreparedStatement ps=cnx.prepareStatement(sql);
        ps.setInt(1,p.getLoanId());
        ps.setInt(2,p.getPayerId());
        ps.setInt(3,p.getReceiverId());
        ps.setDouble(4,p.getAmountPaid());
        ps.executeUpdate();
    }

    public List<LoanPayment> getByLoan(int loanId){

        List<LoanPayment> list=new ArrayList<>();

        try{
            String sql="SELECT * FROM loan_payment WHERE loan_id=? ORDER BY payment_date DESC";
            PreparedStatement ps=cnx.prepareStatement(sql);
            ps.setInt(1,loanId);
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                LoanPayment p=new LoanPayment();
                p.setId(rs.getInt("id"));
                p.setLoanId(rs.getInt("loan_id"));
                p.setPayerId(rs.getInt("payer_id"));
                p.setReceiverId(rs.getInt("receiver_id"));
                p.setAmountPaid(rs.getDouble("amount_paid"));
                p.setPaymentDate(rs.getTimestamp("payment_date"));
                list.add(p);
            }

        }catch(Exception e){ e.printStackTrace(); }

        return list;
    }
}
