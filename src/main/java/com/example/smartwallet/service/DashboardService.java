package com.example.smartwallet.service;

import dao.BudgetDAO;
import dao.DepenseDAO;
import dao.PlanningDAO;
import com.example.smartwallet.model.Budget;
import com.example.smartwallet.model.Depense;
import com.example.smartwallet.model.Planning;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DepenseDAO depenseDAO;
    private final BudgetDAO budgetDAO;
    private final PlanningDAO planningDAO;

    public DashboardService() {
        this.depenseDAO = new DepenseDAO();
        this.budgetDAO = new BudgetDAO();
        this.planningDAO = new PlanningDAO();
    }

    /**
     * Obtenir le total des dépenses pour un utilisateur
     */
    public double getTotalExpenses(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return depenseDAO.getTotalDepenses(userId);
    }

    /**
     * Obtenir le total des dépenses du mois courant
     */
    public double getCurrentMonthExpenses(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        LocalDate now = LocalDate.now();
        return depenseDAO.getTotalDepensesMois(userId, now.getMonthValue(), now.getYear());
    }

    /**
     * Obtenir le total des budgets
     */
    public double getTotalBudgets(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return budgetDAO.getTotalBudgets(userId);
    }

    /**
     * Obtenir le nombre total de plannings
     */
    public int getTotalPlannings(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return planningDAO.getTotalPlannings(userId);
    }

    /**
     * Obtenir le montant total des budgets utilisés ce mois
     */
    public double getCurrentMonthUsedBudget(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        LocalDate now = LocalDate.now();
        List<Budget> budgetsMois = budgetDAO.obtenirBudgetsMois(userId, now.getMonthValue(), now.getYear());
        return budgetsMois.stream().mapToDouble(Budget::getMontantActuel).sum();
    }

    /**
     * Obtenir les dépenses groupées par catégorie
     */
    public Map<String, Double> getExpensesByCategory(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        List<Depense> depenses = depenseDAO.obtenirToutesDepenses(userId);
        return depenses.stream()
                .collect(Collectors.groupingBy(
                        Depense::getCategorie,
                        Collectors.summingDouble(Depense::getMontant)
                ));
    }

    /**
     * Obtenir les dépenses groupées par mois
     */
    public Map<Integer, Double> getExpensesByMonth(int userId, int annee) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        Map<Integer, Double> expenses = new java.util.HashMap<>();
        for (int mois = 1; mois <= 12; mois++) {
            double total = depenseDAO.getTotalDepensesMois(userId, mois, annee);
            expenses.put(mois, total);
        }
        return expenses;
    }

    /**
     * Obtenir le ratio dépenses/budgets
     */
    public double getExpenseToBudgetRatio(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        double totalBudgets = getTotalBudgets(userId);
        double totalExpenses = getTotalExpenses(userId);

        if (totalBudgets == 0) {
            return 0.0;
        }
        return (totalExpenses / totalBudgets) * 100;
    }

    /**
     * Vérifier si le budget du mois est dépassé
     */
    public boolean isMonthlyBudgetExceeded(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        double monthlyExpenses = getCurrentMonthExpenses(userId);
        double monthlyBudgets = getCurrentMonthUsedBudget(userId);
        return monthlyExpenses > monthlyBudgets;
    }

    /**
     * Obtenir le statut du tableau de bord
     */
    public DashboardStats getDashboardStats(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }

        return new DashboardStats(
                getTotalExpenses(userId),
                getCurrentMonthExpenses(userId),
                getTotalBudgets(userId),
                getTotalPlannings(userId),
                getCurrentMonthUsedBudget(userId),
                getExpenseToBudgetRatio(userId),
                isMonthlyBudgetExceeded(userId)
        );
    }

    /**
     * Classe pour encapsuler les statistiques du tableau de bord
     */
    public static class DashboardStats {
        public final double totalExpenses;
        public final double currentMonthExpenses;
        public final double totalBudgets;
        public final int totalPlannings;
        public final double currentMonthUsedBudget;
        public final double expenseToBudgetRatio;
        public final boolean isMonthlyBudgetExceeded;

        public DashboardStats(double totalExpenses, double currentMonthExpenses,
                            double totalBudgets, int totalPlannings,
                            double currentMonthUsedBudget, double expenseToBudgetRatio,
                            boolean isMonthlyBudgetExceeded) {
            this.totalExpenses = totalExpenses;
            this.currentMonthExpenses = currentMonthExpenses;
            this.totalBudgets = totalBudgets;
            this.totalPlannings = totalPlannings;
            this.currentMonthUsedBudget = currentMonthUsedBudget;
            this.expenseToBudgetRatio = expenseToBudgetRatio;
            this.isMonthlyBudgetExceeded = isMonthlyBudgetExceeded;
        }
    }
}
