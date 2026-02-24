package esprit.tn.chayma_pi.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class WalletLayoutController {
    public static WalletLayoutController instance;

    @FXML
    private BorderPane mainContainer;
    private Scene scene;

    public WalletLayoutController() {
        // instance assigned in initialize() when FXML injection completed
    }

    @FXML
    public void initialize() {
        // L'instance doit être définie après injection des champs FXML
        instance = this;
        System.out.println("WalletLayoutController.initialize() called");
        // Charger la page par défaut (Dashboard)
        loadPage("/fxml/dep/dashboarddepens.fxml");
    }

    public void setMainContainer(BorderPane mainContainer) {
        this.mainContainer = mainContainer;
    }

    public void loadPage(String fxmlPath) {
        if (mainContainer == null) {
            System.out.println("WalletLayoutController: mainContainer not set, cannot load " + fxmlPath);
            return;
        }
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContainer.setCenter(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        if (scene == null && mainContainer != null) {
            scene = mainContainer.getScene();
        }
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    // Navigation handlers expected by the FXML
    @FXML
    public void goDashboard() { loadPage("/fxml/dep/dashboarddepens.fxml"); }

    @FXML
    public void goPlannings() { loadPage("/fxml/dep/plannings.fxml"); }

    @FXML
    public void goBudget() { loadPage("/fxml/dep/budget.fxml"); }

    @FXML
    public void goDepenses() { loadPage("/fxml/dep/depenses.fxml"); }

    @FXML
    public void goNotifications() { loadPage("/fxml/dep/notifications.fxml"); }

    @FXML
    public void goAdvisor() { loadPage("/fxml/dep/advisor.fxml"); }

    @FXML
    public void goSettings() { loadPage("/fxml/dep/setting.fxml"); }
}
