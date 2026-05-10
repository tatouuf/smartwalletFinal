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
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.DataChangeNotifier;
import utils.Session;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DepensesClientController {

    // ================= FXML COMPOSANTS =================
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
    @FXML private Label depensesMoisLabel;
    @FXML private Label budgetRestantLabel;
    @FXML private PieChart depensesChart;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterMoisCombo;
    @FXML private ComboBox<Integer> filterAnneeCombo;
    @FXML private Label bienvenueLabel;

    // ================= SERVICES =================
    private DepenseService depenseService = new DepenseService();
    private NotificationService notificationService = new NotificationService();
    private ObservableList<Depense> depensesObservable = FXCollections.observableArrayList();
    private int currentUserId;
    private double budgetMensuel = 1000; // Budget mensuel par défaut (peut venir de la base)

    // Callback pour mettre à jour le module notifications
    private Consumer<Void> notificationCallback = null;

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur connecté
        if (Session.getCurrentUser() != null) {
            currentUserId = Session.getCurrentUser().getId();
            String userName = Session.getCurrentUser().getNom();
            if (bienvenueLabel != null) {
                bienvenueLabel.setText("👋 Bonjour, " + userName + " !");
            }
        } else {
            currentUserId = 1; // Fallback pour test
        }

        // Initialiser les catégories
        categorieCombo.getItems().addAll(
                "🍽️ Alimentation",
                "🚗 Transport",
                "🏠 Logement",
                "🎬 Loisirs",
                "🏥 Santé",
                "📚 Éducation",
                "👕 Vêtements",
                "💡 Factures",
                "📱 Télécom",
                "💰 Épargne",
                "🎁 Cadeaux",
                "✈️ Voyages",
                "🔄 Autres"
        );

        // Initialiser les mois pour le filtre
        filterMoisCombo.getItems().addAll(
                "Tous", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        );
        filterMoisCombo.setValue("Tous");

        // Initialiser les années pour le filtre
        int currentYear = LocalDate.now().getYear();
        filterAnneeCombo.getItems().addAll(currentYear - 2, currentYear - 1, currentYear, currentYear + 1);
        filterAnneeCombo.setValue(currentYear);

        // Charger les données
        loadDepenses();

        // Configurer la recherche
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> filterDepenses());
        }

        // Configurer les filtres
        filterMoisCombo.valueProperty().addListener((obs, old, newVal) -> filterDepenses());
        filterAnneeCombo.valueProperty().addListener((obs, old, newVal) -> filterDepenses());

        // Actions boutons
        ajouterBtn.setOnAction(e -> onAjouter());
        supprimerBtn.setOnAction(e -> onSupprimer());
        modifierBtn.setOnAction(e -> onModifier());

        // Sélection dans la liste
        depensesList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });

        // Mettre à jour les statistiques
        updateStats();

        System.out.println("[DepensesClientController] Initialisation complète pour utilisateur: " + currentUserId);
    }

    private void loadDepenses() {
        List<Depense> list = depenseService.getAllByUser(currentUserId);
        depensesObservable.setAll(list);
        depensesList.setItems(depensesObservable);
        updateTotal();
        updateChart();
    }

    private void filterDepenses() {
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        String selectedMois = filterMoisCombo.getValue();
        Integer selectedAnnee = filterAnneeCombo.getValue();

        List<Depense> filtered = depenseService.getAllByUser(currentUserId).stream()
                .filter(d -> {
                    // Filtre par recherche
                    boolean matchesSearch = searchText.isEmpty() ||
                            d.getDescription().toLowerCase().contains(searchText) ||
                            (d.getCategorie() != null && d.getCategorie().toLowerCase().contains(searchText));

                    // Filtre par mois
                    boolean matchesMois = "Tous".equals(selectedMois) ||
                            (d.getDateDepense() != null &&
                                    getMoisName(d.getDateDepense().getMonthValue()).equals(selectedMois));

                    // Filtre par année
                    boolean matchesAnnee = selectedAnnee == null ||
                            (d.getDateDepense() != null && d.getDateDepense().getYear() == selectedAnnee);

                    return matchesSearch && matchesMois && matchesAnnee;
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
        if (totalDepensesLabel != null) {
            totalDepensesLabel.setText(String.format("%.2f DT", total));
        }

        // Dépenses du mois en cours
        LocalDate now = LocalDate.now();
        double totalMois = depensesObservable.stream()
                .filter(d -> d.getDateDepense() != null &&
                        d.getDateDepense().getMonthValue() == now.getMonthValue() &&
                        d.getDateDepense().getYear() == now.getYear())
                .mapToDouble(Depense::getMontant)
                .sum();

        if (depensesMoisLabel != null) {
            depensesMoisLabel.setText(String.format("%.2f DT", totalMois));
        }

        // Budget restant
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
        if (depensesChart == null) return;

        depensesChart.getData().clear();

        // Regrouper par catégorie
        java.util.Map<String, Double> sumByCat = depensesObservable.stream()
                .filter(d -> d.getCategorie() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getCategorie(),
                        Collectors.summingDouble(Depense::getMontant)
                ));

        for (java.util.Map.Entry<String, Double> e : sumByCat.entrySet()) {
            PieChart.Data data = new PieChart.Data(e.getKey() + " (" + String.format("%.2f", e.getValue()) + " DT)", e.getValue());
            depensesChart.getData().add(data);
        }

        if (depensesChart.getData().isEmpty()) {
            PieChart.Data emptyData = new PieChart.Data("Aucune donnée", 1);
            depensesChart.getData().add(emptyData);
        }
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

            if (montant <= 0) {
                ToastNotification.error("Erreur", "Le montant doit être positif");
                return;
            }

            if (categorie == null || categorie.isEmpty()) {
                ToastNotification.error("Erreur", "Veuillez choisir une catégorie");
                return;
            }

            // Nettoyer la catégorie (enlever l'emoji si présent)
            String cleanCategorie = categorie.replaceAll("^[^A-Za-zÀ-ÿ]+", "").trim();

            Depense d = new Depense(montant, description, date != null ? date : LocalDate.now(), cleanCategorie, currentUserId);

            // CORRECTION: Passer les 2 paramètres
            AddResponse response = depenseService.addWithMessage(d, cleanCategorie);

            if (response.getResult() == AddResult.FAILED) {
                ToastNotification.error("❌ Erreur", "Impossible d'ajouter la dépense: " + response.getMessage());
                return;
            }

            // Ajouter à la liste
            depensesObservable.add(0, d);
            updateStats();
            clearForm();

            // Notification
            if (notificationCallback != null) {
                notificationCallback.accept(null);
            }

            // 🔔 NOTIFIER LE DASHBOARD QU'UNE MODIFICATION A EU LIEU
            DataChangeNotifier.notifyDataChanged();

            if (response.getResult() == AddResult.ADDED_EXCEEDED) {
                ToastNotification.error("⚠️ ALERTE DÉPASSEMENT", response.getMessage());
                DialogUtil.error("⚠️ ALERTE BUDGET",
                        "ATTENTION! Vous avez dépassé votre budget pour la catégorie: " + categorie + "\n\n" +
                                response.getMessage() + "\n\n✓ Une notification a été créée.");
            } else {
                ToastNotification.success("✓ Succès", "Dépense ajoutée: " + montant + " DT");
            }

        } catch (NumberFormatException e) {
            ToastNotification.error("❌ Erreur", "Montant invalide");
        } catch (Exception e) {
            ToastNotification.error("❌ Erreur", "Une erreur s'est produite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onSupprimer() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            ToastNotification.warning("Attention", "Veuillez sélectionner une dépense");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la dépense");
        confirm.setContentText("Voulez-vous vraiment supprimer cette dépense ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = depenseService.delete(sel.getId());
                if (ok) {
                    depensesObservable.remove(sel);
                    updateStats();
                    clearForm();
                    ToastNotification.success("✓ Succès", "Dépense supprimée");

                    if (notificationCallback != null) {
                        notificationCallback.accept(null);
                    }

                    // 🔔 NOTIFIER LE DASHBOARD
                    DataChangeNotifier.notifyDataChanged();
                } else {
                    ToastNotification.error("❌ Erreur", "Impossible de supprimer");
                }
            }
        });
    }

    private void onModifier() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            ToastNotification.warning("Attention", "Veuillez sélectionner une dépense");
            return;
        }

        try {
            double montant = Double.parseDouble(montantField.getText().trim());
            String description = descriptionField.getText().trim();
            LocalDate date = dateDepenseField.getValue();
            String categorie = categorieCombo.getValue();

            sel.setMontant(montant);
            sel.setDescription(description);
            sel.setDateDepense(date);
            sel.setCategorie(categorie);

            boolean ok = depenseService.update(sel);
            if (ok) {
                depensesList.refresh();
                updateStats();
                clearForm();
                ToastNotification.success("✓ Succès", "Dépense mise à jour");

                if (notificationCallback != null) {
                    notificationCallback.accept(null);
                }

                // 🔔 NOTIFIER LE DASHBOARD
                DataChangeNotifier.notifyDataChanged();
            } else {
                ToastNotification.error("❌ Erreur", "Impossible de modifier");
            }
        } catch (Exception e) {
            ToastNotification.error("❌ Erreur", "Données invalides");
        }
    }

    @FXML
    private void setBudgetMensuel() {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(budgetMensuel));
        dialog.setTitle("Budget mensuel");
        dialog.setHeaderText("Définir votre budget mensuel");
        dialog.setContentText("Budget mensuel (DT):");

        dialog.showAndWait().ifPresent(result -> {
            try {
                budgetMensuel = Double.parseDouble(result);
                updateTotal();
                ToastNotification.success("✓ Succès", "Budget mis à jour: " + budgetMensuel + " DT");
            } catch (NumberFormatException e) {
                ToastNotification.error("❌ Erreur", "Montant invalide");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setNotificationCallback(Consumer<Void> callback) {
        this.notificationCallback = callback;
    }
}