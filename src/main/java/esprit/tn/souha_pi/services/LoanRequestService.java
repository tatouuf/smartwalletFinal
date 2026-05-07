package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanRequestService {

    private Connection cnx;
    private WalletService walletService = new WalletService();
    private TransactionService transactionService = new TransactionService();

    public LoanRequestService() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    private Connection fixConnection() {
        try {
            if (cnx == null || cnx.isClosed() || !cnx.isValid(2)) {
                System.out.println("⚠ LoanRequestService reconnecting DB...");
                cnx = MyDataBase.getInstance().getConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnx;
    }

    public void createRequest(int borrowerId, int lenderId, double amount, String message) throws Exception {
        if (amount <= 0)
            throw new Exception("Invalid amount");

        cnx = fixConnection();

        String sql = "INSERT INTO loan_request(borrower_id, lender_id, amount, message, status) VALUES(?, ?, ?, ?, 'PENDING')";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, borrowerId);
            ps.setInt(2, lenderId);
            ps.setDouble(3, amount);
            ps.setString(4, message != null ? message : "");
            ps.executeUpdate();
        }
    }

    public List<LoanRequest> getRequestsForLender(int lenderId) {
        List<LoanRequest> list = new ArrayList<>();
        cnx = fixConnection();

        String sql = "SELECT * FROM loan_request WHERE lender_id=? AND status='PENDING' ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, lenderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LoanRequest r = new LoanRequest();
                r.setId(rs.getInt("id"));
                r.setBorrowerId(rs.getInt("borrower_id"));
                r.setLenderId(rs.getInt("lender_id"));
                r.setAmount(rs.getDouble("amount"));
                r.setMessage(rs.getString("message"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<LoanRequest> getRequestsByBorrower(int borrowerId) {
        List<LoanRequest> list = new ArrayList<>();
        cnx = fixConnection();

        String sql = "SELECT * FROM loan_request WHERE borrower_id=? ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, borrowerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LoanRequest r = new LoanRequest();
                r.setId(rs.getInt("id"));
                r.setBorrowerId(rs.getInt("borrower_id"));
                r.setLenderId(rs.getInt("lender_id"));
                r.setAmount(rs.getDouble("amount"));
                r.setMessage(rs.getString("message"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void acceptRequest(int requestId) throws Exception {
        cnx = fixConnection();

        // Récupérer la demande
        String selectSql = "SELECT * FROM loan_request WHERE id=? AND status='PENDING'";
        PreparedStatement selectPs = cnx.prepareStatement(selectSql);
        selectPs.setInt(1, requestId);
        ResultSet rs = selectPs.executeQuery();

        if (!rs.next())
            throw new Exception("Request already processed");

        int borrowerId = rs.getInt("borrower_id");
        int lenderId = rs.getInt("lender_id");
        double amount = rs.getDouble("amount");

        try {
            cnx.setAutoCommit(false);

            // Vérifier le solde du prêteur
            Wallet lender = walletService.getByUserId(lenderId);
            if (lender.getBalance() < amount)
                throw new Exception("Solde insuffisant pour ce prêt");

            // Transférer l'argent
            walletService.transfer(lenderId, borrowerId, amount);

            // Créer le prêt
            String loanSql = "INSERT INTO loan(lender_id, borrower_id, principal_amount, remaining_amount, status) VALUES(?, ?, ?, ?, 'ACTIVE')";
            PreparedStatement loanPs = cnx.prepareStatement(loanSql);
            loanPs.setInt(1, lenderId);
            loanPs.setInt(2, borrowerId);
            loanPs.setDouble(3, amount);
            loanPs.setDouble(4, amount);
            loanPs.executeUpdate();

            // Mettre à jour la demande
            String updateSql = "UPDATE loan_request SET status='ACCEPTED', responded_at=NOW() WHERE id=?";
            PreparedStatement updatePs = cnx.prepareStatement(updateSql);
            updatePs.setInt(1, requestId);
            updatePs.executeUpdate();

            cnx.commit();

        } catch (Exception e) {
            cnx.rollback();
            throw new Exception("Acceptation échouée: " + e.getMessage());
        } finally {
            cnx.setAutoCommit(true);
        }
    }

    public void rejectRequest(int requestId) throws Exception {
        cnx = fixConnection();

        String sql = "UPDATE loan_request SET status='REJECTED', responded_at=NOW() WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
    }
}