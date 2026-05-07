package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service pour gérer les notifications intelligentes
 * Détecte automatiquement les situations importantes:
 * - Budget presque atteint (80%)
 * - Dépassement de budget
 * - Objectifs de planning non respectés
 * - Factures récurrentes à venir
 */
public class IntelligentNotificationService {

    private final NotificationService notificationService;
    private final BudgetService budgetService;
    private final PlanningService planningService;
    private final Connection conn;

    public IntelligentNotificationService() {
        this.notificationService = new NotificationService();
        this.budgetService = new BudgetService();
        this.planningService = PlanningService.getInstance(); // ✅ Utiliser getInstance()
        this.conn = MyDataBase.getInstance().getConnection();
    }

    /**
     * Vérifie tous les budgets pour un utilisateur et crée des notifications si nécessaire
     */
    public void checkAllBudgets(int userId) {
        try {
            // Récupérer tous les budgets de l'utilisateur
            // Code correct
            //List<Depense> depenses = depenseService.getAllDepenses();
// ou
            List<Budget> budgets = budgetService.getAll();

            for (Budget budget : budgets) {
                checkBudgetStatus(userId, budget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie le statut d'un budget spécifique
     */
    public void checkBudgetStatus(int userId, Budget budget) {
        if (budget == null || budget.getMontantMax() <= 0) return;

        double utilise = budget.getMontantActuel();
        double max = budget.getMontantMax();
        double pourcentage = (utilise / max) * 100;

        // Dépassement complet
        if (utilise > max) {
            String excess = String.format("%.2f", utilise - max);
            String msg = String.format("⚠️ DÉPASSEMENT DU BUDGET: %s\n" +
                            "Dépenses: %.2f DT | Budget: %.2f DT\n" +
                            "Dépassement: +%.2f DT",
                    budget.getCategorie(), utilise, max, Double.parseDouble(excess));
            notificationService.notifyAlert(userId, "Dépassement Budget", msg);
        }
        // Budget presque atteint (80-100%)
        else if (pourcentage >= 80) {
            String msg = String.format("⚠️ BUDGET PRESQUE ATTEINT: %s\n" +
                            "Utilisation: %.0f%% (%.2f DT sur %.2f DT)",
                    budget.getCategorie(), pourcentage, utilise, max);
            notificationService.notifyAlert(userId, "Budget Critique", msg);
        }
        // Budget à 50-80%
        else if (pourcentage >= 50) {
            String msg = String.format("📊 BUDGET À MI-CHEMIN: %s\n" +
                            "Utilisation: %.0f%% (%.2f DT sur %.2f DT)",
                    budget.getCategorie(), pourcentage, utilise, max);
            notificationService.notifyInfo(userId, "Budget Suivi", msg);
        }
    }

    /**
     * Vérifie les objectifs de planning et crée des notifications
     */
    public void checkPlanningGoals(int userId) {
        try {
            // ✅ CORRIGÉ: utiliser getPlanningsByUser() au lieu de getAllByUser()
            List<Planning> plannings = planningService.getPlanningsByUser(userId);

            for (Planning planning : plannings) {
                checkPlanningStatus(userId, planning);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie le statut d'un planning spécifique
     */
    public void checkPlanningStatus(int userId, Planning planning) {
        if (planning == null) return;

        // Vérifier si l'épargne prévue est atteinte
        if (planning.getEpargnePrevue() > 0) {
            // Calculer l'épargne réelle pour ce mois/année
            double savingsActual = calculatePlanningSavings(userId, planning);
            double savingsTarget = planning.getEpargnePrevue();
            double percentage = (savingsActual / savingsTarget) * 100;

            if (percentage >= 100) {
                String msg = String.format("💰 OBJECTIF D'ÉPARGNE ATTEINT: %s\n" +
                                "Épargne: %.2f DT (objectif: %.2f DT)",
                        planning.getNom(), savingsActual, savingsTarget);
                notificationService.notifySavingsGoal(userId, planning.getNom(), savingsActual, savingsTarget);
            } else if (percentage >= 75) {
                String msg = String.format("💪 PRESQUE ATTEINT: %s\n" +
                                "Épargne: %.2f DT sur %.2f DT (%.0f%%)",
                        planning.getNom(), savingsActual, savingsTarget, percentage);
                notificationService.notifyInfo(userId, "Objectif Proche", msg);
            }
        }

        // Vérifier le revenu prévu
        if (planning.getRevenuPrevu() > 0) {
            double revenuActual = calculatePlanningRevenue(userId, planning);
            if (revenuActual < planning.getRevenuPrevu()) {
                String msg = String.format("📊 REVENU INSUFFISANT: %s\n" +
                                "Revenu réel: %.2f DT (prévu: %.2f DT)",
                        planning.getNom(), revenuActual, planning.getRevenuPrevu());
                notificationService.notifyAlert(userId, "Revenu Non Atteint", msg);
            }
        }
    }

    /**
     * Vérifie les dépenses récurrentes (factures mensuelles)
     * Les factures sont identifiées par le même nom/catégorie à chaque mois
     */
    public void checkRecurringBills(int userId) {
        try {
            // Correction : utiliser categorie_id avec JOIN sur categories
            String sql = "SELECT c.nom as categorie_nom, COUNT(*) as count, AVG(d.montant) as avg_montant " +
                    "FROM depenses d " +
                    "LEFT JOIN categories c ON d.categorie_id = c.id " +
                    "WHERE d.user_id = ? " +
                    "GROUP BY d.categorie_id HAVING count > 1 " +
                    "ORDER BY c.nom";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String categorie = rs.getString("categorie_nom");
                    if (categorie == null) categorie = "Autre";
                    int count = rs.getInt("count");
                    double avgMontant = rs.getDouble("avg_montant");

                    if (count >= 3) {
                        String lastDepenseSql = "SELECT MAX(d.date_depense) as last_date FROM depenses d " +
                                "LEFT JOIN categories c ON d.categorie_id = c.id " +
                                "WHERE d.user_id = ? AND c.nom = ?";
                        try (PreparedStatement lastPs = conn.prepareStatement(lastDepenseSql)) {
                            lastPs.setInt(1, userId);
                            lastPs.setString(2, categorie);
                            ResultSet lastRs = lastPs.executeQuery();

                            if (lastRs.next()) {
                                java.sql.Date lastDate = lastRs.getDate("last_date");
                                if (lastDate != null) {
                                    LocalDate last = lastDate.toLocalDate();
                                    LocalDate nextExpected = last.plusMonths(1);
                                    long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), nextExpected);

                                    if (daysUntil > 0 && daysUntil <= 7) {
                                        notificationService.notifyRecurringBill(userId,
                                                categorie, avgMontant, categorie, (int) daysUntil);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calcule l'épargne réelle pour un planning donné
     */
    private double calculatePlanningSavings(int userId, Planning planning) {
        try {
            // La formule simple: Revenu - Dépenses = Épargne
            double revenu = calculatePlanningRevenue(userId, planning);
            double depenses = calculatePlanningExpenses(userId, planning);
            return Math.max(0, revenu - depenses); // L'épargne ne peut pas être négative
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Calcule le revenu réel pour un planning
     */
    private double calculatePlanningRevenue(int userId, Planning planning) {
        // À implémenter selon votre structure de données
        // Pour l'instant, retourne le revenu prévu
        return planning.getRevenuPrevu();
    }

    /**
     * Calcule les dépenses pour un planning donné (pour un mois/année)
     */
    private double calculatePlanningExpenses(int userId, Planning planning) {
        try {
            String sql = "SELECT SUM(montant) as total FROM depenses WHERE user_id = ? " +
                    "AND MONTH(date_depense) = ? AND YEAR(date_depense) = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                if (planning.getMois() != null) {
                    ps.setInt(2, planning.getMois());
                } else {
                    ps.setInt(2, LocalDate.now().getMonthValue());
                }
                if (planning.getAnnee() != null) {
                    ps.setInt(3, planning.getAnnee());
                } else {
                    ps.setInt(3, LocalDate.now().getYear());
                }

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Object total = rs.getObject("total");
                    return total != null ? rs.getDouble("total") : 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lance une vérification complète de tous les indicateurs pour un utilisateur
     * À appeler périodiquement (ex: au démarrage, toutes les heures)
     */
    public void runFullCheck(int userId) {
        checkAllBudgets(userId);
        checkPlanningGoals(userId);
        checkRecurringBills(userId);
    }
}