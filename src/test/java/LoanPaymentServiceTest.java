
import esprit.tn.souha_pi.entities.LoanPayment;
import esprit.tn.souha_pi.services.LoanPaymentService;
import esprit.tn.souha_pi.utils.Mydatabase;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoanPaymentServiceTest {

    private static LoanPaymentService service;
    private static Connection cnx;
    private static int testLoanId;

    @BeforeAll
    static void setup() throws Exception {

        service = new LoanPaymentService();
        cnx = Mydatabase.getInstance().getConnection();

        /* ---------- CREATE TEST LOAN ---------- */

        String createLoan = """
                INSERT INTO loan(lender_id, borrower_id, principal_amount, remaining_amount, status)
                VALUES(1,1,100,100,'ACTIVE')
                """;

        PreparedStatement ps = cnx.prepareStatement(createLoan, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()){
            testLoanId = rs.getInt(1);
        }
    }

    /* ================= TEST ADD PAYMENT ================= */

    @Test
    @Order(1)
    void testAddPayment() throws Exception {

        LoanPayment payment = new LoanPayment(
                testLoanId,
                1,
                1,
                25
        );

        service.add(payment);

        List<LoanPayment> list = service.getByLoan(testLoanId);

        assertFalse(list.isEmpty(), "Le paiement doit être enregistré");
        assertEquals(25, list.get(0).getAmountPaid());
    }

    /* ================= TEST GET PAYMENTS ================= */

    @Test
    @Order(2)
    void testGetByLoan() {

        List<LoanPayment> list = service.getByLoan(testLoanId);

        assertTrue(list.size() >= 1, "Le prêt doit contenir au moins un paiement");
    }

    /* ================= MULTIPLE PAYMENTS ================= */

    @Test
    @Order(3)
    void testMultiplePayments() throws Exception {

        service.add(new LoanPayment(testLoanId,1,1,10));
        service.add(new LoanPayment(testLoanId,1,1,5));

        List<LoanPayment> list = service.getByLoan(testLoanId);

        assertTrue(list.size() >= 3, "Plusieurs paiements doivent être stockés");
    }

    /* ================= CLEANUP ================= */

    @AfterAll
    static void cleanup() throws Exception {

        // supprimer les paiements
        PreparedStatement ps1 = cnx.prepareStatement(
                "DELETE FROM loan_payment WHERE loan_id=?");
        ps1.setInt(1,testLoanId);
        ps1.executeUpdate();

        // supprimer le prêt
        PreparedStatement ps2 = cnx.prepareStatement(
                "DELETE FROM loan WHERE id=?");
        ps2.setInt(1,testLoanId);
        ps2.executeUpdate();
    }
}
