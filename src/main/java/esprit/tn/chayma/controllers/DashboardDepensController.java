package esprit.tn.chayma.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;

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
    private BarChart<?, ?> depensesParMoisChart;

    @FXML
    private LineChart<?, ?> evolutionDepensesChart;

    @FXML
    public void initialize() {
        System.out.println("Dashboard chargé");
    }

    @FXML
    public void goPlannings() {
        System.out.println("Plannings cliqué (navigation temporaire)");
        // TODO: implémenter navigation via WalletLayoutController
    }

    @FXML
    public void goBudget() {
        System.out.println("Budget cliqué (navigation temporaire)");
        // TODO: implémenter navigation via WalletLayoutController
    }

    @FXML
    public void goDepenses() {
        System.out.println("Dashboard cliqué (navigation temporaire)");
        // TODO: implémenter navigation via WalletLayoutController
    }

    @FXML
    public void goAdvisor() {
        System.out.println("Advisor cliqué (navigation temporaire)");
        // TODO: implémenter navigation via WalletLayoutController
    }

    @FXML
    public void goSettings() {
        System.out.println("Settings cliqué (navigation temporaire)");
        // TODO: implémenter navigation via WalletLayoutController
    }
}