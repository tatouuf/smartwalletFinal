package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.services.AddResponse;
import esprit.tn.chayma.services.AddResult;
import esprit.tn.chayma.services.DepenseService;
import esprit.tn.chayma.services.NotificationService;
import esprit.tn.chayma.utils.DialogUtil;
import esprit.tn.chayma.utils.ToastNotification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.DataChangeNotifier;
import utils.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class DepensesController {

    @FXML private TextField montantField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dateDepenseField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private Button ajouterBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private Button btnRetourDashboard;
    @FXML private ListView<Depense> depensesList;
    @FXML private Label totalDepensesLabel;

    private DepenseService depenseService = new DepenseService();
    private NotificationService notificationService = new NotificationService();
    private ObservableList<Depense> depensesObservable = FXCollections.observableArrayList();
    private int currentUserId;
    private Consumer<Void> notificationCallback = null;

    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null) {
            currentUserId = Session.getCurrentUser().getId();
        } else {
            currentUserId = 1;
        }

        categorieCombo.getItems().addAll("Alimentation", "Transport", "Logement", "Loisirs", "Santé", "Autres");
        loadDepenses();

        ajouterBtn.setOnAction(e -> onAjouter());
        supprimerBtn.setOnAction(e -> onSupprimer());
        modifierBtn.setOnAction(e -> onModifier());
        btnRetourDashboard.setOnAction(e -> retourDashboard());

        depensesList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadDepenses();
    }

    public void setNotificationCallback(Consumer<Void> callback) {
        this.notificationCallback = callback;
    }

    private void loadDepenses() {
        List<Depense> list = depenseService.getAllByUser(currentUserId);
        depensesObservable.setAll(list);
        depensesList.setItems(depensesObservable);
        updateTotal();
    }

    private void updateTotal() {
        double total = depensesObservable.stream().mapToDouble(Depense::getMontant).sum();
        totalDepensesLabel.setText(String.format("Total : %.2f DT", total));
    }

    private void populateForm(Depense d) {
        montantField.setText(String.valueOf(d.getMontant()));
        descriptionField.setText(d.getDescription());
        if (d.getDateDepense() != null) dateDepenseField.setValue(d.getDateDepense());
        if (d.getCategorie() != null) categorieCombo.setValue(d.getCategorie());
    }

    private void clearForm() {
        montantField.clear();
        descriptionField.clear();
        dateDepenseField.setValue(null);
        categorieCombo.setValue(null);
        depensesList.getSelectionModel().clearSelection();
    }

    private void onAjouter() {
        try {
            double montant = Double.parseDouble(montantField.getText().trim());
            String description = descriptionField.getText().trim();
            LocalDate date = dateDepenseField.getValue();
            String categorie = categorieCombo.getValue();

            if (montant <= 0) throw new IllegalArgumentException();
            if (categorie == null || categorie.isEmpty()) throw new IllegalArgumentException();

            Depense d = new Depense(montant, description, date != null ? date : LocalDate.now(), categorie, currentUserId);
            AddResponse response = depenseService.addWithMessage(d, categorie);

            if (response.getResult() == AddResult.FAILED) {
                DialogUtil.error("Erreur", "Ajout impossible: " + response.getMessage());
                return;
            }

            depensesObservable.add(0, d);
            updateTotal();
            clearForm();
            if (notificationCallback != null) notificationCallback.accept(null);
            DataChangeNotifier.notifyDataChanged();

            if (response.getResult() == AddResult.ADDED_EXCEEDED) {
                DialogUtil.error("⚠️ ALERTE", "Dépassement budget pour " + categorie + "\n" + response.getMessage());
            } else {
                DialogUtil.info("Succès", "Dépense ajoutée");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Montant invalide ou champs manquants");
        }
    }

    private void onSupprimer() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (DialogUtil.confirm("Confirmation", "Supprimer cette dépense ?")) {
            if (depenseService.delete(sel.getId())) {
                depensesObservable.remove(sel);
                updateTotal();
                clearForm();
                if (notificationCallback != null) notificationCallback.accept(null);
                DataChangeNotifier.notifyDataChanged();
                ToastNotification.success("Succès", "Dépense supprimée");
            } else {
                DialogUtil.error("Erreur", "Suppression impossible");
            }
        }
    }

    private void onModifier() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            double montant = Double.parseDouble(montantField.getText().trim());
            String description = descriptionField.getText().trim();
            LocalDate date = dateDepenseField.getValue();
            String categorie = categorieCombo.getValue();

            sel.setMontant(montant);
            sel.setDescription(description);
            sel.setDateDepense(date);
            sel.setCategorie(categorie);

            if (depenseService.update(sel)) {
                depensesList.refresh();
                updateTotal();
                clearForm();
                if (notificationCallback != null) notificationCallback.accept(null);
                DataChangeNotifier.notifyDataChanged();
                DialogUtil.info("Succès", "Dépense modifiée");
            } else {
                DialogUtil.error("Erreur", "Modification impossible");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Données invalides");
        }
    }

    @FXML
    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartWallet Admin Dashboard");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Erreur", "Impossible de retourner au dashboard");
        }
    }
}