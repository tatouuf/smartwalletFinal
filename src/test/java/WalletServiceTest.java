import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.MyDataBase;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletServiceTest {

    private static WalletService walletService;
    private static Connection cnx;

    private static final int USER_A = 100;
    private static final int USER_B = 200;

    /* ===================================================== */
    /* ================= SETUP DATABASE ==================== */
    /* ===================================================== */

    @BeforeAll
    static void setup() throws Exception {

        walletService = new WalletService();
        cnx = MyDataBase.getInstance().getConnection();

        // clean old test wallets
        PreparedStatement clean = cnx.prepareStatement(
                "DELETE FROM wallet WHERE user_id IN (?,?)");
        clean.setInt(1, USER_A);
        clean.setInt(2, USER_B);
        clean.executeUpdate();

        // create wallets
        PreparedStatement create = cnx.prepareStatement(
                "INSERT INTO wallet(user_id,balance) VALUES(?,?)");

        create.setInt(1, USER_A);
        create.setDouble(2, 0);
        create.executeUpdate();

        create.setInt(1, USER_B);
        create.setDouble(2, 500);
        create.executeUpdate();
    }

    /* ===================================================== */
    /* ================= CREDIT TEST ======================= */
    /* ===================================================== */

    @Test
    @Order(1)
    void testCredit() throws Exception {

        walletService.credit(USER_A, 200);

        Wallet w = walletService.getByUserId(USER_A);

        assertEquals(200, w.getBalance(), 0.01);
    }

    /* ===================================================== */
    /* ================= DEBIT TEST ======================== */
    /* ===================================================== */

    @Test
    @Order(2)
    void testDebit() throws Exception {

        walletService.debit(USER_A, 50);

        Wallet w = walletService.getByUserId(USER_A);

        assertEquals(150, w.getBalance(), 0.01);
    }

    /* ===================================================== */
    /* ========== INSUFFICIENT BALANCE TEST ================ */
    /* ===================================================== */

    @Test
    @Order(3)
    void testDebitInsufficientBalance() {

        Exception ex = assertThrows(Exception.class, () -> {
            walletService.debit(USER_A, 9999);
        });

        assertTrue(ex.getMessage().contains("Insufficient"));
    }

    /* ===================================================== */
    /* ================= TRANSFER TEST ===================== */
    /* ===================================================== */

    @Test
    @Order(4)
    void testTransfer() throws Exception {

        Wallet senderBefore = walletService.getByUserId(USER_B);
        Wallet receiverBefore = walletService.getByUserId(USER_A);

        walletService.transfer(USER_B, USER_A, 100);

        Wallet senderAfter = walletService.getByUserId(USER_B);
        Wallet receiverAfter = walletService.getByUserId(USER_A);

        assertEquals(senderBefore.getBalance() - 100, senderAfter.getBalance(), 0.01);
        assertEquals(receiverBefore.getBalance() + 100, receiverAfter.getBalance(), 0.01);
    }

    /* ===================================================== */
    /* ========= MONEY CONSERVATION (BANK RULE) ============ */
    /* ===================================================== */

    @Test
    @Order(5)
    void testMoneyConservation() throws Exception {

        Wallet a = walletService.getByUserId(USER_A);
        Wallet b = walletService.getByUserId(USER_B);

        double totalBefore = a.getBalance() + b.getBalance();

        walletService.transfer(USER_A, USER_B, 30);

        Wallet a2 = walletService.getByUserId(USER_A);
        Wallet b2 = walletService.getByUserId(USER_B);

        double totalAfter = a2.getBalance() + b2.getBalance();

        // MOST IMPORTANT TEST IN A BANK SYSTEM
        assertEquals(totalBefore, totalAfter, 0.01);
    }
}
