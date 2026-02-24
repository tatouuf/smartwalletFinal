package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.services.PlanningService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PlanningsController {

    @FXML
    private TextField nomField;

    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> statutCombo;

    @FXML
    private TextField revenuPrevuField;

    @FXML
    private TextField epargnePrevueField;

    @FXML
    private TextField pourcentageEpargneField;

    @FXML
    private ComboBox<String> moisCombo;

    @FXML
    private ComboBox<String> anneeCombo;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private ListView<Planning> planningsList;

    @FXML
    private Label totalPlanningsLabel;


    private PlanningService planningService = new PlanningService();
    private ObservableList<Planning> planningsObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // types et statuts par défaut
        typeCombo.getItems().addAll("Mensuel", "Annuel", "Objectif");
        statutCombo.getItems().addAll("EN_COURS", "TERMINE", "ANNULE");

        moisCombo.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12");
        anneeCombo.getItems().addAll("2024","2025","2026","2027");

        loadPlannings();

        ajouterBtn.setOnAction(e -> onAjouter());
        supprimerBtn.setOnAction(e -> onSupprimer());
        modifierBtn.setOnAction(e -> onModifier());

        planningsList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private void loadPlannings() {
        planningsObservable.setAll(planningService.getAllByUser(0));
        planningsList.setItems(planningsObservable);
        updateTotal();
    }

    private void updateTotal() {
        int total = planningsObservable.size();
        totalPlanningsLabel.setText("Total plannings: " + total);
    }

    private void populateForm(Planning p) {
        nomField.setText(p.getNom());
        descriptionField.setText(p.getDescription());
        typeCombo.setValue(p.getType());
        statutCombo.setValue(p.getStatut());
        if (p.getMois() != null) moisCombo.setValue(String.valueOf(p.getMois()));
        if (p.getAnnee() != null) anneeCombo.setValue(String.valueOf(p.getAnnee()));
        revenuPrevuField.setText(String.valueOf(p.getRevenuPrevu()));
        epargnePrevueField.setText(String.valueOf(p.getEpargnePrevue()));
        pourcentageEpargneField.setText(p.getPourcentageEpargne() != null ? String.valueOf(p.getPourcentageEpargne()) : "");
    }

    private void clearForm() {
        nomField.clear();
        descriptionField.clear();
        typeCombo.setValue(null);
        statutCombo.setValue(null);
        revenuPrevuField.clear();
        epargnePrevueField.clear();
        pourcentageEpargneField.clear();
        moisCombo.setValue(null);
        anneeCombo.setValue(null);
        planningsList.getSelectionModel().clearSelection();
    }

    private void onAjouter() {
        try {
            String nom = nomField.getText().trim();
            String type = typeCombo.getValue();
            String description = descriptionField.getText().trim();
            String statut = statutCombo.getValue();
            Integer mois = moisCombo.getValue() != null ? Integer.parseInt(moisCombo.getValue()) : null;
            Integer annee = anneeCombo.getValue() != null ? Integer.parseInt(anneeCombo.getValue()) : null;
            double revenu = revenuPrevuField.getText().isEmpty() ? 0.0 : Double.parseDouble(revenuPrevuField.getText().trim());
            double epargne = epargnePrevueField.getText().isEmpty() ? 0.0 : Double.parseDouble(epargnePrevueField.getText().trim());
            Integer pourcentage = pourcentageEpargneField.getText().isEmpty() ? null : Integer.parseInt(pourcentageEpargneField.getText().trim());

            Planning p = new Planning();
            p.setUserId(0);
            p.setNom(nom);
            p.setType(type);
            p.setDescription(description);
            p.setStatut(statut);
            p.setMois(mois);
            p.setAnnee(annee);
            p.setRevenuPrevu(revenu);
            p.setEpargnePrevue(epargne);
            p.setPourcentageEpargne(pourcentage);

            boolean ok = planningService.addOrUpdate(p);
            if (ok) {
                planningsObservable.add(0, p);
                updateTotal();
                clearForm();
                DialogUtil.info("Succès", "Planning ajouté");
            } else {
                DialogUtil.error("Erreur", "Impossible d'ajouter le planning");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Données invalides");
        }
    }

    private void onSupprimer() {
        Planning sel = planningsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        boolean ok = planningService.delete(sel.getId());
        if (ok) {
            planningsObservable.remove(sel);
            updateTotal();
            clearForm();
            DialogUtil.info("Succès", "Planning supprimé");
        } else {
            DialogUtil.error("Erreur", "Impossible de supprimer le planning");
        }
    }

    private void onModifier() {
        Planning sel = planningsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            String nom = nomField.getText().trim();
            String type = typeCombo.getValue();
            String description = descriptionField.getText().trim();
            String statut = statutCombo.getValue();
            Integer mois = moisCombo.getValue() != null ? Integer.parseInt(moisCombo.getValue()) : null;
            Integer annee = anneeCombo.getValue() != null ? Integer.parseInt(anneeCombo.getValue()) : null;
            double revenu = revenuPrevuField.getText().isEmpty() ? 0.0 : Double.parseDouble(revenuPrevuField.getText().trim());
            double epargne = epargnePrevueField.getText().isEmpty() ? 0.0 : Double.parseDouble(epargnePrevueField.getText().trim());
            Integer pourcentage = pourcentageEpargneField.getText().isEmpty() ? null : Integer.parseInt(pourcentageEpargneField.getText().trim());

            sel.setNom(nom);
            sel.setType(type);
            sel.setDescription(description);
            sel.setStatut(statut);
            sel.setMois(mois);
            sel.setAnnee(annee);
            sel.setRevenuPrevu(revenu);
            sel.setEpargnePrevue(epargne);
            sel.setPourcentageEpargne(pourcentage);

            boolean ok = planningService.update(sel);
            if (ok) {
                planningsList.refresh();
                updateTotal();
                clearForm();
                DialogUtil.info("Succès", "Planning mis à jour");
            } else {
                DialogUtil.error("Erreur", "Impossible de mettre à jour le planning");
            }
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Données invalides");
        }
    }
}