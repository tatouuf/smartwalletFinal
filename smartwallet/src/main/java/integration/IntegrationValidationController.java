package integration;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.*;

public class IntegrationValidationController {
    @FXML private TabPane modulesTabs;
    @FXML private TextArea logArea;

    private final SymfonyApiClient api = new SymfonyApiClient();

    private final Map<String, TableView<Map<String, String>>> tables = new LinkedHashMap<>();
    private final Map<String, String> endpoints = Map.of(
            "Users", "/users",
            "Wallets", "/wallets",
            "Services", "/services",
            "Dépenses", "/depenses",
            "Recurring Payments", "/recurring-payments",
            "Budgets", "/budgets"
    );

    @FXML
    public void initialize() {
        endpoints.keySet().forEach(this::createModuleTab);
        testApi();
        refreshAll();
    }

    private void createModuleTab(String moduleName) {
        TableView<Map<String, String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tables.put(moduleName, table);

        Button refresh = new Button("Refresh depuis Symfony");
        refresh.getStyleClass().add("primary-btn");
        refresh.setOnAction(e -> refresh(moduleName));

        Button addDemo = new Button("Ajouter DEMO");
        addDemo.getStyleClass().add("success-btn");
        addDemo.setOnAction(e -> addDemo(moduleName));

        Button delete = new Button("Supprimer sélection");
        delete.getStyleClass().add("danger-btn");
        delete.setOnAction(e -> deleteSelected(moduleName));

        ToolBar bar = new ToolBar(refresh, addDemo, delete);
        VBox box = new VBox(10, bar, table);
        box.getStyleClass().add("module-box");

        Tab tab = new Tab(moduleName, box);
        modulesTabs.getTabs().add(tab);
    }

    @FXML
    private void refreshAll() {
        endpoints.keySet().forEach(this::refresh);
    }

    @FXML
    private void testApi() {
        runAsync(() -> {
            JsonNode health = api.get("/health");
            log("API OK : " + health.get("message").asText());
        });
    }

    private void refresh(String moduleName) {
        runAsync(() -> {
            JsonNode list = api.get(endpoints.get(moduleName));
            List<Map<String, String>> rows = new ArrayList<>();
            if (list.isArray()) {
                for (JsonNode node : list) rows.add(toRow(node));
            }
            Platform.runLater(() -> fillTable(moduleName, rows));
            log("Refresh " + moduleName + " : " + rows.size() + " lignes");
        });
    }

    private void addDemo(String moduleName) {
        String endpoint = endpoints.get(moduleName);
        String json = demoJson(moduleName);

        runAsync(() -> {
            JsonNode created = api.post(endpoint, json);
            log("Ajout " + moduleName + " OK : id=" + created.path("id").asText());
            refresh(moduleName);
        });
    }

    private void deleteSelected(String moduleName) {
        TableView<Map<String, String>> table = tables.get(moduleName);
        Map<String, String> selected = table.getSelectionModel().getSelectedItem();

        if (selected == null || selected.get("id") == null) {
            alert("Sélectionne une ligne avec id.");
            return;
        }

        runAsync(() -> {
            api.delete(endpoints.get(moduleName) + "/" + selected.get("id"));
            log("Suppression " + moduleName + " OK : id=" + selected.get("id"));
            refresh(moduleName);
        });
    }

    private void fillTable(String moduleName, List<Map<String, String>> rows) {
        TableView<Map<String, String>> table = tables.get(moduleName);
        table.getColumns().clear();

        Set<String> columns = new LinkedHashSet<>();
        rows.forEach(row -> columns.addAll(row.keySet()));

        for (String col : columns) {
            TableColumn<Map<String, String>, String> tc = new TableColumn<>(col);
            tc.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getOrDefault(col, "")
            ));
            table.getColumns().add(tc);
        }

        table.setItems(FXCollections.observableArrayList(rows));
    }

    private Map<String, String> toRow(JsonNode node) {
        Map<String, String> row = new LinkedHashMap<>();
        node.fields().forEachRemaining(e -> row.put(e.getKey(), e.getValue().isNull() ? "" : e.getValue().asText()));
        return row;
    }

    private String demoJson(String moduleName) {
        long t = System.currentTimeMillis();
        return switch (moduleName) {
            case "Users" -> """
                    {"email":"javafx%s@smartwallet.tn","nom":"Integration","prenom":"JavaFX","telephone":"55000000","role":"USER","status":"APPROVED"}
                    """.formatted(t);
            case "Wallets" -> """
                    {"balance":150.0,"numeroCompte":"SW%s","type":"Standard","status":"ACTIVE","rib":"TN590000%s","adresse":"ESPRIT","telephone":"55000000"}
                    """.formatted(t, String.valueOf(t).substring(5));
            case "Services" -> """
                    {"type":"Service JavaFX","prix":25.5,"description":"Service ajouté depuis JavaFX","statut":"ACTIVE","typeService":"GENERAL","localisation":"Tunis","adresse":"ESPRIT","duree":"2h"}
                    """;
            case "Dépenses" -> """
                    {"userId":1,"montant":19.9,"categorie":"Food","description":"Dépense ajoutée depuis JavaFX","dateDepense":"%s"}
                    """.formatted(LocalDate.now());
            case "Recurring Payments" -> """
                    {"name":"Netflix JavaFX","amount":"29.900","frequency":"MONTHLY","nextPaymentDate":"%s","isActive":true}
                    """.formatted(LocalDate.now().plusMonths(1));
            case "Budgets" -> """
                    {"userId":1,"categorie":"Food","montantMax":500,"montantActuel":50,"mois":%d,"annee":%d,"description":"Budget ajouté depuis JavaFX"}
                    """.formatted(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
            default -> "{}";
        };
    }

    private void runAsync(ThrowingRunnable task) {
        new Thread(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log("ERREUR : " + e.getMessage());
                e.printStackTrace();
            }
        }, "symfony-api-thread").start();
    }

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText("• " + message + "\n"));
    }

    private void alert(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.WARNING, message).showAndWait());
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }
}
