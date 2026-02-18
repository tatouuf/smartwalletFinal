package com.example.smartwallet.controller.javafx;

import dao.DepenseDAO;
import dao.BudgetDAO;
import com.example.smartwallet.model.Depense;
import com.example.smartwallet.model.Budget;
import com.example.smartwallet.controller.NotificationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    // Remplacer TableView par ListView
    @FXML
    private ListView<Depense> depensesList;

    @FXML
    private Label totalDepensesLabel;
    @FXML
    private ComboBox<String> filterCategorieCombo;
    @FXML
    private ComboBox<Integer> filterMoisCombo;
    @FXML
    private ComboBox<Integer> filterAnneeCombo;

    private DepenseDAO depenseDAO = new DepenseDAO();
    private BudgetDAO budgetDAO = new BudgetDAO();
    private ObservableList<Depense> depensesData = FXCollections.observableArrayList();
    private int userId = 1; // correspond à l'utilisateur créé
    private Depense depenseActuelle = null;

    @FXML
    public void initialize() {
        setupListView();           // Configuration du ListView
        setupCategories();         // Remplissage des ComboBox de catégories
        setupFilters();            // Configuration des filtres
        loadDepenses();            // Chargement initial

        ajouterBtn.setOnAction(e -> ajouterDepense());
        modifierBtn.setOnAction(e -> modifierDepense());
        supprimerBtn.setOnAction(e -> supprimerDepense());

        // Écoute de la sélection dans le ListView
        depensesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectDepense(newVal));
    }

    private void setupListView() {
        // Définition de l'affichage de chaque dépense
        depensesList.setCellFactory(lv -> new ListCell<Depense>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(Depense depense, boolean empty) {
                super.updateItem(depense, empty);
                if (empty || depense == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DT - %s (%s) [%s]",
                            depense.getMontant(),
                            depense.getDescription(),
                            depense.getDateDepense().format(formatter),
                            depense.getCategorie()));
                }
            }
        });
        depensesList.setItems(depensesData);
    }

    private void setupCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Alimentation", "Transport", "Logement", "Santé", "Loisirs", "Éducation", "Autre"
        );
        categorieCombo.setItems(categories);
        filterCategorieCombo.setItems(FXCollections.observableArrayList("Tous", "Alimentation", "Transport", "Logement", "Santé", "Loisirs", "Éducation", "Autre"));
        filterCategorieCombo.setValue("Tous"); // Valeur par défaut
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
        depensesData.clear();
        List<Depense> depenses = depenseDAO.obtenirToutesDepenses(userId);
        depensesData.addAll(depenses);
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

            // Vérifier le budget
            verifierBudget(depense);

            // Ajout dans la liste observable (le ListView se met à jour automatiquement)
            depensesData.add(depense);
            mettreAJourTotalDepenses();
            clearForm();
            afficherAlerte("Succès", "Dépense ajoutée avec succès");
        }
    }

    private void modifierDepense() {
        if (depenseActuelle != null && validationFormulaire()) {
            // Modifier l'objet sélectionné
            depenseActuelle.setMontant(Double.parseDouble(montantField.getText()));
            depenseActuelle.setDescription(descriptionField.getText());
            depenseActuelle.setDateDepense(dateDepenseField.getValue());
            depenseActuelle.setCategorie(categorieCombo.getValue());

            // Mise à jour en base
            depenseDAO.modifierDepense(depenseActuelle);

            // Vérifier le budget
            verifierBudget(depenseActuelle);

            // Rafraîchir l'affichage de l'élément modifié (nécessite JavaFX 8u60+)
            depensesList.refresh();

            // Alternative si refresh() n'est pas disponible :
            // int index = depensesData.indexOf(depenseActuelle);
            // if (index >= 0) depensesData.set(index, depenseActuelle);

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
            loadDepenses(); // Recharge la liste depuis la base (simple)
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
        String categorieFiltre = filterCategorieCombo.getValue();
        Integer moisFiltre = filterMoisCombo.getValue();
        Integer anneeFiltre = filterAnneeCombo.getValue();

        List<Depense> toutesDepenses = depenseDAO.obtenirToutesDepenses(userId);

        // Filtre par mois/année si spécifié
        if (moisFiltre != null && anneeFiltre != null) {
            toutesDepenses = toutesDepenses.stream()
                    .filter(d -> d.getDateDepense().getMonthValue() == moisFiltre &&
                            d.getDateDepense().getYear() == anneeFiltre)
                    .collect(Collectors.toList());
        }

        // Filtre par catégorie
        if (categorieFiltre != null && !categorieFiltre.equals("Tous")) {
            toutesDepenses = toutesDepenses.stream()
                    .filter(d -> d.getCategorie().equals(categorieFiltre))
                    .collect(Collectors.toList());
        }

        depensesData.setAll(toutesDepenses);
        mettreAJourTotalDepenses();
    }

    private void mettreAJourTotalDepenses() {
        double total = depensesData.stream().mapToDouble(Depense::getMontant).sum();
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
        depensesList.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void verifierBudget(Depense depense) {
        int mois = depense.getDateDepense().getMonthValue();
        int annee = depense.getDateDepense().getYear();
        String categorie = depense.getCategorie();

        // Récupérer le budget pour cette catégorie/mois/année
        Budget budget = budgetDAO.obtenirBudgetParCategorie(userId, categorie, mois, annee);

        if (budget != null) {
            // Calculer le total des dépenses pour cette catégorie/mois/année
            double totalDepenses = depenseDAO.getTotalDepensesCategorieMois(userId, categorie, mois, annee);

            if (totalDepenses > budget.getMontantMax()) {
                NotificationController.showWarning(
                    "Budget Dépassé !",
                    "Attention : Vous avez dépassé votre budget pour " + categorie + 
                    " (" + String.format("%.2f", totalDepenses) + " / " + String.format("%.2f", budget.getMontantMax()) + " DT)"
                );
            }
        }
    }
}
