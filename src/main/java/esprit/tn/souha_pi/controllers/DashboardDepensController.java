package esprit.tn.souha_pi.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

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
        // TODO: Initialiser le tableau de bord des dépenses
    }

    @FXML
    public void goPlannings() {
        WalletLayoutController.instance.loadPage("wallet/plannings.fxml");
    }

    @FXML
    public void goBudget() {
        WalletLayoutController.instance.loadPage("wallet/budget.fxml");
    }

    @FXML
    public void goDepenses() {
        WalletLayoutController.instance.loadPage("wallet/depenses.fxml");
    }

    @FXML
    public void goAdvisor() {
        WalletLayoutController.instance.loadPage("wallet/advisor.fxml");
    }

    @FXML
    public void goSettings() {
        WalletLayoutController.instance.loadPage("wallet/setting.fxml");
    }
}