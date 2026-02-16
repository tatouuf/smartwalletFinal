package services.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceCreditTest {

    private ServiceCredit serviceCredit;
    private Connection testConnection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        // Charger explicitement le driver SQLite
        Class.forName("org.sqlite.JDBC");

        // Création d'une base SQLite en mémoire pour les tests
        testConnection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE credit (" +
                    "id_credit INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nom_client TEXT," +
                    "montant REAL," +
                    "date_credit DATE," +
                    "description TEXT," +
                    "statut TEXT" +
                    ")");
        }

        // Injection de la connexion de test via le constructeur
        serviceCredit = new ServiceCredit(testConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close(); // Fermer la base de test après chaque test
        }
    }

    @Test
    void testAjouterEtRecupererCredit() throws SQLException {
        Credit c = new Credit();
        c.setNomClient("Alice");
        c.setMontant(1000);
        c.setDateCredit(LocalDate.now());
        c.setDescription("Test credit");
        c.setStatut(StatutCredit.NON_REMBOURSE);

        serviceCredit.ajouterCredit(c);

        List<Credit> credits = serviceCredit.recupererCredits();
        assertEquals(1, credits.size());
        assertEquals("Alice", credits.get(0).getNomClient());
        assertEquals(1000, credits.get(0).getMontant());
    }

    @Test
    void testModifierEtSupprimerCredit() throws SQLException {
        // Ajouter un crédit
        Credit c = new Credit();
        c.setNomClient("Bob");
        c.setMontant(500);
        c.setDateCredit(LocalDate.now());
        c.setDescription("Test");
        c.setStatut(StatutCredit.NON_REMBOURSE);
        serviceCredit.ajouterCredit(c);

        // Récupérer l'id
        Credit saved = serviceCredit.recupererCredits().get(0);
        saved.setNomClient("Robert");
        saved.setMontant(750);

        // Modifier le crédit
        serviceCredit.modifierCredit(saved);

        Credit modified = serviceCredit.recupererCredits().get(0);
        assertEquals("Robert", modified.getNomClient());
        assertEquals(750, modified.getMontant());

        // Supprimer le crédit
        serviceCredit.supprimerCredit(modified);
        assertTrue(serviceCredit.recupererCredits().isEmpty());
    }

    @Test
    void testModifierStatutCredit() throws SQLException {
        Credit c = new Credit();
        c.setNomClient("Charlie");
        c.setMontant(300);
        c.setDateCredit(LocalDate.now());
        c.setDescription("Test statut");
        c.setStatut(StatutCredit.NON_REMBOURSE);
        serviceCredit.ajouterCredit(c);

        Credit saved = serviceCredit.recupererCredits().get(0);
        saved.setStatut(StatutCredit.REMBOURSE);

        serviceCredit.modifierStatutCredit(saved);

        Credit updated = serviceCredit.recupererCredits().get(0);
        assertEquals(StatutCredit.REMBOURSE, updated.getStatut());
    }
}
