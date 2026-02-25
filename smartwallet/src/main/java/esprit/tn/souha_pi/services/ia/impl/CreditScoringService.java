package esprit.tn.souha_pi.services.ia.impl;

import esprit.tn.souha_pi.entities.Loan;
import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.LoanService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.UserService;
import esprit.tn.souha_pi.services.ia.ICreditScoringService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CreditScoringService implements ICreditScoringService {

    private final LoanService loanService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final Map<String, Double> weights;

    public CreditScoringService() {
        this.loanService = new LoanService();
        this.transactionService = new TransactionService();
        this.userService = new UserService();
        this.weights = initializeWeights();
    }

    private Map<String, Double> initializeWeights() {
        Map<String, Double> w = new HashMap<>();
        w.put("repayment_history", 0.35);
        w.put("relationship", 0.15);
        w.put("frequency", 0.15);
        w.put("stability", 0.15);
        w.put("social", 0.10);
        w.put("sentiment", 0.10);
        return w;
    }

    @Override
    public TrustScore calculateTrustScore(int borrowerId, int lenderId) {
        Map<String, Double> factors = new HashMap<>();
        List<Loan> loans = loanService.getLoansBetweenUsers(borrowerId, lenderId);

        // Calculer chaque facteur
        factors.put("repayment_history", calculateRepaymentScore(loans));
        factors.put("relationship", calculateRelationshipDuration(borrowerId, lenderId));
        factors.put("frequency", calculateLoanFrequency(borrowerId, lenderId));
        factors.put("stability", calculateFinancialStability(borrowerId));
        factors.put("social", calculateSocialConnections(borrowerId, lenderId));
        factors.put("sentiment", analyzeSentiment(borrowerId, lenderId));

        // Calculer le score pondéré
        double totalScore = 0;
        for (Map.Entry<String, Double> entry : factors.entrySet()) {
            totalScore += entry.getValue() * weights.getOrDefault(entry.getKey(), 0.1);
        }

        double finalScore = Math.min(100, Math.max(0, totalScore * 100));
        String level = getTrustLevel(finalScore);

        // Déterminer les forces et faiblesses
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<RiskFactor> risks = new ArrayList<>();

        if (factors.get("repayment_history") > 0.8) {
            strengths.add("Excellent historique de remboursement");
        } else if (factors.get("repayment_history") < 0.4) {
            weaknesses.add("Historique de remboursement problématique");
            risks.add(new RiskFactor("repayment", "Risque de non-remboursement", 8));
        }

        if (factors.get("stability") > 0.7) {
            strengths.add("Situation financière stable");
        } else if (factors.get("stability") < 0.4) {
            weaknesses.add("Situation financière instable");
            risks.add(new RiskFactor("stability", "Risque d'instabilité", 7));
        }

        if (factors.get("relationship") > 0.7) {
            strengths.add("Relation de longue date");
        }

        if (factors.get("sentiment") > 0.7) {
            strengths.add("Relations toujours positives");
        }

        return new TrustScore(finalScore, level, factors, strengths, weaknesses, risks);
    }

    @Override
    public LoanAnalysis analyzeLoanRequest(LoanRequest request) {
        TrustScore trustScore = calculateTrustScore(request.getBorrowerId(), request.getLenderId());
        RepaymentProbability prob = predictRepayment(request.getBorrowerId(), request.getAmount(), 30);

        String recommendation;
        double confidence;
        String reason;
        List<String> conditions = new ArrayList<>();

        if (trustScore.getScore() >= 70 && prob.getProbability() > 0.7) {
            recommendation = "APPROUVER";
            confidence = 0.9;
            reason = "👍 Emprunteur fiable, bonne capacité de remboursement";
            conditions.add("Durée standard recommandée: 30 jours");
        }
        else if (trustScore.getScore() >= 50 && prob.getProbability() > 0.5) {
            recommendation = "CONDITIONNEL";
            confidence = 0.7;
            reason = "⚠️ Profil acceptable avec quelques réserves";
            conditions.add("Durée maximale recommandée: 15 jours");
            conditions.add("Montant recommandé: " + (request.getAmount() * 0.7) + " TND");
            conditions.add("Rappels automatiques recommandés");
        }
        else {
            recommendation = "REFUSER";
            confidence = 0.8;
            reason = "❌ Risque trop élevé";
            conditions.addAll(trustScore.getWeaknesses());
        }

        SuggestedTerms terms = suggestTerms(request, trustScore.getScore());

        return new LoanAnalysis(recommendation, confidence, reason, conditions, terms);
    }

    @Override
    public RepaymentProbability predictRepayment(int userId, double amount, int days) {
        List<Loan> loans = loanService.getLoansByUserId(userId);
        List<String> factors = new ArrayList<>();
        double probability = 0.5;

        if (!loans.isEmpty()) {
            long total = loans.size();
            long repaid = 0;

            // CORRECTION ICI : Utilisation d'une boucle for au lieu de stream avec isRepaid()
            for (Loan loan : loans) {
                if ("PAID".equals(loan.getStatus())) { // ou "REMBOURSE" selon votre BD
                    repaid++;
                }
            }

            probability = total > 0 ? (double) repaid / total : 0.5;
            factors.add(total + " prêt(s) dans l'historique");

            if (repaid == total) {
                factors.add("✓ Tous les prêts ont été remboursés");
                probability = Math.min(1.0, probability * 1.1);
            }
        } else {
            factors.add("ℹ️ Premier prêt - pas d'historique");
        }

        // Ajustement selon le montant
        if (amount > 1000) {
            probability *= 0.8;
            factors.add("⚠️ Montant élevé");
        } else if (amount < 100) {
            probability *= 1.1;
            factors.add("✓ Petit montant");
        }

        probability = Math.min(1.0, Math.max(0.1, probability));

        String level;
        if (probability > 0.7) level = "ÉLEVÉE";
        else if (probability > 0.4) level = "MOYENNE";
        else level = "FAIBLE";

        return new RepaymentProbability(probability, level, factors);
    }

    @Override
    public SuggestedTerms suggestTerms(LoanRequest request, double trustScore) {
        int duration;
        double amount;
        String warning = null;

        if (trustScore >= 70) {
            duration = 30;
            amount = request.getAmount();
        }
        else if (trustScore >= 50) {
            duration = 15;
            amount = request.getAmount() * 0.7;
            warning = "⚠️ Durée réduite recommandée";
        }
        else {
            duration = 7;
            amount = request.getAmount() * 0.4;
            warning = "⚠️ RISQUE ÉLEVÉ - Montant fortement réduit";
        }

        List<Installment> installments = new ArrayList<>();
        if (trustScore < 60) {
            // Plan de remboursement en plusieurs fois
            double half = amount / 2;
            installments.add(new Installment(LocalDate.now().plusDays(duration / 2), half));
            installments.add(new Installment(LocalDate.now().plusDays(duration), half));
        } else {
            installments.add(new Installment(LocalDate.now().plusDays(duration), amount));
        }

        return new SuggestedTerms(duration, amount, installments, 5, warning);
    }

    // ======================= MÉTHODES PRIVÉES =======================

    private double calculateRepaymentScore(List<Loan> loans) {
        if (loans.isEmpty()) return 0.5;
        long total = loans.size();
        long onTime = 0;

        for (Loan loan : loans) {
            // Vérifier si le prêt est remboursé
            if ("PAID".equals(loan.getStatus())) {
                // Vérifier si c'était dans les temps (si vous avez les dates)
                onTime++;
            }
        }

        return total > 0 ? (double) onTime / total : 0.5;
    }

    private double calculateRelationshipDuration(int id1, int id2) {
        List<Loan> loans = loanService.getLoansBetweenUsers(id1, id2);
        return Math.min(1.0, loans.size() / 10.0);
    }

    private double calculateLoanFrequency(int borrowerId, int lenderId) {
        List<Loan> loans = loanService.getLoansBetweenUsers(borrowerId, lenderId);
        if (loans.size() < 3) return 0.5;
        if (loans.size() > 10) return 0.3;
        if (loans.size() > 5) return 0.6;
        return 0.8;
    }

    private double calculateFinancialStability(int userId) {
        List<Transaction> transactions = transactionService.getUserTransactions(userId);
        if (transactions.size() < 10) return 0.5;
        return 0.7;
    }

    private double calculateSocialConnections(int id1, int id2) {
        return 0.5; // À implémenter si vous avez une table d'amis
    }

    private double analyzeSentiment(int id1, int id2) {
        return 0.6; // À implémenter avec analyse des messages
    }

    private String getTrustLevel(double score) {
        if (score >= 80) return "EXCELLENT";
        if (score >= 65) return "BON";
        if (score >= 50) return "MOYEN";
        if (score >= 30) return "FAIBLE";
        return "TRÈS FAIBLE";
    }
}