package esprit.tn.chayma.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadPage("dashboard.fxml");
    }

    private void loadPage(String fxml) {
        try {
            Parent page = FXMLLoader.load(
                    getClass().getResource("/fxml/wallet/" + fxml)
            );
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard() {
        loadPage("dashboard.fxml");
    }

    @FXML
    private void showDepenses() {
        loadPage("depenses.fxml");
    }

    @FXML
    private void showPlanning() {
        loadPage("plannings.fxml");   // ✅ avec S
    }
}