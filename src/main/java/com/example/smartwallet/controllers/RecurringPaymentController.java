package com.example.smartwallet.controllers;

import com.example.smartwallet.Services.ServiceRecurringPayment;
import com.example.smartwallet.Services.ServiceStripeTransaction;
import com.example.smartwallet.entities.RecurringPayment;
import com.example.smartwallet.entities.StripeTransaction;
import com.example.smartwallet.utils.StripeConfig;
import com.example.smartwallet.utils.EmailService;
import com.example.smartwallet.utils.UserSession;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RecurringPaymentController {

    @FXML private TextField nameField;
    @FXML private TextField amountField;
    @FXML private ComboBox<RecurringPayment.Frequency> frequencyBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField periodsField;
    @FXML private ComboBox<String> unitBox;
    @FXML private Label paymentStatus;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> frequencyFilterBox;
    @FXML private Pagination pagination;

    private static final int ITEMS_PER_PAGE = 6;
    private final ListView<RecurringPayment> internalListView = new ListView<>();

    private final ServiceRecurringPayment service = new ServiceRecurringPayment();
    private final ServiceStripeTransaction stripeService = new ServiceStripeTransaction();

    private final ObservableList<RecurringPayment> masterData = FXCollections.observableArrayList();
    private FilteredList<RecurringPayment> filteredData;

    @FXML
    public void initialize() {
        // Remplir les fréquences
        frequencyBox.getItems().setAll(RecurringPayment.Frequency.values());
        
        // Remplir les unités de paiement
        unitBox.getItems().setAll("Cycles", "Months", "Years");
        unitBox.setValue("Cycles");

        // (Optionnel) rendre l'affichage Frequency plus propre
        frequencyBox.setConverter(new StringConverter<>() {
            @Override public String toString(RecurringPayment.Frequency f) { return f == null ? "" : f.name(); }
            @Override public RecurringPayment.Frequency fromString(String s) { return RecurringPayment.Frequency.valueOf(s); }
        });

        // Setup Filtering
        filteredData = new FilteredList<>(masterData, p -> true);

        refreshList();

        // Config Filter Frequency
        frequencyFilterBox.getItems().setAll("All", "Monthly", "Yearly", "Weekly");
        frequencyFilterBox.setValue("All");

        // Setup Pagination
        pagination.setPageFactory(this::createPage);
        updatePagination();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateFilter();
            updatePagination();
        });
        frequencyFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFilter();
            updatePagination();
        });

        // Config internal ListView (Custom Cell Factory)
        internalListView.getStyleClass().add("list");
        internalListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RecurringPayment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(15);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setPadding(new javafx.geometry.Insets(8, 12, 8, 12));

                    VBox info = new VBox(2);
                    Label name = new Label(item.getName());
                    name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    Label nextDate = new Label("Next: " + (item.getNextPaymentDate() != null ? item.getNextPaymentDate() : "N/A"));
                    nextDate.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 11px;");
                    info.getChildren().addAll(name, nextDate);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Frequency Badge
                    Label freqBadge = new Label(item.getFrequency().name());
                    String badgeColor = "#3498db";
                    if (item.getFrequency() == RecurringPayment.Frequency.MONTHLY) badgeColor = "#2ecc71";
                    if (item.getFrequency() == RecurringPayment.Frequency.YEARLY) badgeColor = "#e67e22";

                    freqBadge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;");

                    Label amount = new Label(String.format("%.2f TND", item.getAmount()));
                    amount.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

                    hbox.getChildren().addAll(info, spacer, freqBadge, amount);
                    setGraphic(hbox);
                }
            }
        });

        internalListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                amountField.setText(String.valueOf(selected.getAmount()));
                frequencyBox.setValue(selected.getFrequency());
                datePicker.setValue(selected.getNextPaymentDate());
            }
        });
    }

    private void updatePagination() {
        int count = (int) Math.ceil((double) filteredData.size() / ITEMS_PER_PAGE);
        if (count == 0) count = 1;
        pagination.setPageCount(count);
        pagination.setCurrentPageIndex(0);
        // Force refresh of current page
        pagination.setPageFactory(this::createPage);
    }

    private javafx.scene.Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredData.size());
        
        if (fromIndex >= filteredData.size() && filteredData.size() > 0) {
            return new Label("No data on this page");
        }
        
        ObservableList<RecurringPayment> pageItems = FXCollections.observableArrayList(
                filteredData.subList(fromIndex, toIndex)
        );
        internalListView.setItems(pageItems);
        return internalListView;
    }

    private void updateFilter() {
        String searchText = (searchField != null) ? searchField.getText().toLowerCase() : "";
        String filterFreq = (frequencyFilterBox != null) ? frequencyFilterBox.getValue() : "All";

        filteredData.setPredicate(p -> {
            boolean nameMatch = p.getName().toLowerCase().contains(searchText);
            boolean freqMatch = "All".equals(filterFreq) || p.getFrequency().name().equalsIgnoreCase(filterFreq);
            return nameMatch && freqMatch;
        });
    }

    private void refreshList() {
        try {
            List<RecurringPayment> list = service.recuperer();
            masterData.setAll(list);
            updatePagination();
        } catch (SQLException e) {
            showError("Erreur DB", e.getMessage());
        }
    }
  @FXML
    private void clearFormAction() {
        nameField.clear();
        amountField.clear();
        frequencyBox.setValue(null);
        datePicker.setValue(null);
        internalListView.getSelectionModel().clearSelection();
    }

    // ===================== VALIDATION =====================
    private boolean validateInputs() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String amountTxt = amountField.getText() == null ? "" : amountField.getText().trim();
        RecurringPayment.Frequency freq = frequencyBox.getValue();
        LocalDate nextDate = datePicker.getValue();

        if (name.isEmpty()) {
            showWarning("Validation", "Le nom est obligatoire.");
            return false;
        }

        if (amountTxt.isEmpty()) {
            showWarning("Validation", "Le montant est obligatoire.");
            return false;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountTxt.replace(",", "."));
        } catch (NumberFormatException ex) {
            showWarning("Validation", "Le montant doit être un nombre (ex: 35.5).");
            return false;
        }

        if (amount <= 0) {
            showWarning("Validation", "Le montant doit être > 0.");
            return false;
        }

        if (freq == null) {
            showWarning("Validation", "La fréquence est obligatoire.");
            return false;
        }

        if (nextDate == null) {
            showWarning("Validation", "La date est obligatoire.");
            return false;
        }

        return true;
    }

    // ===================== CRUD =====================

    @FXML
    private void addPayment() {
        if (!validateInputs()) return;

        try {
            RecurringPayment rp = new RecurringPayment();
            rp.setUserId(1); // ✅ pour l’instant fixe (après login tu changes)
            rp.setName(nameField.getText().trim());
            rp.setAmount(Double.parseDouble(amountField.getText().trim().replace(",", ".")));
            rp.setFrequency(frequencyBox.getValue());
            rp.setNextPaymentDate(datePicker.getValue());
            rp.setActive(true);

            service.ajouter(rp);
            clearForm();
            refreshList();

        } catch (SQLException e) {
            showError("Erreur DB", e.getMessage());
        }
    }

    @FXML
    private void updatePayment() {
        RecurringPayment selected = internalListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Modification", "Sélectionne un paiement à modifier.");
            return;
        }
        if (!validateInputs()) return;

        try {
            selected.setName(nameField.getText().trim());
            selected.setAmount(Double.parseDouble(amountField.getText().trim().replace(",", ".")));
            selected.setFrequency(frequencyBox.getValue());
            selected.setNextPaymentDate(datePicker.getValue());

            service.modifier(selected);
            clearForm();
            refreshList();

        } catch (SQLException e) {
            showError("Erreur DB", e.getMessage());
        }
    }

    @FXML
    private void deletePayment() {
        RecurringPayment selected = internalListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Suppression", "Sélectionne un paiement à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer ce paiement ?");
        confirm.setContentText(selected.toString());

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            service.supprimer(selected);
            clearForm();
            refreshList();
        } catch (SQLException e) {
            showError("Erreur DB", e.getMessage());
        }
    }

    private void clearForm() {
        nameField.clear();
        amountField.clear();
        frequencyBox.setValue(null);
        datePicker.setValue(null);
        internalListView.getSelectionModel().clearSelection();
    }

    // ===================== ALERTS =====================
    private void showWarning(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
    private void payNow() {
        RecurringPayment selected = internalListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Payment", "Please select a recurring payment from the list.");
            return;
        }

        int val;
        try {
            val = Integer.parseInt(periodsField.getText().trim());
            if (val <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showWarning("Invalid Input", "Please enter a valid number.");
            return;
        }

        String unit = unitBox.getValue();
        double totalAmount = 0;
        LocalDate oldDate = selected.getNextPaymentDate() != null ? selected.getNextPaymentDate() : LocalDate.now();
        LocalDate newDate = oldDate;

        if ("Cycles".equals(unit)) {
            totalAmount = selected.getAmount() * val;
            switch (selected.getFrequency()) {
                case MONTHLY: newDate = oldDate.plusMonths(val); break;
                case YEARLY: newDate = oldDate.plusYears(val); break;
                case WEEKLY: newDate = oldDate.plusWeeks(val); break;
            }
        } else if ("Months".equals(unit)) {
            newDate = oldDate.plusMonths(val);
            switch (selected.getFrequency()) {
                case MONTHLY: totalAmount = selected.getAmount() * val; break;
                case YEARLY: totalAmount = (selected.getAmount() / 12.0) * val; break;
                case WEEKLY: totalAmount = (selected.getAmount() * 4.345) * val; break; // Approximatif
            }
        } else if ("Years".equals(unit)) {
            newDate = oldDate.plusYears(val);
            switch (selected.getFrequency()) {
                case MONTHLY: totalAmount = selected.getAmount() * 12.0 * val; break;
                case YEARLY: totalAmount = selected.getAmount() * val; break;
                case WEEKLY: totalAmount = selected.getAmount() * 52.17 * val; break; // Approximatif
            }
        }

            paymentStatus.setText("Processing payment...");

            final double finalTotalAmount = totalAmount; // Need final or effectively final for lambda
            final LocalDate finalNewDate = newDate; // Need final or effectively final for lambda
            final int finalVal = val;
            final String finalUnit = unit;

            new Thread(() -> {
                try {
                    // Utilise la clé centralisée dans StripeConfig
                    Stripe.apiKey = StripeConfig.SECRET_KEY;

                    PaymentIntentCreateParams params =
                            PaymentIntentCreateParams.builder()
                                    .setAmount((long) (finalTotalAmount * 100))
                                    .setCurrency("usd")
                                    .setConfirm(true)
                                    .setPaymentMethod("pm_card_visa")
                                    .setAutomaticPaymentMethods(
                                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                                    .setEnabled(true)
                                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                                    .build()
                                    )
                                    .build();

                    PaymentIntent intent = PaymentIntent.create(params);

                    // Record transaction in our DB
                    StripeTransaction transaction = new StripeTransaction(
                        1, 
                        selected.getProfileId(), 
                        intent.getId(),
                        finalTotalAmount,
                        "usd",
                        intent.getStatus().toUpperCase()
                    );
                    stripeService.ajouter(transaction);

                    // Send Email Notification
                    EmailService.sendPaymentSuccessEmail(UserSession.getInstance().getUserEmail(), intent.getId(), finalTotalAmount, "usd");

                    selected.setNextPaymentDate(finalNewDate);
                    service.modifier(selected);

                    Platform.runLater(() -> {
                        paymentStatus.setText("Payment successful for " + String.format("%.2f", finalTotalAmount) + " " + finalUnit + "! Next date: " + finalNewDate);
                        paymentStatus.setStyle("-fx-text-fill: #2ecc71;");
                        refreshList(); 
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        paymentStatus.setText("Error: " + e.getMessage());
                        paymentStatus.setStyle("-fx-text-fill: #e74c3c;");
                    });
                }
            }).start();
    }

    @FXML
    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/smartwallet/dashboard-view.fxml")
            );

            Scene scene = new Scene(loader.load());
            // Reapply global stylesheet so styles persist when navigating back
            scene.getStylesheets().add(
                    getClass().getResource("/com/example/smartwallet/Styles.css").toExternalForm()
            );

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
