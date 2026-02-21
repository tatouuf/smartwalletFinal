import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.LoanRequestService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.MyDataBase;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoanRequestServiceTest {

    private static LoanRequestService service;
    private static WalletService walletService;
    private static Connection cnx;

    private static int borrowerId = 1;
    private static int lenderId = 2;
    private static int requestId;

    /* ================= CLEAN DATABASE ================= */

    @BeforeAll
    static void setup() throws Exception {

        service = new LoanRequestService();
        walletService = new WalletService();
        cnx = MyDataBase.getInstance().getConnection();

        // ---- CLEAN TABLES (VERY IMPORTANT) ----
        cnx.prepareStatement("DELETE FROM loan_payment").executeUpdate();
        cnx.prepareStatement("DELETE FROM transaction").executeUpdate();
        cnx.prepareStatement("DELETE FROM loan_request").executeUpdate();
        cnx.prepareStatement("DELETE FROM loan").executeUpdate();

        /* ---------- PREPARE WALLETS ---------- */

        PreparedStatement ps1 = cnx.prepareStatement(
                "INSERT IGNORE INTO wallet(user_id,balance) VALUES(?,0)");
        ps1.setInt(1, borrowerId);
        ps1.executeUpdate();

        PreparedStatement ps2 = cnx.prepareStatement(
                "INSERT IGNORE INTO wallet(user_id,balance) VALUES(?,1000)");
        ps2.setInt(1, lenderId);
        ps2.executeUpdate();

        // reset balances
        PreparedStatement ps3 = cnx.prepareStatement(
                "UPDATE wallet SET balance=0 WHERE user_id=?");
        ps3.setInt(1, borrowerId);
        ps3.executeUpdate();

        PreparedStatement ps4 = cnx.prepareStatement(
                "UPDATE wallet SET balance=1000 WHERE user_id=?");
        ps4.setInt(1, lenderId);
        ps4.executeUpdate();
    }

    /* ================= CREATE REQUEST ================= */

    @Test
    @Order(1)
    void testCreateRequest() throws Exception {

        service.createRequest(borrowerId, lenderId, 200, "Need money");

        // get LAST inserted request (not by borrower/lender)
        PreparedStatement ps = cnx.prepareStatement(
                "SELECT id FROM loan_request ORDER BY id DESC LIMIT 1");

        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next(), "La demande doit être créée");

        requestId = rs.getInt("id");
    }

    /* ================= ACCEPT REQUEST ================= */

    @Test
    @Order(2)
    void testAcceptRequest() throws Exception {

        service.acceptRequest(requestId);

        // get the loan created by THIS transaction only
        PreparedStatement ps = cnx.prepareStatement(
                "SELECT principal_amount FROM loan ORDER BY id DESC LIMIT 1");

        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next(), "Le prêt doit être créé après acceptation");

        double amount = rs.getDouble("principal_amount");

        assertEquals(200.0, amount, 0.01);
    }

    /* ================= MONEY TRANSFERRED ================= */

    @Test
    @Order(3)
    void testWalletTransfer() throws Exception {

        Wallet borrower = walletService.getByUserId(borrowerId);
        Wallet lender = walletService.getByUserId(lenderId);

        assertEquals(200.0, borrower.getBalance(), 0.01);
        assertEquals(800.0, lender.getBalance(), 0.01);
    }

    /* ================= REJECT REQUEST ================= */

    @Test
    @Order(4)
    void testRejectRequest() throws Exception {

        service.createRequest(borrowerId, lenderId, 50, "Test reject");

        PreparedStatement ps = cnx.prepareStatement(
                "SELECT id FROM loan_request ORDER BY id DESC LIMIT 1");

        ResultSet rs = ps.executeQuery();
        rs.next();

        int rejectId = rs.getInt("id");

        service.rejectRequest(rejectId);

        PreparedStatement check = cnx.prepareStatement(
                "SELECT status FROM loan_request WHERE id=?");
        check.setInt(1, rejectId);

        ResultSet rs2 = check.executeQuery();
        rs2.next();

        assertEquals("REJECTED", rs2.getString("status"));
    }
}
