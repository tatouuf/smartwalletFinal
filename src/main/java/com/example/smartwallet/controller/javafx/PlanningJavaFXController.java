package com.example.smartwallet.controller.javafx;

import dao.PlanningDAO;
import com.example.smartwallet.model.Planning;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

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
    private TableView<Planning> planningsTable;
    @FXML
    private TableColumn<Planning, Integer> idColumn;
    @FXML
    private TableColumn<Planning, String> nomColumn;
    @FXML
    private TableColumn<Planning, String> typeColumn;
    @FXML
    private TableColumn<Planning, Integer> moisColumn;
    @FXML
    private TableColumn<Planning, Integer> anneeColumn;
    @FXML
    private TableColumn<Planning, Double> revenuColumn;
    @FXML
    private TableColumn<Planning, Double> epargneColumn;
    @FXML
    private TableColumn<Planning, String> statutColumn;
    @FXML
    private Label totalPlanningsLabel;

    private PlanningDAO planningDAO = new PlanningDAO();
    private ObservableList<Planning> planningsList = FXCollections.observableArrayList();
    private int userId = 1; // User connecté
    private Planning planningActuel = null;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        loadPlannings();
        ajouterBtn.setOnAction(e -> ajouterPlanning());
        modifierBtn.setOnAction(e -> modifierPlanning());
        supprimerBtn.setOnAction(e -> supprimerPlanning());
        planningsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectPlanning(newVal));
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        moisColumn.setCellValueFactory(new PropertyValueFactory<>("mois"));
        anneeColumn.setCellValueFactory(new PropertyValueFactory<>("annee"));
        revenuColumn.setCellValueFactory(new PropertyValueFactory<>("revenuPrevu"));
        epargneColumn.setCellValueFactory(new PropertyValueFactory<>("epargnePrevue"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        planningsTable.setItems(planningsList);
    }

    private void setupComboBoxes() {
        // Type de planning
        typeCombo.setItems(FXCollections.observableArrayList(
            "Personnel", "Familial", "Professionnel", "Retraite"
        ));

        // Statut
        statutCombo.setItems(FXCollections.observableArrayList(
            "En cours", "Terminé", "Suspendu", "Annulé"
        ));

        // Mois
        ObservableList<Integer> mois = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) mois.add(i);
        moisCombo.setItems(mois);
        moisCombo.setValue(LocalDate.now().getMonthValue());

        // Année
        ObservableList<Integer> annees = FXCollections.observableArrayList();
        for (int i = 2020; i <= 2030; i++) annees.add(i);
        anneeCombo.setItems(annees);
        anneeCombo.setValue(LocalDate.now().getYear());
    }

    private void loadPlannings() {
        planningsList.clear();
        List<Planning> plannings = planningDAO.obtenirTousPlannings(userId);
        planningsList.addAll(plannings);
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

            // Ajouter dans la base
            planningDAO.ajouterPlanning(planning);

            // Ajouter directement dans la TableView
            planningsList.add(planning);
            mettreAJourTotalPlannings();
            clearForm();
            afficherAlerte("Succès", "Planning ajouté avec succès");
        }
    }


    private void modifierPlanning() {
        if (planningActuel != null && validationFormulaire()) {
            // Modifier l'objet directement
            planningActuel.setNom(nomField.getText());
            planningActuel.setDescription(descriptionField.getText());
            planningActuel.setType(typeCombo.getValue());
            planningActuel.setMois(moisCombo.getValue());
            planningActuel.setAnnee(anneeCombo.getValue());
            planningActuel.setRevenuPrevu(Double.parseDouble(revenuPrevuField.getText()));
            planningActuel.setEpargnePrevue(Double.parseDouble(epargnePrevueField.getText()));
            planningActuel.setPourcentageEpargne(Integer.parseInt(pourcentageEpargneField.getText()));
            planningActuel.setStatut(statutCombo.getValue());

            // Mettre à jour dans la base
            planningDAO.modifierPlanning(planningActuel);

            // **Pas de reload de toute la liste**, juste notifier la TableView
            planningsTable.refresh();  // <== rafraîchit la ligne modifiée
            mettreAJourTotalPlannings();
            clearForm();
            afficherAlerte("Succès", "Planning modifié avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un planning à modifier");
        }
    }


    private void supprimerPlanning() {
        if (planningActuel != null) {
            planningDAO.supprimerPlanning(planningActuel.getId());
            loadPlannings();
            clearForm();
            afficherAlerte("Succès", "Planning supprimé avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un planning à supprimer");
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
        totalPlanningsLabel.setText("Total de plannings: " + planningsList.size());
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
        planningsTable.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


