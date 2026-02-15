package com.example.smartwallet.controller.javafx;

import dao.DepenseDAO;
import com.example.smartwallet.model.Depense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class DepenseJavaFXController {

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
    private TableView<Depense> depensesTable;
    @FXML
    private TableColumn<Depense, Integer> idColumn;
    @FXML
    private TableColumn<Depense, Double> montantColumn;
    @FXML
    private TableColumn<Depense, String> descriptionColumn;
    @FXML
    private TableColumn<Depense, LocalDate> dateColumn;
    @FXML
    private TableColumn<Depense, String> categorieColumn;
    @FXML
    private Label totalDepensesLabel;
    @FXML
    private ComboBox<String> filterCategorieCombo;
    @FXML
    private ComboBox<Integer> filterMoisCombo;
    @FXML
    private ComboBox<Integer> filterAnneeCombo;

    private DepenseDAO depenseDAO = new DepenseDAO();
    private ObservableList<Depense> depensesList = FXCollections.observableArrayList();
    private int userId = 1; // User connecté (à récupérer de la session)
    private Depense depenseActuelle = null;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupCategories();
        setupFilters();
        loadDepenses();
        ajouterBtn.setOnAction(e -> ajouterDepense());
        modifierBtn.setOnAction(e -> modifierDepense());
        supprimerBtn.setOnAction(e -> supprimerDepense());
        depensesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectDepense(newVal));
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDepense"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        depensesTable.setItems(depensesList);
    }

    private void setupCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList(
            "Alimentation", "Transport", "Logement", "Santé", "Loisirs", "Éducation", "Autre"
        );
        categorieCombo.setItems(categories);
        filterCategorieCombo.setItems(FXCollections.observableArrayList("Tous", "Alimentation", "Transport", "Logement", "Santé", "Loisirs", "Éducation", "Autre"));
    }

    private void setupFilters() {
        ObservableList<Integer> mois = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) mois.add(i);
        filterMoisCombo.setItems(mois);

        ObservableList<Integer> annees = FXCollections.observableArrayList();
        for (int i = 2020; i <= 2030; i++) annees.add(i);
        filterAnneeCombo.setItems(annees);

        filterMoisCombo.setOnAction(e -> filtrerDepenses());
        filterAnneeCombo.setOnAction(e -> filtrerDepenses());
        filterCategorieCombo.setOnAction(e -> filtrerDepenses());
    }

    private void loadDepenses() {
        depensesList.clear();
        List<Depense> depenses = depenseDAO.obtenirToutesDepenses(userId);
        depensesList.addAll(depenses);
        mettreAJourTotalDepenses();
    }

    private void ajouterDepense() {
        if (validationFormulaire()) {
            Depense depense = new Depense();
            depense.setMontant(Double.parseDouble(montantField.getText()));
            depense.setDescription(descriptionField.getText());
            depense.setDateDepense(dateDepenseField.getValue());
            depense.setCategorie(categorieCombo.getValue());
            depense.setUserId(userId);

            // Ajout dans la base
            depenseDAO.ajouterDepense(depense);

            // Ajout direct dans la TableView
            depensesList.add(depense);
            mettreAJourTotalDepenses();
            clearForm();
            afficherAlerte("Succès", "Dépense ajoutée avec succès");
        }
    }


    private void modifierDepense() {
        if (depenseActuelle != null && validationFormulaire()) {

            // Modifier l'objet sélectionné directement
            depenseActuelle.setMontant(Double.parseDouble(montantField.getText()));
            depenseActuelle.setDescription(descriptionField.getText());
            depenseActuelle.setDateDepense(dateDepenseField.getValue());
            depenseActuelle.setCategorie(categorieCombo.getValue());

            // Mise à jour en base
            depenseDAO.modifierDepense(depenseActuelle);

            // Rafraîchir seulement la ligne (PAS reload)
            depensesTable.refresh();
            mettreAJourTotalDepenses();

            clearForm();
            afficherAlerte("Succès", "Dépense modifiée avec succès");

        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner une dépense à modifier");
        }
    }

    private void supprimerDepense() {
        if (depenseActuelle != null) {
            depenseDAO.supprimerDepense(depenseActuelle.getId());
            loadDepenses();
            clearForm();
            afficherAlerte("Succès", "Dépense supprimée avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner une dépense à supprimer");
        }
    }

    private void selectDepense(Depense depense) {
        if (depense != null) {
            depenseActuelle = depense;
            montantField.setText(String.valueOf(depense.getMontant()));
            descriptionField.setText(depense.getDescription());
            dateDepenseField.setValue(depense.getDateDepense());
            categorieCombo.setValue(depense.getCategorie());
        }
    }

    private void filtrerDepenses() {
        depensesList.clear();
        String categorieFiltre = filterCategorieCombo.getValue();
        Integer moisFiltre = filterMoisCombo.getValue();
        Integer anneeFiltre = filterAnneeCombo.getValue();

        List<Depense> depenses;

        if (moisFiltre != null && anneeFiltre != null) {
            depenses = depenseDAO.obtenirDepensesMois(userId, moisFiltre, anneeFiltre);
        } else {
            depenses = depenseDAO.obtenirToutesDepenses(userId);
        }

        if (categorieFiltre != null && !categorieFiltre.equals("Tous")) {
            depenses.removeIf(d -> !d.getCategorie().equals(categorieFiltre));
        }

        depensesList.addAll(depenses);
        mettreAJourTotalDepenses();
    }

    private void mettreAJourTotalDepenses() {
        double total = depensesList.stream().mapToDouble(Depense::getMontant).sum();
        totalDepensesLabel.setText(String.format("Total: %.2f DT", total));
    }

    private boolean validationFormulaire() {
        if (montantField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
            dateDepenseField.getValue() == null || categorieCombo.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs");
            return false;
        }
        try {
            Double.parseDouble(montantField.getText());
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le montant doit être un nombre");
            return false;
        }
        return true;
    }

    private void clearForm() {
        montantField.clear();
        descriptionField.clear();
        dateDepenseField.setValue(null);
        categorieCombo.setValue(null);
        depenseActuelle = null;
        depensesTable.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


