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
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @FXML
    private ListView<Budget> budgetsList;
    @FXML
    private Label totalBudgetsLabel;
    @FXML
    private ProgressBar budgetProgressBar;
    @FXML
    private Button retourBtn;

    private BudgetDAO budgetDAO = new BudgetDAO();
    private ObservableList<Budget> budgetsData = FXCollections.observableArrayList();
    private int userId = 1; // Utilisateur connecté
    private Budget budgetActuel = null;

    // Stocker les IDs des budgets cochés
    private Set<Integer> idsSelectionnes = new HashSet<>();

    @FXML
    public void initialize() {
        setupListView();
        setupCategories();
        setupMonthsYears();
        loadBudgets();

        ajouterBtn.setOnAction(e -> ajouterBudget());
        modifierBtn.setOnAction(e -> modifierBudget());
        supprimerBtn.setOnAction(e -> supprimerBudget());

        budgetsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectBudget(newVal));

        if (retourBtn != null) {
            retourBtn.setOnAction(this::goToDashboard);
        }
    }

    private void setupListView() {
        budgetsList.setCellFactory(lv -> new ListCell<Budget>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label label = new Label();
            private final HBox layout = new HBox(10);

            {
                layout.getChildren().addAll(checkBox, label);
            }

            @Override
            protected void updateItem(Budget budget, boolean empty) {
                super.updateItem(budget, empty);
                if (empty || budget == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String text = String.format("%s - Max: %.2f DT / Actuel: %.2f DT (%d/%d)",
                            budget.getCategorie(),
                            budget.getMontantMax(),
                            budget.getMontantActuel(),
                            budget.getMois(),
                            budget.getAnnee());
                    label.setText(text);

                    checkBox.setOnAction(null);
                    checkBox.setSelected(idsSelectionnes.contains(budget.getId()));
                    checkBox.setOnAction(e -> {
                        if (checkBox.isSelected()) {
                            idsSelectionnes.add(budget.getId());
                        } else {
                            idsSelectionnes.remove(budget.getId());
                        }
                    });

                    setGraphic(layout);
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
        idsSelectionnes.clear();
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

            budgetDAO.ajouterBudget(budget);
            
            // Recharger pour avoir l'ID correct
            loadBudgets();
            
            clearForm();
            afficherAlerte("Succès", "Budget ajouté avec succès");
        }
    }

    private void modifierBudget() {
        if (budgetActuel != null && validationFormulaire()) {
            // Créer un objet temporaire
            Budget budgetModifie = new Budget();
            budgetModifie.setId(budgetActuel.getId());
            budgetModifie.setUserId(budgetActuel.getUserId());
            budgetModifie.setMontantActuel(budgetActuel.getMontantActuel());
            budgetModifie.setDateCreation(budgetActuel.getDateCreation());
            
            budgetModifie.setCategorie(categorieCombo.getValue());
            budgetModifie.setMontantMax(Double.parseDouble(montantMaxField.getText()));
            budgetModifie.setDescription(descriptionField.getText());
            budgetModifie.setMois(moisCombo.getValue());
            budgetModifie.setAnnee(anneeCombo.getValue());

            // Tenter la mise à jour SQL
            boolean succes = budgetDAO.modifierBudget(budgetModifie);

            if (succes) {
                // Mettre à jour l'objet en mémoire seulement si succès SQL
                budgetActuel.setCategorie(budgetModifie.getCategorie());
                budgetActuel.setMontantMax(budgetModifie.getMontantMax());
                budgetActuel.setDescription(budgetModifie.getDescription());
                budgetActuel.setMois(budgetModifie.getMois());
                budgetActuel.setAnnee(budgetModifie.getAnnee());
                
                budgetsList.refresh();
                mettreAJourTotalBudgets();
                clearForm();
                afficherAlerte("Succès", "Budget modifié avec succès");
            } else {
                afficherAlerte("Erreur", "Impossible de modifier le budget en base de données.");
            }
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un budget à modifier");
        }
    }

    private void supprimerBudget() {
        if (!idsSelectionnes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de Suppression");
            alert.setHeaderText("Supprimer les éléments cochés ?");
            alert.setContentText("Vous allez supprimer " + idsSelectionnes.size() + " budget(s). Cette action est irréversible.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                List<Integer> idsToDelete = new ArrayList<>(idsSelectionnes);
                budgetDAO.supprimerPlusieursBudgets(idsToDelete);
                loadBudgets();
                clearForm();
                afficherAlerte("Succès", idsToDelete.size() + " budget(s) ont été supprimé(s).");
            }
        } else if (budgetActuel != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Supprimer le budget sélectionné : " + budgetActuel.getCategorie() + " ?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                budgetDAO.supprimerBudget(budgetActuel.getId());
                loadBudgets();
                clearForm();
                afficherAlerte("Succès", "Budget supprimé avec succès");
            }
        } else {
            afficherAlerte("Info", "Veuillez cocher les budgets à supprimer.");
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
