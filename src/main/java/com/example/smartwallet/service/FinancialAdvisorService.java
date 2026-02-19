package com.example.smartwallet.service;

import dao.DepenseDAO;
import dao.PlanningDAO;
import java.time.LocalDate;

public class FinancialAdvisorService {

    private final PlanningDAO planningDAO = new PlanningDAO();
    private final DepenseDAO depenseDAO = new DepenseDAO();
    private final ExpensePredictionService predictionService = new ExpensePredictionService();

    public static class FinancialAdvice {
        public final boolean canAfford;
        public final String advice;
        public final double remainingBalance;

        public FinancialAdvice(boolean canAfford, String advice, double remainingBalance) {
            this.canAfford = canAfford;
            this.advice = advice;
            this.remainingBalance = remainingBalance;
        }
    }

    public FinancialAdvice getPurchaseAdvice(int userId, double purchaseAmount) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // 1. Revenus du mois
        double monthlyIncome = planningDAO.getTotalRevenuMois(userId, currentMonth, currentYear);

        // 2. Dépenses déjà effectuées ce mois-ci
        double expensesSoFar = depenseDAO.getTotalDepensesMois(userId, currentMonth, currentYear);

        // 3. Prédiction du total des dépenses pour le mois complet
        // Note: predictNextMonthExpenses prédit pour le mois SUIVANT, mais on peut l'utiliser comme estimation
        // ou créer une méthode predictCurrentMonthExpenses. Pour l'instant, utilisons-la comme estimation.
        double predictedTotalExpenses = predictionService.predictNextMonthExpenses(userId); 

        // Solde estimé à la fin du mois SANS le nouvel achat
        // On prend le max entre les dépenses réelles et la prédiction pour être prudent
        double estimatedExpenses = Math.max(expensesSoFar, predictedTotalExpenses);
        double estimatedEndBalance = monthlyIncome - estimatedExpenses;

        // Solde final SI l'achat est effectué
        double finalBalanceAfterPurchase = estimatedEndBalance - purchaseAmount;

        if (finalBalanceAfterPurchase >= 0) {
            String advice = String.format(
                "Bonne nouvelle ! Vous pouvez vous permettre cet achat.\n" +
                "Revenu mensuel : %.2f TND\n" +
                "Dépenses estimées : %.2f TND\n" +
                "Montant de l'achat : %.2f TND\n" +
                "Solde restant estimé : %.2f TND",
                monthlyIncome, estimatedExpenses, purchaseAmount, finalBalanceAfterPurchase
            );
            return new FinancialAdvice(true, advice, finalBalanceAfterPurchase);
        } else {
            // Calcul du plan d'épargne
            double shortfall = -finalBalanceAfterPurchase;
            int monthsToSave = 0;
            double monthlySavings = 0;

            // Capacité d'épargne mensuelle estimée (Revenus - Dépenses)
            double monthlySavingsCapacity = monthlyIncome - predictedTotalExpenses;

            if (monthlySavingsCapacity > 0) {
                monthsToSave = (int) Math.ceil(purchaseAmount / monthlySavingsCapacity);
                monthlySavings = monthlySavingsCapacity;
            } else {
                // Si pas de capacité d'épargne, on suggère une épargne arbitraire ou on alerte
                monthsToSave = 6; // Par défaut 6 mois
                monthlySavings = purchaseAmount / 6;
            }
            
            if (monthsToSave <= 0) monthsToSave = 1;

            String advice = String.format(
                "Attention, cet achat dépasse votre budget ce mois-ci.\n" +
                "Revenu mensuel : %.2f TND\n" +
                "Dépenses estimées : %.2f TND\n" +
                "Montant de l'achat : %.2f TND\n" +
                "Déficit prévu : %.2f TND\n\n" +
                "CONSEIL IA : Pour acheter cet article sans stress, essayez d'épargner environ %.2f TND par mois pendant %d mois.",
                monthlyIncome, estimatedExpenses, purchaseAmount, finalBalanceAfterPurchase, monthlySavings, monthsToSave
            );
            return new FinancialAdvice(false, advice, finalBalanceAfterPurchase);
        }
    }
}
