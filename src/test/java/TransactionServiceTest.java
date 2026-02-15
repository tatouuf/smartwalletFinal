import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.utils.Mydatabase;
import org.junit.jupiter.api.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceTest {

    private static TransactionService service;
    private static Connection cnx;

    private static int user1 = 100;
    private static int user2 = 200;

    /* ================================================= */
    /* ================= PREPARE DB ===================== */
    /* ================================================= */

    @BeforeAll
    static void setup() throws Exception {

        service = new TransactionService();
        cnx = Mydatabase.getInstance().getConnection();

        // Clean table to avoid old data affecting tests
        cnx.prepareStatement("DELETE FROM transaction").executeUpdate();
    }

    /* ================================================= */
    /* ================= ADD TRANSACTION ================ */
    /* ================================================= */

    @Test
    @Order(1)
    void testAddTransaction() {

        Transaction t = new Transaction(
                user1,
                "TEST_DEPOSIT",
                150,
                "Wallet"
        );

        service.add(t);

        var list = service.getAll(user1);

        assertFalse(list.isEmpty(), "Transaction should be inserted");
        assertEquals("TEST_DEPOSIT", list.get(0).getType());
        assertEquals(150, list.get(0).getAmount(), 0.01);
    }

    /* ================================================= */
    /* ================= MULTIPLE INSERTS =============== */
    /* ================================================= */

    @Test
    @Order(2)
    void testMultipleTransactions() {

        service.add(new Transaction(user1, "PAYMENT", 50, "Loan #1"));
        service.add(new Transaction(user1, "PAYMENT", 30, "Loan #2"));

        var list = service.getAll(user1);

        assertTrue(list.size() >= 3, "User should have multiple transactions");
    }

    /* ================================================= */
    /* ================= USER ISOLATION ================= */
    /* ================================================= */

    @Test
    @Order(3)
    void testUserIsolation() {

        // transaction for another user
        service.add(new Transaction(user2, "DEPOSIT", 500, "Wallet"));

        var list1 = service.getAll(user1);
        var list2 = service.getAll(user2);

        assertFalse(list1.isEmpty());
        assertEquals(1, list2.size(), "User2 should have only his transactions");
    }

    /* ================================================= */
    /* ================= ORDER BY DATE ================== */
    /* ================================================= */

    @Test
    @Order(4)
    void testOrderByDateDesc() {

        service.add(new Transaction(user1, "LAST_OPERATION", 999, "Test"));

        var list = service.getAll(user1);

        // newest transaction should be first
        assertEquals("LAST_OPERATION", list.get(0).getType());
    }
}
