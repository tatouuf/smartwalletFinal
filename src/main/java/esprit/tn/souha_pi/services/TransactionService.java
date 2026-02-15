package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.utils.Mydatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransactionService  {

    Connection cnx= Mydatabase.getInstance().getConnection();
    public void add(Transaction t){

        String sql="""
    INSERT INTO transaction(user_id,type,amount,target,created_at)
    VALUES(?,?,?,?,NOW())
    """;

        try(PreparedStatement ps=cnx.prepareStatement(sql)){
            ps.setInt(1,t.getUserId());
            ps.setString(2,t.getType());
            ps.setDouble(3,t.getAmount());
            ps.setString(4,t.getTarget());
            ps.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> getAll(int userId){

        List<Transaction> list=new ArrayList<>();

        String sql="""
    SELECT * FROM transaction
    WHERE user_id=?
    ORDER BY created_at DESC, id DESC
    """;

        try(PreparedStatement ps=cnx.prepareStatement(sql)){
            ps.setInt(1,userId);

            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                list.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("target"),
                        rs.getTimestamp("created_at")
                ));
            }

        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return list;
    }
    public void addTransaction(Transaction t){

        String sql = "INSERT INTO transaction(user_id,type,amount,target,created_at) VALUES(?,?,?,?,NOW())";

        try(PreparedStatement ps=cnx.prepareStatement(sql)){


            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getTarget());

            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
