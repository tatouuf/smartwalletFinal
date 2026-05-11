package esprit.tn.souha_pi.services.ia;

import esprit.tn.souha_pi.entities.LoanRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ICreditScoringService {

    /**
     * Calcule le score de confiance entre un emprunteur et un prêteur
     */
    TrustScore calculateTrustScore(int borrowerId, int lenderId);

    /**
     * Analyse une demande de prêt et donne une recommandation
     */
    LoanAnalysis analyzeLoanRequest(LoanRequest request);

    /**
     * Prédit la probabilité de remboursement
     */
    RepaymentProbability predictRepayment(int userId, double amount, int days);

    /**
     * Suggère des conditions optimales pour un prêt
     */
    SuggestedTerms suggestTerms(LoanRequest request, double trustScore);

    // ======================= CLASSES INTERNES =======================

    /**
     * Résultat du score de confiance
     */
    class TrustScore {
        private double score; // 0-100
        private String level; // EXCELLENT, BON, MOYEN, FAIBLE
        private Map<String, Double> factors;
        private List<String> strengths;
        private List<String> weaknesses;
        private List<RiskFactor> risks;

        public TrustScore(double score, String level, Map<String, Double> factors,
                          List<String> strengths, List<String> weaknesses) {
            this(score, level, factors, strengths, weaknesses, new ArrayList<>());
        }

        public TrustScore(double score, String level, Map<String, Double> factors,
                          List<String> strengths, List<String> weaknesses,
                          List<RiskFactor> risks) {
            this.score = score;
            this.level = level;
            this.factors = factors;
            this.strengths = strengths;
            this.weaknesses = weaknesses;
            this.risks = risks;
        }

        public double getScore() { return score; }
        public String getLevel() { return level; }
        public Map<String, Double> getFactors() { return factors; }
        public List<String> getStrengths() { return strengths; }
        public List<String> getWeaknesses() { return weaknesses; }
        public List<RiskFactor> getRisks() { return risks; }
    }

    /**
     * Facteur de risque
     */
    class RiskFactor {
        private String type;
        private String description;
        private int severity; // 1-10

        public RiskFactor(String type, String description, int severity) {
            this.type = type;
            this.description = description;
            this.severity = severity;
        }

        public String getType() { return type; }
        public String getDescription() { return description; }
        public int getSeverity() { return severity; }
    }

    /**
     * Analyse d'une demande de prêt
     */
    class LoanAnalysis {
        private String recommendation; // APPROVE, CONDITIONAL, REJECT
        private double confidence;
        private String reason;
        private List<String> conditions;
        private SuggestedTerms suggestedTerms;

        public LoanAnalysis(String recommendation, double confidence, String reason,
                            List<String> conditions, SuggestedTerms suggestedTerms) {
            this.recommendation = recommendation;
            this.confidence = confidence;
            this.reason = reason;
            this.conditions = conditions;
            this.suggestedTerms = suggestedTerms;
        }

        public String getRecommendation() { return recommendation; }
        public double getConfidence() { return confidence; }
        public String getReason() { return reason; }
        public List<String> getConditions() { return conditions; }
        public SuggestedTerms getSuggestedTerms() { return suggestedTerms; }
    }

    /**
     * Probabilité de remboursement
     */
    class RepaymentProbability {
        private double probability; // 0-1
        private String level; // ÉLEVÉE, MOYENNE, FAIBLE
        private List<String> factors;

        public RepaymentProbability(double probability, String level, List<String> factors) {
            this.probability = probability;
            this.level = level;
            this.factors = factors;
        }

        public double getProbability() { return probability; }
        public String getLevel() { return level; }
        public List<String> getFactors() { return factors; }
    }

    /**
     * Conditions suggérées pour un prêt
     */
    class SuggestedTerms {
        private int maxDuration;
        private double recommendedAmount;
        private List<Installment> installments;
        private int reminderDays;
        private String warning;

        public SuggestedTerms(int maxDuration, double recommendedAmount, String warning) {
            this(maxDuration, recommendedAmount, new ArrayList<>(), 5, warning);
        }

        public SuggestedTerms(int maxDuration, double recommendedAmount,
                              List<Installment> installments, int reminderDays, String warning) {
            this.maxDuration = maxDuration;
            this.recommendedAmount = recommendedAmount;
            this.installments = installments;
            this.reminderDays = reminderDays;
            this.warning = warning;
        }

        public int getMaxDuration() { return maxDuration; }
        public double getRecommendedAmount() { return recommendedAmount; }
        public List<Installment> getInstallments() { return installments; }
        public int getReminderDays() { return reminderDays; }
        public String getWarning() { return warning; }
    }

    /**
     * Échéance de remboursement
     */
    class Installment {
        private java.time.LocalDate dueDate;
        private double amount;

        public Installment(java.time.LocalDate dueDate, double amount) {
            this.dueDate = dueDate;
            this.amount = amount;
        }

        public java.time.LocalDate getDueDate() { return dueDate; }
        public double getAmount() { return amount; }
    }
}