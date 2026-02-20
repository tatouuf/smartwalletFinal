package com.example.smartwallet.service;

import com.example.smartwallet.model.Depense;
import dao.DepenseDAO;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionParserService {

    private final FinancialAdvisorService advisorService = new FinancialAdvisorService();
    private final DepenseDAO depenseDAO = new DepenseDAO();
    private final int userId;

    public QuestionParserService(int userId) {
        this.userId = userId;
    }

    public String answerQuestion(String question) {
        String lowerCaseQuestion = question.toLowerCase();

        // Pattern 1: Simulation d'achat
        // "Puis-je acheter un [objet] à [montant] TND ?"
        Pattern purchasePattern = Pattern.compile("acheter un .* à (\\d+\\.?\\d*)");
        Matcher purchaseMatcher = purchasePattern.matcher(lowerCaseQuestion);
        if (purchaseMatcher.find()) {
            double amount = Double.parseDouble(purchaseMatcher.group(1));
            FinancialAdvisorService.FinancialAdvice advice = advisorService.getPurchaseAdvice(userId, amount);
            return advice.advice;
        }

        // Pattern 2: Plus grosse dépense
        // "Quelle a été ma plus grosse dépense ce mois-ci ?"
        if (lowerCaseQuestion.contains("plus grosse dépense")) {
            LocalDate now = LocalDate.now();
            Depense largestExpense = depenseDAO.getLargestExpenseForMonth(userId, now.getMonthValue(), now.getYear());
            if (largestExpense != null) {
                return String.format(
                    "Votre plus grosse dépense ce mois-ci était pour '%s' dans la catégorie '%s', pour un montant de %.2f TND.",
                    largestExpense.getDescription(),
                    largestExpense.getCategorie(),
                    largestExpense.getMontant()
                );
            } else {
                return "Je n'ai trouvé aucune dépense pour ce mois-ci.";
            }
        }
        
        // Pattern 3: Dépenses totales pour une catégorie
        // "Combien ai-je dépensé en [catégorie] ce mois-ci ?"
        Pattern categoryTotalPattern = Pattern.compile("combien ai-je dépensé en (\\w+)");
        Matcher categoryTotalMatcher = categoryTotalPattern.matcher(lowerCaseQuestion);
        if(categoryTotalMatcher.find()){
            String category = categoryTotalMatcher.group(1);
            LocalDate now = LocalDate.now();
            double total = depenseDAO.getTotalDepensesCategorieMois(userId, category, now.getMonthValue(), now.getYear());
             return String.format("Vous avez dépensé %.2f TND en '%s' ce mois-ci.", total, category);
        }


        // Réponse par défaut si aucune question n'est reconnue
        return "Je ne suis pas sûr de comprendre votre question. Vous pouvez me demander, par exemple :\n" +
               "- 'Puis-je acheter un téléphone à 1500 TND ?'\n" +
               "- 'Quelle a été ma plus grosse dépense ce mois-ci ?'\n" +
               "- 'Combien ai-je dépensé en alimentation ce mois-ci ?'";
    }
}
