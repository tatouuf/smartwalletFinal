package services;

import entities.DashboardAdmin;
import entities.Reclamation;
import entities.Role;
import entities.Transaction;
import entities.User;
import utils.NotificationHelper;

import java.sql.SQLException;
import java.util.List;

public class ServiceDashboardAdmin {

    private ServiceUser serviceUser;
    private ServiceReclamation serviceReclamation;
    private ServiceTransaction serviceTransaction;

    private static final double TAX_RATE = 0.09; // 9% fixed for benefice

    // Milestone tracking
    private static boolean milestone1000UsersReached = false;
    private static boolean milestone10000ProfitReached = false;

    public ServiceDashboardAdmin() {
        serviceUser = new ServiceUser();
        serviceReclamation = new ServiceReclamation();
        serviceTransaction = new ServiceTransaction();
    }

    public DashboardAdmin getDashboard() throws SQLException {

        DashboardAdmin dashboard = new DashboardAdmin();

        // ====== USERS (EXCLUDE ADMINS) ======
        List<User> users = serviceUser.recupererUsersOnly();
        dashboard.setListeUsers(users);
        dashboard.setTotalUsers(users.size());

        // 🔔 CHECK 1000 USERS MILESTONE
        if (users.size() >= 1000 && !milestone1000UsersReached) {
            milestone1000UsersReached = true;
            notifyAllAdmins(
                    NotificationHelper.MILESTONE_USERS,
                    "🎉 Milestone Reached: 1,000 Users!",
                    null
            );
        }

        // ====== RECLAMATIONS ======
        List<Reclamation> reclamations = serviceReclamation.getAllReclamations();
        dashboard.setListeReclamations(reclamations);
        dashboard.setTotalReclamations(reclamations.size());

        // ====== TRANSACTIONS ======
        List<Transaction> transactions = serviceTransaction.recuperer();
        dashboard.setListeTransactions(transactions);
        dashboard.setTotalTransactions(transactions.size());

        // ====== CALCULATE BENEFICE ======
        double totalBenefice = transactions.stream()
                .mapToDouble(t -> t.getAmount() * TAX_RATE) // 9% of amount
                .sum();
        dashboard.setBenefice(totalBenefice);

        // 🔔 CHECK 10K PROFIT MILESTONE
        if (totalBenefice >= 10000 && !milestone10000ProfitReached) {
            milestone10000ProfitReached = true;
            notifyAllAdmins(
                    NotificationHelper.MILESTONE_PROFIT,
                    "💰 Milestone Reached: 10,000 DT Profit!",
                    null
            );
        }

        return dashboard;
    }

    // ================= NOTIFY ALL ADMINS =================
    private void notifyAllAdmins(String type, String message, Integer relatedId) throws SQLException {
        List<User> admins = serviceUser.recuperer().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .toList();

        for (User admin : admins) {
            NotificationHelper.createAndShowNotification(
                    admin.getId(),
                    type,
                    message,
                    relatedId
            );
        }
    }
}