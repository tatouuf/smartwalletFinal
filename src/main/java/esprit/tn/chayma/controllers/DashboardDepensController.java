package esprit.tn.chayma.controllers;

import esprit.tn.chayma_pi.controllers.WalletLayoutController;
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