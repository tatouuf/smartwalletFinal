package com.example.smartwallet.controllers;

import com.example.smartwallet.Services.ServiceRecurringPayment;
import com.example.smartwallet.entities.RecurringPayment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;

public class RecurringPaymentController {

    @FXML private TextField nameField;
    @FXML private TextField amountField;
    @FXML private ComboBox<RecurringPayment.Frequency> frequencyBox;
    @FXML private DatePicker datePicker;
    @FXML private ListView<RecurringPayment> paymentList;

    private final ServiceRecurringPayment service = new ServiceRecurringPayment();

    @FXML
    public void initialize() {
        // Remplir les fréquences
        frequencyBox.getItems().setAll(RecurringPayment.Frequency.values());

        // (Optionnel) rendre l'affichage Frequency plus propre
        frequencyBox.setConverter(new StringConverter<>() {
            @Override public String toString(RecurringPayment.Frequency f) { return f == null ? "" : f.name(); }
            @Override public RecurringPayment.Frequency fromString(String s) { return RecurringPayment.Frequency.valueOf(s); }
        });

        refreshList();

        // ✅ Quand je clique sur un item -> remplir les champs
        paymentList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                amountField.setText(String.valueOf(selected.getAmount()));
                frequencyBox.setValue(selected.getFrequency());
                datePicker.setValue(selected.getNextPaymentDate());
            }
        });
    }

    private void refreshList() {
        try {
            paymentList.getItems().setAll(service.recuperer());
        } catch (SQLException e) {
            showError("Erreur DB", e.getMessage());
        }
    }  @FXML
    private void clearFormAction() {
        nameField.clear();
        amountField.clear();
        frequencyBox.setValue(null);
        datePicker.setValue(null);
        paymentList.getSelectionModel().clearSelection();
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
        RecurringPayment selected = paymentList.getSelectionModel().getSelectedItem();
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
        RecurringPayment selected = paymentList.getSelectionModel().getSelectedItem();
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
        paymentList.getSelectionModel().clearSelection();
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
