package com.example.smartwallet.config;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import com.example.smartwallet.TabManager;

import java.io.IOException;

/**
 * Initialise la scène principale de l'application
 */
@Component
public class PrimaryStageInitializer {

    // Stocker la racine pour permettre la navigation depuis la sidebar
    private BorderPane root;

    public void initStage(Stage primaryStage) {
        // Créer la scène principale
        BorderPane root = new BorderPane();
        this.root = root;
        // Enregistrer le root dans TabManager
        TabManager.setRoot(root);
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12px;");

        // Barre de menu
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Barre latérale de navigation
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Contenu principal
        VBox centerContent = createCenterContent();
        root.setCenter(centerContent);

        // Barre de statut
        Label statusBar = new Label("Prêt");
        statusBar.setStyle("-fx-border-color: #cccccc; -fx-padding: 5px;");
        root.setBottom(statusBar);

        // Créer la scène
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-padding: 5px;");

        // Menu Fichier
        Menu fileMenu = new Menu("Fichier");
        MenuItem newItem = new MenuItem("Nouveau");
        MenuItem openItem = new MenuItem("Ouvrir");
        MenuItem exitItem = new MenuItem("Quitter");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(newItem, openItem, new SeparatorMenuItem(), exitItem);

        // Menu Édition
        Menu editMenu = new Menu("Édition");
        editMenu.getItems().addAll(
            new MenuItem("Couper"),
            new MenuItem("Copier"),
            new MenuItem("Coller")
        );

        // Menu Affichage
        Menu viewMenu = new Menu("Affichage");
        viewMenu.getItems().addAll(
            new MenuItem("Actualiser"),
            new MenuItem("Zoom avant"),
            new MenuItem("Zoom arrière")
        );

        // Menu Aide
        Menu helpMenu = new Menu("Aide");
        MenuItem aboutItem = new MenuItem("À propos");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().addAll(aboutItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
        return menuBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-border-color: #eeeeee; -fx-padding: 10px; -fx-background-color: #f5f5f5;");
        sidebar.setSpacing(10);
        sidebar.setPrefWidth(150);

        Label titleLabel = new Label("SmartWallet");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button dashboardBtn = createNavButton("tableau", "📊 Tableau de Bord");
        Button budgetBtn = createNavButton("budgets", "💰 Budgets");
        Button expensesBtn = createNavButton("depenses", "💳 Dépenses");
        Button planningBtn = createNavButton("plannings", "📅 Planifications");
        Button categoriesBtn = createNavButton("categories", "🏷️ Catégories");
        Button notificationsBtn = createNavButton("notifications", "🔔 Notifications");
        Button settingsBtn = createNavButton("settings", "⚙️ Paramètres");

        dashboardBtn.setStyle(dashboardBtn.getStyle() + "; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

        sidebar.getChildren().addAll(
            titleLabel,
            new Separator(),
            dashboardBtn,
            budgetBtn,
            expensesBtn,
            planningBtn,
            categoriesBtn,
            notificationsBtn,
            new Separator(),
            settingsBtn
        );

        return sidebar;
    }

    private Button createNavButton(String key, String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-padding: 10px; -fx-alignment: CENTER_LEFT;");
        btn.setUserData(key);
        btn.setOnAction(e -> handleNavigation(key));
        return btn;
    }

    private void handleNavigation(String key) {
        // TRACE: afficher ce qui est demandé
        System.out.println("handleNavigation called with key: '" + key + "'");
        // Utiliser d'abord TabManager pour un comportement centralisé
        try {
            if ("budgets".equals(key)) {
                boolean ok = com.example.smartwallet.TabManager.showView("/com/example/smartwallet/budget-view.fxml", "Budgets");
                if (ok) {
                    System.out.println("handleNavigation: TabManager showed Budgets view");
                    return;
                }
            } else if ("tableau".equals(key)) {
                boolean ok = com.example.smartwallet.TabManager.showView("/com/example/smartwallet/dashboard-view.fxml", "Tableau de Bord");
                if (ok) {
                    System.out.println("handleNavigation: TabManager showed Dashboard view");
                    return;
                }
            } else if ("depenses".equals(key)) {
                boolean ok = com.example.smartwallet.TabManager.showView("/com/example/smartwallet/depense-view.fxml", "Dépenses");
                if (ok) {
                    System.out.println("handleNavigation: TabManager showed Depenses view");
                    return;
                }
            } else if ("plannings".equals(key)) {
                boolean ok = com.example.smartwallet.TabManager.showView("/com/example/smartwallet/planning-view.fxml", "Plannings");
                if (ok) {
                    System.out.println("handleNavigation: TabManager showed Plannings view");
                    return;
                }
            } else if ("notifications".equals(key)) {
                boolean ok = com.example.smartwallet.TabManager.showView("/com/example/smartwallet/notification-view.fxml", "Notifications");
                if (ok) {
                    System.out.println("handleNavigation: TabManager showed Notifications view");
                    return;
                }
            }

            // Fallback: charger le FXML directement et le placer au centre selon la clé
            if ("budgets".equals(key)) {
                System.out.println("handleNavigation: fallback loading Budgets FXML");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/budget-view.fxml"));
                Parent content = loader.load();
                root.setCenter(content);
                root.requestLayout();
                return;
            }

            if ("tableau".equals(key)) {
                System.out.println("handleNavigation: fallback loading Dashboard FXML");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/dashboard-view.fxml"));
                Parent content = loader.load();
                root.setCenter(content);
                root.requestLayout();
                return;
            }

            if ("depenses".equals(key)) {
                System.out.println("handleNavigation: fallback loading Depenses FXML");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/depense-view.fxml"));
                Parent content = loader.load();
                root.setCenter(content);
                root.requestLayout();
                return;
            }

            if ("plannings".equals(key)) {
                System.out.println("handleNavigation: fallback loading Plannings FXML");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/planning-view.fxml"));
                Parent content = loader.load();
                root.setCenter(content);
                root.requestLayout();
                return;
            }

            if ("notifications".equals(key)) {
                System.out.println("handleNavigation: fallback loading Notifications FXML");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/notification-view.fxml"));
                Parent content = loader.load();
                root.setCenter(content);
                root.requestLayout();
                return;
            }

            // Pour les autres boutons, on affiche un message temporaire dans le centre
            Label label = new Label("Section: " + key);
            label.setStyle("-fx-padding: 20px; -fx-font-size: 16px;");
            root.setCenter(new VBox(label));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private VBox createCenterContent() {
        VBox center = new VBox();
        center.setPadding(new Insets(20));
        center.setSpacing(15);
        center.setStyle("-fx-background-color: #ffffff;");

        // Titre
        Label titleLabel = new Label("Tableau de Bord");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Grille d'informations
        HBox statsBox = createStatsBox();

        // Section budgets
        VBox budgetsSection = createBudgetsSection();

        // Section dépenses récentes
        VBox expensesSection = createExpensesSection();

        // Barre de recherche et filtres
        HBox filterBox = createFilterBox();

        ScrollPane scrollPane = new ScrollPane();
        VBox scrollContent = new VBox(15);
        scrollContent.setPadding(new Insets(10));
        scrollContent.getChildren().addAll(
            filterBox,
            statsBox,
            budgetsSection,
            expensesSection
        );
        scrollPane.setContent(scrollContent);
        scrollPane.setFitToWidth(true);

        center.getChildren().addAll(titleLabel, scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        return center;
    }

    private HBox createStatsBox() {
        HBox stats = new HBox(15);
        stats.setPadding(new Insets(15));
        stats.setStyle("-fx-border-color: #ecf0f1; -fx-border-radius: 5; -fx-background-color: #f8f9fa; -fx-border-width: 1;");

        VBox revenueStat = createStatCard("Revenu Total", "15,250.00€", "#27ae60");
        VBox expensesStat = createStatCard("Dépenses Totales", "8,430.50€", "#e74c3c");
        VBox savingsStat = createStatCard("Épargne", "6,819.50€", "#3498db");
        VBox budgetStat = createStatCard("Budgets Actifs", "8", "#f39c12");

        stats.getChildren().addAll(revenueStat, expensesStat, savingsStat, budgetStat);
        return stats;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 5; -fx-background-color: white; -fx-border-width: 2;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private VBox createBudgetsSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ecf0f1; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-width: 1;");

        Label titleLabel = new Label("Budgets Actifs");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<BudgetItem> table = createBudgetTable();

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        Button addBtn = new Button("+ Ajouter Budget");
        addBtn.setStyle("-fx-padding: 8px 15px;");
        buttonBox.getChildren().add(addBtn);

        section.getChildren().addAll(titleLabel, table, buttonBox);
        return section;
    }

    private TableView<BudgetItem> createBudgetTable() {
        TableView<BudgetItem> table = new TableView<>();

        TableColumn<BudgetItem, String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<BudgetItem, String> amountCol = new TableColumn<>("Montant");
        amountCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAmount()));

        TableColumn<BudgetItem, String> usedCol = new TableColumn<>("Utilisé");
        usedCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsed()));

        TableColumn<BudgetItem, String> remainCol = new TableColumn<>("Restant");
        remainCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRemaining()));

        table.getColumns().addAll(categoryCol, amountCol, usedCol, remainCol);

        // Ajouter des données d'exemple
        table.getItems().addAll(
            new BudgetItem("Alimentation", "500€", "320€", "180€"),
            new BudgetItem("Transport", "200€", "150€", "50€"),
            new BudgetItem("Loisirs", "300€", "200€", "100€"),
            new BudgetItem("Logement", "1000€", "1000€", "0€")
        );

        table.setPrefHeight(200);
        return table;
    }

    private VBox createExpensesSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ecf0f1; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-width: 1;");

        Label titleLabel = new Label("Dépenses Récentes");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<ExpenseItem> table = createExpenseTable();

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        Button addBtn = new Button("+ Ajouter Dépense");
        addBtn.setStyle("-fx-padding: 8px 15px;");
        buttonBox.getChildren().add(addBtn);

        section.getChildren().addAll(titleLabel, table, buttonBox);
        return section;
    }

    private TableView<ExpenseItem> createExpenseTable() {
        TableView<ExpenseItem> table = new TableView<>();

        TableColumn<ExpenseItem, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));

        TableColumn<ExpenseItem, String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<ExpenseItem, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<ExpenseItem, String> amountCol = new TableColumn<>("Montant");
        amountCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAmount()));

        table.getColumns().addAll(dateCol, categoryCol, descriptionCol, amountCol);

        // Ajouter des données d'exemple
        table.getItems().addAll(
            new ExpenseItem("14/02/2026", "Alimentation", "Supermarché", "65.50€"),
            new ExpenseItem("13/02/2026", "Transport", "Essence", "50.00€"),
            new ExpenseItem("12/02/2026", "Loisirs", "Cinéma", "25.00€"),
            new ExpenseItem("11/02/2026", "Alimentation", "Restaurant", "45.30€")
        );

        table.setPrefHeight(200);
        return table;
    }

    private HBox createFilterBox() {
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(10));
        filterBox.setStyle("-fx-border-color: #ecf0f1; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-border-width: 1;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher...");
        searchField.setPrefWidth(200);

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("Tous", "Alimentation", "Transport", "Loisirs", "Logement");
        categoryFilter.setValue("Tous");
        categoryFilter.setPrefWidth(150);

        ComboBox<String> monthFilter = new ComboBox<>();
        monthFilter.getItems().addAll("Février 2026", "Janvier 2026", "Décembre 2025");
        monthFilter.setValue("Février 2026");
        monthFilter.setPrefWidth(150);

        Button filterBtn = new Button("Filtrer");
        filterBtn.setStyle("-fx-padding: 8px 15px;");

        filterBox.getChildren().addAll(
            new Label("Recherche:"),
            searchField,
            new Label("Catégorie:"),
            categoryFilter,
            new Label("Mois:"),
            monthFilter,
            filterBtn
        );

        return filterBox;
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos de SmartWallet");
        alert.setHeaderText("SmartWallet v1.0");
        alert.setContentText("Gestion Budgétaire Intelligente\n\nVersion 1.0\nDéveloppé avec Spring Boot et JavaFX");
        alert.showAndWait();
    }

    // Classes internes pour les données
    public static class BudgetItem {
        private String category;
        private String amount;
        private String used;
        private String remaining;

        public BudgetItem(String category, String amount, String used, String remaining) {
            this.category = category;
            this.amount = amount;
            this.used = used;
            this.remaining = remaining;
        }

        public String getCategory() { return category; }
        public String getAmount() { return amount; }
        public String getUsed() { return used; }
        public String getRemaining() { return remaining; }
    }

    public static class ExpenseItem {
        private String date;
        private String category;
        private String description;
        private String amount;

        public ExpenseItem(String date, String category, String description, String amount) {
            this.date = date;
            this.category = category;
            this.description = description;
            this.amount = amount;
        }

        public String getDate() { return date; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getAmount() { return amount; }
    }
}
