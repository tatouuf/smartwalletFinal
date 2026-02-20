package com.example.smartwallet.controller.javafx;

import com.example.smartwallet.service.QuestionParserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdvisorJavaFXController {

    @FXML
    private TextField questionField;
    @FXML
    private Button askButton;
    @FXML
    private TextArea answerArea;

    private QuestionParserService questionParser;
    private final int userId = 1; // Hardcoded user ID

    @FXML
    public void initialize() {
        // Initialiser le "cerveau" de l'IA avec l'ID de l'utilisateur
        this.questionParser = new QuestionParserService(userId);
        
        askButton.setOnAction(e -> askQuestion());
        
        // Afficher un message de bienvenue
        answerArea.setText("Bonjour ! Posez-moi une question sur vos finances.\n\n" +
                           "Par exemple :\n" +
                           "- 'Puis-je acheter une voiture à 25000 TND ?'\n" +
                           "- 'Quelle a été ma plus grosse dépense ce mois-ci ?'\n" +
                           "- 'Combien ai-je dépensé en loisirs ce mois-ci ?'");
    }

    private void askQuestion() {
        String question = questionField.getText();
        if (question == null || question.trim().isEmpty()) {
            answerArea.setText("Veuillez d'abord poser une question dans le champ de texte.");
            return;
        }

        // Obtenir la réponse du service d'IA
        String answer = questionParser.answerQuestion(question);

        // Afficher la réponse
        answerArea.setText(answer);
    }
}
