package com.example.smartwallet.controller.javafx;

import dao.BudgetDAO;
import com.example.smartwallet.TabManager;
import com.example.smartwallet.model.Budget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class BudgetJavaFXController {

    @FXML
    private TextField montantMaxField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox<String> categorieCombo;
    @FXML
    private ComboBox<Integer> moisCombo;
    @FXML
    private ComboBox<Integer> anneeCombo;
    @FXML
    private Button ajouterBtn;
    @FXML
    private Button modifierBtn;
    @FXML
    private Button supprimerBtn;

    // Remplacement de TableView par ListView
    @FXML
    private ListView<Budget> budgetsList;

    @FXML
    private Label totalBudgetsLabel;
    @FXML
    private ProgressBar budgetProgressBar;
    @FXML
    private Button retourBtn; // bouton Retour

    private BudgetDAO budgetDAO = new BudgetDAO();
    private ObservableList<Budget> budgetsData = FXCollections.observableArrayList();
    private int userId = 1; // Utilisateur connecté
    private Budget budgetActuel = null;

    @FXML
    public void initialize() {
        setupListView();           // Configuration du ListView
        setupCategories();         // Remplissage des catégories
        setupMonthsYears();        // Remplissage mois/année
        loadBudgets();             // Chargement initial

        ajouterBtn.setOnAction(e -> ajouterBudget());
        modifierBtn.setOnAction(e -> modifierBudget());
        supprimerBtn.setOnAction(e -> supprimerBudget());

        // Écoute de la sélection dans le ListView
        budgetsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectBudget(newVal));

        if (retourBtn != null) {
            retourBtn.setOnAction(this::goToDashboard);
        }
    }

    private void setupListView() {
        // Définition de l'affichage de chaque budget
        budgetsList.setCellFactory(lv -> new ListCell<Budget>() {
            @Override
            protected void updateItem(Budget budget, boolean empty) {
                super.updateItem(budget, empty);
                if (empty || budget == null) {
                    setText(null);
                } else {
                    // Affichage formaté : Catégorie - Max: X DT / Actuel: Y DT (Mois/Année)
                    setText(String.format("%s - Max: %.2f DT / Actuel: %.2f DT (%d/%d)",
                            budget.getCategorie(),
                            budget.getMontantMax(),
                            budget.getMontantActuel(),
                            budget.getMois(),
                            budget.getAnnee()));
                }
            }
        });
        budgetsList.setItems(budgetsData);
    }

    private void setupCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Alimentation", "Transport", "Logement", "Santé", "Loisirs", "Éducation", "Autre"
        );
        categorieCombo.setItems(categories);
    }

    private void setupMonthsYears() {
        ObservableList<Integer> mois = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) mois.add(i);
        moisCombo.setItems(mois);
        moisCombo.setValue(LocalDate.now().getMonthValue());

        ObservableList<Integer> annees = FXCollections.observableArrayList();
        for (int i = 2020; i <= 2030; i++) annees.add(i);
        anneeCombo.setItems(annees);
        anneeCombo.setValue(LocalDate.now().getYear());
    }

    private void loadBudgets() {
        budgetsData.clear();
        List<Budget> budgets = budgetDAO.obtenirTousBudgets(userId);
        budgetsData.addAll(budgets);
        mettreAJourTotalBudgets();
    }

    private void ajouterBudget() {
        if (validationFormulaire()) {
            Budget budget = new Budget();
            budget.setCategorie(categorieCombo.getValue());
            budget.setMontantMax(Double.parseDouble(montantMaxField.getText()));
            budget.setMontantActuel(0.0);
            budget.setMois(moisCombo.getValue());
            budget.setAnnee(anneeCombo.getValue());
            budget.setDescription(descriptionField.getText());
            budget.setUserId(userId);
            budget.setDateCreation(LocalDate.now());

            // Ajout en base
            budgetDAO.ajouterBudget(budget);

            // Ajout dans la liste observable (le ListView se met à jour automatiquement)
            budgetsData.add(budget);
            mettreAJourTotalBudgets();
            clearForm();
            afficherAlerte("Succès", "Budget ajouté avec succès");
        }
    }

    private void modifierBudget() {
        if (budgetActuel != null && validationFormulaire()) {
            // Modifier l'objet sélectionné
            budgetActuel.setCategorie(categorieCombo.getValue());
            budgetActuel.setMontantMax(Double.parseDouble(montantMaxField.getText()));
            budgetActuel.setDescription(descriptionField.getText());
            budgetActuel.setMois(moisCombo.getValue());
            budgetActuel.setAnnee(anneeCombo.getValue());

            // Mise à jour en base
            budgetDAO.modifierBudget(budgetActuel);

            // Rafraîchir l'affichage de l'élément modifié (JavaFX 8u60+)
            budgetsList.refresh();

            // Alternative si refresh() n'est pas disponible :
            // int index = budgetsData.indexOf(budgetActuel);
            // if (index >= 0) budgetsData.set(index, budgetActuel);

            mettreAJourTotalBudgets();
            clearForm();
            afficherAlerte("Succès", "Budget modifié avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un budget à modifier");
        }
    }

    private void supprimerBudget() {
        if (budgetActuel != null) {
            budgetDAO.supprimerBudget(budgetActuel.getId());
            loadBudgets(); // Recharge la liste depuis la base
            clearForm();
            afficherAlerte("Succès", "Budget supprimé avec succès");
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un budget à supprimer");
        }
    }

    private void selectBudget(Budget budget) {
        if (budget != null) {
            budgetActuel = budget;
            categorieCombo.setValue(budget.getCategorie());
            montantMaxField.setText(String.valueOf(budget.getMontantMax()));
            descriptionField.setText(budget.getDescription());
            moisCombo.setValue(budget.getMois());
            anneeCombo.setValue(budget.getAnnee());

            // Afficher la progression
            double pourcentage = (budget.getMontantActuel() / budget.getMontantMax()) * 100;
            budgetProgressBar.setProgress(Math.min(pourcentage / 100, 1.0));
        }
    }

    private void mettreAJourTotalBudgets() {
        double total = budgetsData.stream().mapToDouble(Budget::getMontantMax).sum();
        totalBudgetsLabel.setText(String.format("Total: %.2f DT", total));
    }

    private boolean validationFormulaire() {
        if (montantMaxField.getText().isEmpty() || categorieCombo.getValue() == null ||
                moisCombo.getValue() == null || anneeCombo.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        try {
            Double.parseDouble(montantMaxField.getText());
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le montant doit être un nombre");
            return false;
        }
        return true;
    }

    private void clearForm() {
        montantMaxField.clear();
        descriptionField.clear();
        categorieCombo.setValue(null);
        moisCombo.setValue(LocalDate.now().getMonthValue());
        anneeCombo.setValue(LocalDate.now().getYear());
        budgetProgressBar.setProgress(0.0);
        budgetActuel = null;
        budgetsList.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        boolean ok = TabManager.showView("/com/example/smartwallet/dashboard-view.fxml", "Tableau de Bord");
        if (ok) return;

        // Fallback
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smartwallet/dashboard-view.fxml"));
            Parent content = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            if (stage.getScene() != null && stage.getScene().getRoot() instanceof BorderPane) {
                BorderPane root = (BorderPane) stage.getScene().getRoot();
                root.setCenter(content);
            } else {
                stage.setScene(new Scene(content));
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}