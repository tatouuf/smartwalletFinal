package com.example.smartwallet.controller.javafx;

import com.example.smartwallet.config.PrimaryStageInitializer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WelcomeController {

    @FXML
    private Button btnDepenses;
    @FXML
    private Button btnBudgets;
    @FXML
    private Button btnPlannings;

    @FXML
    public void initialize() {
        btnDepenses.setOnAction(e -> PrimaryStageInitializer.switchToMainScreen("depenses"));
        btnBudgets.setOnAction(e -> PrimaryStageInitializer.switchToMainScreen("budgets"));
        btnPlannings.setOnAction(e -> PrimaryStageInitializer.switchToMainScreen("plannings"));
    }
}
