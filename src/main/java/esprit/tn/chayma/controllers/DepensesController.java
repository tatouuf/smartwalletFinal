package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.services.AddResponse;
import esprit.tn.chayma.services.AddResult;
import esprit.tn.chayma.services.DepenseService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

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
    private ComboBox<String> filterCategorieCombo;


    @FXML
    private ComboBox<String> filterMoisCombo;


    @FXML
    private ComboBox<String> filterAnneeCombo;


    @FXML
    private ListView<Depense> depensesList;


    @FXML
    private Label totalDepensesLabel;


    private DepenseService depenseService = new DepenseService();
    private ObservableList<Depense> depensesObservable = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // Initialiser catégories (exemples)
        categorieCombo.getItems().addAll("Alimentation", "Transport", "Logement", "Loisirs", "Santé", "Autres");
        filterCategorieCombo.getItems().addAll(categorieCombo.getItems());

        // Charger la liste des dépenses (toutes si pas d'utilisateur disponible)
        loadDepenses();

        // Actions boutons
        ajouterBtn.setOnAction(e -> onAjouter());
        supprimerBtn.setOnAction(e -> onSupprimer());
        modifierBtn.setOnAction(e -> onModifier());

        depensesList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private void loadDepenses() {
        List<Depense> list = depenseService.getAll();
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

            Depense d = new Depense(montant, description, date, categorie, 0);
            AddResponse response = depenseService.addWithMessage(d);
            if (response.getResult() == AddResult.FAILED) {
                DialogUtil.error("Erreur", "Impossible d'ajouter la dépense: " + response.getMessage());
                return;
            }

            // Toujours ajouter à la liste locale pour affichage
            depensesObservable.add(0, d);
            updateTotal();
            clearForm();

            if (response.getResult() == AddResult.ADDED_EXCEEDED) {
                DialogUtil.info("Alerte budget", "Il y a dépassement dans les dépenses: " + response.getMessage());
            } else {
                DialogUtil.info("Succès", response.getMessage());
            }

        } catch (Exception e) {
            DialogUtil.error("Erreur", "Montant invalide ou champs manquants");
        }
    }

    private void onSupprimer() {
        Depense sel = depensesList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        boolean ok = depenseService.delete(sel.getId());
        if (ok) {
            depensesObservable.remove(sel);
            updateTotal();
            clearForm();
            DialogUtil.info("Succès", "Dépense supprimée");
        } else {
            DialogUtil.error("Erreur", "Impossible de supprimer la dépense");
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

            boolean ok = depenseService.update(sel);
            if (ok) {
                depensesList.refresh();
                updateTotal();
                clearForm();
                DialogUtil.info("Succès", "Dépense mise à jour");
            } else {
                DialogUtil.error("Erreur", "Impossible de mettre à jour la dépense");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Montant invalide ou champs manquants");
        }
    }
}