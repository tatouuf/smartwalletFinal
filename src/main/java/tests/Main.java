package tests;

import entities.*;
import services.*;

import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println("========== SMART WALLET TEST START ==========");

            // ===== USER CRUD =====
            ServiceUser userService = new ServiceUser();

            User user = new User();
            user.setNom("Nouhe");
            user.setPrenom("Said");
            user.setEmail("souhe" + System.currentTimeMillis() + "@mail.com"); // éviter duplication
            user.setPassword("password1234");
            user.setTelephone("11122258");
            user.setRole(Role.USER);
            user.setDate_creation(LocalDateTime.now());
            user.setDate_update(LocalDateTime.now());
            user.setIs_actif(true);

            if (!userService.isEmailTaken(user.getEmail())) {
                userService.ajouter(user);
                System.out.println("✅ User added");
            }

            // récupérer le user depuis la base
            User dbUser = userService.recupererParEmail(user.getEmail());
            System.out.println("Retrieved User ID: " + dbUser.getId());

            // ===== TRANSACTION CRUD =====
            ServiceTransaction transactionService = new ServiceTransaction();

            Transaction tx = new Transaction();
            tx.setUserId(dbUser.getId());
            tx.setType("DEBIT");
            tx.setAmount(400.0);
            tx.setTarget("Test payment");

            transactionService.ajouter(tx);
            System.out.println("✅ Transaction added");

            List<Transaction> userTxs = transactionService.recupererParUser(dbUser.getId());
            System.out.println("Transactions for user: " + userTxs.size());

            List<Transaction> allTxs = transactionService.recuperer();
            System.out.println("All transactions count: " + allTxs.size());

            double benefice = transactionService.calculerBenefice();
            System.out.println("Calculated benefice: " + benefice);

            // ===== RECLAMATION CRUD =====
            ServiceReclamation reclamationService = new ServiceReclamation();

            reclamationService.sendReclamation(
                    dbUser.getId(),
                    "My reclamation"
            );

            System.out.println("✅ Reclamation sent");

            List<Reclamation> recs = reclamationService.getAllReclamations();
            System.out.println("Reclamations count: " + recs.size());

            System.out.println("========== SMART WALLET TEST END ==========");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
