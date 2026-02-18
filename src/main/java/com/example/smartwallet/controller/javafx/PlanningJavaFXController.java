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
    // Remplacer TableView par ListView
    @FXML
    private ListView<Planning> planningsList; // correspond au fx:id du FXML
    // Supprimer toutes les colonnes TableColumn
    @FXML
    private Label totalPlanningsLabel;

    private PlanningDAO planningDAO = new PlanningDAO();
    private ObservableList<Planning> planningsData = FXCollections.observableArrayList();
    private int userId = 1; // User connecté
    private Planning planningActuel = null;

    @FXML
    public void initialize() {
        // Configuration du ListView avec une cellule personnalisée
        setupListView();
        // Configuration des ComboBox
        setupComboBoxes();
        // Chargement des données
        loadPlannings();
        // Gestion des boutons
        ajouterBtn.setOnAction(e -> ajouterPlanning());
        modifierBtn.setOnAction(e -> modifierPlanning());
        supprimerBtn.setOnAction(e -> supprimerPlanning());
        // Sélection dans la liste
        planningsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectPlanning(newVal));
    }

    private void setupListView() {
        // Définir comment afficher chaque Planning dans la liste
        planningsList.setCellFactory(lv -> new ListCell<Planning>() {
            @Override
            protected void updateItem(Planning planning, boolean empty) {
                super.updateItem(planning, empty);
                if (empty || planning == null) {
                    setText(null);
                } else {
                    // Format d'affichage : "Nom (Type) - Mois/Année - Revenu: X DT"
                    String text = String.format("%s (%s) - %d/%d - Revenu: %.2f DT - Épargne: %.2f DT - %s",
                            planning.getNom(),
                            planning.getType(),
                            planning.getMois(),
                            planning.getAnnee(),
                            planning.getRevenuPrevu(),
                            planning.getEpargnePrevue(),
                            planning.getStatut());
                    setText(text);
                }
            }
        });
        // Lier les données
        planningsList.setItems(planningsData);
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

            // Ajouter dans la base
            planningDAO.ajouterPlanning(planning);

            // Ajouter directement dans la liste observable
            planningsData.add(planning);
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

            // Rafraîchir l'affichage du ListView (car l'objet a été modifié)
            planningsList.refresh(); // Nécessite JavaFX 8u60 ou plus
            // Alternative si refresh() n'existe pas :
            // int index = planningsData.indexOf(planningActuel);
            // if (index >= 0) {
            //     planningsData.set(index, planningActuel); // ceci forcera une mise à jour
            // }

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
            loadPlannings(); // Recharge la liste depuis la base
            clearForm();
            afficherAlerte("Succès", "Planning supprimé avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un planning à supprimer");
        }
    }

    // Méthode pour sélectionner un planning
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