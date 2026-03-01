package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.services.BudgetService;
import esprit.tn.chayma.services.DepenseService;
import esprit.tn.chayma.services.PlanningService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DashboardDepensController {

    private static final Logger logger = Logger.getLogger(DashboardDepensController.class.getName());

    @FXML
    private VBox sidebar;

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
    private Button planningsButton;

    @FXML
    private Button depensesButton;

    @FXML
    private Button budgetsButton;

    @FXML
    private Button notificationsButton;

    @FXML
    private Button parametresButton;

    @FXML
    private PieChart depensesCategorieChart;

    @FXML
    private BarChart<String, Number> depensesParMoisChart;

    @FXML
    private LineChart<String, Number> evolutionDepensesChart;

    private final DepenseService depenseService = new DepenseService();
    private final BudgetService budgetService = new BudgetService();
    private final PlanningService planningService = new PlanningService();

    @FXML
    public void initialize() {
        // Charger et afficher les données initiales
        try {
            DashboardHub.register(this);
            refreshData();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur initialisation dashboard", e);
        }
    }

    // Public method to refresh charts/labels from other controllers
    public void refreshData() {
        Platform.runLater(() -> {
            try {
                List<Depense> depenses = depenseService.getAllByUser(0);
                List<Budget> budgets = budgetService.getAllByUser(0);
                List<Planning> plannings = planningService.getAllByUser(0);

                if (depenses == null) depenses = Collections.emptyList();
                if (budgets == null) budgets = Collections.emptyList();
                if (plannings == null) plannings = Collections.emptyList();

                updateSummaryLabels(depenses, budgets, plannings);
                populatePieChart(depenses);
                populateBarChart(depenses);
                populateLineChart(depenses);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erreur lors du rafraîchissement des données du dashboard", e);
            }
        });
    }

    private void updateSummaryLabels(List<Depense> depenses, List<Budget> budgets, List<Planning> plannings) {
        double totalDepenses = depenses.stream().mapToDouble(Depense::getMontant).sum();
        totalDepensesLabel.setText(String.format("%.2f DT", totalDepenses));

        LocalDate now = LocalDate.now();
        double depensesCeMois = depenses.stream()
                .filter(d -> d.getDateDepense() != null && d.getDateDepense().getMonthValue() == now.getMonthValue() && d.getDateDepense().getYear() == now.getYear())
                .mapToDouble(Depense::getMontant).sum();
        depensesMoisLabel.setText(String.format("%.2f DT", depensesCeMois));

        double totalBudgets = budgets.stream().mapToDouble(Budget::getMontantMax).sum();
        totalBudgetsLabel.setText(String.format("%.2f DT", totalBudgets));

        totalPlanningsLabel.setText(String.valueOf(plannings.size()));

        double budgetUtilise = totalBudgets > 0 ? (totalDepenses / totalBudgets) * 100.0 : 0.0;
        budgetUtiliseLabel.setText(String.format("%.2f %%", budgetUtilise));

        // Simple prediction: moyenne mensuelle des 3 derniers mois
        double prediction = predictNextMonth(depenses);
        predictionDepensesLabel.setText(String.format("%.2f DT", prediction));
    }

    private double predictNextMonth(List<Depense> depenses) {
        LocalDate now = LocalDate.now();
        List<Double> last3 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int month = now.minusMonths(i).getMonthValue();
            int year = now.minusMonths(i).getYear();
            double sum = depenses.stream()
                    .filter(d -> d.getDateDepense() != null && d.getDateDepense().getMonthValue() == month && d.getDateDepense().getYear() == year)
                    .mapToDouble(Depense::getMontant).sum();
            last3.add(sum);
        }
        if (last3.isEmpty()) return 0.0;
        double avg = last3.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        // appliquer un léger facteur de croissance
        return avg * 1.05;
    }

    private void populatePieChart(List<Depense> depenses) {
        Map<String, Double> byCat = depenses.stream()
                .filter(d -> d.getCategorie() != null)
                .collect(Collectors.groupingBy(Depense::getCategorie, Collectors.summingDouble(Depense::getMontant)));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> e : byCat.entrySet()) {
            pieData.add(new PieChart.Data(e.getKey(), e.getValue()));
        }
        depensesCategorieChart.setData(pieData);
    }

    private void populateBarChart(List<Depense> depenses) {
        // total par mois pour l'année en cours
        int year = LocalDate.now().getYear();
        Map<Integer, Double> monthSum = new HashMap<>();
        for (int m = 1; m <= 12; m++) monthSum.put(m, 0.0);

        for (Depense d : depenses) {
            if (d.getDateDepense() == null) continue;
            if (d.getDateDepense().getYear() == year) {
                int m = d.getDateDepense().getMonthValue();
                monthSum.put(m, monthSum.getOrDefault(m, 0.0) + d.getMontant());
            }
        }

        depensesParMoisChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(String.valueOf(year));
        for (int m = 1; m <= 12; m++) {
            String label = LocalDate.of(year, m, 1).getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            series.getData().add(new XYChart.Data<>(label, monthSum.getOrDefault(m, 0.0)));
        }
        depensesParMoisChart.getData().add(series);
    }

    private void populateLineChart(List<Depense> depenses) {
        // Totaux journaliers pour les 30 derniers jours
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);
        Map<LocalDate, Double> daySum = new TreeMap<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) daySum.put(d, 0.0);

        for (Depense dep : depenses) {
            if (dep.getDateDepense() == null) continue;
            LocalDate date = dep.getDateDepense();
            if (!date.isBefore(start) && !date.isAfter(end)) {
                daySum.put(date, daySum.getOrDefault(date, 0.0) + dep.getMontant());
            }
        }

        evolutionDepensesChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Derniers 30 jours");
        for (Map.Entry<LocalDate, Double> e : daySum.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue()));
        }
        evolutionDepensesChart.getData().add(series);
    }

    // Handlers for sidebar buttons
    @FXML
    private void handlePlannings(ActionEvent event) {
        // Charger le FXML des plannings et remplacer la scène principale
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/wallet/plannings.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Plannings");
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de plannings.fxml", e);
            showErrorAlert("Erreur", "Impossible de charger la vue Plannings.");
        }
    }

    @FXML
    private void handleDepenses(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/wallet/depenses.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dépenses");
            stage.show();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de depenses.fxml", e);
            showErrorAlert("Erreur", "Impossible de charger la vue Dépenses.");
        }
    }

    @FXML
    private void handleBudgets(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/wallet/budget.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Budgets");
            stage.show();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de budget.fxml", e);
            showErrorAlert("Erreur", "Impossible de charger la vue Budgets.");
        }
    }

    @FXML
    private void handleNotifications() {
        System.out.println("Notifications clicked");
        // TODO: afficher les notifications
    }


    @FXML
    private void handleSettings(ActionEvent event) {
        System.out.println("Settings clicked");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked");
    }

    @FXML
    private void handleAdvisor(ActionEvent event) {
        System.out.println("Advisor clicked");
    }
    /**
     * Affiche une alerte d'erreur à l'utilisateur
     * @param title Titre de l'alerte
     * @param message Message d'erreur
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
