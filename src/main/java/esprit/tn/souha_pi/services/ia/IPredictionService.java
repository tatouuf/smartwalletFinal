package esprit.tn.souha_pi.services.ia;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IPredictionService {

    /**
     * Prédit le solde futur
     */
    BalancePrediction predictBalance(int userId, int daysAhead);

    /**
     * Prédit les dépenses du mois prochain par catégorie
     */
    Map<String, Double> predictNextMonthExpenses(int userId);

    /**
     * Détecte les risques de découvert
     */
    OverdraftRisk analyzeOverdraftRisk(int userId);

    /**
     * Détecte les transactions récurrentes
     */
    List<RecurringTransaction> detectRecurringTransactions(int userId);

    // ======================= CLASSES INTERNES =======================

    class BalancePrediction {
        private double currentBalance;
        private double predictedBalance;
        private double confidence;
        private LocalDate criticalDate;
        private String recommendation;

        public BalancePrediction(double currentBalance, double predictedBalance,
                                 double confidence, LocalDate criticalDate,
                                 String recommendation) {
            this.currentBalance = currentBalance;
            this.predictedBalance = predictedBalance;
            this.confidence = confidence;
            this.criticalDate = criticalDate;
            this.recommendation = recommendation;
        }

        public double getCurrentBalance() { return currentBalance; }
        public double getPredictedBalance() { return predictedBalance; }
        public double getConfidence() { return confidence; }
        public LocalDate getCriticalDate() { return criticalDate; }
        public String getRecommendation() { return recommendation; }
    }

    class OverdraftRisk {
        private boolean hasRisk;
        private double amount;
        private LocalDate estimatedDate;
        private String alertMessage;
        private String suggestedAction;

        public OverdraftRisk(boolean hasRisk, double amount, LocalDate estimatedDate,
                             String alertMessage, String suggestedAction) {
            this.hasRisk = hasRisk;
            this.amount = amount;
            this.estimatedDate = estimatedDate;
            this.alertMessage = alertMessage;
            this.suggestedAction = suggestedAction;
        }

        public boolean isHasRisk() { return hasRisk; }
        public double getAmount() { return amount; }
        public LocalDate getEstimatedDate() { return estimatedDate; }
        public String getAlertMessage() { return alertMessage; }
        public String getSuggestedAction() { return suggestedAction; }
    }

    class RecurringTransaction {
        private String description;
        private double amount;
        private String frequency; // MENSUEL, HEBDOMADAIRE, etc.
        private LocalDate nextDate;
        private double totalMonthly;

        public RecurringTransaction(String description, double amount,
                                    String frequency, LocalDate nextDate,
                                    double totalMonthly) {
            this.description = description;
            this.amount = amount;
            this.frequency = frequency;
            this.nextDate = nextDate;
            this.totalMonthly = totalMonthly;
        }

        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getFrequency() { return frequency; }
        public LocalDate getNextDate() { return nextDate; }
        public double getTotalMonthly() { return totalMonthly; }
    }
}