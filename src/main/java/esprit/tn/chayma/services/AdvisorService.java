package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.entities.Depense;

import java.util.*;

public class AdvisorService {
    private BudgetService budgetService = new BudgetService();
    private DepenseService depenseService = new DepenseService();

    public String getAdvice(int userId, int mois, int annee) {
        StringBuilder advice = new StringBuilder();
        advice.append("=== AVIS FINANCIER / FINANCIAL ADVICE / النصيحة المالية ===\n\n");

        // Récupérer tous les budgets de l'utilisateur
        List<Budget> budgets = budgetService.getAllByUser(userId);
        if (budgets.isEmpty()) {
            advice.append("❌ Aucun budget défini. Créez d'abord un budget pour recevoir des conseils.\n");
            return advice.toString();
        }

        // Analyser chaque budget
        double totalBudgetMax = 0;
        double totalDepensed = 0;

        for (Budget b : budgets) {
            if (b.getMois() != null && b.getMois() == mois && b.getAnnee() != null && b.getAnnee() == annee) {
                String categorie = b.getCategorie() != null ? b.getCategorie() : "Autre";
                double montantMax = b.getMontantMax();
                totalBudgetMax += montantMax;

                // Calculer les dépenses pour cette catégorie/mois/année
                double montantDepense = calculateExpensesForCategory(userId, categorie, mois, annee);
                totalDepensed += montantDepense;

                // Générer conseil pour cette catégorie
                advice.append(String.format("📌 %s:\n", categorie));
                advice.append(String.format("   Budget: %.2f DT | Dépensé: %.2f DT | Restant: %.2f DT\n",
                        montantMax, montantDepense, Math.max(0, montantMax - montantDepense)));

                if (montantDepense > montantMax) {
                    double depassement = montantDepense - montantMax;
                    advice.append(String.format("   ⚠️ DÉPASSEMENT: %.2f DT (%.0f%% du budget)\n",
                            depassement, (montantDepense / montantMax) * 100));
                    advice.append("   ➜ Réduisez vos achats ou augmentez le budget.\n");
                } else {
                    double remaining = montantMax - montantDepense;
                    double percentageUsed = (montantDepense / montantMax) * 100;
                    advice.append(String.format("   ✅ Dépenses: %.0f%% du budget\n", percentageUsed));

                    if (percentageUsed > 80) {
                        advice.append("   ⚡ Attention: Vous approchez du limite du budget. Réduisez les achats.\n");
                    } else if (percentageUsed < 50) {
                        advice.append(String.format("   💡 Vous pouvez encore dépenser %.2f DT pour cette catégorie.\n", remaining));
                    }
                }
                advice.append("\n");
            }
        }

        // Recommandations globales
        advice.append("\n=== RECOMMANDATIONS GLOBALES / GLOBAL RECOMMENDATIONS ===\n");
        if (totalDepensed > totalBudgetMax) {
            advice.append(String.format("🚨 Vous avez DÉPASSÉ votre budget total de %.2f DT!\n", totalDepensed - totalBudgetMax));
            advice.append("➜ Repérez les catégories avec excédent et réduisez les achats.\n");
        } else {
            double globalRemaining = totalBudgetMax - totalDepensed;
            advice.append(String.format("✅ Budget global utilisé: %.0f%% (Restant: %.2f DT)\n",
                    (totalDepensed / totalBudgetMax) * 100, globalRemaining));
        }

        // Suggestions d'achats à planifier
        advice.append("\n=== PLAN D'ACHATS SUGGÉRÉ / SUGGESTED SHOPPING PLAN ===\n");
        List<String> suggestions = generateShoppingPlan(budgets, mois, annee);
        for (String suggestion : suggestions) {
            advice.append("• " + suggestion + "\n");
        }

        return advice.toString();
    }

    private double calculateExpensesForCategory(int userId, String categorie, int mois, int annee) {
        List<Depense> depenses = depenseService.getAllByUser(userId);
        double total = 0;
        for (Depense d : depenses) {
            if (categorie.equalsIgnoreCase(d.getCategorie()) &&
                d.getDateDepense() != null &&
                d.getDateDepense().getMonthValue() == mois &&
                d.getDateDepense().getYear() == annee) {
                total += d.getMontant();
            }
        }
        return total;
    }

    private List<String> generateShoppingPlan(List<Budget> budgets, int mois, int annee) {
        List<String> suggestions = new ArrayList<>();

        for (Budget b : budgets) {
            if (b.getMois() != null && b.getMois() == mois && b.getAnnee() != null && b.getAnnee() == annee) {
                String categorie = b.getCategorie() != null ? b.getCategorie() : "Autre";
                double montantMax = b.getMontantMax();

                switch (categorie.toLowerCase()) {
                    case "alimentation":
                        suggestions.add("Alimentation: Planifiez vos courses de la semaine pour rester dans les " + montantMax + " DT");
                        suggestions.add("Alimentation: Achetez les produits en promotion et préparez une liste avant de faire les achats");
                        break;
                    case "transport":
                        suggestions.add("Transport: Avec " + montantMax + " DT, planifiez vos trajets et utilisez les transports en commun");
                        suggestions.add("Transport: Comparez les tarifs et achetez des cartes d'abonnement si nécessaire");
                        break;
                    case "logement":
                        suggestions.add("Logement: Vérifiez que votre loyer/charges sont dans les " + montantMax + " DT prévus");
                        break;
                    case "loisirs":
                        suggestions.add("Loisirs: Planifiez vos sorties et activités sans dépasser " + montantMax + " DT");
                        suggestions.add("Loisirs: Cherchez des activités gratuites ou réductions pour économiser");
                        break;
                    case "santé":
                        suggestions.add("Santé: Préservez " + montantMax + " DT pour les dépenses médicales prévues");
                        break;
                    default:
                        suggestions.add(categorie + ": Planifiez vos achats pour ne pas dépasser " + montantMax + " DT");
                }
            }
        }

        return suggestions;
    }
}

