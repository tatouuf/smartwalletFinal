package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.services.*;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import utils.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdvisorController {

    @FXML private ComboBox<Integer> moisCombo;
    @FXML private ComboBox<Integer> anneeCombo;
    @FXML private TextArea adviceArea;
    @FXML private Button analyzeBtn;
    @FXML private Button refreshBtn;

    // Composants chat
    @FXML private TextArea chatHistory;
    @FXML private TextField questionField;
    @FXML private Button sendBtn;
    @FXML private ProgressIndicator loadingIndicator;

    private AdvisorService advisorService = new AdvisorService();
    private DepenseService depenseService = new DepenseService();
    private BudgetService budgetService = new BudgetService();
    private int currentUserId;

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur
        if (Session.getCurrentUser() != null) {
            currentUserId = Session.getCurrentUser().getId();
        } else {
            currentUserId = 1;
        }

        // Mois
        for (int i = 1; i <= 12; i++) moisCombo.getItems().add(i);
        moisCombo.setValue(LocalDate.now().getMonthValue());

        // Années
        for (int i = 2024; i <= 2028; i++) anneeCombo.getItems().add(i);
        anneeCombo.setValue(LocalDate.now().getYear());

        analyzeBtn.setOnAction(e -> onAnalyze());
        refreshBtn.setOnAction(e -> onAnalyze());

        // Chat
        sendBtn.setOnAction(e -> sendQuestion());
        questionField.setOnAction(e -> sendQuestion());
        loadingIndicator.setVisible(false);
    }

    private void onAnalyze() {
        Integer mois = moisCombo.getValue();
        Integer annee = anneeCombo.getValue();
        if (mois == null || annee == null) {
            DialogUtil.error("Erreur", "Sélectionnez mois et année");
            return;
        }
        String advice = advisorService.getAdvice(currentUserId, mois, annee);
        adviceArea.setText(advice);
    }

    private void sendQuestion() {
        String question = questionField.getText().trim();
        if (question.isEmpty()) return;

        chatHistory.appendText("🧑 Vous : " + question + "\n");
        questionField.clear();

        sendBtn.setDisable(true);
        analyzeBtn.setDisable(true);
        loadingIndicator.setVisible(true);

        String context = getFinancialContext();

        Task<String> aiTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                // Appel direct à la classe AIChatService
                return AIChatService.ask(question, context);
            }
        };

        aiTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                chatHistory.appendText("🤖 IA : " + aiTask.getValue() + "\n\n");
                sendBtn.setDisable(false);
                analyzeBtn.setDisable(false);
                loadingIndicator.setVisible(false);
            });
        });

        aiTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                chatHistory.appendText("❌ Erreur : " + aiTask.getException().getMessage() + "\n\n");
                sendBtn.setDisable(false);
                analyzeBtn.setDisable(false);
                loadingIndicator.setVisible(false);
            });
        });

        new Thread(aiTask).start();
    }

    private String getFinancialContext() {
        List<Depense> depenses = depenseService.getAllByUser(currentUserId);
        List<Budget> budgets = budgetService.getAllByUser(currentUserId);

        double totalDepenses = depenses.stream().mapToDouble(Depense::getMontant).sum();
        double totalBudget = budgets.stream().mapToDouble(Budget::getMontantMax).sum();

        Map<String, Double> depensesParCategorie = depenses.stream()
                .collect(Collectors.groupingBy(Depense::getCategorie,
                        Collectors.summingDouble(Depense::getMontant)));

        Map<String, Double> budgetsParCategorie = budgets.stream()
                .collect(Collectors.toMap(Budget::getCategorie, Budget::getMontantMax, (a,b)->a));

        StringBuilder sb = new StringBuilder();
        sb.append("Résumé financier de l'utilisateur (ID ").append(currentUserId).append(") :\n");
        sb.append("- Dépenses totales : ").append(String.format("%.2f", totalDepenses)).append(" DT\n");
        sb.append("- Budget total : ").append(String.format("%.2f", totalBudget)).append(" DT\n");
        sb.append("- Détail par catégorie :\n");
        for (var entry : depensesParCategorie.entrySet()) {
            String cat = entry.getKey();
            double dep = entry.getValue();
            double bud = budgetsParCategorie.getOrDefault(cat, 0.0);
            sb.append("   * ").append(cat).append(" : dépensé ").append(String.format("%.2f", dep))
                    .append(" DT / budget ").append(String.format("%.2f", bud)).append(" DT\n");
        }
        return sb.toString();
    }
}