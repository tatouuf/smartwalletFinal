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
        System.out.println("Plannings cliqué");
        WalletLayoutController.instance.loadPage("/fxml/wallet/plannings.fxml");
    }

    @FXML
    public void goBudget() {
        System.out.println("Budget cliqué");
        WalletLayoutController.instance.loadPage("/fxml/wallet/budget.fxml");
    }

    @FXML
    public void goDepenses() {
        System.out.println("Dashboard cliqué");
        WalletLayoutController.instance.loadPage("/fxml/wallet/dashboarddepens.fxml");
    }

    @FXML
    public void goAdvisor() {
        System.out.println("Advisor cliqué");
        WalletLayoutController.instance.loadPage("/fxml/wallet/advisor.fxml");
    }

    @FXML
    public void goSettings() {
        System.out.println("Settings cliqué");
        WalletLayoutController.instance.loadPage("/fxml/wallet/setting.fxml");
    }
}