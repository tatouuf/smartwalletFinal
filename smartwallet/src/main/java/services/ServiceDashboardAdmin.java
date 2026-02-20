package services;

import entities.DashboardAdmin;
import entities.Reclamation;
import entities.Transaction;
import entities.User;

import java.sql.SQLException;
import java.util.List;

public class ServiceDashboardAdmin {

    private ServiceUser serviceUser;
    private ServiceReclamation serviceReclamation;
    private ServiceTransaction serviceTransaction;

    private static final double TAX_RATE = 0.09; // 9% fixed for benefice

    public ServiceDashboardAdmin() {
        serviceUser = new ServiceUser();
        serviceReclamation = new ServiceReclamation();
        serviceTransaction = new ServiceTransaction(); // should exist in your project
    }

    public DashboardAdmin getDashboard() throws SQLException {

        DashboardAdmin dashboard = new DashboardAdmin();

        // ====== USERS ======
        List<User> users = serviceUser.recuperer();
        dashboard.setListeUsers(users);
        dashboard.setTotalUsers(users.size());

        // ====== RECLAMATIONS ======
        List<Reclamation> reclamations = serviceReclamation.recuperer();
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

        return dashboard;
    }
}
