package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.services.DepenseService;
import esprit.tn.chayma.services.PlanningService;
import esprit.tn.chayma_pi.controllers.WalletLayoutController;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardDepensController {

    @FXML
    private Label totalDepensesLabel;

    @FXML
    private Label depensesMoisLabel;

    @FXML
    private Label totalBudgetsLabel;

    @FXML
    private Label totalPlanningsLabel;

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

    private final DepenseService depenseService = new DepenseService();
    private final PlanningService planningService = new PlanningService();

    @FXML
    public void initialize() {
        System.out.println("Dashboard chargé");
        try {
            updateDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDashboard() {
        List<Depense> depenses = Collections.emptyList();
        try {
            depenses = depenseService.getAllByUser(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTotals(depenses);
        updatePieChart(depenses);
        updateBarChart(depenses);
        updateLineChart(depenses);
    }

    private void updateTotals(List<Depense> depenses) {
        double total = depenses.stream().mapToDouble(Depense::getMontant).sum();
        totalDepensesLabel.setText(String.format("Total dépenses : %.2f DT", total));

        LocalDate now = LocalDate.now();
        double moisTotal = depenses.stream()
                .filter(d -> d.getDateDepense() != null && d.getDateDepense().getMonthValue() == now.getMonthValue() && d.getDateDepense().getYear() == now.getYear())
                .mapToDouble(Depense::getMontant).sum();
        depensesMoisLabel.setText(String.format("Dépenses ce mois : %.2f DT", moisTotal));

        // total plannings (count)
        try {
            int countPlannings = planningService.getAllByUser(0).size();
            totalPlanningsLabel.setText("Plannings: " + countPlannings);
        } catch (Exception e) {
            totalPlanningsLabel.setText("Plannings: -");
        }

        // budgets and prediction placeholders
        totalBudgetsLabel.setText("Budgets: -");
        budgetUtiliseLabel.setText(String.format("Utilisé: %.2f DT", moisTotal));
        predictionDepensesLabel.setText("Prévision: -");
    }

    private void updatePieChart(List<Depense> depenses) {
        if (depenses == null || depenses.isEmpty()) {
            depensesCategorieChart.getData().clear();
            return;
        }
        Map<String, Double> sumByCat = depenses.stream()
                .collect(Collectors.groupingBy(d -> d.getCategorie() == null ? "Autres" : d.getCategorie(), Collectors.summingDouble(Depense::getMontant)));

        depensesCategorieChart.getData().clear();
        for (Map.Entry<String, Double> e : sumByCat.entrySet()) {
            PieChart.Data d = new PieChart.Data(e.getKey(), e.getValue());
            depensesCategorieChart.getData().add(d);
        }
    }

    private void updateBarChart(List<Depense> depenses) {
        depensesParMoisChart.getData().clear();
        if (depenses == null || depenses.isEmpty()) return;

        // compute last 6 months totals
        LocalDate now = LocalDate.now();
        List<LocalDate> months = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            months.add(now.minusMonths(i).withDayOfMonth(1));
        }

        Map<String, Double> totals = new LinkedHashMap<>();
        for (LocalDate m : months) {
            int mm = m.getMonthValue();
            int yy = m.getYear();
            double sum = depenses.stream()
                    .filter(d -> d.getDateDepense() != null && d.getDateDepense().getMonthValue() == mm && d.getDateDepense().getYear() == yy)
                    .mapToDouble(Depense::getMontant).sum();
            String label = m.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + yy;
            totals.put(label, sum);
        }

        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Dépenses");
        totals.forEach((k, v) -> series.getData().add(new javafx.scene.chart.XYChart.Data<>(k, v)));
        depensesParMoisChart.getData().add(series);
    }

    private void updateLineChart(List<Depense> depenses) {
        evolutionDepensesChart.getData().clear();
        if (depenses == null || depenses.isEmpty()) return;

        // monthly totals for last 12 months
        LocalDate now = LocalDate.now();
        List<LocalDate> months = new ArrayList<>();
        for (int i = 11; i >= 0; i--) months.add(now.minusMonths(i).withDayOfMonth(1));

        Map<String, Double> totals = new LinkedHashMap<>();
        double cumulative = 0.0;
        for (LocalDate m : months) {
            int mm = m.getMonthValue();
            int yy = m.getYear();
            double sum = depenses.stream()
                    .filter(d -> d.getDateDepense() != null && d.getDateDepense().getMonthValue() == mm && d.getDateDepense().getYear() == yy)
                    .mapToDouble(Depense::getMontant).sum();
            cumulative += sum;
            String label = m.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + yy;
            totals.put(label, cumulative);
        }

        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Cumulé dépenses");
        totals.forEach((k, v) -> series.getData().add(new javafx.scene.chart.XYChart.Data<>(k, v)));
        evolutionDepensesChart.getData().add(series);
    }

    @FXML
    public void goPlannings() {
        System.out.println("Plannings cliqué");
        if (WalletLayoutController.instance != null) {
            WalletLayoutController.instance.loadPage("/fxml/dep/plannings.fxml");
        }
    }

    @FXML
    public void goBudget() {
        System.out.println("Budget cliqué");
        if (WalletLayoutController.instance != null) {
            WalletLayoutController.instance.loadPage("/fxml/dep/budget.fxml");
        }
    }

    @FXML
    public void goDepenses() {
        System.out.println("Dépenses cliqué");
        if (WalletLayoutController.instance != null) {
            WalletLayoutController.instance.loadPage("/fxml/dep/depenses.fxml");
        }
    }

    @FXML
    public void goAdvisor() {
        System.out.println("Advisor cliqué");
        if (WalletLayoutController.instance != null) {
            WalletLayoutController.instance.loadPage("/fxml/dep/advisor.fxml");
        }
    }

    @FXML
    public void goSettings() {
        System.out.println("Settings cliqué");
        if (WalletLayoutController.instance != null) {
            WalletLayoutController.instance.loadPage("/fxml/dep/setting.fxml");
        }
    }
}