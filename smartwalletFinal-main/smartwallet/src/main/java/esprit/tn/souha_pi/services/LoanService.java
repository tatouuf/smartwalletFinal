package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Loan;
import esprit.tn.souha_pi.entities.LoanPayment;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    private Connection cnx;
    private WalletService walletService = new WalletService();
    private TransactionService transactionService = new TransactionService();
    private LoanPaymentService paymentService = new LoanPaymentService();

    public LoanService() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    // ==================== MÉTHODE À AJOUTER ====================
    public List<Loan> getLoansBetweenUsers(int userId1, int userId2) {
        List<Loan> list = new ArrayList<>();

        String sql = """
            SELECT * FROM loan 
            WHERE (lender_id = ? AND borrower_id = ?) 
               OR (lender_id = ? AND borrower_id = ?)
            ORDER BY start_date DESC
            """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Loan l = new Loan();
                l.setId(rs.getInt("id"));
                l.setLenderId(rs.getInt("lender_id"));
                l.setBorrowerId(rs.getInt("borrower_id"));
                l.setPrincipalAmount(rs.getDouble("principal_amount"));
                l.setRemainingAmount(rs.getDouble("remaining_amount"));
                l.setStatus(rs.getString("status"));
                l.setStartDate(rs.getTimestamp("start_date"));
                l.setEndDate(rs.getTimestamp("end_date"));
                list.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==================== MÉTHODES EXISTANTES ====================
    public Loan getById(int id) throws Exception {
        String sql = "SELECT * FROM loan WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
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
                l.setStartDate(rs.getTimestamp("start_date"));
                l.setEndDate(rs.getTimestamp("end_date"));
                return l;
            }
        }

        throw new Exception("Loan not found");
    }

    public List<Loan> getLoansForUser(int userId) {
        List<Loan> list = new ArrayList<>();

        String sql = "SELECT * FROM loan WHERE borrower_id=? OR lender_id=? ORDER BY id DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Loan l = new Loan();
                l.setId(rs.getInt("id"));
                l.setLenderId(rs.getInt("lender_id"));
                l.setBorrowerId(rs.getInt("borrower_id"));
                l.setPrincipalAmount(rs.getDouble("principal_amount"));
                l.setRemainingAmount(rs.getDouble("remaining_amount"));
                l.setStatus(rs.getString("status"));
                l.setStartDate(rs.getTimestamp("start_date"));
                l.setEndDate(rs.getTimestamp("end_date"));
                list.add(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Loan> getLoansByUserId(int userId) {
        return getLoansForUser(userId);
    }

    public void payLoan(int loanId, int borrowerId, double amount) throws Exception {
        if (amount <= 0)
            throw new Exception("Montant invalide");

        Loan loan = getById(loanId);



        if (loan.getStatus().equals("PAID"))
            throw new Exception("Prêt déjà remboursé");

        if (amount > loan.getRemainingAmount())
            amount = loan.getRemainingAmount();

        try {
            cnx.setAutoCommit(false);

            // Transférer l'argent
            walletService.transfer(borrowerId, loan.getLenderId(), amount);

            // Enregistrer le paiement
            LoanPayment payment = new LoanPayment(loanId, borrowerId, loan.getLenderId(), amount);
            paymentService.add(payment);

            // Mettre à jour le prêt
            double newRemaining = loan.getRemainingAmount() - amount;
            String status = newRemaining <= 0 ? "PAID" : "ACTIVE";

            String updateSql = "UPDATE loan SET remaining_amount=?, status=? WHERE id=?";
            try (PreparedStatement ps = cnx.prepareStatement(updateSql)) {
                ps.setDouble(1, newRemaining);
                ps.setString(2, status);
                ps.setInt(3, loanId);
                ps.executeUpdate();
            }

            // Enregistrer les transactions
            transactionService.add(new Transaction(
                    borrowerId,
                    "LOAN_PAYMENT",
                    -amount,
                    "Remboursement prêt #" + loanId
            ));

            transactionService.add(new Transaction(
                    loan.getLenderId(),
                    "LOAN_RECEIVED",
                    amount,
                    "Réception remboursement prêt #" + loanId
            ));

            cnx.commit();

        } catch (Exception e) {
            cnx.rollback();
            throw new Exception("Paiement échoué: " + e.getMessage());
        } finally {
            cnx.setAutoCommit(true);
        }
    }
}