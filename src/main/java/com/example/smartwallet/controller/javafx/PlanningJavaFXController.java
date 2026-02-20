package com.example.smartwallet.controller.javafx;

import dao.PlanningDAO;
import com.example.smartwallet.model.Planning;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlanningJavaFXController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField revenuPrevuField;
    @FXML
    private TextField epargnePrevueField;
    @FXML
    private TextField pourcentageEpargneField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private ComboBox<Integer> moisCombo;
    @FXML
    private ComboBox<Integer> anneeCombo;
    @FXML
    private ComboBox<String> statutCombo;
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

    private PlanningDAO planningDAO = new PlanningDAO();
    private ObservableList<Planning> planningsData = FXCollections.observableArrayList();
    private int userId = 1; // User connecté
    private Planning planningActuel = null;

    // Stocker les IDs des plannings cochés
    private Set<Integer> idsSelectionnes = new HashSet<>();

    @FXML
    public void initialize() {
        setupListView();
        setupComboBoxes();
        loadPlannings();
        
        ajouterBtn.setOnAction(e -> ajouterPlanning());
        modifierBtn.setOnAction(e -> modifierPlanning());
        supprimerBtn.setOnAction(e -> supprimerPlanning());
        
        planningsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectPlanning(newVal));
    }

    private void setupListView() {
        planningsList.setCellFactory(lv -> new ListCell<Planning>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label label = new Label();
            private final HBox layout = new HBox(10);

            {
                layout.getChildren().addAll(checkBox, label);
            }

            @Override
            protected void updateItem(Planning planning, boolean empty) {
                super.updateItem(planning, empty);
                if (empty || planning == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String text = String.format("%s (%s) - %d/%d - Revenu: %.2f DT - Épargne: %.2f DT - %s",
                            planning.getNom(),
                            planning.getType(),
                            planning.getMois(),
                            planning.getAnnee(),
                            planning.getRevenuPrevu(),
                            planning.getEpargnePrevue(),
                            planning.getStatut());
                    label.setText(text);

                    checkBox.setOnAction(null);
                    checkBox.setSelected(idsSelectionnes.contains(planning.getId()));
                    checkBox.setOnAction(e -> {
                        if (checkBox.isSelected()) {
                            idsSelectionnes.add(planning.getId());
                        } else {
                            idsSelectionnes.remove(planning.getId());
                        }
                    });

                    setGraphic(layout);
                }
            }
        });
        planningsList.setItems(planningsData);
    }

    private void setupComboBoxes() {
        typeCombo.setItems(FXCollections.observableArrayList(
                "Personnel", "Familial", "Professionnel", "Retraite"
        ));

        statutCombo.setItems(FXCollections.observableArrayList(
                "En cours", "Terminé", "Suspendu", "Annulé"
        ));

        ObservableList<Integer> mois = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) mois.add(i);
        moisCombo.setItems(mois);
        moisCombo.setValue(LocalDate.now().getMonthValue());

        ObservableList<Integer> annees = FXCollections.observableArrayList();
        for (int i = 2020; i <= 2030; i++) annees.add(i);
        anneeCombo.setItems(annees);
        anneeCombo.setValue(LocalDate.now().getYear());
    }

    private void loadPlannings() {
        idsSelectionnes.clear();
        planningsData.clear();
        List<Planning> plannings = planningDAO.obtenirTousPlannings(userId);
        planningsData.addAll(plannings);
        mettreAJourTotalPlannings();
    }

    private void ajouterPlanning() {
        if (validationFormulaire()) {
            Planning planning = new Planning();
            planning.setNom(nomField.getText());
            planning.setDescription(descriptionField.getText());
            planning.setType(typeCombo.getValue());
            planning.setMois(moisCombo.getValue());
            planning.setAnnee(anneeCombo.getValue());
            planning.setRevenuPrevu(Double.parseDouble(revenuPrevuField.getText()));
            planning.setEpargnePrevue(Double.parseDouble(epargnePrevueField.getText()));
            planning.setPourcentageEpargne(Integer.parseInt(pourcentageEpargneField.getText()));
            planning.setStatut(statutCombo.getValue());
            planning.setUserId(userId);

            planningDAO.ajouterPlanning(planning);
            planningsData.add(planning);
            mettreAJourTotalPlannings();
            clearForm();
            afficherAlerte("Succès", "Planning ajouté avec succès");
        }
    }

    private void modifierPlanning() {
        if (planningActuel != null && validationFormulaire()) {
            planningActuel.setNom(nomField.getText());
            planningActuel.setDescription(descriptionField.getText());
            planningActuel.setType(typeCombo.getValue());
            planningActuel.setMois(moisCombo.getValue());
            planningActuel.setAnnee(anneeCombo.getValue());
            planningActuel.setRevenuPrevu(Double.parseDouble(revenuPrevuField.getText()));
            planningActuel.setEpargnePrevue(Double.parseDouble(epargnePrevueField.getText()));
            planningActuel.setPourcentageEpargne(Integer.parseInt(pourcentageEpargneField.getText()));
            planningActuel.setStatut(statutCombo.getValue());

            planningDAO.modifierPlanning(planningActuel);
            planningsList.refresh();
            mettreAJourTotalPlannings();
            clearForm();
            afficherAlerte("Succès", "Planning modifié avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un planning à modifier (cliquez sur le texte)");
        }
    }

    private void supprimerPlanning() {
        if (!idsSelectionnes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de Suppression");
            alert.setHeaderText("Supprimer les éléments cochés ?");
            alert.setContentText("Vous allez supprimer " + idsSelectionnes.size() + " planning(s). Cette action est irréversible.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                List<Integer> idsToDelete = new ArrayList<>(idsSelectionnes);
                planningDAO.supprimerPlusieursPlannings(idsToDelete);
                loadPlannings();
                clearForm();
                afficherAlerte("Succès", idsToDelete.size() + " planning(s) ont été supprimé(s).");
            }
        } else if (planningActuel != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Supprimer le planning sélectionné : " + planningActuel.getNom() + " ?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                planningDAO.supprimerPlanning(planningActuel.getId());
                loadPlannings();
                clearForm();
                afficherAlerte("Succès", "Planning supprimé avec succès");
            }
        } else {
            afficherAlerte("Info", "Veuillez cocher les plannings à supprimer.");
        }
    }

    private void selectPlanning(Planning planning) {
        if (planning != null) {
            planningActuel = planning;
            nomField.setText(planning.getNom());
            descriptionField.setText(planning.getDescription());
            typeCombo.setValue(planning.getType());
            revenuPrevuField.setText(String.valueOf(planning.getRevenuPrevu()));
            epargnePrevueField.setText(String.valueOf(planning.getEpargnePrevue()));
            pourcentageEpargneField.setText(String.valueOf(planning.getPourcentageEpargne()));
            moisCombo.setValue(planning.getMois());
            anneeCombo.setValue(planning.getAnnee());
            statutCombo.setValue(planning.getStatut());
        }
    }

    private void mettreAJourTotalPlannings() {
        totalPlanningsLabel.setText("Total de plannings: " + planningsData.size());
    }

    private boolean validationFormulaire() {
        if (nomField.getText().isEmpty() || typeCombo.getValue() == null ||
                revenuPrevuField.getText().isEmpty() || epargnePrevueField.getText().isEmpty() ||
                pourcentageEpargneField.getText().isEmpty() || statutCombo.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        try {
            Double.parseDouble(revenuPrevuField.getText());
            Double.parseDouble(epargnePrevueField.getText());
            Integer.parseInt(pourcentageEpargneField.getText());
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Veuillez entrer des nombres valides");
            return false;
        }
        return true;
    }

    private void clearForm() {
        nomField.clear();
        descriptionField.clear();
        revenuPrevuField.clear();
        epargnePrevueField.clear();
        pourcentageEpargneField.clear();
        typeCombo.setValue(null);
        statutCombo.setValue(null);
        moisCombo.setValue(LocalDate.now().getMonthValue());
        anneeCombo.setValue(LocalDate.now().getYear());
        planningActuel = null;
        planningsList.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
