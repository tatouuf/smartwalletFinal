package esprit.tn.chayma.controllers;

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

    @FXML
    public void initialize() {
        instance = this;
        System.out.println("WalletLayout chargé");
        loadPage("/fxml/wallet/dashboarddepens.fxml"); // page par défaut
    }

    public void loadPage(String fxmlPath) {
        try {
            System.out.println("Chargement de la page : " + fxmlPath);
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContainer.setCenter(page);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur chargement FXML : " + fxmlPath);
        }
    }

    public Scene getScene() {
        if (scene == null) {
            scene = mainContainer.getScene();
        }
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    // ================= Navigation =================
    @FXML
    public void goDashboard() {
        System.out.println("Navigation : Dashboard");
        loadPage("/fxml/wallet/dashboarddepens.fxml");
    }

    @FXML
    public void goDepenses() {
        System.out.println("Navigation : Dépenses");
        loadPage("/fxml/wallet/depenses.fxml");
    }

    @FXML
    public void goBudget() {
        System.out.println("Navigation : Budget");
        loadPage("/fxml/wallet/budget.fxml");
    }

    @FXML
    public void goPlannings() {
        System.out.println("Navigation : Plannings");
        loadPage("/fxml/wallet/plannings.fxml");
    }

    @FXML
    public void goNotifications() {
        System.out.println("Navigation : Notifications");
        loadPage("/fxml/wallet/notifications.fxml");
    }

    @FXML
    public void goAdvisor() {
        System.out.println("Navigation : Advisor");
        loadPage("/fxml/wallet/advisor.fxml");
    }

    @FXML
    public void goSettings() {
        System.out.println("Navigation : Settings");
        loadPage("/fxml/wallet/setting.fxml");
    }
}