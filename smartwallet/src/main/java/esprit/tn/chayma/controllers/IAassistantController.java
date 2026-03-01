package esprit.tn.chayma.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class IAassistantController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField inputField;

    // Données simulées (à connecter plus tard à ta base de données)
    private double depensesTotal = 1000;
    private double budgetTotal = 1500;
    private double objectifEpargne = 200;

    @FXML
    void handleSend() {

        String message = inputField.getText();

        if (message == null || message.isEmpty()) {
            return;
        }

        chatArea.appendText("👤 Vous : " + message + "\n");

        String reponse = analyserMessage(message);

        chatArea.appendText("🤖 IA : " + reponse + "\n\n");

        inputField.clear();
    }

    private String analyserMessage(String message) {

        message = message.toLowerCase();

        if (message.contains("planning")) {
            return genererPlanningIntelligent();
        }

        if (message.contains("shopping")) {
            return genererPlanningShopping();
        }

        if (message.contains("alimentation")) {
            return genererPlanningAlimentaire();
        }

        if (message.contains("budget")) {
            return analyserBudget();
        }

        return "Je peux vous aider avec un planning shopping, alimentaire ou analyser votre budget.";
    }

    // 🔥 Analyse budgétaire intelligente
    private String analyserBudget() {

        double budgetRestant = budgetTotal - depensesTotal;

        return "📊 Analyse Budgétaire :\n" +
                "- Budget total : " + budgetTotal + "€\n" +
                "- Dépenses actuelles : " + depensesTotal + "€\n" +
                "- Budget restant : " + budgetRestant + "€\n\n" +
                (budgetRestant < objectifEpargne
                        ? "⚠️ Attention : vous risquez de ne pas atteindre votre objectif d'épargne."
                        : "✅ Vous êtes sur la bonne voie pour atteindre votre objectif d'épargne.");
    }

    // 🛒 Planning shopping intelligent
    private String genererPlanningShopping() {

        double budgetRestant = budgetTotal - depensesTotal;

        if (budgetRestant > 400) {
            return "🛒 Planning Shopping (Confortable) :\n" +
                    "- Supermarché premium : 120€\n" +
                    "- Vêtements : 100€\n" +
                    "- Loisirs : 80€\n" +
                    "- Épargne : 100€";
        }

        if (budgetRestant > 200) {
            return "🛒 Planning Shopping (Optimisé) :\n" +
                    "- Supermarché standard : 90€\n" +
                    "- Vêtements essentiels : 50€\n" +
                    "- Loisirs limités : 30€\n" +
                    "- Épargne : 80€";
        }

        return "🛒 Planning Shopping (Économique) :\n" +
                "- Courses strictes nécessaires : 70€\n" +
                "- Pas d’achats vêtements\n" +
                "- Pas de loisirs\n" +
                "- Priorité épargne : 50€";
    }

    // 🥗 Planning alimentaire intelligent
    private String genererPlanningAlimentaire() {

        double budgetRestant = budgetTotal - depensesTotal;

        if (budgetRestant > 300) {
            return "🥗 Planning Alimentaire Semaine :\n" +
                    "Lundi : Poulet + Légumes\n" +
                    "Mardi : Poisson + Riz\n" +
                    "Mercredi : Pâtes + Viande\n" +
                    "Jeudi : Salade composée\n" +
                    "Vendredi : Pizza maison\n" +
                    "Weekend : Repas libre\n\n" +
                    "Budget estimé : 120€";
        }

        if (budgetRestant > 150) {
            return "🥗 Planning Alimentaire Économique :\n" +
                    "Lundi : Lentilles\n" +
                    "Mardi : Omelette\n" +
                    "Mercredi : Pâtes\n" +
                    "Jeudi : Riz + Légumes\n" +
                    "Vendredi : Poulet\n\n" +
                    "Budget estimé : 80€";
        }

        return "🥗 Planning Alimentaire Strict :\n" +
                "Repas simples à base de riz, pâtes, légumes\n" +
                "Réduction des protéines coûteuses\n\n" +
                "Budget estimé : 50€";
    }

    // 📅 Planning global intelligent
    private String genererPlanningIntelligent() {

        double budgetRestant = budgetTotal - depensesTotal;

        return "📅 Planning Global Personnalisé :\n\n" +
                genererPlanningShopping() + "\n\n" +
                genererPlanningAlimentaire() + "\n\n" +
                "💡 Conseil IA : " +
                (budgetRestant < 200
                        ? "Réduisez les dépenses non essentielles ce mois-ci."
                        : "Vous pouvez maintenir un équilibre confortable.");
    }
}