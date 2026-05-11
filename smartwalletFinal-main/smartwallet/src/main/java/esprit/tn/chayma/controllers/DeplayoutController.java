package esprit.tn.chayma.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class DeplayoutController {

    @FXML
    private BorderPane mainContainer;

    public static DeplayoutController instance;

    @FXML
    public void initialize() {
        instance = this;
        System.out.println("DeplayoutController chargé");
        // Initialiser avec le tableau de bord par défaut
        javafx.application.Platform.runLater(this::goDashboard);
    }

    @FXML
    public void goDashboard() {
        loadPage("dep/DashboardDepens.fxml");
    }

    @FXML
    public void goPlannings() {
        loadPage("dep/Plannings.fxml");
    }

    @FXML
    public void goBudget() {
        loadPage("dep/budget.fxml");
    }

    @FXML
    public void goDepenses() {
        loadPage("dep/Depenses.fxml");
    }

    @FXML
    public void goNotifications() {
        loadPage("dep/Notifications.fxml");
    }

    @FXML
    public void goAdvisor() {
        loadPage("dep/advisor.fxml");
    }

    @FXML
    public void goSettings() {
        loadPage("dep/Setting.fxml");
    }

    @FXML
    public void goBack() {
        if (mainContainer.getScene() != null && mainContainer.getScene().getWindow() != null) {
            ((javafx.stage.Stage) mainContainer.getScene().getWindow()).close();
        }
    }

    private void loadPage(String fxmlPath) {
        try {
            String path = "/fxml/" + fxmlPath;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            if (loader.getLocation() == null) {
                System.err.println("FXML non trouvé: " + path);
                return;
            }
            Parent view = loader.load();
            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
