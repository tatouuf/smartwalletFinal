package esprit.tn.souha_pi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdvisorController {

    @FXML
    private TextArea conversationArea;

    @FXML
    private TextField questionField;

    @FXML
    private void initialize() {
        System.out.println("✅ AdvisorController initialized");
        if (conversationArea != null) {
            conversationArea.setText("🤖 Advisor IA prêt. Posez votre question ci-dessous.\n\n");
        } else {
            System.out.println("❌ conversationArea is NULL in initialize()");
        }
    }

    @FXML
    private void handleAsk() {
        System.out.println("📝 handleAsk() called");

        String userQuestion = questionField.getText();

        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            System.out.println("❌ Question vide");
            return;
        }

        System.out.println("📤 User asked: " + userQuestion);

        // Ajouter question utilisateur
        conversationArea.appendText("👤 Vous : " + userQuestion + "\n");

        // Réponse générée
        String response = generateResponse(userQuestion);

        conversationArea.appendText("🤖 Advisor : " + response + "\n\n");

        // Effacer le champ
        questionField.clear();

        System.out.println("✅ Response displayed");
    }

    private String generateResponse(String message) {
        message = message.toLowerCase();

        if (message.contains("solde") || message.contains("balance") || message.contains("argent")) {
            return "Pour voir votre solde, allez dans Dashboard.";
        }

        if (message.contains("carte") || message.contains("visa") || message.contains("master")) {
            return "Les cartes sont listées dans 'Mes Cartes'.";
        }

        if (message.contains("prêt") || message.contains("loan")) {
            return "Vous pouvez demander un prêt via 'Demander prêt'.";
        }

        if (message.contains("bonjour") || message.contains("salut")) {
            return "Bonjour ! Comment puis-je vous aider ?";
        }

        return "Désolé, je n'ai pas bien compris. Reformulez votre question.";
    }
}

