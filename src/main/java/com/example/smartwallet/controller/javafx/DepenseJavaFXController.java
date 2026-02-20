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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    // Stocker les IDs des dépenses cochées
    private Set<Integer> idsSelectionnes = new HashSet<>();

    @FXML
    public void initialize() {
        setupListView();
        setupCategories();
        setupFilters();
        loadDepenses();

        ajouterBtn.setOnAction(e -> ajouterDepense());
        modifierBtn.setOnAction(e -> modifierDepense());
        supprimerBtn.setOnAction(e -> supprimerDepense());

        // La sélection classique (clic sur la ligne) sert à l'édition (remplir le formulaire)
        depensesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectDepense(newVal));
    }

    private void setupListView() {
        depensesList.setCellFactory(lv -> new ListCell<Depense>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label label = new Label();
            private final HBox layout = new HBox(10);
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            {
                layout.getChildren().addAll(checkBox, label);
            }

            @Override
            protected void updateItem(Depense depense, boolean empty) {
                super.updateItem(depense, empty);
                
                if (empty || depense == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Texte de la dépense
                    label.setText(String.format("%.2f DT - %s (%s) [%s]",
                            depense.getMontant(),
                            depense.getDescription(),
                            depense.getDateDepense().format(formatter),
                            depense.getCategorie()));

                    // Gestion de la CheckBox
                    // 1. Désactiver le listener pour éviter les déclenchements lors du recyclage de la cellule
                    checkBox.setOnAction(null);
                    
                    // 2. Définir l'état actuel
                    checkBox.setSelected(idsSelectionnes.contains(depense.getId()));
                    
                    // 3. Réactiver le listener
                    checkBox.setOnAction(e -> {
                        if (checkBox.isSelected()) {
                            idsSelectionnes.add(depense.getId());
                        } else {
                            idsSelectionnes.remove(depense.getId());
                        }
                    });

                    setGraphic(layout);
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
        filterCategorieCombo.setValue("Tous");
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
        // Vider la sélection précédente lors du rechargement
        idsSelectionnes.clear();
        
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

            depenseDAO.ajouterDepense(depense);
            verifierBudget(depense);
            depensesData.add(depense);
            mettreAJourTotalDepenses();
            clearForm();
            afficherAlerte("Succès", "Dépense ajoutée avec succès");
        }
    }

    private void modifierDepense() {
        if (depenseActuelle != null && validationFormulaire()) {
            depenseActuelle.setMontant(Double.parseDouble(montantField.getText()));
            depenseActuelle.setDescription(descriptionField.getText());
            depenseActuelle.setDateDepense(dateDepenseField.getValue());
            depenseActuelle.setCategorie(categorieCombo.getValue());

            depenseDAO.modifierDepense(depenseActuelle);
            verifierBudget(depenseActuelle);
            depensesList.refresh();
            mettreAJourTotalDepenses();
            clearForm();
            afficherAlerte("Succès", "Dépense modifiée avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner une dépense à modifier (cliquez sur le texte)");
        }
    }

    private void supprimerDepense() {
        // On utilise maintenant le Set idsSelectionnes au lieu de la sélection du ListView
        if (!idsSelectionnes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de Suppression");
            alert.setHeaderText("Supprimer les éléments cochés ?");
            alert.setContentText("Vous allez supprimer " + idsSelectionnes.size() + " dépense(s). Cette action est irréversible.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                // Convertir le Set en List pour le DAO
                List<Integer> idsToDelete = new ArrayList<>(idsSelectionnes);
                
                depenseDAO.supprimerPlusieursDepenses(idsToDelete);
                
                loadDepenses(); // Recharge et vide la sélection
                clearForm();
                afficherAlerte("Succès", idsToDelete.size() + " dépense(s) ont été supprimée(s).");
            }
        } else {
            // Fallback : si rien n'est coché, on regarde si une ligne est sélectionnée (bleue)
            if (depenseActuelle != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("Supprimer la dépense sélectionnée : " + depenseActuelle.getDescription() + " ?");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    depenseDAO.supprimerDepense(depenseActuelle.getId());
                    loadDepenses();
                    clearForm();
                }
            } else {
                afficherAlerte("Info", "Veuillez cocher les dépenses à supprimer.");
            }
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

        if (moisFiltre != null && anneeFiltre != null) {
            toutesDepenses = toutesDepenses.stream()
                    .filter(d -> d.getDateDepense().getMonthValue() == moisFiltre &&
                            d.getDateDepense().getYear() == anneeFiltre)
                    .collect(Collectors.toList());
        }

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

        Budget budget = budgetDAO.obtenirBudgetParCategorie(userId, categorie, mois, annee);

        if (budget != null) {
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
