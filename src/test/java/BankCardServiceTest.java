
import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.utils.Mydatabase;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankCardServiceTest {

    private static BankCardService service;
    private static Connection cnx;

    @BeforeAll
    static void setup() throws Exception {
        service = new BankCardService();
        cnx = Mydatabase.getInstance().getConnection();

        // nettoyer table avant tests
        PreparedStatement ps = cnx.prepareStatement("DELETE FROM bank_card WHERE user_id=1");
        ps.executeUpdate();
    }

    /* ================= TEST ADD ================= */

    @Test
    @Order(1)
    void testAddCard() {

        BankCard card = new BankCard(
                0,
                1,
                "souha saidi",
                "1234567812345678",
                "12/28",
                "123",
                "VISA"
        );

        service.add(card);

        List<BankCard> cards = service.getAllByUser(1);

        assertFalse(cards.isEmpty(), "La carte doit être ajoutée");
    }

    /* ================= TEST GET ================= */

    @Test
    @Order(2)
    void testGetAllByUser(){

        List<BankCard> cards = service.getAllByUser(1);

        assertTrue(cards.size() >= 1, "L'utilisateur doit avoir au moins une carte");
    }

    /* ================= TEST LIMIT 5 CARDS ================= */

    @Test
    @Order(3)
    void testMaxFiveCards(){

        // ajouter jusqu'à 5
        for(int i=0;i<5;i++){
            try{
                BankCard card = new BankCard(
                        0,
                        1,
                        "User Test",
                        "99998888777766" + i,
                        "11/29",
                        "456",
                        "MASTERCARD"
                );
                service.add(card);
            }catch(Exception ignored){}
        }

        // la 6ème doit échouer
        Exception exception = assertThrows(RuntimeException.class, () -> {

            BankCard card = new BankCard(
                    0,
                    1,
                    "Overflow User",
                    "4444333322221111",
                    "10/30",
                    "999",
                    "VISA"
            );

            service.add(card);
        });

        assertEquals("Maximum 5 cards allowed", exception.getMessage());
    }

    /* ================= TEST DELETE ================= */

    @Test
    @Order(4)
    void testDeleteCard() throws Exception {

        List<BankCard> cards = service.getAllByUser(1);
        assertFalse(cards.isEmpty());

        int id = cards.get(0).getId();

        service.delete(id);

        List<BankCard> newList = service.getAllByUser(1);

        assertTrue(newList.size() < cards.size(), "La carte doit être supprimée");
    }
}
