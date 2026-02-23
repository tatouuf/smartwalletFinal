package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletService {

    Connection cnx = MyDataBase.getInstance().getConnection();

    // Dans WalletService.java
    public List<Wallet> getAll() {
        List<Wallet> list = new ArrayList<>();
        String sql = "SELECT * FROM wallet";

        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Wallet w = new Wallet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("balance")
                );
                list.add(w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Dans WalletService.java
    public boolean aUnWallet(int userId) {
        try {
            getByUserId(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* ================= GET WALLET BY USER ID ================= */
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

    /* ================= CRÉER UN NOUVEAU WALLET ================= */
    public void creerWallet(int userId, double depotInitial) throws SQLException {
        String sql = "INSERT INTO wallet (user_id, balance) VALUES (?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDouble(2, depotInitial);
            ps.executeUpdate();

            System.out.println("✅ Wallet créé pour l'utilisateur ID: " + userId);
        }
    }

    /* ================= GET WALLET (pour user_id=1 par défaut) ================= */
    public Wallet getWallet() {
        try {
            PreparedStatement ps = cnx.prepareStatement("SELECT * FROM wallet WHERE user_id=1");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Wallet(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("balance")
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
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

        if (w.getBalance() < amount) {
            throw new Exception("Solde insuffisant");
        }

        String sql = "UPDATE wallet SET balance = balance - ? WHERE user_id=?";

        cnx = fixConnection();
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setDouble(1, amount);
        ps.setInt(2, userId);
        ps.executeUpdate();
    }

    /* ================= TRANSFER ================= */
    public void transfer(int senderId, int receiverId, double amount) throws Exception {
        debit(senderId, amount);
        credit(receiverId, amount);
    }

    /* ================= SEND (pour user_id=1) ================= */
    public boolean send(double amount) {
        Wallet w = getWallet();
        if (w.getBalance() < amount) return false;
        try {
            PreparedStatement ps = cnx.prepareStatement(
                    "UPDATE wallet SET balance = balance - ? WHERE user_id=1"
            );
            ps.setDouble(1, amount);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* ================= ADD BALANCE ================= */
    public void addBalance(int userId, double amount) {
        String sql = "UPDATE wallet SET balance = balance + ? WHERE user_id = ?";
        try (var ps = cnx.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addBalance(double amount) {
        addBalance(1, amount);
    }

    /* ================= UPDATE WALLET ================= */
    public void updateWallet(Wallet wallet) {
        String sql = "UPDATE wallet SET balance=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setDouble(1, wallet.getBalance());
            ps.setInt(2, wallet.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= FIX CONNECTION ================= */
    private Connection fixConnection() {
        try {
            if (cnx == null || cnx.isClosed() || !cnx.isValid(2)) {
                System.out.println("⚠ Database connection lost... reconnecting");

                java.lang.reflect.Field instance =
                        MyDataBase.class.getDeclaredField("instance");
                instance.setAccessible(true);
                instance.set(null, null);

                cnx = MyDataBase.getInstance().getConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnx;
    }


}