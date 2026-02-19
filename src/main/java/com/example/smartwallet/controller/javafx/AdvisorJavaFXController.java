package com.example.smartwallet.controller.javafx;

import com.example.smartwallet.service.FinancialAdvisorService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class AdvisorJavaFXController {

    @FXML
    private TextField purchaseAmountField;
    @FXML
    private Button analyzeButton;
    @FXML
    private TextArea adviceTextArea;
    @FXML
    private Label resultLabel;

    private final FinancialAdvisorService advisorService = new FinancialAdvisorService();
    private final int userId = 1; // Hardcoded user ID

    @FXML
    public void initialize() {
        analyzeButton.setOnAction(e -> analyzePurchase());
    }

    private void analyzePurchase() {
        String amountText = purchaseAmountField.getText();
        if (amountText.isEmpty()) {
            resultLabel.setText("Veuillez entrer un montant.");
            resultLabel.setTextFill(Color.RED);
            return;
        }

        try {
            double purchaseAmount = Double.parseDouble(amountText);
            FinancialAdvisorService.FinancialAdvice advice = advisorService.getPurchaseAdvice(userId, purchaseAmount);

            adviceTextArea.setText(advice.advice);

            if (advice.canAfford) {
                resultLabel.setText("ACHAT RECOMMANDÉ");
                resultLabel.setTextFill(Color.GREEN);
            } else {
                resultLabel.setText("ACHAT NON RECOMMANDÉ POUR L'INSTANT");
                resultLabel.setTextFill(Color.RED);
            }

        } catch (NumberFormatException e) {
            resultLabel.setText("Le montant doit être un nombre valide.");
            resultLabel.setTextFill(Color.RED);
        }
    }
}
