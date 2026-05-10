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
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import utils.DataChangeNotifier;
import utils.Session;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DepensesClientController {

    @FXML private TextField montantField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dateDepenseField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private Button ajouterBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private ListView<Depense> depensesList;
    @FXML private Label totalDepensesLabel;
    @FXML private Label depensesMoisLabel;
    @FXML private Label budgetRestantLabel;
    @FXML private PieChart depensesChart;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterMoisCombo;
    @FXML private ComboBox<Integer> filterAnneeCombo;
    @FXML private Label bienvenueLabel;

    private DepenseService depenseService = new DepenseService();
    private NotificationService notificationService = new NotificationService();
    private ObservableList<Depense> depensesObservable = FXCollections.observableArrayList();
    private int currentUserId;
    private double budgetMensuel = 1000;
    private Consumer<Void> notificationCallback = null;

    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null) {
            currentUserId = Session.getCurrentUser().getId();
            bienvenueLabel.setText("👋 Bonjour, " + Session.getCurrentUser().getNom() + " !");
        } else {
            currentUserId = 1;
        }

        // Catégories avec emojis
        categorieCombo.getItems().addAll(
                "🍽️ Alimentation", "🚗 Transport", "🏠 Logement", "🎬 Loisirs",
                "🏥 Santé", "📚 Éducation", "👕 Vêtements", "💡 Factures",
                "📱 Télécom", "💰 Épargne", "🎁 Cadeaux", "✈️ Voyages", "🔄 Autres"
        );

        // Filtres mois
        filterMoisCombo.getItems().addAll("Tous", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre");
        filterMoisCombo.setValue("Tous");

        int currentYear = LocalDate.now().getYear();
        filterAnneeCombo.getItems().addAll(currentYear - 2, currentYear - 1, currentYear, currentYear + 1);
        filterAnneeCombo.setValue(currentYear);

        loadDepenses();

        searchField.textProperty().addListener((obs, old, newVal) -> filterDepenses());
        filterMoisCombo.valueProperty().addListener((obs, old, newVal) -> filterDepenses());
        filterAnneeCombo.valueProperty().addListener((obs, old, newVal) -> filterDepenses());

        ajouterBtn.setOnAction(e -> onAjouter());
        supprimerBtn.setOnAction(e -> onSupprimer());
        modifierBtn.setOnAction(e -> onModifier());

        depensesList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });

        updateStats();
    }

    private void loadDepenses() {
        depensesObservable.setAll(depenseService.getAllByUser(currentUserId));
        depensesList.setItems(depensesObservable);
        updateTotal();
        updateChart();
    }

    private void filterDepenses() {
        String searchText = searchField.getText().toLowerCase();
        String selectedMois = filterMoisCombo.getValue();
        Integer selectedAnnee = filterAnneeCombo.getValue();

        List<Depense> filtered = depenseService.getAllByUser(currentUserId).stream()
                .filter(d -> {
                    boolean matchSearch = searchText.isEmpty() ||
                            d.getDescription().toLowerCase().contains(searchText) ||
                            (d.getCategorie() != null && d.getCategorie().toLowerCase().contains(searchText));
                    boolean matchMois = "Tous".equals(selectedMois) ||
                            (d.getDateDepense() != null &&
                                    getMoisName(d.getDateDepense().getMonthValue()).equals(selectedMois));
                    boolean matchAnnee = selectedAnnee == null ||
                            (d.getDateDepense() != null && d.getDateDepense().getYear() == selectedAnnee);
                    return matchSearch && matchMois && matchAnnee;
                })
                .collect(Collectors.toList());
        depensesObservable.setAll(filtered);
        updateTotal();
        updateChart();
    }

    private String getMoisName(int mois) {
        return LocalDate.of(2000, mois, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
    }

    private void updateTotal() {
        double total = depensesObservable.stream().mapToDouble(Depense::getMontant).sum();
        totalDepensesLabel.setText(String.format("%.2f DT", total));

        LocalDate now = LocalDate.now();
        double totalMois = depensesObservable.stream()
                .filter(d -> d.getDateDepense() != null &&
                        d.getDateDepense().getMonthValue() == now.getMonthValue() &&
                        d.getDateDepense().getYear() == now.getYear())
                .mapToDouble(Depense::getMontant).sum();
        depensesMoisLabel.setText(String.format("%.2f DT", totalMois));

        double budgetRestant = budgetMensuel - totalMois;
        if (budgetRestantLabel != null) {
            if (budgetRestant < 0) {
                budgetRestantLabel.setText(String.format("⚠️ %.2f DT (Dépassé)", Math.abs(budgetRestant)));
                budgetRestantLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            } else {
                budgetRestantLabel.setText(String.format("%.2f DT", budgetRestant));
                budgetRestantLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            }
        }
    }

    private void updateChart() {
        depensesChart.getData().clear();
        java.util.Map<String, Double> sumByCat = depensesObservable.stream()
                .filter(d -> d.getCategorie() != null)
                .collect(Collectors.groupingBy(Depense::getCategorie, Collectors.summingDouble(Depense::getMontant)));
        for (var e : sumByCat.entrySet()) {
            depensesChart.getData().add(new PieChart.Data(e.getKey() + " (" + String.format("%.2f", e.getValue()) + " DT)", e.getValue()));
        }
        if (depensesChart.getData().isEmpty())
            depensesChart.getData().add(new PieChart.Data("Aucune donnée", 1));
    }

    private void updateStats() {
        updateTotal();
        updateChart();
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

            if (montant <= 0) { ToastNotification.error("Erreur", "Montant positif requis"); return; }
            if (categorie == null) { ToastNotification.error("Erreur", "Choisissez une catégorie"); return; }

            String cleanCategorie = categorie.replaceAll("^[^A-Za-zÀ-ÿ]+", "").trim();
            Depense d = new Depense(montant, description, date != null ? date : LocalDate.now(), cleanCategorie, currentUserId);
            AddResponse response = depenseService.addWithMessage(d, cleanCategorie);

            if (response.getResult() == AddResult.FAILED) {
                ToastNotification.error("Erreur", "Ajout impossible: " + response.getMessage());
                return;
            }

            depensesObservable.add(0, d);
            updateStats();
            clearForm();
            if (notificationCallback != null) notificationCallback.accept(null);
            DataChangeNotifier.notifyDataChanged();

            if (response.getResult() == AddResult.ADDED_EXCEEDED) {
                DialogUtil.error("⚠️ ALERTE DÉPASSEMENT",
                        "Attention! Vous avez dépassé votre budget pour " + categorie + "\n" + response.getMessage());
            } else {
                ToastNotification.success("Succès", "Dépense ajoutée: " + montant + " DT");
            }
        } catch (NumberFormatException e) {
            ToastNotification.error("Erreur", "Montant invalide");
        }
    }

    private void onSupprimer() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (DialogUtil.confirm("Confirmation", "Supprimer cette dépense ?")) {
            if (depenseService.delete(sel.getId())) {
                depensesObservable.remove(sel);
                updateStats();
                clearForm();
                if (notificationCallback != null) notificationCallback.accept(null);
                DataChangeNotifier.notifyDataChanged();
                ToastNotification.success("Succès", "Dépense supprimée");
            } else {
                ToastNotification.error("Erreur", "Suppression impossible");
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
                updateStats();
                clearForm();
                if (notificationCallback != null) notificationCallback.accept(null);
                DataChangeNotifier.notifyDataChanged();
                ToastNotification.success("Succès", "Dépense modifiée");
            } else {
                ToastNotification.error("Erreur", "Modification impossible");
            }
        } catch (Exception e) {
            ToastNotification.error("Erreur", "Données invalides");
        }
    }

    public void setNotificationCallback(Consumer<Void> callback) {
        this.notificationCallback = callback;
    }
}