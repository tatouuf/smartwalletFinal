package com.example.smartwallet.controllers;

import com.example.smartwallet.Services.FinancialAnalyticsService;
import com.example.smartwallet.Services.HuggingFaceAiService;
import com.example.smartwallet.Services.ServiceRecurringPayment;
import com.example.smartwallet.entities.DashboardResult;
import com.example.smartwallet.entities.RecurringPayment;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private TextField incomeField;
    @FXML private TextField balanceField;

    @FXML private Label scoreLabel;
    @FXML private Label riskLabel;
    @FXML private Label forecastLabel;
    @FXML private Label recurringLabel;

    @FXML private Label headlineLabel;
    @FXML private ListView<String> tipsList;

    private final ServiceRecurringPayment recurringService = new ServiceRecurringPayment();
    private final FinancialAnalyticsService analytics = new FinancialAnalyticsService();

    /**
     * Robust HF token resolution:
     * - tries multiple env var names
     * - tries JVM properties too
     * - returns placeholder if missing (so UI can show a friendly message)
     */
    private static String resolveHfKey() {
        String[] envCandidates = new String[] {
                System.getenv("HUGGINGFACE_API_KEY"),
                System.getenv("HF_TOKEN"),
                System.getenv("HUGGINGFACEHUB_API_TOKEN"),
                System.getenv("HUGGING_FACE_HUB_TOKEN"),
                System.getenv("HF_API_KEY")
        };
        for (String k : envCandidates) {
            if (k != null && !k.isBlank()) return k.trim();
        }

        String[] propCandidates = new String[] {
                System.getProperty("huggingface.api.key"),
                System.getProperty("hf.token"),
                System.getProperty("huggingfacehub.api.token"),
                System.getProperty("HF_TOKEN")
        };
        for (String k : propCandidates) {
            if (k != null && !k.isBlank()) return k.trim();
        }

        return "PUT_YOUR_HUGGINGFACE_KEY_HERE";
    }

    // ✅ IMPORTANT: pass the resolved token into the AI service
    private final HuggingFaceAiService ai = new HuggingFaceAiService(resolveHfKey());

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
    private void generateAI() {
        if (lastResult == null) {
            headlineLabel.setText("No data yet. Click Save Profile.");
            return;
        }

        if ("PUT_YOUR_HUGGINGFACE_KEY_HERE".equals(resolveHfKey())) {
            headlineLabel.setText("Set HUGGINGFACE_API_KEY (or HF_TOKEN) then retry.");
            return;
        }

        headlineLabel.setText("Generating tips...");
        tipsList.getItems().clear();

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                String prompt = """
                        You are a professional financial advisor.
                        The user is in Tunisia.
                        Language: French

                        Provide 5 concise, actionable tips based on these metrics:
                        - monthlyBudget: %s TND
                        - savingsGoal: %s TND
                        - score: %d/100
                        - riskLevel: %s
                        - recurringMonthly: %.2f TND
                        - remainingThisMonth: %.2f TND

                        Constraints:
                        - Output bullet points only (one tip per line)
                        - No intro, no outro, no disclaimers
                        """.formatted(
                        String.valueOf(lastResult.getMonthlyBudget()),
                        String.valueOf(lastResult.getSavingsGoal()),
                        lastResult.getScore(),
                        lastResult.getRiskLevel(),
                        lastResult.getRecurringMonthly(),
                        lastResult.getRemainingThisMonth()
                );

                try {
                    // ✅ Method must exist in HuggingFaceAiService
                    return ai.generateFinancialTip(prompt);
                } catch (Exception e) {
                    return "AI Error: " + e.getMessage();
                }
            }
        };

        task.setOnSucceeded(evt -> {
            String tipsText = task.getValue();

            List<String> tips = Arrays.stream(tipsText.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(s -> s.replaceFirst("^[•*\\-]\\s*", "").trim())
                    .collect(Collectors.toList());

            tipsList.getItems().setAll(tips.isEmpty() ? List.of(tipsText) : tips);
            headlineLabel.setText("AI Insights");
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            String msg = ex != null ? ex.getMessage() : "unknown error";
            headlineLabel.setText("AI Error: " + msg);
        });

        Thread t = new Thread(task, "hf-ai-task");
        t.setDaemon(true);
        t.start();
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