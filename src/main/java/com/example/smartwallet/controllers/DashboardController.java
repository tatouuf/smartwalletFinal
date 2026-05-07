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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    @FXML private CheckBox succeededCheck;
    @FXML private CheckBox failedCheck;
    @FXML private CheckBox pendingCheck;

    private final ServiceRecurringPayment recurringService = new ServiceRecurringPayment();
    private final ServiceStripeTransaction stripeService = new ServiceStripeTransaction();
    private final FinancialAnalyticsService analytics = new FinancialAnalyticsService();

    private DashboardResult lastResult;
    private final ObservableList<StripeTransaction> masterData = FXCollections.observableArrayList();
    private FilteredList<StripeTransaction> filteredData;

    private final com.example.smartwallet.Services.ServiceFinanceProfile profileService = new com.example.smartwallet.Services.ServiceFinanceProfile();

    @FXML
    public void initialize() {
        // Defaults from Session & Profile
        entities.User sessionUser = utils.Session.getCurrentUser();
        if (sessionUser != null) {
            try {
                com.example.smartwallet.entities.FinanceProfile profile = profileService.getByUserId(sessionUser.getId());
                if (profile != null) {
                    incomeField.setText(String.valueOf(profile.getMonthlyIncome()));
                    balanceField.setText(String.valueOf(profile.getCurrentBalance()));
                } else {
                    incomeField.setText("0");
                    balanceField.setText("0");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Default Filtering Config

        // Setup Filtering
        filteredData = new FilteredList<>(masterData, p -> true);
        if (transactionListView != null) {
            transactionListView.setItems(filteredData);
            
            // Search & Filter Listeners
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
            }
            if (succeededCheck != null) {
                succeededCheck.selectedProperty().addListener((obs, oldVal, newVal) -> updateFilter());
            }
            if (failedCheck != null) {
                failedCheck.selectedProperty().addListener((obs, oldVal, newVal) -> updateFilter());
            }
            if (pendingCheck != null) {
                pendingCheck.selectedProperty().addListener((obs, oldVal, newVal) -> updateFilter());
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
                        amount.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 13px;");
                        
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

        boolean showSucceeded = succeededCheck.isSelected();
        boolean showFailed = failedCheck.isSelected();
        boolean showPending = pendingCheck.isSelected();

        filteredData.setPredicate(item -> {
            String s = item.getStatus().toLowerCase();
            
            // Filter by Status Checkboxes
            boolean matchesStatus = false;
            if (showSucceeded && s.contains("succeeded")) matchesStatus = true;
            if (showFailed && (s.contains("fail") || s.contains("cancel"))) matchesStatus = true;
            if (showPending && (!s.contains("succeeded") && !s.contains("fail") && !s.contains("cancel"))) matchesStatus = true;
            
            if (!matchesStatus) return false;

            // Filter by Search Text (ID or Amount)
            if (searchText.isEmpty()) return true;

            if (item.getStripePaymentIntentId().toLowerCase().contains(searchText)) return true;
            if (String.valueOf(item.getAmount()).contains(searchText)) return true;

            return false;
        });
    }

    @FXML
    private void refreshDashboard() {
        double income = 0;
        double savings = 0;
        try {
            income = parseDoubleOrZero(incomeField.getText());
            savings = parseDoubleOrZero(balanceField.getText());
        } catch (Exception ignored) {}

        try {
            entities.User sessionUser = utils.Session.getCurrentUser();
            int userId = (sessionUser != null) ? sessionUser.getId() : 1;

            List<RecurringPayment> recurring = recurringService.recupererParUser(userId);
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
        entities.User sessionUser = utils.Session.getCurrentUser();
        if (sessionUser != null) {
            try {
                double income = parseDoubleOrZero(incomeField.getText());
                double savings = parseDoubleOrZero(balanceField.getText());
                
                com.example.smartwallet.entities.FinanceProfile profile = new com.example.smartwallet.entities.FinanceProfile(
                    0, sessionUser.getId(), income, savings, "TND"
                );
                profileService.upsert(profile);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        refreshDashboard();
    }

    @FXML
    private void handleFetchHistory() {
        new Thread(() -> {
            try {
                entities.User sessionUser = utils.Session.getCurrentUser();
                int userId = (sessionUser != null) ? sessionUser.getId() : 1;
                
                List<StripeTransaction> list = stripeService.recupererParUser(userId);
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

    @FXML
    private void goBack() {
        tests.MainFxml.getInstance().showWalletHome();
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

    @FXML
    private void exportToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(searchField.getScene().getWindow());

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                document.add(new Paragraph("Transaction History"));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(4);
                table.addCell("ID");
                table.addCell("Amount");
                table.addCell("Date");
                table.addCell("Status");

                for (StripeTransaction tx : filteredData) {
                    table.addCell(tx.getStripePaymentIntentId());
                    table.addCell(String.format("%.2f %s", tx.getAmount(), tx.getCurrency().toUpperCase()));
                    table.addCell(tx.getCreatedAt().toString());
                    table.addCell(tx.getStatus());
                }

                document.add(table);
                document.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "PDF exported successfully!");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error exporting PDF: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel Data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(searchField.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Transactions");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("ID");
                headerRow.createCell(1).setCellValue("Amount");
                headerRow.createCell(2).setCellValue("Currency");
                headerRow.createCell(3).setCellValue("Date");
                headerRow.createCell(4).setCellValue("Status");

                int rowNum = 1;
                for (StripeTransaction tx : filteredData) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(tx.getStripePaymentIntentId());
                    row.createCell(1).setCellValue(tx.getAmount());
                    row.createCell(2).setCellValue(tx.getCurrency().toUpperCase());
                    row.createCell(3).setCellValue(tx.getCreatedAt().toString());
                    row.createCell(4).setCellValue(tx.getStatus());
                }

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Excel data exported successfully!");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error exporting Excel: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}