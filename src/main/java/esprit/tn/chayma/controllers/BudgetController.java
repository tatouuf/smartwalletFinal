package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.services.BudgetService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BudgetController {

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private TextField montantMaxField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> moisCombo;

    @FXML
    private ComboBox<String> anneeCombo;

    @FXML
    private ProgressBar budgetProgressBar;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private ListView<Budget> budgetsList;

    @FXML
    private Label totalBudgetsLabel;


    private BudgetService budgetService = new BudgetService();
    private ObservableList<Budget> budgetsObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // catégories par défaut (exemple)
        categorieCombo.getItems().addAll("Alimentation", "Transport", "Logement", "Loisirs", "Santé", "Autres");

        // mois et années simples
        moisCombo.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12");
        anneeCombo.getItems().addAll("2024","2025","2026","2027");

        // Définir une cell factory lisible pour la ListView afin d'éviter d'afficher le nom de la classe
        budgetsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Budget b, boolean empty) {
                super.updateItem(b, empty);
                if (empty || b == null) {
                    setText(null);
                } else {
                    String period = "";
                    if (b.getMois() != null && b.getAnnee() != null) period = String.format(" (%02d/%d)", b.getMois(), b.getAnnee());
                    setText(String.format("%s - %.2f DT%s", b.getCategorie() != null ? b.getCategorie() : "(aucune)", b.getMontantMax(), period));
                }
            }
        });

        // charger budgets
        loadBudgets();

        ajouterBtn.setOnAction(e -> onAjouter());
        supprimerBtn.setOnAction(e -> onSupprimer());
        modifierBtn.setOnAction(e -> onModifier());

        budgetsList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private void loadBudgets() {
        // pour l'instant on charge tous les budgets
        budgetsObservable.setAll(budgetService.getAllByUser(0));
        budgetsList.setItems(budgetsObservable);
        updateTotal();
    }

    private void updateTotal() {
        double totalMax = budgetsObservable.stream().mapToDouble(Budget::getMontantMax).sum();
        totalBudgetsLabel.setText(String.format("Total max : %.2f DT", totalMax));
    }

    private void populateForm(Budget b) {
        montantMaxField.setText(String.valueOf(b.getMontantMax()));
        descriptionField.setText(b.getDescription());
        if (b.getMois() != null) moisCombo.setValue(String.valueOf(b.getMois()));
        if (b.getAnnee() != null) anneeCombo.setValue(String.valueOf(b.getAnnee()));
        if (b.getCategorie() != null) categorieCombo.setValue(b.getCategorie());

        // mettre à jour progress bar si montant_max > 0
        if (b.getMontantMax() > 0) {
            double progress = Math.min(1.0, b.getMontantActuel() / b.getMontantMax());
            budgetProgressBar.setProgress(progress);
        } else {
            budgetProgressBar.setProgress(0);
        }
    }

    private void clearForm() {
        montantMaxField.clear();
        descriptionField.clear();
        moisCombo.setValue(null);
        anneeCombo.setValue(null);
        categorieCombo.setValue(null);
        budgetsList.getSelectionModel().clearSelection();
        budgetProgressBar.setProgress(0);
    }

    private void onAjouter() {
        try {
            double montantMax = Double.parseDouble(montantMaxField.getText().trim());
            String description = descriptionField.getText().trim();
            Integer mois = moisCombo.getValue() != null ? Integer.parseInt(moisCombo.getValue()) : null;
            Integer annee = anneeCombo.getValue() != null ? Integer.parseInt(anneeCombo.getValue()) : null;
            String categorie = categorieCombo.getValue();

            Budget b = new Budget();
            b.setUserId(0);
            b.setMontantMax(montantMax);
            b.setDescription(description);
            b.setMois(mois);
            b.setAnnee(annee);
            b.setCategorie(categorie);
            b.setMontantActuel(0);

            boolean ok = budgetService.add(b);
            if (ok) {
                budgetsObservable.add(0, b);
                updateTotal();
                clearForm();
                DialogUtil.info("Succès", "Budget ajouté");
            } else {
                DialogUtil.error("Erreur", "Impossible d'ajouter le budget");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Montant invalide ou champs manquants");
        }
    }

    private void onSupprimer() {
        Budget sel = budgetsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        boolean ok = budgetService.delete(sel.getId());
        if (ok) {
            budgetsObservable.remove(sel);
            updateTotal();
            clearForm();
            DialogUtil.info("Succès", "Budget supprimé");
        } else {
            DialogUtil.error("Erreur", "Impossible de supprimer le budget");
        }
    }

    private void onModifier() {
        Budget sel = budgetsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            double montantMax = Double.parseDouble(montantMaxField.getText().trim());
            String description = descriptionField.getText().trim();
            Integer mois = moisCombo.getValue() != null ? Integer.parseInt(moisCombo.getValue()) : null;
            Integer annee = anneeCombo.getValue() != null ? Integer.parseInt(anneeCombo.getValue()) : null;
            String categorie = categorieCombo.getValue();

            sel.setMontantMax(montantMax);
            sel.setDescription(description);
            sel.setMois(mois);
            sel.setAnnee(annee);
            sel.setCategorie(categorie);

            boolean ok = budgetService.update(sel);
            if (ok) {
                budgetsList.refresh();
                updateTotal();
                clearForm();
                DialogUtil.info("Succès", "Budget mis à jour");
            } else {
                DialogUtil.error("Erreur", "Impossible de mettre à jour le budget");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Montant invalide ou champs manquants");
        }
    }
}