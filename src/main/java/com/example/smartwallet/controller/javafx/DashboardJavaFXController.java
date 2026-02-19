package com.example.smartwallet.controller.javafx;

import dao.BudgetDAO;
import dao.DepenseDAO;
import dao.PlanningDAO;
import com.example.smartwallet.TabManager;
import com.example.smartwallet.model.Budget;
import com.example.smartwallet.model.Depense;
import com.example.smartwallet.model.Planning;
import com.example.smartwallet.service.ExpensePredictionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardJavaFXController {

    @FXML
    private Label totalDepensesLabel;
    @FXML
    private Label totalBudgetsLabel;
    @FXML
    private Label totalPlanningsLabel;
    @FXML
    private Label depensesMoisLabel;
    @FXML
    private Label budgetUtiliseLabel;
    @FXML
    private Label predictionDepensesLabel;
    @FXML
    private PieChart depensesCategorieChart;
    @FXML
    private BarChart<String, Number> depensesParMoisChart;
    @FXML
    private LineChart<String, Number> evolutionDepensesChart;

    private DepenseDAO depenseDAO = new DepenseDAO();
    private BudgetDAO budgetDAO = new BudgetDAO();
    private PlanningDAO planningDAO = new PlanningDAO();
    private ExpensePredictionService predictionService = new ExpensePredictionService();
    private int userId = 1; // ID du user connecté

    @FXML
    public void initialize() {
        chargerDonneesDashboard();
    }

    private void chargerDonneesDashboard() {
        // Charger les totaux
        double totalDepenses = depenseDAO.getTotalDepenses(userId);
        double totalBudgets = budgetDAO.getTotalBudgets(userId);
        int totalPlannings = planningDAO.getTotalPlannings(userId);

        totalDepensesLabel.setText(String.format("%.2f DT", totalDepenses));
        totalBudgetsLabel.setText(String.format("%.2f DT", totalBudgets));
        totalPlanningsLabel.setText(String.valueOf(totalPlannings));

        // Dépenses du mois en cours
        LocalDate now = LocalDate.now();
        double depensesMois = depenseDAO.getTotalDepensesMois(userId, now.getMonthValue(), now.getYear());
        depensesMoisLabel.setText(String.format("%.2f DT", depensesMois));

        // Montant de budget utilisé
        List<Budget> budgetsMois = budgetDAO.obtenirBudgetsMois(userId, now.getMonthValue(), now.getYear());
        double montantBudgetUtilise = budgetsMois.stream().mapToDouble(Budget::getMontantActuel).sum();
        budgetUtiliseLabel.setText(String.format("%.2f DT", montantBudgetUtilise));

        // Charger la prédiction IA
        double prediction = predictionService.predictNextMonthExpenses(userId);
        predictionDepensesLabel.setText(String.format("%.2f DT", prediction));

        // Graphique des dépenses par catégorie (Pie Chart)
        chargerGraphiquePieChart();

        // Graphique des dépenses par mois (Bar Chart)
        chargerGraphiqueBarChart();

        // Graphique d'évolution des dépenses (Line Chart)
        chargerGraphiqueLineChart();
    }

    private void chargerGraphiquePieChart() {
        List<Depense> depenses = depenseDAO.obtenirToutesDepenses(userId);
        Map<String, Double> depensesParCategorie = depenses.stream()
                .collect(Collectors.groupingBy(Depense::getCategorie, Collectors.summingDouble(Depense::getMontant)));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        depensesParCategorie.forEach((categorie, montant) ->
                pieChartData.add(new PieChart.Data(categorie, montant))
        );

        depensesCategorieChart.setData(pieChartData);
        depensesCategorieChart.setTitle("Dépenses par Catégorie");
    }

    private void chargerGraphiqueBarChart() {
        LocalDate now = LocalDate.now();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Dépenses mensuelles");

        for (int i = 1; i <= 12; i++) {
            double montant = depenseDAO.getTotalDepensesMois(userId, i, now.getYear());
            series.getData().add(new XYChart.Data<>(getMonthName(i), montant));
        }

        depensesParMoisChart.getData().clear();
        depensesParMoisChart.getData().add(series);
        depensesParMoisChart.setTitle("Dépenses par Mois");
    }

    private void chargerGraphiqueLineChart() {
        List<Depense> depenses = depenseDAO.obtenirToutesDepenses(userId);
        Map<LocalDate, Double> depensesParJour = depenses.stream()
                .collect(Collectors.groupingBy(Depense::getDateDepense, Collectors.summingDouble(Depense::getMontant)));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Évolution des dépenses");

        depensesParJour.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue())));

        evolutionDepensesChart.getData().clear();
        evolutionDepensesChart.getData().add(series);
        evolutionDepensesChart.setTitle("Évolution des dépenses");
    }

    private String getMonthName(int mois) {
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        return months[mois - 1];
    }

    // -------------------------------
    // MÉTHODE DE NAVIGATION VERS BUDGET
    // -------------------------------
    @FXML
    private void goToBudget(ActionEvent event) {
        // Essayer via le TabManager
        boolean ok = TabManager.showView("/com/example/smartwallet/budget-view.fxml", "Budgets");
        if (ok) return;

        // Fallback : charger la vue et l'insérer dans le BorderPane racine si possible
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/budget-view.fxml"));
            Parent content = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            if (stage.getScene() != null && stage.getScene().getRoot() instanceof BorderPane) {
                BorderPane root = (BorderPane) stage.getScene().getRoot();
                root.setCenter(content);
            } else {
                stage.setScene(new Scene(content));
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
