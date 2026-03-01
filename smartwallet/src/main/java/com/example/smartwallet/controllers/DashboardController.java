package com.example.smartwallet.controllers;

import com.example.smartwallet.Services.FinancialAnalyticsService;
import com.example.smartwallet.Services.ServiceRecurringPayment;
import com.example.smartwallet.entities.DashboardResult;
import com.example.smartwallet.entities.RecurringPayment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class DashboardController {

    @FXML private TextField incomeField;
    @FXML private TextField balanceField;

    @FXML private Label scoreLabel;
    @FXML private Label riskLabel;
    @FXML private Label forecastLabel;
    @FXML private Label recurringLabel;

    @FXML private Label headlineLabel;

    private final ServiceRecurringPayment recurringService = new ServiceRecurringPayment();
    private final FinancialAnalyticsService analytics = new FinancialAnalyticsService();

    private DashboardResult lastResult;

    @FXML
    public void initialize() {
        // Defaults (getText() is usually "", not null)
        if (incomeField != null && incomeField.getText().isBlank()) incomeField.setText("1200");
        if (balanceField != null && balanceField.getText().isBlank()) balanceField.setText("300");

        refreshDashboard();
    }

    @FXML
    public void refreshDashboard() {
        try {
            double monthlyBudget = parseDoubleOrZero(incomeField.getText());
            double savingsGoal = parseDoubleOrZero(balanceField.getText());

            List<RecurringPayment> recurring = recurringService.recuperer();
            lastResult = analytics.compute(monthlyBudget, savingsGoal, recurring);

            scoreLabel.setText(lastResult.getScore() + "/100");
            riskLabel.setText("Risk: " + lastResult.getRiskLevel());
            forecastLabel.setText(String.format("Remaining: %.2f TND", lastResult.getRemainingThisMonth()));
            recurringLabel.setText(String.format("Recurring total: %.2f TND", lastResult.getRecurringMonthly()));

        } catch (SQLException e) {
            headlineLabel.setText("DB Error: " + e.getMessage());
        } catch (Exception e) {
            headlineLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void saveProfile() {
        refreshDashboard();
    }

    @FXML
    private void goRecurring() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/smartwallet/recurring-view.fxml")
            );
            Scene scene = new Scene(loader.load());

            // Ensure styles persist when navigating
            scene.getStylesheets().add(
                    getClass().getResource("/com/example/smartwallet/Styles.css").toExternalForm()
            );

            Stage stage = (Stage) incomeField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            headlineLabel.setText("Navigation error: " + e.getMessage());
        }
    }

    private double parseDoubleOrZero(String s) {
        try {
            if (s == null) return 0;
            s = s.trim().replace(",", ".");
            if (s.isBlank()) return 0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
}