package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.services.PlanningService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PlanningsController {

    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField revenuPrevuField;
    @FXML private TextField epargnePrevueField;
    @FXML private TextField pourcentageEpargneField;
    @FXML private ComboBox<String> moisCombo;
    @FXML private ComboBox<String> anneeCombo;
    @FXML private Button ajouterBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private ListView<Planning> planningsList;
    @FXML private Label totalPlanningsLabel;

    private final PlanningService planningService = new PlanningService();
    private final ObservableList<Planning> planningsObservable = FXCollections.observableArrayList();

    // ==========================
    // INITIALIZE
    // ==========================
    @FXML
    public void initialize() {

        typeCombo.getItems().addAll("MENSUEL", "TRIMESTRIEL", "ANNUEL");
        statutCombo.getItems().addAll("EN_COURS", "TERMINE", "ANNULE");
        moisCombo.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12");
        anneeCombo.getItems().addAll("2024","2025","2026","2027");

        loadPlannings();

        ajouterBtn.setOnAction(e -> onAjouter());
        modifierBtn.setOnAction(e -> onModifier());
        supprimerBtn.setOnAction(e -> onSupprimer());

        planningsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });
    }

    // ==========================
    // GET CURRENT USER (A ADAPTER)
    // ==========================
    private int getCurrentUserId() {
        // ⚠️ Remplace par ton système de session
        return 1;
    }

    // ==========================
    // LOAD DATA
    // ==========================
    private void loadPlannings() {
        try {
            planningsObservable.setAll(
                    planningService.getAllByUser(getCurrentUserId())
            );
            planningsList.setItems(planningsObservable);
            updateTotal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotal() {
        totalPlanningsLabel.setText("Total plannings: " + planningsObservable.size());
    }

    // ==========================
    // POPULATE FORM
    // ==========================
    private void populateForm(Planning p) {

        nomField.setText(p.getNom());
        descriptionField.setText(p.getDescription());
        typeCombo.setValue(p.getType());
        statutCombo.setValue(p.getStatut());

        moisCombo.setValue(
                p.getMois() != null ? String.valueOf(p.getMois()) : null
        );

        anneeCombo.setValue(
                p.getAnnee() != null ? String.valueOf(p.getAnnee()) : null
        );

        revenuPrevuField.setText(String.valueOf(p.getRevenuPrevu()));
        epargnePrevueField.setText(String.valueOf(p.getEpargnePrevue()));

        pourcentageEpargneField.setText(
                p.getPourcentageEpargne() != null
                        ? String.valueOf(p.getPourcentageEpargne())
                        : ""
        );
    }

    // ==========================
    // CLEAR FORM
    // ==========================
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

    // ==========================
    // AJOUTER
    // ==========================
    private void onAjouter() {

        try {

            int userId = getCurrentUserId();

            String nom = nomField.getText().trim();
            if (nom.isEmpty()) {
                DialogUtil.error("Erreur", "Le nom est obligatoire");
                return;
            }

            Integer mois = moisCombo.getValue() != null ?
                    Integer.parseInt(moisCombo.getValue()) : null;

            Integer annee = anneeCombo.getValue() != null ?
                    Integer.parseInt(anneeCombo.getValue()) : null;

            if (planningService.exists(userId, mois, annee)) {
                DialogUtil.error("Erreur", "Un planning existe déjà pour ce mois et cette année");
                return;
            }

            double revenu;
            double epargne;

            try {
                revenu = revenuPrevuField.getText().isEmpty() ? 0 :
                        Double.parseDouble(revenuPrevuField.getText());
            } catch (NumberFormatException e) {
                DialogUtil.error("Erreur", "Revenu invalide");
                return;
            }

            try {
                epargne = epargnePrevueField.getText().isEmpty() ? 0 :
                        Double.parseDouble(epargnePrevueField.getText());
            } catch (NumberFormatException e) {
                DialogUtil.error("Erreur", "Épargne invalide");
                return;
            }

            Double pourcentage;
            try {
                pourcentage = pourcentageEpargneField.getText().isEmpty() ?
                        20.0 :
                        Double.parseDouble(pourcentageEpargneField.getText());
            } catch (NumberFormatException e) {
                DialogUtil.error("Erreur", "Pourcentage invalide");
                return;
            }

            if (pourcentage < 0) pourcentage = 0.0;
            if (pourcentage > 100) pourcentage = 100.0;

            Planning p = new Planning();
            p.setUserId(userId);
            p.setNom(nom);
            p.setDescription(descriptionField.getText());
            p.setType(typeCombo.getValue() != null ? typeCombo.getValue() : "MENSUEL");
            p.setStatut(statutCombo.getValue() != null ? statutCombo.getValue() : "EN_COURS");
            p.setMois(mois);
            p.setAnnee(annee);
            p.setRevenuPrevu(revenu);
            p.setEpargnePrevue(epargne);
            p.setPourcentageEpargne(pourcentage);

            if (planningService.add(p)) {
                planningsObservable.add(0, p);
                updateTotal();
                clearForm();
                DialogUtil.info("Succès", "Planning ajouté avec succès");
            } else {
                DialogUtil.error("Erreur", "Insertion échouée");
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Erreur", "Données invalides");
        }
    }

    // ==========================
    // MODIFIER
    // ==========================
    private void onModifier() {

        Planning selected = planningsList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {

            selected.setNom(nomField.getText());
            selected.setDescription(descriptionField.getText());
            selected.setType(typeCombo.getValue());
            selected.setStatut(statutCombo.getValue());

            selected.setMois(
                    moisCombo.getValue() != null ?
                            Integer.parseInt(moisCombo.getValue()) : null
            );

            selected.setAnnee(
                    anneeCombo.getValue() != null ?
                            Integer.parseInt(anneeCombo.getValue()) : null
            );

            selected.setRevenuPrevu(Double.parseDouble(revenuPrevuField.getText()));
            selected.setEpargnePrevue(Double.parseDouble(epargnePrevueField.getText()));

            selected.setPourcentageEpargne(
                    pourcentageEpargneField.getText().isEmpty() ?
                            20.0 :
                            Double.parseDouble(pourcentageEpargneField.getText())
            );

            if (planningService.update(selected)) {
                loadPlannings();
                clearForm();
                DialogUtil.info("Succès", "Planning modifié");
            } else {
                DialogUtil.error("Erreur", "Modification échouée");
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Erreur", "Données invalides");
        }
    }

    // ==========================
    // SUPPRIMER
    // ==========================
    private void onSupprimer() {

        Planning selected = planningsList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (planningService.delete(selected.getId())) {
            loadPlannings();
            clearForm();
            DialogUtil.info("Succès", "Planning supprimé");
        } else {
            DialogUtil.error("Erreur", "Suppression échouée");
        }
    }
}