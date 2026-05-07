package esprit.tn.souha_pi.services.ia.impl;

import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.services.ia.IPredictionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PredictionService implements IPredictionService {

    private final TransactionService transactionService;
    private final WalletService walletService;

    public PredictionService() {
        this.transactionService = new TransactionService();
        this.walletService = new WalletService();
    }

    @Override
    public BalancePrediction predictBalance(int userId, int daysAhead) {
        try {
            Wallet wallet = walletService.getByUserId(userId);
            List<Transaction> transactions = transactionService.getUserTransactions(userId);

            double currentBalance = wallet.getBalance();
            double avgDailyExpense = calculateAverageDailyExpense(transactions);
            double predictedBalance = currentBalance;

            LocalDate criticalDate = null;
            String recommendation = null;

            // Simulation jour par jour
            for (int i = 1; i <= daysAhead; i++) {
                predictedBalance -= avgDailyExpense;

                if (predictedBalance < 0 && criticalDate == null) {
                    criticalDate = LocalDate.now().plusDays(i);
                    recommendation = "⚠️ Risque de découvert dans " + i + " jours. " +
                            "Envisagez de réduire les dépenses ou d'ajouter des fonds.";
                }
            }

            double confidence = transactions.size() > 20 ? 0.85 : 0.65;

            return new BalancePrediction(
                    currentBalance,
                    predictedBalance,
                    confidence,
                    criticalDate,
                    recommendation
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new BalancePrediction(0, 0, 0, null, "Erreur de prédiction");
        }
    }

    @Override
    public Map<String, Double> predictNextMonthExpenses(int userId) {
        Map<String, Double> predictions = new HashMap<>();
        List<Transaction> transactions = transactionService.getUserTransactions(userId);

        // Grouper par mois et catégorie (simplifié)
        Map<String, List<Transaction>> last3Months = transactions.stream()
                .filter(t -> t.getCreatedAt().toLocalDateTime().isAfter(LocalDateTime.now().minusMonths(3)))
                .collect(Collectors.groupingBy(t -> t.getTarget()));

        for (Map.Entry<String, List<Transaction>> entry : last3Months.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToDouble(Transaction::getAmount)
                    .average()
                    .orElse(0);
            if (avg < 0) { // Dépense
                predictions.put(entry.getKey(), Math.abs(avg));
            }
        }

        return predictions;
    }

    @Override
    public OverdraftRisk analyzeOverdraftRisk(int userId) {
        try {
            BalancePrediction prediction = predictBalance(userId, 15);

            if (prediction.getCriticalDate() != null) {
                return new OverdraftRisk(
                        true,
                        Math.abs(prediction.getPredictedBalance()),
                        prediction.getCriticalDate(),
                        prediction.getRecommendation(),
                        "Transférer " + String.format("%.2f", Math.abs(prediction.getPredictedBalance()) + 50) + " TND depuis l'épargne"
                );
            }

            return new OverdraftRisk(false, 0, null, "✓ Aucun risque détecté", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new OverdraftRisk(false, 0, null, "Erreur d'analyse", null);
        }
    }

    @Override
    public List<RecurringTransaction> detectRecurringTransactions(int userId) {
        List<RecurringTransaction> recurring = new ArrayList<>();
        List<Transaction> transactions = transactionService.getUserTransactions(userId);

        // Grouper par description
        Map<String, List<Transaction>> byDescription = transactions.stream()
                .filter(t -> t.getAmount() < 0) // Dépenses seulement
                .collect(Collectors.groupingBy(Transaction::getTarget));

        for (Map.Entry<String, List<Transaction>> entry : byDescription.entrySet()) {
            List<Transaction> list = entry.getValue();
            if (list.size() >= 2) {
                // Vérifier si c'est mensuel (dates similaires)
                double avgAmount = list.stream()
                        .mapToDouble(Transaction::getAmount)
                        .average()
                        .orElse(0);

                LocalDate lastDate = list.get(0).getCreatedAt().toLocalDateTime().toLocalDate();
                LocalDate nextDate = lastDate.plusMonths(1);

                if (nextDate.isAfter(LocalDate.now()) &&
                        nextDate.isBefore(LocalDate.now().plusDays(15))) {
                    recurring.add(new RecurringTransaction(
                            entry.getKey(),
                            Math.abs(avgAmount),
                            "MENSUEL",
                            nextDate,
                            Math.abs(avgAmount)
                    ));
                }
            }
        }

        return recurring;
    }

    // ======================= MÉTHODES PRIVÉES =======================

    private double calculateAverageDailyExpense(List<Transaction> transactions) {
        if (transactions.isEmpty()) return 0;

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<Transaction> lastMonth = transactions.stream()
                .filter(t -> t.getCreatedAt().toLocalDateTime().isAfter(oneMonthAgo) && t.getAmount() < 0)
                .collect(Collectors.toList());

        if (lastMonth.isEmpty()) return 20; // Valeur par défaut

        double totalExpenses = lastMonth.stream()
                .mapToDouble(t -> Math.abs(t.getAmount()))
                .sum();

        return totalExpenses / 30; // Moyenne journalière
    }
}