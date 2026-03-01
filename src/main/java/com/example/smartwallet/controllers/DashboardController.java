package com.example.smartwallet.controllers;

import com.example.smartwallet.Services.FinancialAnalyticsService;
import com.example.smartwallet.Services.ServiceRecurringPayment;
import com.example.smartwallet.entities.DashboardResult;
import com.example.smartwallet.entities.RecurringPayment;
import com.example.smartwallet.entities.StripeTransaction;
import com.example.smartwallet.Services.ServiceStripeTransaction;
import com.example.smartwallet.utils.StripeConfig;
import com.example.smartwallet.utils.EmailService;
import com.example.smartwallet.utils.UserSession;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class DashboardController {

    @FXML private TextField incomeField;
    @FXML private TextField balanceField;

    @FXML private Label scoreLabel;
    @FXML private Label riskLabel;
    @FXML private Label forecastLabel;
    @FXML private Label recurringLabel;

    @FXML private Label aiInsightLabel;
    @FXML private VBox aiInsightCard;
    @FXML private ProgressIndicator aiProgress;

    @FXML private Label headlineLabel;

    @FXML private ListView<StripeTransaction> transactionListView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterBox;

    private final ServiceRecurringPayment recurringService = new ServiceRecurringPayment();
    private final ServiceStripeTransaction stripeService = new ServiceStripeTransaction();
    private final FinancialAnalyticsService analytics = new FinancialAnalyticsService();

    private DashboardResult lastResult;
    private final ObservableList<StripeTransaction> masterData = FXCollections.observableArrayList();
    private FilteredList<StripeTransaction> filteredData;

    @FXML
    public void initialize() {
        // Defaults
        if (incomeField != null && incomeField.getText().isBlank()) incomeField.setText("1200");
        if (balanceField != null && balanceField.getText().isBlank()) balanceField.setText("300");

        // Status filter config
        if (statusFilterBox != null) {
            statusFilterBox.getItems().setAll("All", "Succeeded", "Failed", "Pending");
            statusFilterBox.setValue("All");
        }

        // Setup Filtering
        filteredData = new FilteredList<>(masterData, p -> true);
        if (transactionListView != null) {
            transactionListView.setItems(filteredData);
            
            // Search & Filter Listeners
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
            }
            if (statusFilterBox != null) {
                statusFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
            }

            transactionListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(StripeTransaction item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox hbox = new HBox(12);
                        hbox.setAlignment(Pos.CENTER_LEFT);

                        VBox info = new VBox(2);
                        Label amount = new Label(String.format("%.2f %s", item.getAmount(), item.getCurrency().toUpperCase()));
                        amount.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
                        
                        Label date = new Label(item.getCreatedAt().toString());
                        date.getStyleClass().add("subtitle");
                        
                        Label id = new Label(item.getStripePaymentIntentId());
                        id.getStyleClass().add("transaction-id");
                        
                        info.getChildren().addAll(amount, date, id);

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        Label status = new Label(item.getStatus().toUpperCase());
                        status.getStyleClass().add("status-label");
                        
                        String s = item.getStatus().toLowerCase();
                        if (s.contains("succeeded")) {
                            status.getStyleClass().add("status-succeeded");
                        } else if (s.contains("fail") || s.contains("cancel")) {
                            status.getStyleClass().add("status-failed");
                        } else {
                            status.getStyleClass().add("status-pending");
                        }

                        hbox.getChildren().addAll(info, spacer, status);
                        setGraphic(hbox);
                    }
                }
            });
        }

        refreshDashboard();
    }

    private void updateFilter() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = statusFilterBox.getValue();

        filteredData.setPredicate(item -> {
            // Filter by Status
            if (statusFilter != null && !"All".equals(statusFilter)) {
                if (!item.getStatus().equalsIgnoreCase(statusFilter)) {
                    return false;
                }
            }

            // Filter by Search Text (ID or Amount)
            if (searchText.isEmpty()) return true;

            if (item.getStripePaymentIntentId().toLowerCase().contains(searchText)) return true;
            if (String.valueOf(item.getAmount()).contains(searchText)) return true;

            return false;
        });
    }

    @FXML
    private void refreshDashboard() {
        double income = 1200;
        double savings = 300;
        try {
            income = Double.parseDouble(incomeField.getText());
            savings = Double.parseDouble(balanceField.getText());
        } catch (Exception ignored) {}

        try {
            List<RecurringPayment> recurring = recurringService.recuperer();
            lastResult = analytics.compute(income, savings, recurring);

            scoreLabel.setText(lastResult.getScore() + "/100");
            riskLabel.setText("Risk: " + lastResult.getRiskLevel());
            forecastLabel.setText(String.format("%.2f TND", lastResult.getRemainingThisMonth()));
            recurringLabel.setText(String.format("Recurring total: %.2f", lastResult.getRecurringMonthly()));

            // AI Call
            fetchAIInsights(income, savings, lastResult.getRecurringMonthly(), lastResult.getRemainingThisMonth());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchAIInsights(double income, double savings, double recurring, double remaining) {
        aiInsightCard.setVisible(true);
        aiInsightCard.setManaged(true);
        aiProgress.setVisible(true);
        aiInsightLabel.setText("AI is analyzing your financial patterns...");

        com.example.smartwallet.utils.AIInsightService.getRiskAnalysis(income, savings, recurring, remaining)
            .thenAccept(insight -> Platform.runLater(() -> {
                aiInsightLabel.setText(insight);
                aiProgress.setVisible(false);
            }));
    }

    @FXML
    private void saveProfile() {
        refreshDashboard();
    }

    @FXML
    private void handleFetchHistory() {
        new Thread(() -> {
            try {
                List<StripeTransaction> list = stripeService.recuperer();
                Platform.runLater(() -> masterData.setAll(list));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void goRecurring() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/recurring-view.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Reapply Styles.css to ensure the dark theme persists
            scene.getStylesheets().add(
                    getClass().getResource("/com/example/smartwallet/Styles.css").toExternalForm()
            );

            Stage stage = (Stage) scoreLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double parseDoubleOrZero(String s) {
        try {
            if (s == null) return 0;
            s = s.trim().replace(",", ".");
            if (s.isBlank()) return 0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
}