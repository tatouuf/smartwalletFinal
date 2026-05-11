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
import utils.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class DepensesController {

    @FXML
    private TextField montantField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dateDepenseField;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private Button btnRetourDashboard;  // ✅ AJOUTÉ

    @FXML
    private ListView<Depense> depensesList;

    @FXML
    private Label totalDepensesLabel;

    private DepenseService depenseService = new DepenseService();
    private NotificationService notificationService = new NotificationService();
    private ObservableList<Depense> depensesObservable = FXCollections.observableArrayList();
    private int currentUserId = 1;

    // Callback pour mettre à jour le module notifications
    private Consumer<Void> notificationCallback = null;

    @FXML
    public void initialize() {
        // Initialiser catégories
        categorieCombo.getItems().addAll("Alimentation", "Transport", "Logement", "Loisirs", "Santé", "Autres");

        // Charger la liste des dépenses
        loadDepenses();

        // Actions boutons
        ajouterBtn.setOnAction(e -> onAjouter());
        supprimerBtn.setOnAction(e -> onSupprimer());
        modifierBtn.setOnAction(e -> onModifier());

        depensesList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });

        System.out.println("[DepensesController] Initialisation complète");
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadDepenses();
    }

    /**
     * Définit le callback pour mettre à jour le module notifications
     */
    public void setNotificationCallback(Consumer<Void> callback) {
        this.notificationCallback = callback;
        System.out.println("[DepensesController] Callback notifications configuré");
    }

    private void loadDepenses() {
        List<Depense> list = depenseService.getAllByUser(currentUserId);
        depensesObservable.setAll(list);
        depensesList.setItems(depensesObservable);
        updateTotal();
    }

    private void updateTotal() {
        double total = depensesObservable.stream().mapToDouble(Depense::getMontant).sum();
        if (totalDepensesLabel != null) {
            totalDepensesLabel.setText(String.format("Total : %.2f DT", total));
        }
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
                DialogUtil.error("Erreur", "Le montant doit être positif");
                ToastNotification.error("Erreur", "Montant invalide");
                return;
            }

            if (categorie == null || categorie.isEmpty()) {
                DialogUtil.error("Erreur", "Veuillez choisir une catégorie");
                ToastNotification.error("Erreur", "Catégorie manquante");
                return;
            }

            Depense d = new Depense(montant, description, date != null ? date : LocalDate.now(), categorie, currentUserId);
            AddResponse response = depenseService.addWithMessage(d, categorie);

            if (response.getResult() == AddResult.FAILED) {
                DialogUtil.error("Erreur", "Impossible d'ajouter la dépense: " + response.getMessage());
                ToastNotification.error("❌ Erreur", "Ajout échoué");
                return;
            }

            // Toujours ajouter à la liste locale pour affichage
            depensesObservable.add(0, d);
            updateTotal();
            clearForm();

            // NOTIFICATION CALLBACK: Mettre à jour le module notifications
            if (notificationCallback != null) {
                System.out.println("[DepensesController] Appel callback notifications");
                notificationCallback.accept(null);
            }

            if (response.getResult() == AddResult.ADDED_EXCEEDED) {
                System.out.println("[DepensesController] DÉPASSEMENT DÉTECTÉ: " + categorie);
                ToastNotification.error("⚠️ DÉPASSEMENT BUDGET", response.getMessage());
                DialogUtil.error("⚠️ ALERTE DÉPASSEMENT",
                        "ATTENTION! Vous avez dépassé votre budget pour la catégorie: " + categorie + "\n\n" +
                                response.getMessage() +
                                "\n\n✓ Une notification a été créée et enregistrée.\n" +
                                "Consultez votre page 'Notifications' pour plus de détails.");
            } else {
                System.out.println("[DepensesController] Dépense ajoutée: " + montant + " DT");
                ToastNotification.success("✓ Succès", "Dépense ajoutée: " + montant + " DT");
                DialogUtil.info("Succès", "✓ Dépense ajoutée avec succès\n\nMontant: " + montant + " DT\nCatégorie: " + categorie);
            }

        } catch (NumberFormatException e) {
            DialogUtil.error("Erreur", "Montant invalide - veuillez entrer un nombre");
            ToastNotification.error("❌ Erreur", "Montant invalide");
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Erreur: " + e.getMessage());
            ToastNotification.error("❌ Erreur", "Une erreur s'est produite");
            System.err.println("[DepensesController] ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onSupprimer() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogUtil.warning("Attention", "Veuillez sélectionner une dépense");
            return;
        }
        if (DialogUtil.confirm("Confirmation", "Êtes-vous sûr de vouloir supprimer cette dépense?")) {
            boolean ok = depenseService.delete(sel.getId());
            if (ok) {
                depensesObservable.remove(sel);
                updateTotal();
                clearForm();
                ToastNotification.success("✓ Succès", "Dépense supprimée");

                if (notificationCallback != null) {
                    notificationCallback.accept(null);
                }
            } else {
                DialogUtil.error("Erreur", "Impossible de supprimer la dépense");
                ToastNotification.error("❌ Erreur", "Suppression échouée");
            }
        }
    }

    private void onModifier() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogUtil.warning("Attention", "Veuillez sélectionner une dépense");
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
                updateTotal();
                clearForm();
                ToastNotification.success("✓ Succès", "Dépense mise à jour");

                if (notificationCallback != null) {
                    notificationCallback.accept(null);
                }
            } else {
                DialogUtil.error("Erreur", "Impossible de mettre à jour la dépense");
                ToastNotification.error("❌ Erreur", "Mise à jour échouée");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Montant invalide ou champs manquants");
            ToastNotification.error("❌ Erreur", "Erreur de mise à jour");
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
            System.out.println("✅ Retour au DashboardAdmin effectué");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au Dashboard !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}