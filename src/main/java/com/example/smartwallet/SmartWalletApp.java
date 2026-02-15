package com.example.smartwallet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartWalletApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Créer une TabPane pour naviguer entre les différents modules
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-font-size: 12;");

        // Stocker la référence globale
        TabManager.setTabPane(tabPane);

        // Tab Tableau de Bord
        Tab dashboardTab = createTab("Tableau de Bord", "dashboard-view.fxml");
        Tab depensesTab = createTab("Dépenses", "depense-view.fxml");
        Tab budgetsTab = createTab("Budgets", "budget-view.fxml");
        Tab planningsTab = createTab("Plannings", "planning-view.fxml");

        tabPane.getTabs().addAll(dashboardTab, depensesTab, budgetsTab, planningsTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Empêcher la fermeture des onglets

        // Créer la scène
        Scene scene = new Scene(tabPane, 1200, 800);

        // Configurer la fenêtre principale
        primaryStage.setTitle("SmartWallet - Gestion Financière Personnelle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createTab(String title, String fxmlFile) throws IOException {
        Tab tab = new Tab();
        tab.setText(title);
        tab.setClosable(false);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            tab.setContent(loader.load());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier: " + fxmlFile);
            e.printStackTrace();
            throw e;
        }

        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
