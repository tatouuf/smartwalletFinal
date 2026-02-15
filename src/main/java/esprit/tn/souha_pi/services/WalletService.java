package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.utils.Mydatabase;

import java.sql.*;

public class WalletService  {

    Connection cnx = Mydatabase.getInstance().getConnection();

    /* ================= GET WALLET ================= */

    public Wallet getByUserId(int userId) throws Exception {

        String sql = "SELECT * FROM wallet WHERE user_id=?";

        cnx = fixConnection();
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Wallet(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("balance")
            );
        }

        throw new Exception("Wallet not found for user " + userId);
    }

    /* ================= CREDIT ================= */

    public void credit(int userId, double amount) throws Exception {

        String sql = "UPDATE wallet SET balance = balance + ? WHERE user_id=?";

        cnx = fixConnection();
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setDouble(1, amount);
        ps.setInt(2, userId);
        ps.executeUpdate();
    }

    /* ================= DEBIT ================= */

    public void debit(int userId, double amount) throws Exception {

        Wallet w = getByUserId(userId);

        if (w.getBalance() < amount)
            throw new Exception("Insufficient balance");

        String sql = "UPDATE wallet SET balance = balance - ? WHERE user_id=?";

        cnx = fixConnection();
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setDouble(1, amount);
        ps.setInt(2, userId);
        ps.executeUpdate();
    }

    /* ================= TRANSFER ================= */
    // used for LOAN and SEND MONEY

    public void transfer(int senderId, int receiverId, double amount) throws Exception {

        // DO NOT TOUCH AUTOCOMMIT HERE

        // debit sender
        debit(senderId, amount);

        // credit receiver
        credit(receiverId, amount);
    }
     private Connection fixConnection() {

        try {
            if (cnx == null || cnx.isClosed() || !cnx.isValid(2)) {

                System.out.println("⚠ Database connection lost... reconnecting");

                // destroy old singleton instance
                java.lang.reflect.Field instance =
                        Mydatabase.class.getDeclaredField("instance");
                instance.setAccessible(true);
                instance.set(null, null);

                // recreate connection
                cnx = Mydatabase.getInstance().getConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnx;
    }

    public Wallet getWallet(){

        try{
            PreparedStatement ps=cnx.prepareStatement("SELECT * FROM wallet WHERE user_id=1");
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                return new Wallet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("balance")
                );
            }
        }catch(Exception e){throw new RuntimeException(e);}

        return null;
    }public boolean send(double amount){ Wallet w=getWallet(); if(w.getBalance()<amount) return false; try{ PreparedStatement ps=cnx.prepareStatement( "UPDATE wallet SET balance = balance - ? WHERE user_id=1" ); ps.setDouble(1,amount); ps.executeUpdate(); return true; }catch(Exception e){throw new RuntimeException(e);} }
    public void addBalance(int userId,double amount){ String sql=" UPDATE wallet SET balance = balance + ? WHERE user_id = ? "; try(var ps=cnx.prepareStatement(sql)){ ps.setDouble(1,amount); ps.setInt(2,userId); ps.executeUpdate(); }catch(Exception e){ throw new RuntimeException(e); } }
    public void updateWallet(Wallet wallet){
        String sql = "UPDATE wallet SET balance=? WHERE id=?";

        try(PreparedStatement ps=cnx.prepareStatement(sql)){


            ps.setDouble(1, wallet.getBalance());
            ps.setInt(2, wallet.getId());
            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void addBalance(double amount){ try{ PreparedStatement ps=cnx.prepareStatement( "UPDATE wallet SET balance = balance + ? WHERE user_id=1" ); ps.setDouble(1,amount); ps.executeUpdate(); }catch(Exception e){throw new RuntimeException(e);} }
}
